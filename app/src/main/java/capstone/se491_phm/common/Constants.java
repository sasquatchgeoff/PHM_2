package capstone.se491_phm.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Acer on 10/21/2016.
 */

public class Constants {
    public static enum ActivityGroup{
        DAILY,
        WEEKLY,
        TOTAL;
    }

    private static List<String> dailyMoods = new ArrayList<>();
    public static List<String> getDailyMood() {
        if (dailyMoods.isEmpty()) {
            dailyMoods.add("Depressed");//0
            dailyMoods.add("Irritated");//1
            dailyMoods.add("Normal");//2
            dailyMoods.add("Hyper");//3
            dailyMoods.add("Mania");//4
        }
        return new ArrayList<>(dailyMoods);
    }

    /**
     * Returs the default value to display for daily mood slider
     * @return
     */
    public static int getDailyMoodDefault() {
        return 2;
    }
}