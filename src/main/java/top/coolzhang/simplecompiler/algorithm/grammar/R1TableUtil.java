package top.coolzhang.simplecompiler.algorithm.grammar;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.*;

public class R1TableUtil {
    /**
     * 存放终结符
     */
    private HashSet<String> terminal = new HashSet<>();

    /**
     * 存放非终结符
     */
    private HashSet<String> nonTerminal = new HashSet<>();

    /**
     * 存放文法
     */
    private GrammarNode[] grammar;
    private ArrayList<GrammarNode> grammar1 = new ArrayList<>();

    /**
     * 存放项目集
     */
    private ArrayList<ProjectSetNode> projectSet = new ArrayList<>();

    /**
     * 存放 First 集合
     */
    private HashMap<String, LinkedHashSet<String>> first = new HashMap<>();

    /**
     * 存放 DFA 集合
     */
    private ArrayList<Status> DFA = new ArrayList<>();

    /**
     * 存放 DFA 的边
     */
    private ArrayList<Edge> edges = new ArrayList<>();

    /**
     * 存放 Action 表
     */
    private String[][] action;

    /**
     * 存放 Goto表
     */
    private String[][] GOTO;

    /**
     * 初始化文法，读取文法文件，转为链表
     */
    private void initGrammar() throws IOException {
        // 加载文法文件并转换为字符串
        URL url = R1TableUtil.class.getClassLoader().getResource("grammar.txt");
        assert url != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(url.getFile()))));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.length() > 0) {
                stringBuilder.append(line).append("\n");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        // 将文法字符串转存为链表
        String[] lines = stringBuilder.toString().split("\n");
        GrammarNode[] nodes = new GrammarNode[lines.length];
        for (int i = 0; i < nodes.length; i++) {
            String[] LR = lines[i].split("->");
            GrammarNode node = new GrammarNode(LR[0]);
            nodes[i] = node;
            String[] RR = LR[1].split(" \\$ ");
            GrammarNode p = node;
            for (String s : RR) {
                p.setNext(new GrammarNode(s));
                p = p.getNext();
            }

            String LS = LR[0];

            while (node.getNext() != null) {
                node = node.getNext();
                String RS = node.getValue();
                GrammarNode node2 = new GrammarNode(RS);
                GrammarNode node1 = new GrammarNode(LS);
                node1.setNext(node2);
                grammar1.add(node1);
            }
        }
        grammar = nodes;
        br.close();
    }

    /**
     * 初始化终结符与非终结符集合
     */
    private void initTerminalandNonTerminal() {
        // 初始化非终结符集合（产生式左边均为非终结符）
        for (GrammarNode head : grammar) {
            nonTerminal.add(head.getValue());
        }

        // 初始化终结符集合（只出现在产生式右边的均为终结符）
        for (GrammarNode head : grammar) {
            GrammarNode p = head;
            while (p.getNext() != null) {
                p = p.getNext();
                String s = p.getValue();
                String[] symbols = s.split(" ");
                for (String symbol : symbols) {
                    if (!nonTerminal.contains(symbol)) {
                        terminal.add(symbol);
                    }
                }
            }
        }
        terminal.add("#");
    }

    /**
     * 计算 First 集合
     */
    private void getFirst() {
        // 初始化 first 集合
        for (GrammarNode head : grammar) {
            first.put(head.getValue(), new LinkedHashSet<>());
        }

        // 辅助变量，标记 first 集合是否发生改变
        boolean isChange = true;

        // 循环遍历文法计算 first 集合，直至 first 集合不在改变
        while (isChange) {
            isChange = false;

            for (GrammarNode head : grammar) {
                String LS = head.getValue();
                GrammarNode p = head;

                while (p.getNext() != null) {
                    p = p.getNext();
                    LinkedHashSet<String> set = first.get(LS);
                    String RS = p.getValue();
                    String[] symbols = RS.split(" ");
                    if (terminal.contains(symbols[0])) {
                        int size = set.size();
                        set.add(symbols[0]);
                        if (size != set.size()) {
                            isChange = true;
                        }
                    } else {
                        if (first.get(symbols[0]).size() != 0) {
                            int size = set.size();
                            set.addAll(first.get(symbols[0]));
                            if (size != set.size()) {
                                isChange = true;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化项目集
     */
    private void initProjectSet() {
        for (GrammarNode head : grammar) {
            String LS = head.getValue();
            GrammarNode p = head;

            LinkedList<String> list = new LinkedList<>();
            list.add(LS);
            ProjectSetNode node = new ProjectSetNode(list);
            ProjectSetNode q = node;

            while (p.getNext() != null) {
                p = p.getNext();
                String RS = p.getValue();
                String[] symbols = RS.split(" ");
                LinkedList<String> list1 = new LinkedList<>(Arrays.asList(symbols));

                for (int i = 0; i <= list1.size(); i++) {
                    LinkedList<String> temp = new LinkedList<>(list1);
                    temp.add(i, ".");
                    ProjectSetNode node1 = new ProjectSetNode(temp);
                    q.setNext(node1);
                    q = q.getNext();
                }
            }
            projectSet.add(node);
        }
    }

    private void merge(Status status) {

        int i = 1;
        while (i < status.list.size() - 1) {
            HashMap<String, LinkedList<String>> map = status.list.get(i);
            String key = "";
            for (String s : map.keySet()) {
                key = s;
            }
            LinkedList<String> list = map.get(key);
            List<String> list1 = list.subList(0, list.indexOf("$"));
            for (int j = i + 1; j < status.list.size(); j++) {
                HashMap<String, LinkedList<String>> map1 = status.list.get(j);
                String key1 = "";
                for (String s : map1.keySet()) {
                    key1 = s;
                }
                if (key.equals(key1)) {
                    LinkedList<String> list2 = map1.get(key1);
                    List<String> list3 = list2.subList(0, list2.indexOf("$"));
                    List<String> list4 = list2.subList(list2.indexOf("$") + 1, list2.size());
                    if (isEquals(list1, list3)) {
                        list.addAll(list4);
                        status.list.remove(map1);
                        list1 = list.subList(0, list.indexOf("$"));
                        j--;
                    }
                }
            }

            i++;
        }
    }

    private boolean isEquals(List<String> list, List<String> list1) {
        if (list.size() != list1.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals(list1.get(i))) {
                return false;
            }
        }
        return true;
    }

    private LinkedList<String> calFirst(LinkedList<String> list) {
        LinkedList<String> result = new LinkedList<>();
        if (list.size() > 0) {
            String s = list.get(0);
            if (terminal.contains(s)) {
                result.add(s);
            } else {
                result.addAll(first.get(s));
            }
        }
        return result;
    }

    private int indexOfDFA(Status status) {
        for (int i = 0; i < DFA.size(); i++) {
            if (DFA.get(i).compareTo(status) > 0) {
                return i;
            }
        }
        return -1;
    }

    private boolean containsStatus(Status status) {
        if (DFA.size() <= 0) {
            return false;
        }
        for (Status s : DFA) {
            if (status.compareTo(s) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isMapEquals(HashMap<String, LinkedList<String>> map1, HashMap<String, LinkedList<String>> map2) {
        String key1 = "";
        String key2 = "";
        Iterator<String> iterator1 = map1.keySet().iterator();
        Iterator<String> iterator2 = map2.keySet().iterator();
        while (iterator1.hasNext()) {
            key1 = iterator1.next();
        }
        while (iterator2.hasNext()) {
            key2 = iterator2.next();
        }

        if (key1.equals(key2)) {
            LinkedList<String> list1 = map1.get(key1);
            LinkedList<String> list2 = map2.get(key2);

            if (isEquals(list1, list2)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isContainsProject(Status status, HashMap<String, LinkedList<String>> map) {
        for (HashMap<String, LinkedList<String>> map1 : status.list) {
            if (isMapEquals(map, map1)) {
                return true;
            }
        }
        return false;
    }

    private int indexOfGrammar1(List<String> list) {
        for (int i = 0; i < grammar1.size(); i++) {
            String RS = grammar1.get(i).getNext().getValue();

            List<String> list1 = Arrays.asList(RS.split(" "));
            if (isEquals(list, list1)) {
                return i;
            }
        }
        return -1;
    }

    private void closure(Status status, String key, HashMap<String, LinkedList<String>> map, LinkedList<String> forward) {
        LinkedList<String> list1 = map.get(key); // 产生式左部列表
        int index = list1.indexOf(".");
        if (index != list1.size() - 1) {
            index++;
            String LS = list1.get(index); // 得到 . 后面的符号
            List<String> flag = list1.subList(list1.indexOf("$") + 1, list1.size());
            HashSet<String> hashSet = new HashSet<>(flag);
            list1.removeAll(hashSet);
            hashSet.addAll(forward);
            list1.addAll(hashSet);

            if (!terminal.contains(LS)) {
                for (ProjectSetNode node : projectSet) { // 遍历项目集
                    String LS1 = node.getList().get(0); // 得到项目集左部非终结符
                    if (LS.equals(LS1)) {
                        ProjectSetNode node1 = node;
                        while (node1.getNext() != null) {
                            node1 = node1.getNext();
                            LinkedList<String> list = node1.getList();

                            if (list.indexOf(".") == 0) {
                                LinkedList<String> first1 = new LinkedList<>();
                                if ((index + 1) < list1.indexOf("$")) {
                                    LinkedList<String> temp = new LinkedList<>(list1.subList(index + 1, list1.indexOf("$")));
                                    first1 = calFirst(temp);
                                } else if ((index + 1) == list1.indexOf("$")) {
                                    first1 = new LinkedList<>(list1.subList(list1.indexOf("$") + 1, list1.size()));
                                }

                                LinkedList<String> cloneList = (LinkedList<String>) list.clone();
                                HashMap<String, LinkedList<String>> map1 = new HashMap<>();
                                cloneList.add("$");
                                cloneList.addAll(first1);
                                map1.put(LS, cloneList);
                                if (!isContainsProject(status, map1)) {

                                    status.list.add(map1);
                                    if (cloneList.indexOf(".") + 1 < cloneList.indexOf("$") && !terminal.contains(cloneList.get(cloneList.indexOf(".") + 1))) {

                                        LinkedList<String> list2 = new LinkedList<>(cloneList.subList(0, cloneList.indexOf("$") + 1));
                                        HashMap<String, LinkedList<String>> h = new HashMap<>();
                                        h.put(LS, list2);
                                        closure(status, LS, h, first1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Status containsEdge(Status status, String s) {
        Edge head = status.next;
        while (head != null) {
            if (s.equals(head.s)) {
                if (DFA.contains(head.to)) {
                    return head.to;
                }
            }
            head = head.next;
        }
        return null;
    }

    private void createDFA() {
        // 初始化
        ProjectSetNode init_node = projectSet.get(0);
        LinkedList<String> init_list = init_node.getList();
        LinkedList<String> init_list1 = init_node.getNext().getList();
        init_list1.add("$");
        LinkedList<String> init_forward = new LinkedList<>();
        init_forward.add("#");
        HashMap<String, LinkedList<String>> init_map = new HashMap<>();
        init_map.put(init_list.get(0), init_node.getNext().getList());
        Status init_status = new Status(init_map, null);
        closure(init_status, init_list.get(0), init_map, init_forward);
        merge(init_status);
        if (!containsStatus(init_status)) {
            DFA.add(init_status);
            System.out.println("DFA增加:" + DFA.size());
        }

        // 构造 DFA
        int i = 0;

        while (true) {
            if (i == DFA.size()) {
                break;
            }

            Status status = DFA.get(i);
            Edge head = status.next;
            LinkedList<HashMap<String, LinkedList<String>>> list = status.list;
            for (int k = 0; k < list.size(); k++) {
                HashMap<String, LinkedList<String>> map = list.get(k);
                Iterator<String> iterator = map.keySet().iterator();
                String key = "";
                while (iterator.hasNext()) {
                    key = iterator.next();
                }

                LinkedList<String> list1 = (LinkedList<String>) map.get(key).clone();
                LinkedList<String> forward = new LinkedList<>(list1.subList(list1.indexOf("$") + 1, list1.size()));
                int index = list1.indexOf(".");
                if (index < list1.indexOf("$") - 1) {
                    int index1 = index + 1;
                    String s = list1.get(index1);
                    list1.set(index, s);
                    list1.set(index1, ".");
                    LinkedList<String> list2 = new LinkedList<>(list1.subList(0, list1.indexOf("$") + 1));

                    HashMap<String, LinkedList<String>> map1 = new HashMap<>();
                    map1.put(key, list2);

                    Status result = containsEdge(status, s);
                    if (result == null) {
                        Status status1 = new Status(map1, null);
                        closure(status1, key, map1, forward);
                        merge(status1);
                        if (!containsStatus(status1)) {
                            DFA.add(status1);
                            System.out.println("DFA增加:" + DFA.size());
                            Edge edge = new Edge(status1, s, head);
                            edges.add(edge);
                            status.next = edge;
                            head = edge;
                        } else {
                            Edge edge = new Edge(status1, s, head);
                            edges.add(edge);
                            status.next = edge;
                            head = edge;
                        }
                    } else {
                        Status status1 = result;
                        int iii = indexOfDFA(status1);
                        status1.list.add(map1);
                        closure(status1, key, map1, forward);
                        merge(status1);
                        DFA.remove(result);

                        if (containsStatus(status1)) {


                            int index2 = indexOfDFA(status1);
                            Status status2 = DFA.get(index2);

                            boolean b = false;
                            Edge edge = status.next;
                            while (edge != null) {
                                if (edge.to.compareTo(status2) > 0) {
                                    b = true;
                                    break;
                                }
                                edge = edge.next;
                            }

                            if (!b) {
                                List<Edge> list3 = new ArrayList<>();
                                edge = status.next;
                                while (edge != null) {
                                    if (edge.to.compareTo(status1) > 0) {
                                        list3.add(edge);
                                    }
                                    edge = edge.next;
                                }
                                for (Edge edge1 : list3) {
                                    edge1.to = status2;
                                }
                            }
                            System.out.println("DFA减少:" + DFA.size());

                        } else {
                            DFA.add(iii, status1);
                        }
                    }
                }
            }

            i++;
        }
    }

    private void createActionAndGoto() {
        // 初始化 action 表
        action = new String[DFA.size() + 1][terminal.size() + 1];
        for (int i = 1; i < action.length; i++) {
            action[i][0] = (i - 1) + "";
        }
        ArrayList<String> temp = new ArrayList<>(terminal);
        for (int i = 1; i < action[0].length; i++) {
            action[0][i] = temp.get(i - 1);
        }

        // 初始化 GOTO 表
        edges.clear();
        for (Status status : DFA) {
            Edge edge = status.next;
            while (edge != null) {
                edges.add(edge);
                edge = edge.next;
            }
        }
        for (Edge edge : edges) {
            String s = edge.s;
            if (!terminal.contains(s)) {
                nonTerminal.add(s);
            }
        }
        GOTO = new String[DFA.size() + 1][nonTerminal.size() + 1];
        for (int i = 1; i < GOTO.length; i++) {
            GOTO[i][0] = (i - 1) + "";
        }
        ArrayList<String> temp1 = new ArrayList<>(nonTerminal);
        for (int i = 1; i < GOTO[0].length; i++) {
            GOTO[0][i] = temp1.get(i - 1);
        }

        // 填写 action 与 GOTO 表
        for (int i = 0; i < DFA.size(); i++) {
            Status status = DFA.get(i);
            LinkedList<HashMap<String, LinkedList<String>>> list = status.list;
            for (HashMap<String, LinkedList<String>> map : list) {
                for (String key : map.keySet()) {
                    LinkedList<String> strings = map.get(key);
                    if (strings.indexOf(".") == strings.indexOf("$") - 1) {
                        List<String> list1 = strings.subList(0, strings.indexOf("."));
                        List<String> list2 = strings.subList(strings.indexOf("$") + 1, strings.size());
                        int index = indexOfGrammar1(list1);

                        for (String s : list2) {
                            int index1 = temp.indexOf(s);
                            if (index == 0) {
                                action[i + 1][index1 + 1] = "acc";
                            } else {
                                action[i + 1][index1 + 1] = "r" + index;
                            }
                        }
                    } else if (strings.indexOf(".") < strings.indexOf("$") - 1) {
                        String s = strings.subList(strings.indexOf(".") + 1, strings.indexOf("$")).get(0);
                        if (terminal.contains(s)) {
                            Edge edge = status.next;
                            while (edge != null) {
                                String ss = edge.s;
                                if (s.equals(ss)) {
                                    int index = indexOfDFA(edge.to);
                                    action[i + 1][temp.indexOf(ss) + 1] = "s" + index;
                                }
                                edge = edge.next;
                            }
                        } else {
                            Edge edge = status.next;
                            while (edge != null) {
                                String ss = edge.s;
                                if (s.equals(ss)) {
                                    int index = indexOfDFA(edge.to);
                                    GOTO[i + 1][temp1.indexOf(ss) + 1] = "" + index;
                                }
                                edge = edge.next;
                            }
                        }
                    }
                }
            }
        }
    }

    private void saveActionAndGoto() {
        // 保存 action 表
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/test-classes/", "");
            File file = new File(path + "src/main/resources/action.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            for (int i = 1; i < action.length; i++) {
                for (int j = 1; j < action[0].length; j++) {
                    String s = action[i][j];
                    if (s != null) {
                        writer.write(action[i][j] + " ");
                    } else {
                        writer.write("null ");
                    }
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保存 GOTO 表
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/test-classes/", "");
            File file = new File(path + "src/main/resources/goto.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            for (int i = 1; i < GOTO.length; i++) {
                for (int j = 1; j < GOTO[0].length; j++) {
                    String s = GOTO[i][j];
                    if (s != null) {
                        writer.write(GOTO[i][j] + " ");
                    } else {
                        writer.write("null ");
                    }
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保存终结符
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/test-classes/", "");
            File file = new File(path + "src/main/resources/terminalSymbol.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            ArrayList<String> list = new ArrayList<>(terminal);
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i) + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保存非终结符
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/test-classes/", "");
            File file = new File(path + "src/main/resources/nonTerminalSymbol.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            ArrayList<String> list = new ArrayList<>(nonTerminal);
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i) + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保存产生式
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/test-classes/", "");
            File file = new File(path + "src/main/resources/production.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            for (GrammarNode node : grammar1) {
                writer.write(node.getValue() + "->" + node.getNext().getValue() + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
    }

    @Test
    public void test() throws IOException {
        initGrammar();
        initTerminalandNonTerminal();
        getFirst();
        initProjectSet();
        createDFA();
        createActionAndGoto();
        saveActionAndGoto();
    }
}
