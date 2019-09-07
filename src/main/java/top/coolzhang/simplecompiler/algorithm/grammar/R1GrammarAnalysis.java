package top.coolzhang.simplecompiler.algorithm.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class R1GrammarAnalysis {
    // 临时变量计数器
    private static int count = 0;

    // 四元式编号
    private static int num = 1;
    private static int flag1 = 0;

    // 存放四元式
    private static ArrayList<ArrayList<String>> quaternary = new ArrayList<>();

    // 假出口链
    private static ArrayList<ArrayList<String>> FC = new ArrayList<>();
    private static ArrayList<ArrayList<String>> FC1 = new ArrayList<>();

    // 保存终结符
    private static ArrayList<String> terminal = new ArrayList<>();

    // 保存非终结符
    private static ArrayList<String> nonTerminal = new ArrayList<>();

    // 保存产生式
    private static ArrayList<String> production = new ArrayList<>();

    // 保存产生式属性
    private static HashMap<String, Stack<String>> attribute = new LinkedHashMap<>();

    // 保存 action 表
    private static String[][] action;

    // 保存 GOTO 表
    private static String[][] GOTO;

    static {
        initTerminalSymbol();
        initNonTerminalSymbol();
        initProduction();
        initAction();
        initGOTO();
//        initError();
    }

    // 初始化终结符
    private static void initTerminalSymbol() {
        try {
            URL url = R1GrammarAnalysis.class.getClassLoader().getResource("terminalSymbol.txt");
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
            URL url = R1GrammarAnalysis.class.getClassLoader().getResource("nonTerminalSymbol.txt");
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
            URL url = R1GrammarAnalysis.class.getClassLoader().getResource("production.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    production.add(line);
                    attribute.put(line.split("->")[0], new Stack<>());
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
            URL url = R1GrammarAnalysis.class.getClassLoader().getResource("action.txt");
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
            URL url = R1GrammarAnalysis.class.getClassLoader().getResource("goto.txt");
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

    // 初始化错误处理
    private static void initError() {
        // 变量后缺少运算符
        action[38][6] = "error0";
        action[98][15] = "error0";
        action[19][6] = "error0";
        action[67][6] = "error0";
        action[305][6] = "error0";

        // 运算符缺少操作数
        action[1][33] = "error1";
        action[6][33] = "error1";
        action[90][33] = "error1";
        action[79][31] = "error1";
        action[307][35] = "error1";
        action[788][35] = "error1";
        action[517][33] = "error1";
        action[40][17] = "error2";
        action[86][29] = "error2";

        // 缺少分号
        action[101][13] = "error3";
        action[202][13] = "error3";
        action[202][15] = "error3";
        action[101][34] = "error3";
        action[202][34] = "error3";
        action[101][17] = "error3";
        action[202][17] = "error3";
        action[101][5] = "error3";
        action[1137][34] = "error3";

        // 缺少左括号
        action[28][15] = "error4";
        action[33][15] = "error4";
        action[173][15] = "error4";
        action[512][15] = "error4";

        // 缺少右括号
        action[673][30] = "error5";
        action[279][30] = "error5";
        action[279][29] = "error5";

        // 缺少左大括号
        action[1462][26] = "error6";
        action[319][9] = "error6";
        action[786][26] = "error6";
        action[78][7] = "error6";
        action[177][26] = "error6";

        // 缺少右大括号
        action[306][17] = "error7";
        action[306][12] = "error7";
        action[1067][12] = "error7";
    }

    // 生成临时变量符号
    private static String newTemp() {
        String result = "T" + count;
        count++;
        return result;
    }

    // 添加四元式
    private static void addQuaternary(String s1, String s2, String s3, String s4) {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(s1);
        temp.add(s2);
        temp.add(s3);
        temp.add(s4);
        quaternary.add(temp);
        if ("j".equals(s1)) {
            FC.add(temp);
        }
    }

    public void judge(List<String> list, List<String> list1) {
        LinkedList<String> token = new LinkedList<>(list);
        LinkedList<String> word = new LinkedList<>(list1);
        Stack<Integer> status = new Stack<>();
        Stack<String> symbol = new Stack<>();
        status.push(0);
        symbol.push("#");
        token.addLast("#");

        int flag = 0;
        while (true) {
            Integer topStatus = status.peek();
            String firstToken = token.peekFirst();

            if (terminal.contains(firstToken)) {
                String info = action[topStatus + 1][terminal.indexOf(firstToken) + 1];
                if (info != null) {
                    if ("acc".equals(info)) {
                        System.out.println("语法分析通过");
                        int a = 1;
                        for (ArrayList<String> li : quaternary) {
                            StringBuilder builder = new StringBuilder();
                            builder.append(a).append(" (");
                            for (String s : li) {
                                builder.append(s).append(", ");
                            }
                            builder.delete(builder.length()-2, builder.length());
                            builder.append(")");
                            System.out.println(builder.toString());
                            a++;
                        }
                        break;
                    } else if ("s".equals(info.substring(0, 1))) {
                        int newStatus = Integer.parseInt(info.substring(1));
                        status.push(newStatus);
                        symbol.push(token.pollFirst());
                    } else if ("r".equals(info.substring(0, 1))) {
                        int index = Integer.parseInt(info.substring(1));

                        String s = production.get(index);
                        String[] array = s.split("->");

                        String newSymbol = array[0];

                        String[] split = array[1].split(" @ ");
                        String[] array1 = split[0].split(" ");

                        if (split.length > 1) {
                            try {
                                String[] split1 = split[1].split(" ");
                                if ("E".equals(split1[0])) {
                                    attribute.get(array[0]).push(word.get(word.size() - token.size()));
                                } else if ("N".equals(split1[0])) {
                                    attribute.get(array[0]).push(newTemp());
                                } else if ("P0".equals(split1[0])) {
                                    if (terminal.contains(array1[0])) {
                                        attribute.get(array[0]).push(word.get(word.size() - token.size()));
                                    } else {
                                        attribute.get(array[0]).push(attribute.get(symbol.peek()).pop());
                                    }
                                } else if ("P1".equals(split1[0])) {
                                    if (terminal.contains(array1[1])) {
                                        attribute.get(array[0]).push(word.get(word.size() - token.size()));
                                    } else {
                                        attribute.get(array[0]).push(attribute.get(array1[1]).pop());
                                    }
                                }

                                if ("G1".equals(split1[1])) {
                                    System.out.println("(" + array1[0] + ", " + attribute.get(array1[1]) + ", , " + attribute.get(array[0]) + ")");
                                } else if ("G2".equals(split1[1])) {
                                    String op1 = attribute.get(array1[0]).pop();
                                    String op2 = attribute.get(array1[2]).pop();
                                    if (terminal.contains(array1[1])) {
                                        if ("=".equals(array1[1])) {
                                            addQuaternary(array1[1], op1, " ", op2);
                                            num++;
                                        } else {
                                            addQuaternary(array1[1], op2, op1, attribute.get(array[0]).peek());
                                            num++;
                                        }
                                    } else {
                                        addQuaternary(attribute.get(array1[1]).pop(), op2, op1, attribute.get(array[0]).peek());
                                        num++;
                                    }
                                } else if ("G3".equals(split1[1])) {
                                    String op1 = attribute.get(array1[0]).pop();
                                    String op2 = attribute.get(array1[2]).pop();
                                    String op = attribute.get(array1[1]).pop();

                                    addQuaternary("j" + op, op2, op1, (num + 2) + "");
                                    num++;
                                    addQuaternary("j", " ", " ", flag + "");
                                    flag = num;
                                    num++;


                                } else if ("G4".equals(split1[1])) {
                                    String op1 = attribute.get(array1[0]).pop();
                                    String op2 = attribute.get(array1[2]).pop();

                                    addQuaternary("jnz", op2, " ", (num + 2) + "");
                                    num++;
                                    addQuaternary("j", " ", " ", flag + "");
                                    flag = num;
                                    num++;
                                    addQuaternary("jnz", op1, " ", (num + 2) + "");
                                    num++;
                                    addQuaternary("j", " ", " ", flag + "");
                                    flag = num;
                                    num++;
                                } else if ("G5".equals(split1[1])) {
                                    for (ArrayList<String> li : FC) {
                                        li.remove(li.size() - 1);
                                        li.add(num + "");
                                    }
                                    FC.clear();
                                } else if ("G6".equals(split1[1])) {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add("j");
                                    temp.add(" ");
                                    temp.add(" ");
                                    temp.add("0");
                                    quaternary.add(temp);
                                    for (ArrayList<String> li : quaternary) {
                                        if ((num + "").equals(li.get(li.size()-1))) {
                                            li.remove(li.size() - 1);
                                            li.add((num + 1) + "");
                                        }
                                    }
                                    FC1.add(temp);
                                } else if ("G7".equals(split1[1])) {
                                    for (ArrayList<String> li : FC1) {
                                        li.remove(li.size() - 1);
                                        li.add((num + 1) + "");
                                    }
                                    FC1.clear();
                                } else if ("G8".equals(split1[1])) {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add("j");
                                    temp.add(" ");
                                    temp.add(" ");
                                    temp.add(flag1 + "");
                                    quaternary.add(temp);
                                    for (ArrayList<String> li : FC) {
                                        li.remove(li.size() - 1);
                                        li.add((num + 1) + "");
                                    }
                                    FC.clear();
                                } else if ("G9".equals(split1[1])) {
                                    flag1 = num;
                                }
                            }catch (Exception e) {}
                        }

                        for (int i = 0; i < array1.length; i++) {
                            status.pop();
                            symbol.pop();
                        }

                        String info1 = GOTO[status.peek() + 1][nonTerminal.indexOf(newSymbol) + 1];
                        if (info1 == null) {
                            System.out.println("goto查表为空 坐标：(" + (status.peek() + 1) + ", " + (nonTerminal.indexOf(newSymbol) + 1) + ")");
                            break;
                        }
                        int newStatus = Integer.parseInt(GOTO[status.peek() + 1][nonTerminal.indexOf(newSymbol) + 1]);
                        status.push(newStatus);
                        symbol.push(newSymbol);
                    } else {
//                        error(info, word, token);
                        System.out.println("错误");
                        break;
                    }
                } else {
                    System.out.println("action查表为空 坐标：(" + (topStatus + 1) + ", " + (terminal.indexOf(firstToken) + 1) + ")");
                    break;
                }
            } else {
                System.out.println("错误");
                break;
            }
        }
    }

    private void error(String status, LinkedList<String> word, LinkedList<String> token) {
        switch (status) {
            case "error0":
                System.out.println("变量 " + (word.get(word.size() - token.size())) + " 后缺少运算符");
                break;
            case "error1":
                System.out.println("运算符 " + (word.get(word.size() - token.size() + 1)) + " 前缺少操作数");
                break;
            case "error2":
                System.out.println("运算符 " + (word.get(word.size() - token.size() - 1)) + " 后缺少操作数");
                break;
            case "error3":
                System.out.println((word.get(word.size() - token.size())) + " 后缺少分号");
                break;
            case "error4":
                System.out.println((word.get(word.size() - token.size())) + " 后缺少左括号");
                break;
            case "error5":
                System.out.println((word.get(word.size() - token.size())) + " 后缺少右括号");
                break;
            case "error6":
                System.out.println((word.get(word.size() - token.size())) + " 后缺少左大括号");
                break;
            case "error7":
                System.out.println((word.get(word.size() - token.size())) + " 后缺少右大括号");
                break;
            default:
                System.out.println();
        }
    }

}
