package top.coolzhang.simplecompiler.algorithm.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class operatorFirst {
    /**
     * 存放终结符
     */
    private static HashSet<String> terminal;

    /**
     * 存放文法
     */
    private static Node[] grammar;

    /**
     * firstVT 集合
     */
    private static HashMap<String, LinkedHashSet<String>> firstVT = new HashMap<>();

    /**
     * lastVT 集合
     */
    private static HashMap<String, LinkedHashSet<String>> lastVT = new HashMap<>();

    /**
     * 算符优先表
     */
    private static String[][] table;

    private static HashSet<String> changeSet = new HashSet<>();
    private static boolean flag = true;

    private static HashSet<String> set = new HashSet<>();

    static {
        // 初始化终结符
        HashSet<String> set = new HashSet<>();
        try {
            URL url = operatorFirst.class.getClassLoader().getResource("terminalSymbol.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    set.add(line);
                }
            }
            terminal = set;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化文法
        // 读取文法文件
        try {
            URL url = operatorFirst.class.getClassLoader().getResource("grammar.txt");
            File file = new File(url.getFile());
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    stringBuilder.append(line).append("\n");
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            // 将文法转换为链表
            String[] strings = stringBuilder.toString().split("\n");
            Node[] nodes = new Node[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String[] ss = strings[i].split("->");
                Node node = new Node(ss[0], null);
                nodes[i] = node;
                String[] sss = ss[1].split(" \\$ ");
                Node p = node;
                for (int j = 0; j < sss.length; j++) {
                    p.next = new Node(sss[j], null);
                    p = p.next;
                }
            }
            grammar = nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFLVT();
        initTable();
    }

    private static boolean addAndJudge(LinkedHashSet<String> set, String RS, String LS) {
        int before = set.size();
        set.add(RS);

        if (changeSet.contains(LS) && flag) {
            int after = set.size();
            if (before != after) {
                flag = false;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean addAllAndJudge(LinkedHashSet<String> set, LinkedHashSet<String> temp, String LS) {
        int before = set.size();
        set.addAll(temp);

        if (changeSet.contains(LS) && flag) {
            int after = set.size();
            if (before != after) {
                flag = false;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 计算文法中每个非终结符的 firstVT 与 lastVT 集合
     */
    private static void initFLVT() {
        if (grammar == null || terminal == null) {
            return;
        }

        // 初始化 firstVT 与 lastVT
        for (Node head : grammar) {
            String LS = head.s;
            firstVT.put(LS, new LinkedHashSet<>());
            lastVT.put(LS, new LinkedHashSet<>());
        }

        // 用于判断循环遍历过程中 firstVT 与 lastVT 是否有变化
        boolean status = true;

        // 循环遍历文法求 firstVT
        while (status) {
            status = false;
            // 遍历文法
            for (Node head : grammar) {
                flag = true;
                Node node = head;
                String LS = head.s; // 产生式左边非终结符

                // 求 firstVT
                while (node.next != null) {
                    node = node.next;
                    String RS = node.s; // 产生式右部字符串
                    LinkedHashSet<String> set = firstVT.get(LS);

                    String[] array = RS.split(" ");
                    if (array.length == 1) {
                        if (terminal.contains(array[0])) {
                            if (addAndJudge(set, array[0], LS)) {
                                status = true;
                            }
                        } else {
                            LinkedHashSet<String> temp = firstVT.get(array[0]);
                            if (addAllAndJudge(set, temp, LS)) {
                                status = true;
                            }
                            changeSet.add(array[0]);
                        }
                    } else if (array.length > 1) {
                        if (terminal.contains(array[0])) {
                            if (addAndJudge(set, array[0], LS)) {
                                status = true;
                            }
                        } else if (terminal.contains(array[1])) {
                            if (addAndJudge(set, array[1], LS)) {
                                status = true;
                            }
                        }
                    }
                }
            }
        }

        status = true;
        changeSet.clear();
        // 循环遍历文法求 lastVT
        while (status) {
            status = false;
            // 遍历文法
            for (Node head : grammar) {
                flag = true;
                Node node = head;
                String LS = head.s; // 产生式左边非终结符

                // 求 lastVT
                while (node.next != null) {
                    node = node.next;
                    String RS = node.s; // 产生式右部字符串
                    LinkedHashSet<String> set = lastVT.get(LS);

                    String[] array = RS.split(" ");
                    if (array.length == 1) {
                        if (terminal.contains(array[0])) {
                            if (addAndJudge(set, array[0], LS)) {
                                status = true;
                            }
                        } else {
                            LinkedHashSet<String> temp = lastVT.get(array[0]);
                            if (addAllAndJudge(set, temp, LS)) {
                                status = true;
                            }
                            changeSet.add(array[0]);
                        }
                    } else if (array.length > 1) {
                        int len = array.length;
                        if (terminal.contains(array[len - 1])) {
                            if (addAndJudge(set, array[len - 1], LS)) {
                                status = true;
                            }
                        } else if (terminal.contains(array[len - 2])) {
                            set.add(array[len - 2]);
                            if (addAndJudge(set, array[len - 2], LS)) {
                                status = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void initTable() {
        if (grammar == null || terminal == null) {
            return;
        }

        int size = terminal.size() + 1;
        table = new String[size][size];

        // 初始化
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                table[i][j] = null;
            }
        }
        Iterator<String> iterator = terminal.iterator();
        int flag = 1;
        while (iterator.hasNext()) {
            String value = iterator.next();
            table[0][flag] = value;
            table[flag][0] = value;
            flag++;
        }

        // 生成算符优先表
        for (Node head : grammar) {
            Node node = head;
            String s = node.s;

            while (node.next != null) {
                node = node.next;
                String ss = node.s;

                String[] array = ss.split(" ");
                if (containsT(array) && array.length > 1) {
                    for (int j = 1; j < array.length; j++) {
                        String c1 = array[j - 1];
                        String c2 = array[j];
                        if (terminal.contains(c1) && !terminal.contains(c2)) {
                            LinkedHashSet<String> first = firstVT.get(c2);
                            for (String sss : first) {
                                if (table[getIndex(c1)][getIndex(sss)] == null || "<".equals(table[getIndex(c1)][getIndex(sss)])) {
                                    table[getIndex(c1)][getIndex(sss)] = "<";
                                } else {
                                    System.out.println("文法不是算符优先文法 " + c1 + " " + sss);
                                }
                            }
                        } else if (!terminal.contains(c1) && terminal.contains(c2)) {
                            LinkedHashSet<String> last = lastVT.get(c1);
                            for (String sss : last) {
                                if (table[getIndex(sss)][getIndex(c2)] == null || ">".equals(table[getIndex(sss)][getIndex(c2)])) {
                                    table[getIndex(sss)][getIndex(c2)] = ">";
                                } else {
                                    System.out.println("文法不是算符优先文法 " + sss + " " + c2);
                                }
                            }
                        } else if (terminal.contains(c1) && terminal.contains(c2)) {
                            if (table[getIndex(c1)][getIndex(c2)] == null || "=".equals(table[getIndex(c1)][getIndex(c2)])) {
                                table[getIndex(c1)][getIndex(c2)] = "=";
                            } else {
                                System.out.println("文法不是算符优先文法 " + c1 + " " + c2);
                            }
                        }
                    }
                    if (array.length > 2) {
                        for (int j = 1; j < array.length - 1; j++) {
                            String c1 = array[j - 1];
                            String c2 = array[j];
                            String c3 = array[j + 1];
                            if (terminal.contains(c1) && !terminal.contains(c2) && terminal.contains(c3)) {
                                if (table[getIndex(c1)][getIndex(c3)] == null || "=".equals(table[getIndex(c1)][getIndex(c3)])) {
                                    table[getIndex(c1)][getIndex(c3)] = "=";
                                } else {
                                    System.out.println("文法不是算符优先文法 " + c1 + " " + c3);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private static boolean containsT(String[] array) {
        for (String s : array) {
            if (terminal.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static int getIndex(String s) {
        for (int i = 0; i < table.length; i++) {
            if (s.equals(table[0][i])) {
                return i;
            }
        }
        return 0;
    }

    private static void judge1(LinkedList<String> token) {
        LinkedList<String> stack = new LinkedList<>();
        stack.addFirst("#");
        token.addLast("#");
        int k = 0, j;

        while (true) {
            String a = token.pollFirst();
            if (terminal.contains(stack.get(k))) {
                j = k;
            } else {
                j = k - 1;
            }

            while (">".equals(table[getIndex(stack.get(j))][getIndex(a)])) {
                while (true) {
                    String Q = stack.get(j);
                    if (terminal.contains(stack.get(j - 1))) {
                        j = j - 1;
                    } else {
                        j = j - 2;
                    }

                    if ("<".equals(table[getIndex(stack.get(j))][getIndex(Q)])) {
                        break;
                    }
                }

                // 规约
                LinkedList<String> list = new LinkedList<>(stack.subList(j + 1, k + 1));
                String result = gui(list);
                if (result != null) {
                    for (int i = j; i < k; i++) {
                        stack.pollLast();
                    }
                    k = k - (k - j - 1);
                    stack.addLast(result);
                }
            }

            if ("<".equals(table[getIndex(stack.get(j))][getIndex(a)]) || "=".equals(table[getIndex(stack.get(j))][getIndex(a)])) {
                k++;
                stack.addLast(a);
            }

            if ("#".equals(a)) {
                break;
            }
        }
        for (String string : stack) {
            System.out.println(string);
        }
    }

    private static void judge(LinkedList<String> token) {

        Stack<String> stack = new Stack<>();
        stack.push("#");
        token.addLast("#");

        LinkedList<String> flag = new LinkedList<>();

        while (!stack.isEmpty()) {
            if (stack.size() == 1 && stack.peek().equals("EXPRESSION")) {
                System.out.println("正确的表达式");
                break;
            }
            if (terminal.contains(stack.peek())) {
                if (token.size() > 0) {
                    if (">".equals(table[getIndex(stack.peek())][getIndex(token.getFirst())])) {
                        // 规约
                        if (flag.size() != 0) {
                            while (flag.size() > 0) {
                                stack.push(flag.pollFirst());
                            }
                        }

                        LinkedList<String> temp = new LinkedList<>();
                        temp.addFirst(stack.pop());
                        String result;
                        while ((result = gui(temp)) == null) {
                            if (stack.isEmpty()) {
                                break;
                            }
                            temp.addFirst(stack.pop());
                        }
                        if (result == null) {
                            System.out.println("错误的表达式");
                            break;
                        } else {
                            stack.push(result);
                        }
                    } else {
                        // 移进
                        if (flag.size() != 0) {
                            while (flag.size() > 0) {
                                stack.push(flag.pollFirst());
                            }
                        }

                        stack.push(token.pollFirst());
                    }
                } else {
                    if (flag.size() != 0) {
                        while (flag.size() > 0) {
                            stack.push(flag.pollFirst());
                        }
                    }

                    LinkedList<String> temp = new LinkedList<>();
                    temp.addFirst(stack.pop());
                    String result;
                    while ((result = gui(temp)) == null) {
                        if (stack.isEmpty()) {
                            break;
                        }
                        temp.addFirst(stack.pop());
                    }
                    if (result == null) {
                        System.out.println("错误的算术表达式");
                        break;
                    } else {
                        stack.push(result);
                    }
                }
            } else {
                flag.addFirst(stack.pop());
            }
        }
    }

    private static String gui(LinkedList<String> list) {

        boolean status;

        for (Node node : grammar) {
            Node head = node;
            String s = node.s;

            while (node.next != null) {
                node = node.next;
                String[] ss = node.s.split(" ");
                status = true;
                if (ss.length == list.size()) {
                    for (int i = 0; i < ss.length; i++) {
                        if (terminal.contains(ss[i])) {
                            if (terminal.contains(list.get(i))) {
                                if (ss[i].contains(list.get(i))) {
                                } else {
                                    status = false;
                                    break;
                                }
                            } else {
                                status = false;
                                break;
                            }
                        } else {
                            if (!terminal.contains(list.get(i))) {
                            } else {
                                status = false;
                                break;
                            }
                        }
                    }
                    if (list.size() == 1) {
                        if (getIndex(list.get(0)) <= getIndex(s)) {
                            status = false;
                        }
                    }
                    if (status) {
                        return s;
                    }
                }
            }
            set.add(s);
        }

        return null;
    }

    public static void main(String[] args) {
//        String ss = "identifier + integer * ( decimal + integer ) / identifier";
//        String ss = "integer + integer > decimal - decimal";
//        String ss = "integer + integer > decimal - decimal ;";
//        String[] split = ss.split(" ");
//
//        LinkedList<String> token = new LinkedList<>(Arrays.asList(split));
//        judge1(token);
//        judge(token);
    }
}
