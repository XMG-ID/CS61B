package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.OBJECTS_DIR;
import static gitlet.Utils.*;

/* A binary large object of a file content. */
public class Blob implements Serializable, Dumpable {
    private String fileName;
    private byte[] content;
    private String blobUID;

    /* Take in a file to create the file blob. */
    public Blob(File file)  {
        fileName = file.getName();
        content = readContents(file);
        blobUID = sha1((Object) serialize(this));
    }

    /* Return the content of the blob. */
    public byte[] getContent(){
        return content;
    }

    /* Return the UID of the blob. */
    public String getUID() {
        return blobUID;
    }

    /* Store the blob in the right place. */
    public void save(){
        // Serialize blob itself and store in the .gitlet/object.
        File blobFile = join(OBJECTS_DIR, this.getUID());
        writeObject(blobFile, this);
    }

    /* A helper method to print out the needed information
    to check if the object is what we expected. */
    @Override
    public void dump() {
        System.out.println("File name: "+ fileName);
        System.out.print("File content: ");
        for(byte b:content){
            System.out.print(b);
        }
    }
}
