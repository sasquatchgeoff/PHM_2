package capstone.se491_phm;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class MainActivity extends Activity {
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSensors();
    }

    private void initSensors(){
        Detector.initiate(getContextMain());
    }

    public Context getContextMain(){
        if(context == null){
            context = getBaseContext();
        }
        return context;
    }
}
