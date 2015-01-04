/**
 * Created with IntelliJ IDEA.
 * User: thelesteanu
 * Date: 3/27/14
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LiumSpeakerInstance {
    public float occurencies;
    public String speakerName;

    public LiumSpeakerInstance(String speakerNameValue) {
        occurencies = 1;
        speakerName = speakerNameValue;
    }

    public void incrementOccurencies() {
        occurencies = occurencies + 1;
    }

    public float getLiumSpeakerOccurencies() {
        return occurencies;
    }

    public String getLiumSpeakerInstanceName() {
        return speakerName;
    }

    public String getLiumSpeakeInstanceComplete() {
        return speakerName + "(" + occurencies + " Segmente)";
    }

}
