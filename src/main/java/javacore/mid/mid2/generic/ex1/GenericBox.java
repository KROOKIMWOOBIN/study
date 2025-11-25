package javacore.mid.mid2.generic.ex1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericBox<T> {
    private T value;
}
