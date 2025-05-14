package gitlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;


/* Represents a gitlet repository. */
public class Repository {
    /* The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /* The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /* The .gitlet/object/ directory has many files named after UID.
    In each UID file is the serialized commit or blob. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /* The .gitlet/refs/heads/ directory has many files named after branches' name such as master.
    And in the file is the branch's head commit UID. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs/heads");
    /* The HEAD file stores the current branch's relative path, such as "refs/heads/master". */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    /* Create a gitlet repository. */
    public static void createRepository() throws IOException {
        // If it's already exists, it should not override the existing .gitlet
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // Initialize all the necessary directories and files
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        REFS_DIR.mkdirs();
        HEAD.createNewFile();

        Commit initialCommit = new Commit("initial commit", null, null, new HashMap<>());
        initialCommit.save();

        // Create the master branch
        File master = join(REFS_DIR, "master");
        master.createNewFile();
        writeContents(master, initialCommit.getUID());

        // .gitlet/HEAD file store the current branch. It starts with master
        writeContents(HEAD, "master");

        // Create a stagingArea for the repository
        StagingArea stagingArea = new StagingArea();
        stagingArea.save();
    }


    /* Given the fileName(String), add it to the staging area.
    Make its blob(store), in the .gitlet/staging/add(ADD_DIR) directory
    create a file named fileName and its content is the blob SHA-1 code. */
    public static void add(String fileName) throws IOException {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob blob = new Blob(file);
        blob.save();
        /* If the blob you want to add to the staging area is the same in the current commit version,
        we should remove the file's previous blob in the staging area if it exists. */
        if (getCurrentCommit().exist(blob)) {
            getStagingArea().getAddedFiles().remove(fileName);
            return;
        }

        StagingArea stagingArea = getStagingArea();
        /* If you stage a file, then it should be removed from the removedFiles if it exists. */
        stagingArea.stageFile(fileName, blob.getUID());
        stagingArea.getRemovedFiles().remove(fileName);
        stagingArea.save();
    }

    /* Make a current commit and save. */
    public static void commit(String message) {
        createCommit(message, null);
    }

    /* Provide a commit method for merge commit that need to specify the secondParent. */
    private static void commit(String message, String secondParentUID) {
        createCommit(message, secondParentUID);
    }

