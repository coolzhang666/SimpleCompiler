package top.coolzhang.simplecompiler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.coolzhang.simplecompiler.algorithm.lexical.Token;
import top.coolzhang.simplecompiler.service.RunService;
import top.coolzhang.simplecompiler.utils.ResultObject;
import top.coolzhang.simplecompiler.utils.ResultUtil;

import java.util.ArrayList;

@RestController
public class RunController {
    @Autowired
    RunService runService;

    @PostMapping("/run")
    public ResultObject run(String code) {
        ArrayList<Token> tokens = runService.run(code);
        return ResultUtil.success("执行成功", tokens);
    }
}
