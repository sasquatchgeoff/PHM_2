package capstone.se491_phm.questionnaire;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IQuestionnare {
    /**
     * Prepares the questions in activity for display
     */
    public void prepareQuestion();

    /**
     * Gets the history saved on the system and returns a json object with the specified
     * history limit
     * @param limit
     * @return
     */
    public String getQuestionHistory(int limit);

    /**
     * Delete any saved data
     */
    public boolean cleanUpStorage();
}
