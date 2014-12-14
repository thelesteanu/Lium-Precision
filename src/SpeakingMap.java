import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: thelesteanu
 * Date: 3/27/14
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpeakingMap {
    public String speakerId;
    public ArrayList<SpeakingPeriod> speakingPeriods;

    public SpeakingMap(String speakerIdValue,ArrayList<SpeakingPeriod> speakingPeriodsArray) {
        speakerId = speakerIdValue;
        speakingPeriods = speakingPeriodsArray;
    }

    public void setSpeakingPeriods(ArrayList<SpeakingPeriod> newValue) {
        speakingPeriods = newValue;
    }

    public void setSpeakerId(String speakerValue) {
        speakerId = speakerValue;
    }

    public ArrayList<SpeakingPeriod> getSpeakingPeriodsArray() {
        return speakingPeriods;
    }

    public String getSpeakerId() {
        return speakerId;
    }

}
