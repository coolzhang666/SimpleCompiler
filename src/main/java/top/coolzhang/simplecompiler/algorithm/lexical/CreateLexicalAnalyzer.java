package top.coolzhang.simplecompiler.algorithm.lexical;

import jflex.anttask.JFlexTask;

import java.io.File;
import java.net.URL;

/**
 * 利用 JFlex 进行词法分析
 * 词法分析器生成类
 */
public class CreateLexicalAnalyzer {

    public void create(String filename) {
        JFlexTask task = new JFlexTask();
        URL url =CreateLexicalAnalyzer.class.getClassLoader().getResource(filename);
        if (url == null) {
            System.out.println("未找到 " + filename);
            return;
        }
        task.setFile(new File(url.getFile()));
        task.execute();
    }
}
