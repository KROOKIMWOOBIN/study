package javacore.mid.mid2.collection.link;

public class MyLinkedListV1 {
    private Node first;
    private int size = 0;
    public void add(Object e) {
        Node newNode = new Node(e);
        if (first == null) {
            first = newNode;
        } else {
            Node lastNode = getLastNode();
            lastNode.next = newNode;
        }
        size++;
    }
    private Node getLastNode() {
        Node x = first;
        while (x.next != null) {
            x = x.next;
        }
        return x;
    }
    public int size() {
        return size;
    }
    public Object set(int index, Object element) {
        Node x = getNode(index);
        Object oldValue = x.item;
        x.item = element;
        return oldValue;
    }
    public Object get(int index) {
        return getNode(index).item;
    }
    private Node getNode(int index) {
        Node x = first;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    }
    public int indexOf(Object o) {
        int index = 0;
        for (Node x = first; x != null; x = x.next, index++) {
            if (o.equals(x.item)) {
                return index;
            }
        }
        return -1;
    }
    @Override
    public String toString() {
        return "MyLinkedListV1{" +
                "first=" + first +
                ", size=" + size +
                '}';
    }
}
