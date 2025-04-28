package deque;

import java.util.Comparator;

public class OurComparators {

    /*DogSizeComparator is to compare the size of dogs*/
    private static class DogSizeComparator implements Comparator<Dog> {
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.size() - o2.size();
        }
    }

    /*DogNameComparator is to compare the name of dogs*/
    private static class DogNameComparator implements Comparator<Dog> {
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.name().compareTo(o2.name());
        }
    }

    public static DogNameComparator getDogNameCmp(){
        return new DogNameComparator();
    }

    public static DogSizeComparator getDogSizeCmp(){
        return new DogSizeComparator();
    }
}
