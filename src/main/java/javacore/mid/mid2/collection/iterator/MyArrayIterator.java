package javacore.mid.mid2.collection.iterator;

import java.util.Iterator;

public class MyArrayIterator implements Iterator<Integer> {

    private int currentIndex = -1;
    private int[] targetArray;

    public MyArrayIterator(int[] targetArray) {
        this.targetArray = targetArray;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < targetArray.length - 1;
    }

    @Override
    public Integer next() {
        return targetArray[++currentIndex];
    }
}
