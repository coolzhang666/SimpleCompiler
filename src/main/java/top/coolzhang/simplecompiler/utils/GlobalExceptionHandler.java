package top.coolzhang.simplecompiler.utils;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 词法分析错误捕获
     * @param e 捕获的异常类型
     * @return 返回消息封装类ReturnObject
     */
    @ExceptionHandler(LexicalError.class)
    public ResultObject defaultExceptionHandler(LexicalError e) {
        return ResultUtil.fail(-1, e.getMessage(), null);
    }
}
