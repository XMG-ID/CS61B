package deque;

public class Dog {
    private int size;
    private String name;

    public Dog(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int size() {
        return size;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "I am " + name;
    }
}
