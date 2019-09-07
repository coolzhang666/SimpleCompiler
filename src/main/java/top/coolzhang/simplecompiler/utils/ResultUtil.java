package top.coolzhang.simplecompiler.utils;

import top.coolzhang.simplecompiler.algorithm.lexical.Token;

import java.util.ArrayList;

public final class ResultUtil {
    private final static int SUCCESS_CODE = 1;
    private final static String SUCCESS_MSG = "SUCCESS";
    private final static int FAIL_CODE = 0;
    private final static String FAIL_MSG = "FAIL";
    private final static int ERROR_CODE = -1;
    private final static String ERROR_MSG = "ERROR";


    public static ResultObject success() {
        return success(null);
    }

    public static ResultObject success(ArrayList<Token> o) {
        return success(null, o);
    }

    public static ResultObject success(String msg, ArrayList<Token> o) {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(SUCCESS_CODE);
        resultObject.setMsg(msg == null ? SUCCESS_MSG : msg);
        resultObject.setLexical(o);
        return resultObject;
    }


    public static ResultObject fail() {
        return fail(null);
    }

    public static ResultObject fail(ArrayList<Token> o) {
        return fail(null, o);
    }

    public static ResultObject fail(String msg, ArrayList<Token> o) {
        return fail(FAIL_CODE, msg, o);
    }

    public static ResultObject fail(int status, String msg, ArrayList<Token> o) {
        ResultObject resultObject = new ResultObject();
        resultObject.setStatus(status);
        resultObject.setMsg(msg == null ? FAIL_MSG : msg);
        resultObject.setLexical(o);
        return resultObject;
    }
}
