package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    /*Create a MaxArrayDeque with the given Comparator*/
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    /*Return the max item in the deque as governed by the previously given Comparator*/
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            T curItem = get(i);
            if (comparator.compare(curItem, maxItem) > 0) {
                maxItem = curItem;
            }
        }
        return maxItem;
    }

    /*Return the max item in the deque as governed by the given parameter Comparator c*/
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            T curItem = get(i);
            if (c.compare(curItem, maxItem) > 0) {
                maxItem = curItem;
            }
        }
        return maxItem;
    }
}
