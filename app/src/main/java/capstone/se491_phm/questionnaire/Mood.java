package capstone.se491_phm.questionnaire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;

/**
 * Created by Acer on 10/9/2016.
 */

public class Mood extends Activity implements IQuestionnare {

    private Context context;
    private static final String mFileName = "moodSurveyJson.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        //TODO for testing only delete this line
        cleanUpStorage();

        prepareQuestion();
    }

    @Override
    public void prepareQuestion() {
        setContentView(R.layout.q_mood);

        //set previous answers
        //RadioButton radioButton = (RadioButton) findViewById(new Integer(2131492981));
        //radioButton.setChecked(true);
    }

    @Override
    public String getQuestionHistory(int limit) {
        String history = "";
        try {
            JSONArray jsonArray = readMoodSurveyFromStorageJsonArray(mFileName);

            if(jsonArray.length() > 0){
                if(jsonArray.length() < limit){
                    history = jsonArray.toString();
                } else {
                    JSONArray newList = new JSONArray();
                    for(int i = 0; i < limit; i++){
                        newList.put(jsonArray.get(i));
                    }
                    history = newList.toString();
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return history;
    }

    /**
     * Save the survey form as a json string to internal storage
     * @param view
     */
    public void saveMoodSurvey(View view) {
        //get form answers
        JSONObject survey = new JSONObject();
        try {
            LinearLayout layout = (LinearLayout)findViewById(R.id.moodSurvey);
            RadioGroup radioGroup = null;
            for (int i = 0; i < layout.getChildCount(); i++) {
                View element = layout.getChildAt(i);
                if (element instanceof RadioGroup) {
                    radioGroup = (RadioGroup) element;
                    survey.put(getResources().getResourceEntryName(radioGroup.getId()),
                            ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText());
                }
            }
            survey.put("created_timestamp",System.currentTimeMillis());
        }catch (JSONException e){
            //TODO handle exception
        }

        boolean saved = true;
        JSONArray surveyHistoryJsonArray = null;
        //read survey history
        try {
            surveyHistoryJsonArray = readMoodSurveyFromStorageJsonArray(mFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add new results to history array
        surveyHistoryJsonArray.put(survey);

        //save to file
        saved = writeFile(mFileName, surveyHistoryJsonArray.toString());

        //Display success or error message
        TextView textView = (TextView) findViewById(R.id.saveSurveyMessage);
        if(saved){
            textView.setText("Success");
            textView.setTextColor(Color.GREEN);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //null is fine because its being ignored
                    goHomeFromMoodSurvey(null);
                }
            }, 500);
        } else {
            textView.setText("Error while saving");
            textView.setTextColor(Color.RED);
        }
        textView.setVisibility(View.VISIBLE);
    }

    private boolean writeFile(String fileName, String content){
        File path = null;
        File file = null;
        boolean saved = true;
        try {
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
        } catch (Exception e) {
            saved = false;
        }
        return saved;
    }

    public String readMoodSurveyFromStorage(String fileName) throws IOException {
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

    public JSONArray readMoodSurveyFromStorageJsonArray(String fileName) throws IOException {
        JSONArray surveyHistoryJsonArray = null;
        String surveyHistory = readMoodSurveyFromStorage(fileName);
        if(!"".equals(surveyHistory)){
            try {
                surveyHistoryJsonArray = new JSONArray(surveyHistory);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(surveyHistoryJsonArray == null){
            surveyHistoryJsonArray = new JSONArray();
        }
        return surveyHistoryJsonArray;
    }

    /**
     * Takes user back to main activity.
     * NOTE: if editing, please handle view being null (check references for more info)
     * @param view
     */
    public void goHomeFromMoodSurvey(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean cleanUpStorage(){
        File path = context.getFilesDir();
        File file = new File(path, mFileName);
        boolean deleted = false;
        if(file.exists()) {
            deleted = file.delete();
        }
        return deleted;
    }
}

