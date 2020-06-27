package ons.tools;

/**
 *
 * @author lucas
 */
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
 
public class SaveObject {
 
    public static void save(Object object, String path) {
 
           try {
             FileOutputStream saveFile = new FileOutputStream(path);
             ObjectOutputStream stream = new ObjectOutputStream(saveFile);
 
              // salva o objeto
             stream.writeObject(object);
 
             stream.close();
           } catch (Exception exc) {
             exc.printStackTrace();
           }
    }
}
