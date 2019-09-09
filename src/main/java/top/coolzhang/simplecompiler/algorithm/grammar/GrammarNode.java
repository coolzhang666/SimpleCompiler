package top.coolzhang.simplecompiler.algorithm.grammar;

public class GrammarNode {
    private String value;
    private GrammarNode next;

    public GrammarNode(String value){
        this.value = value;
        this.next = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public GrammarNode getNext() {
        return next;
    }

    public void setNext(GrammarNode next) {
        this.next = next;
    }
}
