## 해시 알고리즘
### [Hash Code]와 [Equals]를 재정의하는 이유
1. [Hash Code]를 재정의 하지 않으면 Object.hashcode()로 주소 기반으로 만들어지기 때문에 재정의하여 사용하여 한다.
   ```java
   class Member {
      String id;
      String name;
      @Override
      public int hashcode() {
         return Objects.hash(id, name);
      }
   }
   ```
2. [Equals]를 재정의하지 않으먼 동일성만 검사한다. 그래서 필드 값이 같아도 다른 인스턴스면 false가 나온다.

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
### 해시 인덱스(Hash Index)
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
    
    private static int hashIndex(Object value) {
        return Math.abs(value.hashCode()) % CAPACITY;
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
    private int hashIndex(Object value) {
        return Math.abs(value.hashCode()) % CAPACITY;
    }
}
```
### 해시 코드(Hash Code)
```java
public class Main {
    static final int CAPACITY = 10;
    public static void main(String[] args) {
        char charA = 'A';
        char charB = 'B';
        System.out.println("charA = " + (int)charA);
        System.out.println("charB = " + (int)charB);

        System.out.println();
        System.out.println("hashCode(\"A\") = " + hashCode("A"));
        System.out.println("hashCode(\"B\") = " + hashCode("B"));
        System.out.println("hashCode(\"AB\") = " + hashCode("AB"));

        System.out.println();
        System.out.println("hashIndex(hashCode(\"A\")) = " + hashIndex(hashCode("A")));
        System.out.println("hashIndex(hashCode(\"B\")) = " + hashIndex(hashCode("B")));
        System.out.println("hashIndex(hashCode(\"AB\")) = " + hashIndex(hashCode("AB")));
    }

    static int hashCode(String str) {
        char[] charArrays = str.toCharArray();
        int sum = 0;
        for (char c : charArrays) {
            sum += c;
        }
        return sum;
    }

    static int hashIndex(Object value) {
        return Math.abs(value.hashCode()) % CAPACITY;
    }
}
```
### 해시 함수(Hash Function)
- 해시 함수는 임의의 길이의 데이터를 입력으로 받아, 고정된 길이의 해시값(해시 코드)를 출력하는 함수이다.
  - 여기서 의미하는 고정된 길이는 저장 공간의 크기를 뜻한다. 예를 들어서 int형 1, 100은 둘  4Byte를 차지하는 고정된 길이를 뜻한다.
- 같은 데이터를 입력하면 항상 같은 해시 코드가 출력된다.