package top.coolzhang.simplecompiler.algorithm.grammar;

public class Attribute {
    // 类型
    public String type;

    // 值
    public String value;

    // 传递属性
    public String transfer;

    // 变量地址
    public String place;

    public Attribute() {}

    public Attribute(String type, String value, String transfer, String place) {
        this.type = type;
        this.value = value;
        this.transfer = transfer;
        this.place = place;
    }
}
