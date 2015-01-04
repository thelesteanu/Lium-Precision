import com.sun.org.apache.xpath.internal.SourceTree;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.sound.sampled.*;

class AudioFileProcessor {
    public static String location = "/home/thelesteanu/Desktop/resurse PS/Lium Precision/";
    public static String liumFile = location + "resurse/jurnal30oct.seg";
    public static String labelFile = location + "resurse/30oct_ora19.txt";

    public static void main(String[] args) {
        int bumper = 0;
        System.out.println("Please insert bumper timing in miliseconds: ");
        System.out.println("**Note: For Lium the basic bumper is ~ 250 ms");
        Scanner s = new Scanner(System.in);
        bumper = s.nextInt();
        s.close();


        ArrayList<SpeakingMap> speakingMaps = makeSpeakingMapArray(liumFile);

        ArrayList<SpeakingMap> speakingLabels = makeSpeakingLabesArray(labelFile);

        detectPrecision(speakingMaps, speakingLabels, bumper);
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static ArrayList<SpeakingMap> makeSpeakingMapArray(String destinationFileName) {
        ArrayList<SpeakingMap> speakingMapArrayList = new ArrayList<SpeakingMap>();
        InputStream fis;
        BufferedReader br;
        String line;
        String cluster = "cluster";
        ArrayList<String> speakerArray = new ArrayList<String>();
        String speakerId = "";

        try {
            fis = new FileInputStream(destinationFileName);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            ArrayList<SpeakingPeriod> speakingPeriods = new ArrayList<SpeakingPeriod>();
            try {
                while ((line = br.readLine()) != null) {

                    if (line.contains("cluster")) {
                        if (speakerId != "") {
                            speakingMapArrayList.add(new SpeakingMap(speakerId, speakingPeriods));
                        }
                        String parser[] = line.split(" ");
                        speakerId = parser[2];
                        speakerArray.add(speakerId);
                        speakingPeriods = new ArrayList<SpeakingPeriod>();
                    } else {
                        String words[] = line.split(" ");
                        if (words[2].length() > 2 && words[3].length() > 2) {

                            speakingPeriods.add(new SpeakingPeriod(Float.parseFloat(words[2].substring(0, words[2].length() - 2)) + Float.parseFloat("0." + words[2].substring(words[2].length() - 2, words[2].length())), Float.parseFloat(words[3].substring(0, words[3].length() - 2)) + Float.parseFloat("0." + words[3].substring(words[3].length() - 2, words[3].length())), speakerId));
                        } else if (words[2].length() < 3) {
                            speakingPeriods.add(new SpeakingPeriod(Float.parseFloat("0." + words[2]), Float.parseFloat(words[3].substring(0, words[3].length() - 2)) + Float.parseFloat("0." + words[3].substring(words[3].length() - 2, words[3].length())), speakerId));
                        }

                    }
                }
                speakingMapArrayList.add(new SpeakingMap(speakerId, speakingPeriods));
                br.close();
                br = null;
                fis = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException fnf) {
            println(fnf);
        }

        return speakingMapArrayList;
    }

    public static ArrayList<SpeakingMap> makeSpeakingLabesArray(String destinationFileName) {
        ArrayList<SpeakingMap> speakingMapArrayList = new ArrayList<SpeakingMap>();
        InputStream fis;
        BufferedReader br;
        String line;
        ArrayList<String> speakerArray = new ArrayList<String>();
        String speakerId = "";

        try {
            fis = new FileInputStream(destinationFileName);
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            ArrayList<SpeakingPeriod> speakingPeriods = new ArrayList<SpeakingPeriod>();
            try {
                while ((line = br.readLine()) != null) {

                    if (line.contains("vorbitor") || line.contains("V") || line.contains("Vorbitor")) {
                        if (speakerId != "") {
                            speakingMapArrayList.add(new SpeakingMap(speakerId, speakingPeriods));
                        }
                        String parser[] = line.split("\t");
                        speakerId = parser[2];
                        if (!speakerArray.contains(speakerId)) {
                            speakerArray.add(speakerId);
                        }
                        if (parser[0].length() > 0) {
                            String startPeriod = "";
                            String endPeriod = "";
                            if (parser[0].contains(",")) {
                                startPeriod = parser[0].replace(",", ".");
                            } else {
                                startPeriod = parser[0];
                            }
                            if (parser[1].contains(",")) {
                                endPeriod = parser[1].replace(",", ".");
                            } else {
                                endPeriod = parser[1];
                            }
                            float duration = Float.parseFloat(endPeriod) - Float.parseFloat(startPeriod);
                            String durationTime = String.format("%.2f", duration);
                            speakingPeriods.add(new SpeakingPeriod(Float.parseFloat(startPeriod), Float.parseFloat(durationTime), speakerId));
                        }
                    }
                }
                speakingMapArrayList.add(new SpeakingMap(speakerId, speakingPeriods));
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException fnf) {
            println(fnf);
        }

        return speakingMapArrayList;
    }

    public static void detectPrecision(ArrayList<SpeakingMap> LiumArray, ArrayList<SpeakingMap> LabelsArray, int bumper) {
        float ok = 0;
        float segmentNumber = 0;
        double correctDuration = 0;
        double liumDuration = 0;
        Map speakerResultMap = new HashMap();
        float newBumper = (float) bumper / 1000;
        for (SpeakingMap LiumSpeaker : LiumArray) {
            for (SpeakingPeriod LiumPeriod : LiumSpeaker.getSpeakingPeriodsArray()) {
                liumDuration = liumDuration + LiumPeriod.getDuration();
                segmentNumber++;
                for (SpeakingPeriod LabelSpeaker : LabelsArray.get(0).getSpeakingPeriodsArray()) {

                    float LiumEnd = LiumPeriod.getStart() + LiumPeriod.getDuration();
                    float LabelEnd = LabelSpeaker.getStart() + LabelSpeaker.getDuration();

                    if (LiumPeriod.getStart() > LabelSpeaker.getStart() - newBumper && LiumEnd < LabelEnd + newBumper) {
                        ok++;
                        LiumPeriod.isCorrect(true);
                        correctDuration = correctDuration + LiumPeriod.getDuration();
                        String[] speakerInfo = LabelSpeaker.getSpeakerId().split("_");
                        String speakerName = speakerInfo[0] + speakerInfo[1];
                        if (speakerResultMap.containsKey(speakerName)) {
                            List<LiumSpeakerInstance> LiumInstances = (ArrayList) speakerResultMap.get(speakerName);
                            boolean isInList = false;
                            for (int i = 0; i < LiumInstances.size(); i++) {
                                LiumSpeakerInstance speakerInstance = LiumInstances.get(i);
                                if (LiumPeriod.getSpeakerId() == speakerInstance.getLiumSpeakerInstanceName()) {
                                    speakerInstance.incrementOccurencies();
                                    isInList = true;
                                }
                            }
                            if (!isInList) {
                                LiumInstances.add(new LiumSpeakerInstance(LiumPeriod.getSpeakerId()));
                            }
                            speakerResultMap.put(speakerName, LiumInstances);

                        } else {
                            List<LiumSpeakerInstance> LiumInstances = new ArrayList<LiumSpeakerInstance>();
                            LiumInstances.add(new LiumSpeakerInstance(LiumPeriod.getSpeakerId()));
                            speakerResultMap.put(speakerName, LiumInstances);
                        }
                    } else if (LiumPeriod.getStart() > LabelSpeaker.getStart() - 500 && LiumEnd < LabelEnd + 500) {

                    }

                    if (ok == segmentNumber) break;
                }
            }
        }

        Iterator it = speakerResultMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            List theList = (ArrayList) pairs.getValue();
            System.out.println("\n" + pairs.getKey());
            System.out.println("______________");
            for (int i = 0; i < theList.size(); i++) {
                LiumSpeakerInstance speakerInstance = (LiumSpeakerInstance) theList.get(i);
                System.out.println(speakerInstance.getLiumSpeakeInstanceComplete());
            }
            System.out.println("______________\n");
            it.remove(); // avoids a ConcurrentModificationException
        }
        boolean errors = false;
        for (SpeakingMap LiumSpeaker : LiumArray) {
            for (SpeakingPeriod LiumPeriod : LiumSpeaker.getSpeakingPeriodsArray()) {
                if (!LiumPeriod.getStatus()) {
                    if (!errors) {
                        errors = true;
                        System.out.println("Urmatoarele segmente sunt " +
                                "prezente in etichetarea a 2 vorbitori conform Lium:\n__________");
                    }

                    System.out.println("Eroare la:  " + LiumPeriod.getSpeakerId() +
                            ". Segmentul incepe la secunda: " + LiumPeriod.getStart() + ". Segmentul dureaza: " +
                            LiumPeriod.getDuration() + " secunde si este continut de etichetarile a 2 vorbitori.");
                }
            }
        }


        System.out.println("\n");
        System.out.println("Nume fisier = " + liumFile);
        System.out.println("Fisier etichete = " + labelFile);
        System.out.println("");
        System.out.println("_________________________");
        System.out.println("Segmente lium atribuite unui singur vorbitor= " + ok);
        System.out.println("Numar total de segmente= " + segmentNumber);
        float precision = (ok / segmentNumber) * 100;
        System.out.println("Raportul segmentelor atribuite unui singur vorbitor= " + String.format("%.2f", precision) + " %");
        System.out.println("______________");
        System.out.println("Durata totala a segmentelor = " + String.format("%.2f", liumDuration) + " secunde");
    }

}