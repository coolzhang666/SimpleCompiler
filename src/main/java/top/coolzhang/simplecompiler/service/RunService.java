package top.coolzhang.simplecompiler.service;

import org.springframework.stereotype.Service;
import top.coolzhang.simplecompiler.algorithm.grammar.R1TableUtil;
import top.coolzhang.simplecompiler.algorithm.lexical.LexicalAnalyzer;
import top.coolzhang.simplecompiler.algorithm.lexical.Token;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class RunService {
    public ArrayList<Token> run(String code) {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(new StringReader(code));
        try {
            lexicalAnalyzer.next_token();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lexicalAnalyzer.getToken();
    }

    public boolean grammar(List<String> token, List<String> word) {
//        R1TableUtil r1TableUtil = new R1TableUtil();
//        r1TableUtil.init();
//        return r1TableUtil.judge(new LinkedList<>(token));
        return false;
    }
}
