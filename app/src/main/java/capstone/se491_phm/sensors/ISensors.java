package capstone.se491_phm.sensors;

import android.content.Context;

/**
 * Created by Acer on 10/21/2016.
 */

public interface ISensors {
    boolean initialize(Context context);
    void saveData(Context context);
}
