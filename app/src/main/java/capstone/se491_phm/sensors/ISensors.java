package capstone.se491_phm.sensors;

import android.content.Context;
import android.view.View;

/**
 * Created by Acer on 10/21/2016.
 */

public interface ISensors {
    boolean initialize(Context context);
    void saveData(Context context);
    void startMonitoring(View view);
    void stopMonitoring(View view);
}
