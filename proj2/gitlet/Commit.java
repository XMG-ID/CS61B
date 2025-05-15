package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Repository.CWD;
import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;

/* Represents a gitlet commit object.*/
public class Commit implements Serializable, Dumpable {

    /* The message of this Commit. */
    public String message;
    /* The time when the Commit is created. */
    public String timestamp;
    /* The SHA-1 code of its parent Commit. */
    public String parentUID;
    /* The SHA-1 code of its second parent Commit. */
    public String secondParentUID;
    /* The map of the Commit's files : file name -> file blob's SHA-1. */
    public Map<String, String> fileMap;
    /* Store the uid once we won't make any change to the commit object. */
    private String UID;


    /* Constructor for one commit. */
    public Commit(String message, String parentUID, String secondParentUID, Map<String, String> fileMap) {
        this.message = message;
        this.timestamp = getCurrentTimestamp();
        this.parentUID = parentUID;
        this.secondParentUID = secondParentUID;
        this.fileMap = fileMap;
    }


    /* Add the files to the fileMap.
    Whenever we make change to a commit, we must save it. */
    public void add(Map<String, String> addedFiles) {
        fileMap.putAll(addedFiles);
    }

    /* Remove the files in the fileMap.
    Whenever we make change to a commit, we must save it. */
    public void remove(Map<String, String> removedFiles) {
        for (String fileName : removedFiles.keySet()) {
            fileMap.remove(fileName);
        }
    }

    /* Given the fileName, find the version in the current commit and return its content.
     *  The return type should be byte[] which can represent the real content. */
    public byte[] getFileContent(String fileName) {
        File file = join(OBJECTS_DIR, fileMap.get(fileName));
        Blob blob = readObject(file, Blob.class);
        return blob.getContent();
    }


    /* Return the commit's parent commit. If not exists, return null. */
    public Commit parentCommit() {
        if (this.parentUID == null) {
            return null;
        }
        File parentCommitFile = join(OBJECTS_DIR, this.parentUID);
        return readObject(parentCommitFile, Commit.class);
    }


    /* Write the commit object to the specified file:
    Serialize commit itself and store in the .gitlet/objects */
    public void save() {
        /* The sequence of iteration for HashMap is truly random.
         *  So, we should calculate the UID just once and immediately store it. */
        UID = Utils.sha1((Object) Utils.serialize(this));
        File commitFile = join(OBJECTS_DIR, this.getUID());
        writeObject(commitFile, this);
    }

    /* Return the UID(SHA-1 code) of the commit. */
    public String getUID() {
        return UID;
    }


    /* Return the file's UID if it exists in the commit's fileMap. Otherwise, return null. */
    public String getFileUID(String fileName){
        return this.fileMap.get(fileName);
    }



    /* Return true if the commit contains the blob. */
    public boolean track(Blob blob) {
        return fileMap.containsValue(blob.getUID());
    }

    /* Return true if the commit contains the file. */
    public boolean track(String fileName) {
        return fileMap.containsKey(fileName);
    }

    /* A helper method to get the current timestamp. */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return sdf.format(new Date());
    }


    /* Given the commitUID prefix, return the commit object if it exists, otherwise return null.
     * Error case: if the commitUID prefix can match more than one commitUID.*/
    public static Commit getCommit(String prefix) {
        File[] files = OBJECTS_DIR.listFiles();
        Commit commit = null;

        if (files == null || prefix == null) {
            return null;
        }
        for (File file : files) {
            if (file.getName().startsWith(prefix)) {
                if (commit != null) {
                    System.out.println("Ambiguous commit ID prefix.");
                    System.exit(0);
                }
                commit = readObject(file, Commit.class);
            }
        }
        return commit;
    }

    /* Return the Collection of its parents commit. */
    public Iterable<Commit> getParents(){
        List<Commit> parents = new ArrayList<>();
        if(parentUID != null){
            parents.add(Commit.getCommit(parentUID));
        }
        if(secondParentUID != null){
            parents.add(Commit.getCommit(secondParentUID));
        }
        return parents;
    }


    /* Given the fileName, return true if the file content in CWD is different from that in this commit.
    *  If the given file simply doesn't exist in the commit, handle the error and exit. */
    public boolean isFileModified(String fileName){
        return !isFileUnchanged(fileName);
    }

    /* Given the fileName, return true if the file content in CWD is the same as that in this commit.
     *  If the given file simply doesn't exist in the commit, handle the error and exit. */
    public boolean isFileUnchanged(String fileName){
        if(!this.track(fileName)){
            handleErrorAndExit("The file name you pass to isFileModified is invalid.");
        }
        // Compare the UID of commitFile and CWDFile to determine whether is modified
        String fileUID = this.getFileUID(fileName);
        File CWDFile = join(CWD, fileName);
        Blob blob = new Blob(join(CWD, fileName));
        return fileUID.equals(blob.getUID());
    }












    /* Return true if the two commit has the same UID. */
    @Override
    public boolean equals(Object other){
        if(this == other) return true;
        if(other == null || this.getClass() != other.getClass()) return false;
        return this.getUID().equals(((Commit)other).getUID());
    }



    /* A helper method to print out the needed information
    to check if the object is what we expected. */
    @Override
    public void dump() {
        System.out.println("Message: " + message);
        System.out.println("Time: " + timestamp);
        System.out.println("ParentUID：" + parentUID);
        for (String fileName : fileMap.keySet()) {
            System.out.println(fileName + " -> " + fileMap.get(fileName));
        }
    }

    /* A helper method to handleError. */
    private static void handleErrorAndExit(String errorMessage) {
        System.out.println(errorMessage);
        System.exit(0);
    }

}
