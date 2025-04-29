package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /*Test addFirst and addLast without resizing*/
    public void testNoResizeAdd() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 4; i++) {
            ad.addFirst(i);
            ad.addLast(i + 1);
        }
    }

    @Test
    /*Test add method which will invoke resize*/
    public void testResize() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 100; i++) {
            ad.addFirst(i);
            ad.addLast(i + 1);
        }
    }

    @Test
    /*Test removeFirst and removeLast*/
    public void remove() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 20; i++) {
            ad.addLast(i);
        }
        //[0 1 2 ... 99]
        for(int i=0;i<19;i++){
            ad.removeLast();
        }


    }

    @Test
    /*Test get method*/
    public void testGet() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 4; i++) {
            ad.addFirst(i);
            ad.addLast(i + 1);
        }
        //[0 / 1 2 3 last:4 first:3 2 1]
        assertEquals(3, (int) ad.get(0));
        assertEquals(2, (int) ad.get(1));
        assertEquals(1, (int) ad.get(2));
        assertEquals(0, (int) ad.get(3));
        assertEquals(1, (int) ad.get(4));
        assertEquals(2, (int) ad.get(5));
        assertEquals(3, (int) ad.get(6));
        assertEquals(4, (int) ad.get(7));
    }

    @Test
    /*Test printDeque method, it should not spoil the size, nextFirst and nextLast variable*/
    public void testPrintDeque() {
        //Create a deque 1,2,3,...,100
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i <= 100; i++) {
            ad.addLast(i);
        }
        ad.printDeque();
        //Test whether the other method goes well after calling printDeque
        assertEquals(50, (int) ad.get(50));
        assertEquals(0, (int) ad.removeFirst());
        assertEquals(100, (int) ad.removeLast());
        assertEquals(99, ad.size());
        assertEquals(51, (int) ad.get(50));
    }

    @Test
    public void testIterator(){
        ArrayDeque<String> ad = new ArrayDeque<>();
        for (int i = 0; i <= 10; i++) {
            ad.addLast(i+ "str");
        }
        for(String str:ad){
            System.out.println(str);
        }
    }

    @Test
    public void testEqualsForTwoDeque(){
        ArrayDeque<Integer> arrd = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            arrd.addLast(i);
        }
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        for (int i = 0; i < 10; i++) {
            lld.addLast(i);
        }

        assertEquals(arrd,lld);
    }

    /*Test errors in the ARRD found by autograder*/
    @Test
    public void test1() {
        ArrayDeque<Integer> lld = new ArrayDeque<>();
        lld.addFirst(0);
        assertEquals(0, (int)lld.removeLast());
        assertEquals(true,lld.isEmpty());
        lld.addFirst(3);
        assertEquals(3,(int)lld.removeLast());
    }

    @Test
    public void test2(){
        ArrayDeque<Integer> arrd = new ArrayDeque<>();
        arrd.addFirst(0);
        assertEquals(0,(int)arrd.removeFirst());
    }
}
