package deque;


import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private IntNode sentinel;
    private int size;
    public LinkedListDeque() {
        size = 0;
        sentinel = new IntNode((T)new Object());
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }
    public class IntNode {
        IntNode next;
        IntNode prev;
        T item;
        public IntNode(T item) {
            this.item = item;
        }
        public IntNode(T item, IntNode prev, IntNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);

    }
    private T getRecursiveHelper(int index, IntNode temp) {
        if (index == 0) {
            return temp.item;
        } else {
            return getRecursiveHelper(index - 1, temp.next);
        }
    }
    @Override
    public void addFirst(T item) {
        size++;
        IntNode temp = new IntNode(item);
        temp.prev = sentinel;
        temp.next = sentinel.next;
        sentinel.next.prev = temp;
        sentinel.next = temp;
    }

    @Override
    public void addLast(T item) {
        size++;
        IntNode temp = new IntNode(item);
        temp.next = sentinel;
        temp.prev = sentinel.prev;
        sentinel.prev.next = temp;
        sentinel.prev = temp;
    }




    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        IntNode temp = sentinel.next;
        for (int i = 0; i < size; i++) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        IntNode temp = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size--;
        return temp.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        IntNode temp = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size--;
        return temp.item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        IntNode temp = sentinel.next;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.item;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;
        public LinkedListDequeIterator() {
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
