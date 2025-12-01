package javacore.mid.mid2.collection.list;

public class BatchProcessorMain {
    public static void main(String[] args) {
        MyList<Integer> list = new MyArrayList<>();
        BatchProcessor processor = new BatchProcessor(list);
        processor.logic(50_000);

        list = new MyLinkedList<>();
        processor = new BatchProcessor(list);
        processor.logic(50_000);
    }
}
