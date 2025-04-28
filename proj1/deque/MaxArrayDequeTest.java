package deque;

import org.junit.Test;

import java.util.Comparator;

public class MaxArrayDequeTest {
    @Test
    public void testMaxWithGivenCmp(){
        Dog dog1 = new Dog(12,"mingtian");
        Dog dog2 = new Dog(99,"jiaozi");
        Dog dog3 = new Dog(34,"tangyaun");

        MaxArrayDeque<Dog> dSize = new MaxArrayDeque<>(OurComparators.getDogSizeCmp());
        dSize.addLast(dog1);
        dSize.addLast(dog2);
        dSize.addLast(dog3);

        System.out.println(dSize.max());
        System.out.println(dSize.max(OurComparators.getDogNameCmp()));

    }
}
