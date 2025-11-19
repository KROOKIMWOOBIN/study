package javacore.intermediate.class5;

public class NestedOuterMainEx1 {
    public static void main(String[] args) {
        Network network = new Network();
        network.send("Hello Java");
    }
}

class Network {
    public void send(String text) {
        NetworkMessage networkMessage = new NetworkMessage(text);
        networkMessage.print();
    }
    private static class NetworkMessage {
        private String content;
        public NetworkMessage(String content) {
            this.content = content;
        }
        public void print() {
            System.out.println(content);
        }
    }
}
