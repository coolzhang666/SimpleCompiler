package top.coolzhang.simplecompiler.algorithm.grammar;

import top.coolzhang.simplecompiler.algorithm.lexical.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class GrammarAnalyzer {
    // 保存终结符
    private static ArrayList<String> terminal = new ArrayList<>();

    // 保存非终结符
    private static ArrayList<String> nonTerminal = new ArrayList<>();

    // 保存产生式
    private static ArrayList<String> production = new ArrayList<>();

    // 保存 action 表
    private static String[][] action;

    // 保存 GOTO 表
    private static String[][] GOTO;

    // 属性文法
    private static HashMap<String, Attribute> attributes = new HashMap<>();

    static {
        initTerminalSymbol();
        initNonTerminalSymbol();
        initProduction();
        initAction();
        initGOTO();
    }

    // 初始化终结符
    private static void initTerminalSymbol() {
        try {
            URL url = GrammarAnalyzer.class.getClassLoader().getResource("terminalSymbol.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    terminal.add(line);
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化非终结符
    private static void initNonTerminalSymbol() {
        try {
            URL url = GrammarAnalyzer.class.getClassLoader().getResource("nonTerminalSymbol.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    nonTerminal.add(line);
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化产生式
    private static void initProduction() {
        try {
            URL url = GrammarAnalyzer.class.getClassLoader().getResource("production.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    production.add(line);
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化 action 表
    private static void initAction() {
        try {
            URL url = GrammarAnalyzer.class.getClassLoader().getResource("action.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    lineCount++;
                }
            }
            br.close();
            reader.close();

            action = new String[lineCount + 1][terminal.size() + 1];
            for (int i = 1; i < action.length; i++) {
                action[i][0] = (i - 1) + "";
            }
            for (int i = 1; i < action[0].length; i++) {
                action[0][i] = terminal.get(i - 1);
            }

            InputStreamReader reader1 = new InputStreamReader(new FileInputStream(file));
            BufferedReader br1 = new BufferedReader(reader1);
            lineCount = 0;
            while ((line = br1.readLine()) != null) {
                if (line.length() > 0) {
                    lineCount++;
                    String[] array = line.split(" ");
                    for (int i = 1; i < action[0].length; i++) {
                        String s = array[i - 1];
                        if (!"null".equals(s)) {
                            action[lineCount][i] = s;
                        }
                    }
                }
            }


            br1.close();
            reader1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化 GOTO 表
    private static void initGOTO() {
        try {
            URL url = GrammarAnalyzer.class.getClassLoader().getResource("goto.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;

            int num = action.length;

            GOTO = new String[num][nonTerminal.size() + 1];
            for (int i = 1; i < GOTO.length; i++) {
                GOTO[i][0] = (i - 1) + "";
            }
            for (int i = 1; i < GOTO[0].length; i++) {
                GOTO[0][i] = nonTerminal.get(i - 1);
            }

            num = 0;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    num++;
                    String[] array = line.split(" ");
                    for (int i = 1; i < GOTO[0].length; i++) {
                        String s = array[i - 1];
                        if (!"null".equals(s)) {
                            GOTO[num][i] = s;
                        }
                    }
                }
            }

            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean judge(List<Token> list) {
        LinkedList<Token> token = new LinkedList<>(list);
        Stack<Integer> status = new Stack<>();
        Stack<Token> symbol = new Stack<>();

        status.push(0);
        symbol.push(new Token("#", "#", 0, 0));
        token.addLast(new Token("#", "#", 0, 0));

        while (true) {
            Integer topStatus = status.peek();
            Token firstToken = token.peekFirst();

            if (terminal.contains(firstToken.getName())) {
                String info = action[topStatus + 1][terminal.indexOf(firstToken.getName()) + 1];
                if (info != null) {
                    if ("acc".equals(info)) {
                        System.out.println("语法分析通过");
                        return true;
                    } else if ("s".equals(info.substring(0, 1))) {
                        int newStatus = Integer.parseInt(info.substring(1));
                        status.push(newStatus);
                        symbol.push(token.pollFirst());
                    } else if ("r".equals(info.substring(0, 1))) {

                        int index = Integer.parseInt(info.substring(1));

                        String s = production.get(index);
                        String[] array = s.split("->");

                        String newSymbol = array[0];

                        String[] array1 = array[1].split(" ");
                        for (int i = 0; i < array1.length; i++) {
                            status.pop();
                        }

                        String info1 = GOTO[status.peek() + 1][nonTerminal.indexOf(newSymbol) + 1];
                        if (info1 == null) {
                            System.out.println("goto查表为空 坐标：(" + (status.peek() + 1) + ", " + (nonTerminal.indexOf(newSymbol) + 1) + ")");
                            return false;
                        }
                        int newStatus = Integer.parseInt(GOTO[status.peek() + 1][nonTerminal.indexOf(newSymbol) + 1]);
                        status.push(newStatus);
                        symbol.push(new Token(newSymbol, newSymbol, 0, 0));
                    } else {
                        System.out.println("错误");
                        return false;
                    }
                } else {
                    System.out.println("action查表为空 坐标：(" + (topStatus + 1) + ", " + (terminal.indexOf(firstToken.getName()) + 1) + ")");
                    return false;
                }
            } else {
                System.out.println("错误");
                return false;
            }
        }
    }
}
