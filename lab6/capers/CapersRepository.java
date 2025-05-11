package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/**
 * A repository for Capers
 * The structure of a Capers Repository is as follows:
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 * - dogs/ -- folder containing all the persistent data for dogs
 * - story -- file containing the current story
 */
public class CapersRepository {
    /**
     * Current Working Directory.
     */
    static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * Main metadata folder.
     */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers");

    /* Creates any necessary folders or files. */
    public static void setupPersistence() {
        CAPERS_FOLDER.mkdir();
        Dog.DOG_FOLDER.mkdir();
        File storyFile = Utils.join(CAPERS_FOLDER, "story");
        try {
            storyFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Fail to create story file.");
        }
    }

    /* Appends the first non-command argument in args to a file called `story` in the .capers directory. */
    public static void writeStory(String text) {
        File storyFile = Utils.join(CAPERS_FOLDER, "story");
        String oldStory = readContentsAsString(storyFile);
        if(oldStory.isEmpty()){
            writeContents(storyFile,text);
            System.out.print(text);
            return;
        }
        String newStory = oldStory + "\n" + text;
        writeContents(storyFile, newStory);
        System.out.print(newStory);
    }

    /* Creates and persistently saves a dog, then prints out the dog's information using toString().*/
    public static void makeDog(String name, String breed, int age) {
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog.toString());
    }

    /* Advances a dog's age persistently and prints out a celebratory message. Also prints out the dog's information using toString(). */
    public static void celebrateBirthday(String name) {
        Dog birthdayDog = Dog.fromFile(name);
        birthdayDog.haveBirthday();
        birthdayDog.saveDog();
    }
}
