import android.content.Context;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {
    public static final String FILENAME = "penghutanginfo.dat";

    public static void WriteData(ArrayList<String> penghutang, Context context) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(FILENAME, 0));
            oos.writeObject(penghutang);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static ArrayList<String> readData(Context context) {
        try {
            return (ArrayList) new ObjectInputStream(context.openFileInput(FILENAME)).readObject();
        } catch (FileNotFoundException e) {
            ArrayList<String> penghutangList = new ArrayList<>();
            e.printStackTrace();
            return penghutangList;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } catch (ClassNotFoundException e3) {
            e3.printStackTrace();
            return null;
        }
    }
}
