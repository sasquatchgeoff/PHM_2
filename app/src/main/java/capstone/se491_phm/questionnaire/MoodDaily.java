package capstone.se491_phm.questionnaire;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 10/9/2016.
 */

public class MoodDaily extends Activity implements IQuestionnare {
    private SeekBar mSeekBarMood;
    private TextView mSelectedDailyMoodTextView;
    private List<String> availableMoods = new ArrayList<String>();
    //if daily mood average is below 1.5 or greater than 2.5 need to fire mood survey
    public static double dailyMoodAverage = 0;
    public static int numberOfEntries = 0;
    private int mSelectedMood = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.q_mood_daily);
        prepareQuestion();
        mSeekBarMood = (SeekBar)findViewById(R.id.seekBar_mood_selection);
        mSeekBarMood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedDailyMoodTextView.setText(availableMoods.get(progress));
                mSelectedMood = progress;
            }
        });
    }

    @Override
    public void prepareQuestion() {
        availableMoods.addAll(Constants.getDailyMood());
        mSelectedDailyMoodTextView = (TextView)findViewById(R.id.selectDailyMood);
        mSelectedDailyMoodTextView.setText(availableMoods.get(Constants.getDailyMoodDefault()));
    }

    @Override
    public String getQuestionHistory(int limit) {
        return "";
    }

    @Override
    public boolean cleanUpStorage() {
        return false;
    }

    public void moodDailyHome(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveMoodDaily(View view) {
        numberOfEntries += 1;
        dailyMoodAverage = (dailyMoodAverage + (double) mSelectedMood)/(double) numberOfEntries;

        moodDailyHome(view);
    }
}
