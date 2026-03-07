package javacore.annotation.validator;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class User {

    @NotEmpty(message = "이름은 필수입니다.")
    private String name;

    @Range(min = 1, max = 100, message = "나이는 1부터 100 사이만 가능합니다.")
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

}
