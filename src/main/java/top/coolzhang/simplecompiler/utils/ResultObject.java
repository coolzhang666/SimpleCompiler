package top.coolzhang.simplecompiler.utils;

import top.coolzhang.simplecompiler.algorithm.lexical.Token;

import java.util.ArrayList;

public class ResultObject {
    private Integer status;

    private String msg;

    private ArrayList<Token> lexical;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<Token> getLexical() {
        return lexical;
    }

    public void setLexical(ArrayList<Token> lexical) {
        this.lexical = lexical;
    }
}
