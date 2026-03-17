package javacore.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MyStream<T> {

    private final List<T> list;

    private MyStream (List<T> list) {
        this.list = list;
    }

    // static factory
    public static <T> MyStream<T> of(List<T> list) {
        return new MyStream<>(list);
    }

    public MyStream<T> filter(Predicate<T> predicate) {
        List<T> newList = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                newList.add(t);
            }
        }
        return MyStream.of(newList);
    }

    public <R> MyStream<R> map(Function<T, R> function) {
        List<R> newList = new ArrayList<>();
        for (T t : list) {
            newList.add(function.apply(t));
        }
        return MyStream.of(newList);
    }

    public List<T> toList() {
        return list;
    }

    public void forEach(Consumer<T> consumer) {
        for (T t : list) {
            consumer.accept();
        }
    }

}
