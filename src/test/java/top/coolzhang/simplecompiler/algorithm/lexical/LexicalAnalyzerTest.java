package top.coolzhang.simplecompiler.algorithm.lexical;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class LexicalAnalyzerTest {

    @Test
    public void testLexicalAnalyzer() throws IOException {
        String s = "int a = 0;";
        StringReader stringReader = new StringReader(s);
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(stringReader);
        lexicalAnalyzer.next_token();
        ArrayList<Token> tokens = lexicalAnalyzer.getToken();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
