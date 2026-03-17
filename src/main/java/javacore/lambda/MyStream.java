package javacore.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class MyStream<T> {

    private List<T> list;

    private MyStream (List<T> list) {
        this.list = list;
    }

    // static factory
    public static <T> MyStream of(List<T> list) {
        return new MyStream(list);
    }

    public MyStream filter(Predicate<T> predicate) {
        List<T> newList = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                newList.add(t);
            }
        }
        return new MyStream(newList);
    }

    public <R> MyStream map(Function<T, R> function) {
        List<T> newList = new ArrayList<>();
        for (T t : list) {
            newList.add(function.apply(r));
        }
        return new MyStream(newList);
    }

}
