package capstone.se491_phm;

/**
 * Created by Acer on 11/13/2016.
 */
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;
import capstone.se491_phm.common.util.FileManager;
import capstone.se491_phm.sensors.StepCounter;

//@RunWith(AndroidJUnit4.class)
public class FileManagementTest {

    private Context context;
    private Context targetContext;
    private final String FILE_NAME = "unit_test.txt";

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();
    }

    /**
     * Save file, exception is raised if any issue while saving
     * @throws Exception
     */
    @Test
    public void saveFileTest() throws Exception {
        deleteFile();
        FileManager.writeFile(targetContext,FILE_NAME,"unit test");
    }

    /**
     * Read file from storage. Existing file.
     * @throws Exception
     */
    @Test
    public void readFileExistingTest() throws Exception {
        saveFileTest();
        String jsonString = FileManager.readFromStorage(targetContext,FILE_NAME);
        assertEquals("unit test", jsonString);
    }

    /**
     * Read file from storage. No Existing file.
     * @throws Exception
     */
    @Test
    public void readFileNonExistingTest() throws Exception {
        deleteFile();

        String jsonString = FileManager.readFromStorage(targetContext,FILE_NAME);
        assertEquals("", jsonString);
    }

    /**
     * Save and read step counter test group data using save flow of step counter
     * !IMPORTANT! This will erase step counter history
     * @throws Exception
     */
    @Test
    public void saveStepCounterGroupDataTest() throws Exception {
        String groupName = "unit_test";
        long groupValue = 123L;
        deleteFile("activityData.txt");
        StepCounter sensor = new StepCounter();
        sensor.updateActivityGroup(groupName,groupValue);
        sensor.saveData(targetContext);

        String jsonString = FileManager.readFromStorage(targetContext,"activityData.txt");
        assertTrue("Test activity group is empty", !"".equals(jsonString));

        JSONObject jsonObject = null;
        try {
            if(!"".equals(jsonString)) {
                jsonObject = new JSONObject(jsonString);
            }
        } catch (JSONException e) {
            Log.e("FileManagementTest","unable to create json object of activity data");
        }

        if (jsonObject != null){
            assertTrue("Json activity object is null", jsonObject.get(groupName) != null);
            assertTrue("Json activity object is not correct", Long.parseLong(jsonObject.get(groupName).toString()) == groupValue);
        }

        deleteFile("activityData.txt");
    }

    private void deleteFile(){
        //delete if file exist
        File path = targetContext.getFilesDir();
        File file = new File(path, FILE_NAME);
        if(file.exists()) {
            file.delete();
        }
    }
    private void deleteFile(String fileName){
        //delete if file exist
        File path = targetContext.getFilesDir();
        File file = new File(path, fileName);
        if(file.exists()) {
            file.delete();
        }
    }
}
