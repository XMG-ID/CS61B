package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 **/
/* Golden rules:
   1. hashTable is an array of buckets, each bucket is a collection of generic type.
   2. It is not only array that is iterable. Collection is iterable too.
   Collection extends Iterable, so every collection such as ArrayList、HashSet is iterable.
   3. Interface can extend interface but can not extend class.
   4. If a class is Iterable, it must have a method called iterator().
   When call iterator(), it can return an Iterator that can iterate through the class.
   Every Iterator should have the method of next() and hasNext().
   5.Whenever you implement Iterator interface, you should specify what the iterator will return by adding a generic type.
   Iterator<K> means this iterator return K type item. That correspond to the return type of next().
   If you simply use Iterator, the compiler thinks it's Iterator<Object>, (Object is the so-called raw-type)
   but Object doesn't correspond to the return type of next().*/

public class MyHashMap<K, V> implements Map61B<K, V> {
    /* Protected helper class to store key/value pairs. The protected qualifier allows subclass access*/
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* A nested class for myHashMap iterator. */
    private class myHashMapIterator implements Iterator<K>{
        private Iterator<K> keyIter;

        /* Simply use the iterator of keySet. */
        public myHashMapIterator(){
            keyIter = keySet().iterator();
        }

        @Override
        public boolean hasNext(){
            return keyIter.hasNext();
        }

        @Override
        public K next(){
            return keyIter.next();
        }
    }

    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private Collection<Node>[] buckets;
    private double loadFactor;
    private int size;// size is the actual number of objects that the map has.

    /* Constructors : default initialSize=16, loadFactor=0.75 */
    public MyHashMap() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
    }

    /* Returns a new node to be placed in a hash table bucket. */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /* Returns a data structure to be a hash table bucket*/
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /* Returns a table to back our hash table.Guarantee all the elements is created by the factory method. */
    private Collection<Node>[] createTable(int tableSize) {
        /* In Java, you cannot create an array of parameterized type.
        So, new Collection<Node>[size] is not allowed.
        Instead, you should create like this: new Collection[size].
        This means the elements of the array could be a Collection of any type */
        Collection<Node>[] hashTable = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            hashTable[i] = createBucket();
        }
        return hashTable;
    }

    // TODO: Implement the methods of the Map61B Interface below
    /* Removes all the mappings from this map. */
    @Override
    public void clear() {
        /* Every class that implements Collection interface has the method clear()
        which can clear all the element in the collection. */
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /* Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key. */
    @Override
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map.
    If the map previously contained a mapping for the key, the old value is replaced. */
    @Override
    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        // If the key already exists, replace the old value
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        // Else, consider if we need resize, each time we resize it by doubling
        if ((double) size / buckets.length >= loadFactor) {
            resize();
        }
        index = Math.floorMod(key.hashCode(), buckets.length);
        buckets[index].add(createNode(key, value));
        size++;
    }

    /* Resize the buckets, therefore we should reassign all the node in it. */
    private void resize() {
        Collection<Node>[] newBuckets = createTable(buckets.length * 2);
        for (Collection<Node> bucket: buckets) {
            for (Node node : bucket) {
                int index = Math.floorMod(node.key.hashCode(), newBuckets.length);
                newBuckets[index].add(createNode(node.key,node.value));
            }
        }
        buckets = newBuckets;
    }


    /* Returns a Set view of the keys contained in this map.*/
    @Override
    public Set<K> keySet(){
        Set<K> keys = new HashSet<>();
        for(Collection<Node> bucket: buckets){
            for(Node node: bucket){
                keys.add(node.key);
            }
        }
        return  keys;
    }

    /* Removes the mapping for the specified key from this map if present. Else, return null. */
    @Override
    public V remove(K key){
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for(Node node: buckets[index]){
            if(node.key.equals(key)){
                V removedValue = node.value;
                buckets[index].remove(node);
                size--;
                return removedValue;
            }
        }
        return null;
    }

    /* Removes the entry for the specified key only if it is currently mapped to the specified value. */
    @Override
    public V remove(K key, V value){
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for(Node node: buckets[index]){
            if(node.key.equals(key) && node.value.equals(value)){
                V removedValue = node.value;
                buckets[index].remove(node);
                size--;
                return removedValue;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator(){
        return new myHashMapIterator();
    }

}
