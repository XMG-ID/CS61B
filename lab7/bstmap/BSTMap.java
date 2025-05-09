package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*A map using binary search tree to store keys and values*/
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }
    }

    BSTNode root;
    int size;

    /*Create an empty BSTMap*/
    public BSTMap() {
        root = null;
        size = 0;
    }

    /* Removes all the mappings from this map.*/
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return containsKey(node.right, key);
        } else if (cmp < 0) {
            return containsKey(node.left, key);
        } else {
            return true;
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.*/
    @Override
    public V get(K key) {
        return getHelper(root, key);
    }

    /*Return the value to which the key is mapped, but start search from node.*/
    private V getHelper(BSTNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            return getHelper(node.right, key);
        } else if (cmp < 0) {
            return getHelper(node.left, key);
        } else {
            return node.value;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
        size++;
    }

    /*Starting from the current node, insert a new node. */
    private BSTNode putHelper(BSTNode node, K key, V value) {
        if (node == null) return new BSTNode(key, value);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = putHelper(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putHelper(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    /* Returns a Set view of the keys contained in this map.  */
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        keySetHelper(root, keys);
        return keys;
    }

    /*Starting from the current node, return a Set view of the keys.*/
    private void keySetHelper(BSTNode node, Set<K> keys) {
        if (node == null) return;
        keys.add(node.key);
        keySetHelper(node.left, keys);
        keySetHelper(node.right, keys);
    }

    /* Removes the mapping for the specified key from this map if present. */
    @Override
    public V remove(K key) {
        if (!containsKey(key)) return null;
        size--;
        V value = get(key);
        root = removeHelper(root, key);
        return value;
    }

    /*Remove the node with key and return the current node.*/
    private BSTNode removeHelper(BSTNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            node.right = removeHelper(node.right, key);
        } else if (cmp < 0) {
            node.left = removeHelper(node.left, key);
        } else {
            //Find the node that needs to be removed : no child, one child, or two child
            if (node.right == null && node.left == null) return null;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            /*If the node that needs to be removed has two child,
            find the smallest child on its right, replace the current node with it, and remove it*/
            BSTNode rightMinNode = findMinNode(node.right);
            node.key = rightMinNode.key;
            node.value = rightMinNode.value;
            node.right = removeHelper(node.right, rightMinNode.key);
        }
        return node;
    }

    /*Find the min node(with the smallest key) and return.*/
    private BSTNode findMinNode(BSTNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /* Removes the entry for the specified key only if it is currently mapped to the specified value.*/
    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        }
        return null;
    }

    /*Print out the key in increasing order.*/
    public void printInOrder() {
        print(root);
    }

    private void print(BSTNode node) {
        if (node == null) return;
        print(node.left);
        System.out.println(node.key);
        print(node.right);
    }


    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
