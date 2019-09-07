package top.coolzhang.simplecompiler.utils;

import top.coolzhang.simplecompiler.algorithm.lexical.Token;

import java.util.ArrayList;

public class ResultObject {
    private Integer status;

    private String msg;

    private ArrayList<Token> lexical;

    private String grammarResult;

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

    public String getGrammarResult() {
        return grammarResult;
    }

    public void setGrammarResult(String grammarResult) {
        this.grammarResult = grammarResult;
    }
}
