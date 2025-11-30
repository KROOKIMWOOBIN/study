package javacore.mid.mid2.collection.link;

public class NodeMain3 {
    public static void main(String[] args) {
        Node first = new Node("a");
        first.next = new Node("b");
        first.next.next = new Node("c");
        System.out.println(first);
        System.out.println("모든 노드 탐색하기");
        printAll(first);
        System.out.println("마지막 노드 탐색하기");
        Node lastNode = getLastNode(first);
        System.out.println("lastNode = " + lastNode);
        System.out.println("특정 인덱스의 노드 조회하기");
        Node index2Node = getNode(first, 2);
        System.out.println("index2Node = " + index2Node);
        System.out.println("노드 데이터 추가하기");
        add(first, "D");
        System.out.println(first);
        add(first, "E");
        System.out.println(first);
        add(first, "F");
        System.out.println(first);
    }

    private static void add(Node first, String param) {
        Node lastNode = getLastNode(first);
        lastNode.next = new Node(param);
    }

    private static Node getNode(Node first, int index) {
        Node x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    }

    private static Node getLastNode(Node node) {
        Node x = node;
        while (x.next != null) {
            x = x.next;
        }
        return x;
    }

    private static void printAll(Node node) {
        Node x = node;
        while (x != null) {
            System.out.println(x.item);
            x = x.next;
        }
    }
}
