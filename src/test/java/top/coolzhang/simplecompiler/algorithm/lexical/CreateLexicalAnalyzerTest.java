package top.coolzhang.simplecompiler.algorithm.lexical;

import org.junit.Test;

public class CreateLexicalAnalyzerTest {

    @Test
    public void testCreateLexicalAnalyzer() {
        CreateLexicalAnalyzer creator = new CreateLexicalAnalyzer();
        creator.create("lexical.flex");
    }
}
