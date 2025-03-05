package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{

    private static final double FACTOR = 0.25;
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] array;
    public ArrayDeque() {
        size = 0;
        array = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
    }
    @Override
    public void addFirst(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        size++;
        array[nextFirst] = item;
        nextFirst = parseIndex(nextFirst - 1);
    }

    private void resize(int length) {
        T[] tempArray = (T[]) new Object[length];
        int tempIndex = parseIndex(nextFirst + 1);
        int tempArrayIndex = 0;
        for (int i = 0; i < size; i++) {
            tempArray[tempArrayIndex++] = array[tempIndex];
            tempIndex = parseIndex(tempIndex + 1);
        }
        nextFirst = tempArray.length - 1;
        nextLast = size;
        array = tempArray;
    }
    private int parseIndex(int index) {
        if (index == -1) {
            return array.length - 1;
        } else if (index == array.length) {
            return 0;
        } else {
            return index;
        }
    }


    @Override
    public void addLast(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        size++;
        array[nextLast] = item;
        nextLast = parseIndex(nextLast + 1);
    }



    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int tempIndex = parseIndex(nextFirst + 1);
        for (int i = 0; i < size; i++) {
            System.out.print(array[tempIndex] + " ");
            tempIndex = parseIndex(tempIndex + 1);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (array.length >= 16 && size < array.length * FACTOR) {
            resize(array.length / 2);
        }
        size--;
        T item = array[parseIndex(nextFirst + 1)];
        nextFirst = parseIndex(nextFirst + 1);
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (array.length >= 16 && size < array.length * FACTOR) {
            resize(array.length / 2);
        }
        size--;
        T item = array[parseIndex(nextLast - 1)];
        nextLast = parseIndex(nextLast - 1);
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int tempIndex = parseIndex(nextFirst + 1);
        for (int i = 0; i < index; i++) {
            tempIndex = parseIndex(tempIndex + 1);
        }
        return array[tempIndex];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;
        public ArrayDequeIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        if (size() != ((Deque<?>) o).size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(((Deque<?>) o).get(i))) {
                return false;
            }
        }
        return true;
    }
}
