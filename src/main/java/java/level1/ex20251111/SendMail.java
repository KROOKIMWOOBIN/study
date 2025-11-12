package java.level1.ex20251111;

public class SendMail {
    public static void main(String[] args) {
        Sender[] senders = {new Email(), new Sms(), new FaceBook()};
        print(senders);
    }
    private static void print(Sender[] senders) {
        for(Sender sender : senders) {
            sender.send("Welcome");
        }
    }
}
interface Sender {
    void send(String message);
}
class Email implements Sender {
    @Override
    public void send(String message) {
        System.out.println("Email::" + message);
    }
}
class Sms implements Sender {
    @Override
    public void send(String message) {
        System.out.println("Sms::" + message);
    }
}
class FaceBook implements Sender {
    @Override
    public void send(String message) {
        System.out.println("FaceBook::" + message);
    }
}

