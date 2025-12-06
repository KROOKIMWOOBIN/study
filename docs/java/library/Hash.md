## 해시 알고리즘
### 인덱스 사용
자기 자신의 값을 인덱스로 사용하여 검색 속도가 O(1)이 된다.
```java
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Integer[] intList = new Integer[100];
        intList[1] = 1;
        intList[4] = 4;
        intList[8] = 8;
        System.out.println("intList = " + Arrays.toString(intList));
        int search = 8;
        // 인덱스를 사용하면 O(1)로 검색할 수 있다.
        Integer result = intList[search];
        System.out.println("result = " + result);
    }
}
```
### 해시 인덱스
원래의 값을 계산한 인덱스를 해시 인덱스라 한다.
```java
public class Main {
    
    private final int CAPACITY = 10;
    
    public static void main(String[] args) {
        Integer[] intArray = new Integer[CAPACITY];
        add(intArray, 1);
        add(intArray, 2);
        add(intArray, 8);
        add(intArray, 14);
        add(intArray, 99);
        // {1, 2, 4, 8, 9} <- 해쉬 인덱스로 인해 값 변환
        // {1, 2, 14, 8, 99} <- 실제 해쉬 인덱스에 값
        int searchValue = 14;
        System.out.println("result: " + intArray[hashIndex(searchValue)]);
    }
    
    private static void add(Integer[] array, int value) {
        array[hashIndex(value)] = value;
    }
    
    private static int hashIndex(int value) {
        return value % CAPACITY;
    }
}
```
### 해쉬 충돌
다른 값을 입력했지만, 같은 해시 코드가 나오게 되는데 이것을 해시 충돌이라 한다.
```java
import java.util.LinkedList;

public class Main {
    private final int CAPACITY = 10;

    public static void main(String[] args) {
        LinkedList<Integer>[] buckets = new LinkedList[CAPACITY];
        // [null, null, null, null, null, null, null, null, null, null]
        for (int i = 0; i < CAPACITY; i++) {
            buckets[i] = new LinkedList<>();
        }
        // [[], [], [], [], [], [], [], [], [], []]
        add(buckets, 1);
        add(buckets, 2);
        add(buckets, 5);
        add(buckets, 8);
        add(buckets, 14);
        add(buckets, 99);
        add(buckets, 9);
        // [[], [1], [2], [], [14], [5], [], [], [8], [99, 9]]
        int searchValue = 9;
        System.out.println("result: " + contains(buckets, searchValue));
    }
    private static void add(LinkedList<Integer>[] buckets, int value) {
        int hashIndex = hashIndex(value);
        LinkedList<Integer> bucket = buckets[hashIndex];
        if (!bucket.contains(value)) {
            bucket.add(value);
        }
    }
    private static boolean contains(LinkedList<Integer>[] buckets, int searchValue) {
        int hashIndex = hashIndex(searchValue);
        LinkedList<Integer> bucket = buckets[hashIndex];
        return bucket.contains(searchValue);
    }
    private int hashIndex(int value) {
        return value % CAPACITY;
    }
}
```