    /* A helper method to simplify the code. */
    private static void createCommit(String message, String secondParentUID) {
        StagingArea stagingArea = getStagingArea();
        // Handle error case and exit
        if (stagingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        // Use current commit to create a new commit
        Commit parentCommit = getCurrentCommit();
        Commit newCommit = new Commit(message, parentCommit.getUID(), secondParentUID, parentCommit.fileMap);
        // Add new files and remove rm files
        newCommit.add(stagingArea.getAddedFiles());
        newCommit.remove(stagingArea.getRemovedFiles());
        newCommit.save();
        // Change the head to point at the newCommit
        changeHeadTo(newCommit);
        stagingArea.clear();
    }


    /* Conduct remove:
     * 2. If the file exits in the current commit,
     *   label it removed by adding it to the stagingArea.removedFiles,
     *   and truly remove it in CWD by calling restrictDelete.
     * 1. If the file no exists in the current commit, but already be added to the stagingArea.addFiles,
     *   just unstage it by removing the file in stagingArea.addedFiles.
     * */
    public static void remove(String fileName) {
        StagingArea stagingArea = getStagingArea();
        Commit curCommit = getCurrentCommit();
        boolean isStaged = stagingArea.hasAddedFile(fileName);
        boolean isTracked = curCommit.exist(fileName);

        if (!isStaged && !isTracked) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (isStaged) {
            stagingArea.unStageFile(fileName);
        }

        if (isTracked) {
            stagingArea.removeFile(fileName);
            restrictedDelete(fileName);
        }
        stagingArea.save();
    }


    /* Print out all the commit information from head to initial commit. */
    public static void printLog() {
        Commit curCommit = getCurrentCommit();
        while (curCommit != null) {
            printCommit(curCommit);
            curCommit = curCommit.parentCommit();
        }
    }


    /* Print out all the commit in whatever order. */
    public static void printGlobalLog() {
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        if (files == null) {
            return;
        }
        for (String fileName : files) {
            File commitFile = join(OBJECTS_DIR, fileName);
            try {
                Commit commit = readObject(commitFile, Commit.class);
                printCommit(commit);
            } catch (IllegalArgumentException e) {
                /* Deal with the file that is blob or something else. */
            }
        }
    }


    /* Print out the UID of all commits that matches the given commit message. */
    public static void find(String message) {
        List<String> files = plainFilenamesIn(OBJECTS_DIR);
        boolean isFind = false;
        if (files != null) {
            for (String fileName : files) {
                File commitFile = join(OBJECTS_DIR, fileName);
                try {
                    Commit commit = readObject(commitFile, Commit.class);
                    if (commit.message.equals(message)) {
                        System.out.println(commit.getUID());
                        isFind = true;
                    }
                } catch (IllegalArgumentException e) {
                    /* Deal with the file that is blob or something else. */
                }
            }
        }
        if (!isFind) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /* Print out the Branches, Staged Files and Removed Files in lexicographic order. */
    public static void status() {
        StagingArea area = getStagingArea();

        System.out.print("=== Branches ===\n*");
        String currentBranch = getCurrentBranch();
        System.out.println(currentBranch);
        printLexically(plainFilenamesIn(REFS_DIR), currentBranch);

        System.out.println("=== Staged Files ===");
        printLexically(area.getAddedFiles());

        System.out.println("=== Removed Files ===");
        printLexically(area.getRemovedFiles());

        System.out.println();
    }

    /* Give checkoutFile two parameters(fileName and commitUID) or just one(fileName)
     * With only one fileName, we assume the file should be in the current commit in default.
     *  parameters sequence will be [0]fileName, [1]commitUID prefix. */
    public static void checkoutFile(String... parameters) {
        // Initialize the commit using commitUID prefix if it exists. Otherwise, use the current commit
        String fileName = parameters[0];
        Commit commit;
        if (parameters.length == 2) {
            String prefix = parameters[1];
            commit = Commit.getCommit(prefix);
        } else {
            commit = getCurrentCommit();
        }

        // Handle error
        if (commit == null) {
            handleErrorAndExit("No commit with that id exists.");
        }
        if (!commit.exist(fileName)) {
            handleErrorAndExit("File does not exist in that commit.");
        }

        // Get the file content and put it in the CWD. If it already exists, override it
        byte[] fileContent = commit.getFileContent(fileName);
        File fileInCWD = join(CWD, fileName);
        writeContents(fileInCWD, (Object) fileContent);
    }


    /* Checkout to another branch. */
    public static void checkoutBranch(String branch) {
        /* Handle three possible errors .*/
        if (!exists(branch)) {
            handleErrorAndExit("No such branch exists.");
        }
        if (isCurrentBranch(branch)) {
            handleErrorAndExit("No need to checkout the current branch.");
        }
        /* Get the new branch's head commit and deal with the third error. */
        Commit commit = Commit.getCommit(readContentsAsString(join(REFS_DIR, branch)));
        if (hasUntrackedFileConflict(commit)) {
            handleErrorAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        checkoutCommit(commit);
        /* Record the branch in the HEAD, meaning the current branch is changed. */
        writeContents(HEAD, branch);
        getStagingArea().clear();
    }

    /* Reset to a commit simply means checkout commit and reset the HEAD. */
    public static void reset(String prefix) {
        Commit commit = Commit.getCommit(prefix);
        if (commit == null) {
            handleErrorAndExit("No commit with that id exists.");
        }
        if (hasUntrackedFileConflict(commit)) {
            handleErrorAndExit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        checkoutCommit(commit);
        changeHeadTo(commit);
        getStagingArea().clear();
    }


    /* Change the CWD totally with the given commit -> "Check out(捡出) all the files in the given commit"
     *  1. Get the new branch's head commitUID. It is in the REF_DIR/branch file.
     *  2. For each file in the commit's fileMap, writeContent(CWDFile, fileContent).
     *  3. For each file in current commit's fileMap, if it doesn't exist in commit's fileMap,
     *     Remove it from the CWD using restrictedDelete. */
    public static void checkoutCommit(Commit commit) {
        for (String fileName : commit.fileMap.keySet()) {
            File newFile = join(CWD, fileName);
            writeContents(newFile, (Object) commit.getFileContent(fileName));
        }
        for (String fileName : getCurrentCommit().fileMap.keySet()) {
            if (commit.fileMap.containsKey(fileName)) {
                continue;
            }
            restrictedDelete(fileName);
        }
    }


    /* Create a new branch with the given name and it should point to the current commit. */
    public static void branch(String branch) {
        /* Just write the current commitUID into the file called
         *  .gitlet/refs/heads/branchName. Nothing more should be done! */
        Commit curCommit = getCurrentCommit();
        File branchFile = join(REFS_DIR, branch);
        if (branchFile.exists()) {
            handleErrorAndExit("A branch with that name already exists.");
        }
        writeContents(branchFile, curCommit.getUID());
    }

    /* Delete the branch with the given name.
     *  Just remove the file called REF_DIR/branch in reality. */
    public static void removeBranch(String branch) {
        // Handle errors
        if (!exists(branch)) {
            handleErrorAndExit("A branch with that name does not exist.");
        }
        if (isCurrentBranch(branch)) {
            handleErrorAndExit("Cannot remove the current branch.");
        }

        File branchFile = join(REFS_DIR, branch);
        restrictedDelete(branchFile);
    }


    /* Merge files from the given branch into the current branch. */
    public static void merge(String givenBranch) throws IOException {
        String currentBranch = getCurrentBranch();
        File currentBranchFile = join(REFS_DIR, currentBranch);
        File givenBranchFile = join(REFS_DIR, givenBranch);
        Commit currentBranchHead = readObject(currentBranchFile, Commit.class);
        Commit givenBranchHead = readObject(givenBranchFile, Commit.class);
        Commit splitPoint = latestCommonAncestor(givenBranch, currentBranch);

        /* Two cases that don't need to merge, not error but early return. */
        if (givenBranchHead.equals(splitPoint)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (currentBranchHead.equals(splitPoint)) {
            /* Fast-forward the currentBranchHead to the givenBranchHead
             *  Just move the currentBranchHead to point at the givenBranchHead.
             *  And since the head is changed, we should change the CWD to the givenCommit. */
            writeContents(currentBranchFile, givenBranchHead.getUID());
            checkoutCommit(givenBranchHead);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        /* Merge the files, return true if encounter merge conflict. */
        boolean hasConflict = mergeFiles(currentBranchHead, givenBranchHead, splitPoint);

        /* Commit automatically. */
        Formatter formatter = new Formatter();
        String message = formatter.format("Merged %s into %s", givenBranch, currentBranch).toString();
        commit(message, givenBranchHead.getUID());

        /* After everything is done, print out the conflict message. */
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }


    /* Simply merge the files according to the 8 rules, return true if it has merge conflict. */
    private static boolean mergeFiles(Commit current, Commit given, Commit split) throws IOException {
        Set<String> allFiles = getAllFiles(current, given, split);
        StagingArea area = getStagingArea();
        boolean hasConflict = false;
        // Handle the file one by one
        for (String fileName : allFiles) {
            // The fileUID in three commits which represents the file's content. If the fileUID is null, means the file doesn't exist.
            String UIDInGiven = given.getFileUID(fileName);
            String UIDInCurrent = current.getFileUID(fileName);
            String UIDInSplit = split.getFileUID(fileName);

            // Record the change since the splitPoint
            boolean currentModified = UIDInSplit != null && UIDInCurrent != null && !UIDInSplit.equals(UIDInCurrent);
            boolean givenModified = UIDInSplit != null && UIDInGiven != null && !UIDInSplit.equals(UIDInGiven);
            boolean currentAdded = UIDInSplit == null && UIDInCurrent != null;
            boolean givenAdded = UIDInSplit == null && UIDInGiven != null;
            boolean currentRemoved = UIDInSplit != null && UIDInCurrent == null;
            boolean givenRemoved = UIDInSplit != null && UIDInGiven == null;
            boolean sameModified = (UIDInCurrent == null && UIDInGiven == null) || UIDInCurrent.equals(UIDInGiven);

            if (givenModified && !currentModified) {// Only modify in the given branch
                checkoutFile(fileName, given.getUID());
                area.stageFile(fileName, UIDInGiven);
            } else if (currentModified && !givenModified) {// Only modify in the current branch
                continue;
            } else if (sameModified) {// Two branch have the same modification
                continue;
            } else if (currentAdded && !givenAdded) { // Only add in the current branch
                continue;
            } else if (givenAdded && !currentAdded) { // Only add in the given branch
                checkoutFile(fileName, given.getUID());
                area.stageFile(fileName, UIDInGiven);
            } else if (givenRemoved && !currentModified) { // Exist in splitPoint but removed in given, unmodified in current
                remove(fileName);
            } else if (currentRemoved) { // Exist in splitPoint but removed in current, unmodified in given
                continue;
            } else if (hasMergeConflict(UIDInCurrent, UIDInGiven, UIDInSplit)) {
                rewriteConflictFile(fileName, UIDInCurrent, UIDInGiven);
                add(fileName);
                hasConflict = true;
            } else {
                handleErrorAndExit("Merge encounters a mysterious error.");
            }
        }
        return hasConflict;
    }


    /* Given the file name, rewrite the file with conflict message.
     *  The file is expected to exist in the CWD. If not just create it. */
    private static void rewriteConflictFile(String fileName, String UIDInCurrent, String UIDInGiven) throws IOException {
        // Locate the conflict file in the CWD, current branch and given branch
        File conflictFile = join(CWD, fileName);
        File currentFile = join(OBJECTS_DIR, UIDInCurrent);
        File givenFile = join(OBJECTS_DIR, UIDInGiven);
        if (!conflictFile.exists()) {
            conflictFile.createNewFile();
        }
        // Rewrite it with the conflict message
        Formatter formatter = new Formatter();
        String conflictMessage = formatter.format("<<<<<<< HEAD\n%s\n=======\n%s>>>>>>>",
                readContentsAsString(currentFile), readContentsAsString(givenFile)).toString();

        writeContents(conflictFile, conflictMessage);
    }


    /* Get all the fileNames in commits and return the set of fileName. */
    private static Set<String> getAllFiles(Commit... commits) {
        Set<String> allFiles = new HashSet<>();
        for (Commit commit : commits) {
            allFiles.addAll(commit.fileMap.keySet());
        }
        return allFiles;
    }


    /* Return true if
     *  1. Two different version of modification.
     *  2. One modify, the other remove.
     *  3. Not exist in split, but add in both branches different version. */
    private static boolean hasMergeConflict(String UIDInCurrent, String UIDInGiven, String UIDISplit) {
        if (UIDISplit == null) {// If not exist in split, only both addition can return true
            return UIDInCurrent != null && UIDInGiven != null && !UIDInCurrent.equals(UIDInGiven);
        } else {
            boolean currentChanged = !Objects.equals(UIDInCurrent, UIDISplit);
            boolean givenChanged = !Objects.equals(UIDInGiven, UIDISplit);
            return currentChanged && givenChanged && !Objects.equals(UIDInCurrent, UIDInGiven);
        }
    }


    //todo ====== The following two method now don't fit the secondParent situation.=====

    /* Return the latest common ancestor (just a commit) for two given branch. */
    private static Commit latestCommonAncestor(String branch1, String branch2) {
        // Get all the parent of branch1 and branch2 separately. Store in map<commitUID, depth>
        Map<String, Integer> ancestor1 = getAllAncestorWithDepth(branch1);
        Map<String, Integer> ancestor2 = getAllAncestorWithDepth(branch2);

        // Find the shared parent that has the smallest depth
        int minDepth = Integer.MAX_VALUE;
        String splitPointUID = null;

        for (String UID : ancestor1.keySet()) {
            if (!ancestor2.containsKey(UID)) continue;
            int depth = ancestor1.get(UID);
            if (depth < minDepth) {
                minDepth = depth;
                splitPointUID = UID;
            }
        }
        return splitPointUID == null ? null : Commit.getCommit(splitPointUID);
    }

    /* Return Map<commitUID, depth> of the given branch. */
    private static Map<String, Integer> getAllAncestorWithDepth(String branch) {
        Commit curCommit = Commit.getCommit(readContentsAsString(join(REFS_DIR, branch)));
        Map<String, Integer> commitMap = new HashMap<>();
        int depth = 0;
        while (curCommit != null) {
            commitMap.put(curCommit.getUID(), depth);
            curCommit = curCommit.parentCommit();
            depth++;
        }
        return commitMap;
    }


    /* Return true if the branch actually exists in the REF_DIR. */
    private static boolean exists(String branch) {
        File branchFile = join(REFS_DIR, branch);
        return branchFile.exists();
    }

    /* Return true if the given branch is the current branch. */
    private static boolean isCurrentBranch(String branch) {
        String currentBranch = getCurrentBranch();
        return branch.equals(currentBranch);
    }

    /* Return true if there is any untracked file (not exist in current commit)
     *  would be overwritten by the given commit. */
    private static boolean hasUntrackedFileConflict(Commit commit) {
        Commit curCommit = getCurrentCommit();
        for (String fileName : commit.fileMap.keySet()) {
            File file = join(CWD, fileName);
            boolean fileExistInCWD = file.exists();
            boolean isTracked = curCommit.fileMap.containsKey(fileName);
            if (!isTracked && fileExistInCWD) {
                return true;
            }
        }
        return false;
    }

    /* A helper method to print the file names lexically. */
    private static void printLexically(Map<String, String> fileMap) {
        // Put in the TreeSet and get back, TreeSet will arrange data in lexicographic order
        Set<String> fileSet = new TreeSet<>(fileMap.keySet());
        for (String fileName : fileSet) {
            System.out.println(fileName);
        }
        System.out.println();
    }

    /* A helper method to print the branches names lexically. */
    private static void printLexically(List<String> branches, String currentBranch) {
        if (branches == null) {
            return;
        }
        Set<String> branchSet = new TreeSet<>(branches);
        for (String branch : branchSet) {
            if (branch.equals(currentBranch)) {
                continue;
            }
            System.out.println(branch);
        }
        System.out.println();
    }

    /* A helper method to print commit in specified format. */
    private static void printCommit(Commit commit) {
        Formatter formatter = new Formatter();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        formatter.format("===\ncommit %s\nDate: %s\n%s\n\n",
                commit.getUID(), commit.timestamp, commit.message);
        System.out.print(formatter.toString());
    }

    /* Change the head to point at the given commit. */
    private static void changeHeadTo(Commit newCommit) {
        File branchFile = join(REFS_DIR, getCurrentBranch());
        writeContents(branchFile, newCommit.getUID());
    }

    /* Return the current commit object. */
    private static Commit getCurrentCommit() {
        File branchFile = join(REFS_DIR, getCurrentBranch());
        File currentCommitFile = join(OBJECTS_DIR, readContentsAsString(branchFile));
        return readObject(currentCommitFile, Commit.class);
    }

    /* Return the current staging area object. */
    private static StagingArea getStagingArea() {
        File stagingAreaFile = join(OBJECTS_DIR, "stagingArea");
        return readObject(stagingAreaFile, StagingArea.class);
    }

    /* Return the current branch name. */
    private static String getCurrentBranch() {
        return readContentsAsString(HEAD);
    }

    /* A helper method to handleError. */
    private static void handleErrorAndExit(String errorMessage) {
        System.out.println(errorMessage);
        System.exit(0);
    }

}
