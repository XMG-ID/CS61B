package deque;

public class ArrayDeque<T> implements Deque<T> {
    private int nextFirst;
    private int nextLast;
    private int size;
    private T[] items;
    private static final int START_SIZE = 8;

    /*Create an empty array deque*/
    public ArrayDeque() {
        items = (T[]) new Object[START_SIZE];
        size = 0;
        nextFirst = START_SIZE - 1;
        nextLast = 0;
    }

    /*Add an item of type T to the front of the deque assuming item is never null*/
    public void addFirst(T item) {
        if (size == items.length) {
            resize();
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size++;
    }

    /*Adds an item of type T to the back of the deque assuming item is never null*/
    public void addLast(T item) {
        if (size == items.length) {
            resize();
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1 + items.length) % items.length;
        size++;
    }

    /*Resize the array and change the nextFirst, nextLast position*/
    private void resize() {
        int REFACTOR = 2;//Simulate the ratio of resize
        T[] newItems = (T[]) new Object[size * REFACTOR];
        //Copy the items by its order from first to last by directly removing the first item
        for (int i = 0; i < items.length; i++) {
            newItems[i] = removeFirst();
        }
        size = items.length;//Regain the actual size, because it's changed during the removeFirst session
        items = newItems;
        //Change nextFirst and nextLast
        nextFirst = newItems.length - 1;
        nextLast = size;
    }

    /*Return true if the deque is empty, false otherwise*/
    public boolean isEmpty() {
        return size == 0;
    }

    /*Return the number of items in the deque*/
    public int size() {
        return size;
    }

    /*Print all the items from first to last, seperated by a space
    Once all items have been printed, print out a new line*/
    public void printDeque() {
        //Also use removeFirst method, remember to reset the size, nextFirst, nextLast variable
        int oriNextFirst = nextFirst, oriNextLast = nextLast, oriSize = size;
        while (size > 0) {
            System.out.print(removeFirst() + " ");
        }
        System.out.println();
        nextFirst = oriNextFirst;
        nextLast = oriNextLast;
        size = oriSize;
    }

    /*Remove and return the item at the front of the deque.
    If no such item exists, return null*/
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int firstIndex = (nextFirst + 1) % items.length;
        nextFirst = firstIndex;
        size--;
        return items[firstIndex];
    }

    /*Remove and return the item at the last of the deque.
    If no such item exists, return null*/
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = (nextLast - 1 + items.length) % items.length;
        nextLast = lastIndex;
        size--;
        return items[lastIndex];
    }

    /*Get the ith(starting from 0) item from the deque
    If no such item exists, return null*/
    public T get(int index) {
        if (isEmpty()) {
            return null;
        }
        int targetIndex = (nextFirst + 1 + index) % items.length;
        return items[targetIndex];
    }
}
