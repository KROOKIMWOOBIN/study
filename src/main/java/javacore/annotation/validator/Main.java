package javacore.annotation.validator;

import static javacore.annotation.validator.Validator.*;

public class Main {

    public static void main(String[] args) {
        User user = new User("", 0);
        validate(user);
    }

}
