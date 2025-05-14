package gitlet;

/* Understandings of git command:
 *  Whenever we change the head, the CWD's file will be converted into the commit pointed by head.
 *  reset commit :  change the head of the current branch to the given commit
 * */

/* Driver class for Gitlet, a subset of the Git version-control system.*/
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            handleErrorAndExit("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.createRepository();
                break;
            case "add":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    handleErrorAndExit("Please enter a commit message.");
                }
                Repository.commit(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.remove(args[1]);
                break;
            case "log":
                Repository.printLog();
                break;
            case "global-log":
                Repository.printGlobalLog();
                break;
            case "find":
                if (args.length != 2) {
                    handleErrorAndExit("Please enter a commit message.");
                }
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (args.length == 3 && args[1].equals("--")) {// checkout -- [file name]
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {// checkout [commit id] -- [file name]
                    Repository.checkoutFile(args[3], args[1]);
                } else if (args.length == 2) {// checkout [branch name]
                    Repository.checkoutBranch(args[1]);
                } else {
                    handleErrorAndExit("Incorrect operands.");
                }
                break;
            case "branch":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.removeBranch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    handleErrorAndExit("Incorrect operands.");
                }
                Repository.merge(args[1]);
                break;
            default:
                handleErrorAndExit("No command with that name exists.");
                break;
        }
    }

    /* A helper method to handleError. */
    private static void handleErrorAndExit(String errorMessage) {
        System.out.println(errorMessage);
        System.exit(0);
    }
}
