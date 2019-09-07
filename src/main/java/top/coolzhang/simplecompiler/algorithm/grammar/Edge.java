package top.coolzhang.simplecompiler.algorithm.grammar;

public class Edge {
    //    public int index;
    public Status to;
    public String s;
    public Edge next;

    //    public Edge(int index, String s, Edge next) {
//        this.index = index;
//        this.s = s;
//        this.next = next;
//    }
    public Edge(Status status, String s, Edge next) {
        this.to = status;
        this.s = s;
        this.next = next;
    }
}
