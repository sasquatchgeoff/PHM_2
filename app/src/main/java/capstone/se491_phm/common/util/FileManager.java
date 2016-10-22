package capstone.se491_phm.common.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Acer on 10/21/2016.
 */

public class FileManager {
    public static void writeFile(Context context, String fileName, String content) throws FileNotFoundException, IOException {
        File path = null;
        File file = null;
        boolean saved = true;
        path = context.getFilesDir();
        file = new File(path, fileName);

        if (file != null) {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(content.toString().getBytes());
            } finally {
                stream.close();
            }
        }
    }

    public static String readFromStorage(Context context, String fileName) throws IOException {
        File path = context.getFilesDir();
        File file = new File(path, fileName);
        String contents = "";
        if(file.exists()) {
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(bytes);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            contents = new String(bytes);
        }
        return contents;
    }
}
