package ons.tools;

/**
 *
 * @author lucas
 */
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class LoadObject {

    public static Object load(String path) {

        Object object = null;

        try {
            FileInputStream restFile = new FileInputStream(path);
            ObjectInputStream stream = new ObjectInputStream(restFile);

            // recupera o objeto
            object = stream.readObject();

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }
}