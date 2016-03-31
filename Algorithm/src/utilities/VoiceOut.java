package utilities;

import sun.audio.*;
import java.io.*;

/**
 * Created by Jiaxiang on 15/3/16.
 */
public class VoiceOut {


    public static void voiceOut(String fileName){

        try {
            // open the sound file as a Java input stream
            InputStream in = new FileInputStream(fileName);

            // create an audiostream from the inputstream
            AudioStream audioStream = new AudioStream(in);

            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        System.out.println("Starting...");
        voiceOut("WLExplorationDone.wav");
        System.out.println("Ending...");
    }

}


