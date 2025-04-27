package timingtest;

import com.sun.java.accessibility.util.AccessibilityListenerList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    /*Generate the time running for AList to add cnt numbers to the last*/
    public static double generateTime(int cnt) {
        Stopwatch sw = new Stopwatch();
        AList<Integer> lst = new AList<>();
        //Start making the list
        while (cnt > 0) {
            lst.addLast(cnt);
            cnt--;
        }
        return sw.elapsedTime();
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> N = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        //Generate N, times, ops
        int cnt = 1000;
        while (cnt <= 12800000) {
            N.addLast(cnt);
            times.addLast(generateTime(cnt));
            ops.addLast(cnt);
            cnt *= 2;
        }
        printTimingTable(N,times,ops);
    }
}
