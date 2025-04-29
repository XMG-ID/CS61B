package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node {
        public T item;
        public Node pre;
        public Node next;

        /*Create a node with nothing in item*/
        public Node() {
        }

        /*Create a node with given item x, its previous node pre and its next node next*/
        public Node(T x, Node preNode, Node nextNode) {
            item = x;
            pre = preNode;
            next = nextNode;
        }
    }

    private class LLDIterator implements Iterator<T> {
        public Node trace;

        public LLDIterator() {
            //We only care about what its next is, trace always points at the node we just get
            trace = sentinel;
        }

        @Override
        public boolean hasNext() {
            return trace.next != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T returnItem = trace.next.item;
            trace = trace.next;
            return returnItem;
        }

    }

    private Node sentinel;
    private int size;

    /*Create an empty linked list deque*/
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node();
        sentinel.next = sentinel;
        sentinel.pre = sentinel;
    }

    /*Add an item of type T to the front of the deque assuming item is never null*/
    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);
        sentinel.next.pre = newNode;
        sentinel.next = newNode;
        size++;
    }

    /*Adds an item of type T to the back of the deque assuming item is never null*/
    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.pre, sentinel);
        sentinel.pre.next = newNode;
        sentinel.pre = newNode;
        size++;
    }


    /*Return the number of items in the deque*/
    @Override
    public int size() {
        return size;
    }

    /*Print all the items from first to last, seperated by a space
    Once all items have been printed, print out a new line*/
    @Override
    public void printDeque() {
        if (!isEmpty()) {
            Node cur = sentinel.next;
            while (cur != sentinel) {
                System.out.print(cur.item + " ");
                cur = cur.next;
            }
        }
        System.out.println();
    }

    /*Remove and return the item at the front of the deque.
    If no such item exists, return null*/
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T removedItem = sentinel.next.item;
        sentinel.next.next.pre = sentinel;
        sentinel.next = sentinel.next.next;
        return removedItem;
    }

    /*Remove and return the item at the last of the deque.
    If no such item exists, return null*/
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T removedItem = sentinel.pre.item;
        sentinel.pre.pre.next = sentinel;
        sentinel.pre = sentinel.pre.pre;
        return removedItem;
    }

    /*Get the ith(starting from 0) item from the deque
    If no such item exists, return null*/
    @Override
    public T get(int index) {
        Node cur = sentinel;
        while (index >= 0) {
            if (cur.next == sentinel && index != 0) {
                return null;
            }
            cur = cur.next;
            index--;
        }
        return cur.item;
    }

    /*Same as get, but uses recursion.*/
    public T getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node cur) {
        if (index == 0) {
            return cur.item;
        }
        if (cur == sentinel) {
            return null;
        }
        return getRecursiveHelper(index - 1, cur.next);
    }

    /*The iterator methods returns an Iterator that has the method "hasNext" and "next".
    Since Iterator is an original interface in java, we must create a class to implement that*/
    @Override
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

}
