package deque;

/*The method in the interface can be seen in the same package, so public is redundant*/
public interface Deque<T> {

    /*Add an item of type T to the front of the deque assuming item is never null*/
    void addFirst(T item);

    /*Adds an item of type T to the back of the deque assuming item is never null*/
    void addLast(T item);

    /*Return the number of items in the deque*/
    int size();

    /*Print all the items from first to last, seperated by a space
    Once all items have been printed, print out a new line*/
    void printDeque();

    /*Remove and return the item at the front of the deque.
    If no such item exists, return null*/
    T removeFirst();

    /*Remove and return the item at the last of the deque.
    If no such item exists, return null*/
    T removeLast();

    /*Get the ith(starting from 0) item from the deque
    If no such item exists, return null*/
    T get(int index);

    /*Return true if the deque is empty, false otherwise*/
    default boolean isEmpty() {
        return size() == 0;
    }

}
