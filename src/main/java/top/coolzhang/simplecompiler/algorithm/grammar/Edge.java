package top.coolzhang.simplecompiler.algorithm.grammar;

public class Edge {
    public Status to;
    public String s;
    public Edge next;

    public Edge(Status status, String s, Edge next) {
        this.to = status;
        this.s = s;
        this.next = next;
    }
}
