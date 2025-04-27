package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */

public class TestBuggyAList {
    @Test
    public void test1() {
        int expected = 1;
        int input = 1;

        assertEquals(1, 1);
    }

    @Test
    /*Test addLast and removeLast method in BuggyAList by
    comparing the result with the AListNoResizing*/
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> correctLst = new AListNoResizing<>();
        BuggyAList<Integer> buggyLst = new BuggyAList<>();
        //Add three items to both class
        int cnt = 3;
        while (cnt > 0) {
            correctLst.addLast(cnt);
            buggyLst.addLast(cnt);
            cnt--;
        }
        //Remove three items and assertEquals
        cnt = 3;
        while (cnt > 0) {
            assertEquals(correctLst.removeLast(), buggyLst.removeLast());
            cnt--;
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(L.size(),B.size());
            } else if (operationNumber == 1) {
                // getLast
                if(L.size()==0){
                    continue;
                }
                assertEquals(L.getLast(),B.getLast());
            } else if (operationNumber == 2){
                //removeLast
                if(L.size()==0){
                    continue;
                }
                assertEquals(L.removeLast(),B.removeLast());
            }
        }
    }
}
