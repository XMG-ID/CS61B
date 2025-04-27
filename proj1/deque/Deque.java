package deque;

public interface Deque<T> {

    /*Add an item of type T to the front of the deque assuming item is never null*/
    public void addFirst(T item);

    /*Adds an item of type T to the back of the deque assuming item is never null*/
    public void addLast(T item);

    /*Return true if the deque is empty, false otherwise*/
    public boolean isEmpty();

    /*Return the number of items in the deque*/
    public int size();

    /*Print all the items from first to last, seperated by a space
    Once all items have been printed, print out a new line*/
    public void printDeque();

    /*Remove and return the item at the front of the deque.
    If no such item exists, return null*/
    public T removeFirst();

    /*Remove and return the item at the last of the deque.
    If no such item exists, return null*/
    public T removeLast();

    /*Get the ith(starting from 0) item from the deque
    If no such item exists, return null*/
    public T get(int index);

}
