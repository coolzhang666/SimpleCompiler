package top.coolzhang.simplecompiler.algorithm.grammar;

import java.util.LinkedList;

public class ProjectSetNode {
    private LinkedList<String> list;
    private ProjectSetNode next;

    public ProjectSetNode(LinkedList<String> list) {
        this.list = list;
        this.next = null;
    }

    public LinkedList<String> getList() {
        return list;
    }

    public void setList(LinkedList<String> list) {
        this.list = list;
    }

    public ProjectSetNode getNext() {
        return next;
    }

    public void setNext(ProjectSetNode next) {
        this.next = next;
    }
}
