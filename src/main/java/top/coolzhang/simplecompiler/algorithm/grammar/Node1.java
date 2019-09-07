package top.coolzhang.simplecompiler.algorithm.grammar;

import java.util.LinkedList;

public class Node1 {
    LinkedList<String> list;
    Node1 next;

    public Node1(LinkedList<String> list, Node1 next) {
        this.list = list;
        this.next = next;
    }
}
