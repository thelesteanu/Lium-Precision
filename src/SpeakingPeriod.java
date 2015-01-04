/**
 * Created with IntelliJ IDEA.
 * User: thelesteanu
 * Date: 3/27/14
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpeakingPeriod {
    public float start;
    public float duration;
    public String speakerId;
    public boolean correct;

    public SpeakingPeriod(float startValue, float durationValue, String speakerIdValue) {
        start = startValue;
        duration = durationValue;
        speakerId = speakerIdValue;
        correct= false;
    }

    public void setStart(float newValue) {
        start = newValue;
    }

    public void setDuration(float newValue) {
        duration = newValue;
    }

    public void setSpeakerId(String speakerValue) {
        speakerId = speakerValue;
    }

    public float getStart() {
        return start;
    }

    public float getDuration() {
        return duration;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void isCorrect(boolean status){
        correct = status;
    }

    public boolean getStatus(){
        return correct;
    }

}
