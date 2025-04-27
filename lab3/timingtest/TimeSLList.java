package timingtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
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

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> ops = new AList<>();
        //Build a list of n items (we only test Integer as items), then start timing
        //Get m items to the last, then record elapsed time
        int n = 1000, m = 10000;
        while (n <= 128000) {
            SLList<Integer> lst = buildList(n);
            Stopwatch sw = new Stopwatch();
            getItems(m, lst);
            //Store the information in the table
            times.addLast(sw.elapsedTime());
            Ns.addLast(n);
            ops.addLast(m);
            n *= 2;
        }
        printTimingTable(Ns,times,ops);
    }

    /*Get m items to the last*/
    public static void getItems(int m, SLList<Integer> lst) {
        while (m > 0) {
            lst.getLast();
            m--;
        }
    }

    /*Build a list of n items, and return the list*/
    public static SLList<Integer> buildList(int n) {
        SLList<Integer> lst = new SLList<>();
        while (n > 0) {
            lst.addFirst(0);//Add anything is ok
            n--;
        }
        return lst;
    }

}
