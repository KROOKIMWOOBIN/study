package javacore.mid.mid2.generic.ex5;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Box<T> {
    private T value;
}
