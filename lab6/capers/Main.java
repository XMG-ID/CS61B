package capers;

import java.io.File;
import java.util.Arrays;

import static capers.Utils.*;

/** Canine Capers: A Gitlet Prelude.
 * @author TODO
*/
public class Main {

    /* Only support three cmd : story, dog, birthday. */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithError("Must have at least one argument");
        }

        CapersRepository.setupPersistence();
        String text;
        switch (args[0]) {
        case "story":
            /* This call has been handled for you. The rest will be similar. */
            validateNumArgs("story", args, 2);
            text = args[1];
            CapersRepository.writeStory(text);
            break;
        case "dog":
            validateNumArgs("dog", args, 4);
            String name = args[1], breed = args[2];
            int age = Integer.parseInt(args[3]);
            CapersRepository.makeDog(name, breed, age);
            break;
        case "birthday":
            validateNumArgs("birthday", args, 2);
            String dogName = args[1];
            CapersRepository.celebrateBirthday(dogName);
            break;
        default:
            exitWithError(String.format("Unknown command: %s", args[0]));
        }
        return;
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                String.format("Invalid number of arguments for: %s.", cmd));
        }
    }
}
