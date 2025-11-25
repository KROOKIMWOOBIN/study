package javacore.mid.mid1.test;

import java.util.Scanner;

public class EnumEx2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("HTTP CODE : ");
        int httpCodeInput = scanner.nextInt();
        HttpStatus status = HttpStatus.findByCode(httpCodeInput);
        if(status == null) {
            System.out.println("정의되지 않은 코드");
        } else {
            System.out.println(status.getCode() + " " + status.getMessage());
            System.out.println("isSuccess : " + status.isSuccess());
        }
    }
}

enum HttpStatus {

    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    private int code;
    private String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus findByCode(int code) {
        HttpStatus[] values = values();
        for(HttpStatus status : values) {
            if(status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public boolean isSuccess() {
        return code >= 200 && code <= 299;
    }

}
