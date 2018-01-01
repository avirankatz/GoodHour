package avirankatz.goodhour.helpers;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import avirankatz.goodhour.goodhourprefs.GoodHour;

public class GlobalMethods {
    private static final String ALARMS_FILENAME = "alarms";

    public static boolean saveGoodHoursToFile(Context context, List<GoodHour> goodHours) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutput objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(goodHours);
            objectOutput.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            FileOutputStream fileOutputStream = context.openFileOutput(ALARMS_FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write(bytes);
            return true;
        } catch (java.io.IOException e) {
            Log.e(GlobalMethods.class.getName(), "Failed to save good hours to file.", e);
            return false;
        }
    }

    public static ArrayList<GoodHour> loadGoodHoursFromFile(Context context) {
        try {
            File file = new File(context.getFilesDir(), ALARMS_FILENAME);
            if (!file.exists()) return new ArrayList<>();
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fileInputStream = context.openFileInput(ALARMS_FILENAME);
            fileInputStream.read(bytes);
            ObjectInput objectInput = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (ArrayList<GoodHour>) objectInput.readObject();
        } catch (java.io.IOException | ClassNotFoundException | ClassCastException e) {
            Log.e(GlobalMethods.class.getName(), "Failed to read good hours from file.", e);
            return new ArrayList<>();
        }
    }
}
