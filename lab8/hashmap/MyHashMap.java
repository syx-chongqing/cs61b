package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        buckets = createTable(this.initialSize);
        this.size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < this.initialSize; i++) {
            Iterator<Node> iterator = buckets[i].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                if (next.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        for (int i = 0; i < this.initialSize; i++) {
            Iterator<Node> iterator = buckets[i].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                if (next.key.equals(key)) {
                    return next.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        Node node = new Node(key, value);
        if (get(key) != null) {
            putHelper(node, buckets);
            return;
        }
        size += 1;

        Collection<Node>[] tempBuckets;
        if (size / (double) this.initialSize > this.loadFactor) {
            tempBuckets = createTable(this.initialSize * 2);
            for (int i = 0; i < this.initialSize; i++) {
                Iterator<Node> iterator = buckets[i].iterator();
                while (iterator.hasNext()) {
                    Node next = iterator.next();
                    putHelper(next, tempBuckets);
                }
            }
            putHelper(node, tempBuckets);
            buckets = tempBuckets;
            this.initialSize = this.initialSize * 2;
        } else {
            putHelper(node, buckets);
        }
    }
    private void putHelper(Node node, Collection<Node>[] buckets) {
        int hashValue = Math.floorMod(node.key.hashCode(), buckets.length);
        Iterator<Node> iterator = buckets[hashValue].iterator();
        while (iterator.hasNext()) {
            Node next = iterator.next();
            if (next.key.equals(node.key)) {
                next.value = node.value;
                return;
            }
        }
        buckets[hashValue].add(node);
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (int i = 0; i < this.initialSize; i++) {
            Iterator<Node> iterator = buckets[i].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                set.add(next.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }
    class HashMapIterator implements Iterator {
        int index = 0;
        @Override
        public boolean hasNext() {
            if (index == initialSize - 1 && buckets[initialSize - 1].iterator().hasNext() == false) {
                return false;
            }
            return true;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                return null;
            }
            if (buckets[index].iterator().hasNext()) {
                return buckets[index].iterator().next();
            } else {
                index += 1;
                return next();
            }
        }
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int initialSize;
    private double loadFactor;
    private int size = 0;

    /** Constructors */
    public MyHashMap() {
        this.initialSize = 16;
        this.loadFactor = 0.75;
        buckets = createTable(this.initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.loadFactor = 0.75;
        buckets = createTable(this.initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(this.initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }


    // Your code won't compile until you do so!

}
