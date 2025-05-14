package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;

/* Represent the staging area for gitlet. */
public class StagingArea implements Serializable, Dumpable {
    /* File name -> file blob UID. */
    private Map<String, String> addedFiles;
    private Map<String, String> removedFiles;

    /* Initialize an empty staging area. */
    public StagingArea() {
        addedFiles = new HashMap<>();
        removedFiles = new HashMap<>();
    }

    /* Stage file for addition. */
    public void stageFile(String fileName, String blobUID) {
        addedFiles.put(fileName, blobUID);
    }

    /* Remove file from staging area means add it to the stagingArea.removedFiles.
    * So when the next commit is made, it will first inherit all the fileMap from its parent commit,
    * Then we'll use stagingArea.removedFiles to remove some files. */
    public void removeFile(String fileName) {
        // It doesn't matter, because we just need the fileName
        removedFiles.put(fileName, null);
    }


    /* Unstage the file in the addedFiles, and return the blob UID string. */
    public String unStageFile(String fileName) {
        return addedFiles.remove(fileName);
    }

    /* Return true if the staging area has added the file called fileName. */
    public boolean hasAddedFile(String fileName) {
        return addedFiles.containsKey(fileName);
    }


    /* Serialize the stagingArea object and store it in the right place. */
    public void save() {
        File stagingAreaFile = join(OBJECTS_DIR, "stagingArea");
        writeObject(stagingAreaFile, this);
    }

    /* Return the UID of the stagingArea object. */
    public String getUID() {
        return sha1((Object) serialize(this));
    }

    /* Make the staging area empty. */
    public void clear() {
        addedFiles.clear();
        removedFiles.clear();
        this.save();
    }


    /* Return true if the staging area is empty. */
    public boolean isEmpty() {
        return addedFiles.isEmpty() && removedFiles.isEmpty();
    }

    /* Return the addedFiles. */
    public Map<String, String> getAddedFiles() {
        return addedFiles;
    }

    /* Return the removedFiles. */
    public Map<String, String> getRemovedFiles() {
        return removedFiles;
    }

    /* A helper method to print out the needed information
    to check if the object is what we expected. */
    @Override
    public void dump() {
        System.out.println("Added files: ");
        for (String fileName : addedFiles.keySet()) {
            System.out.println(fileName + " -> " + addedFiles.get(fileName));
        }
        System.out.println("Removed files: ");
        for (String fileName : removedFiles.keySet()) {
            System.out.println(fileName + " -> " + removedFiles.get(fileName));
        }
    }

}
