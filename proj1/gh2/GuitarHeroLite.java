package gh2;

import deque.ArrayDeque;
import deque.Deque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHeroLite {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void main(String[] args) {
        /*Create all guitar strings, store in the guitarStringDeque*/
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        Deque<GuitarString> guitarStringDeque = new ArrayDeque<>();
        for (int i = 0; i < keyboard.length(); i++) {
            double concert = CONCERT_A * Math.pow(2, (i - 24) / 12.0);
            guitarStringDeque.addLast(new GuitarString(concert));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index == -1) {
                    continue;
                }
                guitarStringDeque.get(index).pluck();
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for(int i=0;i<guitarStringDeque.size();i++){
                sample+=guitarStringDeque.get(i).sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for(int i=0;i<guitarStringDeque.size();i++){
                guitarStringDeque.get(i).tic();
            }
        }
    }
}

