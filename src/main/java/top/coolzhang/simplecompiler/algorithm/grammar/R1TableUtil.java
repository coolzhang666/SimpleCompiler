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
     * 存放文法
     */
    private Node[] grammar;
    private ArrayList<Node> grammar1;

    /**
     * 存放项目集
     */
    private ArrayList<Node1> projectSet = new ArrayList<>();

    /**
     * 存放 DFA
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
     * firstVT 集合
     */
    private HashMap<String, LinkedHashSet<String>> first = new HashMap<>();

    private HashSet<String> changeSet = new HashSet<>();
    private boolean flag = true;

    private HashSet<String> nonTerminal = new HashSet<>();

    private void start() {

        // 初始化文法
        // 读取文法文件
        try {
            URL url = R1TableUtil.class.getClassLoader().getResource("grammar.txt");
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
            grammar1 = new ArrayList<>();
            for (int i = 0; i < strings.length; i++) {
                String[] ss = strings[i].split("->");
                Node node = new Node(ss[0], null);
                nodes[i] = node;
                String[] sss = ss[1].split(" \\$ ");
                Node p = node;
                for (int j = 0; j < sss.length; j++) {
//                    String[] split = sss[j].split(" @ ");
//                    p.next = new Node(split[0], null);
                    p.next = new Node(sss[j], null);
                    p = p.next;
                }
                String LS = ss[0];

                while (node.next != null) {
                    node = node.next;
                    String RS = node.s;
                    Node node2 = new Node(RS, null);
                    Node node1 = new Node(LS, node2);
                    grammar1.add(node1);
                }
            }
            grammar = nodes;
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (grammar != null) {
            HashSet<String> hashSet = new HashSet<>();
            for (Node head : grammar) {
                hashSet.add(head.s);
            }

            for (Node head : grammar) {
                Node node = head;

                while (node.next != null) {
                    node = node.next;
                    String RS = node.s;
                    String[] list = RS.split(" ");
                    for (String s : list) {
                        if (!hashSet.contains(s)) {
                            terminal.add(s);
                        }
                    }
                }
            }
        }
        terminal.add("#");

        getFirst();
//        initGrammar1();
        initProjectSet();
        createDFA();
        createActionAndGoto();
        saveActionAndGoto();
    }

    private void initProjectSet() {
        for (Node head : grammar) {
            String LS = head.s;
            Node node = head;

            LinkedList<String> n = new LinkedList<>();
            n.add(LS);
            Node1 head1 = new Node1(n, null);
            Node1 node1 = head1;
            while (node.next != null) {
                node = node.next;
                String RS = node.s.split(" @ ")[0];
                String[] array = RS.split(" ");
                LinkedList<String> list = new LinkedList<>(Arrays.asList(array));

                for (int i = 0; i <= list.size(); i++) {
                    LinkedList<String> temp = (LinkedList<String>) list.clone();

                    temp.add(i, ".");
                    Node1 node2 = new Node1(temp, null);
                    node1.next = node2;
                    node1 = node1.next;
                }
            }
            projectSet.add(head1);
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

    private void createDFA() {
        // 初始化
        Node1 init_node = projectSet.get(0);
        LinkedList<String> init_list = init_node.list;
        LinkedList<String> init_list1 = init_node.next.list;
        init_list1.add("$");
        LinkedList<String> init_forward = new LinkedList<>();
        init_forward.add("#");
        HashMap<String, LinkedList<String>> init_map = new HashMap<>();
        init_map.put(init_list.get(0), init_node.next.list);
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

//                            for (Status status2 : DFA) {
//                                Edge edge = status2.next;
//                                while (edge != null) {
//                                    if (edge.index == result) {
//                                        edge.index = index2;
//                                    }
//                                    edge = edge.next;
//                                }
//                            }
//                            for (Status status2 : DFA) {
//                                Edge edge = status2.next;
//                                while (edge != null) {
//                                    int index3 = edge.index;
//                                    if (index3 > result) {
//                                        edge.index = index3 - 1;
//                                    }
//                                    edge = edge.next;
//                                }
//                            }

//                            i--;
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
//                                    int index = edge.index;
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
//                                    int index = edge.index;
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


//        System.out.println("action表");
//        for (int i = 0; i < action.length; i++) {
//            for (int j = 0; j < action[0].length; j++) {
//                System.out.print(String.format("%-30s", action[i][j]));
//            }
//            System.out.println();
//        }
//        System.out.println("GOTO表");
//        for (int i = 0; i < GOTO.length; i++) {
//            for (int j = 0; j < GOTO[0].length; j++) {
//                System.out.print(String.format("%-30s", GOTO[i][j]));
//            }
//            System.out.println();
//        }
    }

    private void saveActionAndGoto() {
        // 保存 action 表
        try {
            String path = R1TableUtil.class.getResource("/").getPath();
            path = path.replace("target/classes/", "");
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
            path = path.replace("target/classes/", "");
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
            path = path.replace("target/classes/", "");
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
            path = path.replace("target/classes/", "");
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
            path = path.replace("target/classes/", "");
            File file = new File(path + "src/main/resources/production.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            for (Node node : grammar1) {
                writer.write(node.s + "->" + node.next.s + "\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void judge(LinkedList<String> token) {
        Stack<Integer> status = new Stack<>();
        Stack<String> symbol = new Stack<>();
        status.push(0);
        symbol.push("#");
        token.addLast("#");
        LinkedList<String> temp = new LinkedList<>(terminal);
        LinkedList<String> temp1 = new LinkedList<>(nonTerminal);

        while (true) {
            Integer top1 = status.peek();
            String top2 = token.peekFirst();

            if (terminal.contains(top2)) {
                int index1 = top1 + 1;
                int index2 = temp.indexOf(top2) + 1;
                String s = action[index1][index2];
                if (s != null) {
                    if ("acc".equals(s)) {
                        System.out.println("成功");
                        break;
                    } else if ("s".equals(s.substring(0, 1))) {
                        int newStatus = Integer.parseInt(s.substring(1));
                        status.push(newStatus);
                        symbol.push(token.pollFirst());
                    } else if ("r".equals(s.substring(0, 1))) {
                        int id = Integer.parseInt(s.substring(1));

                        Node node = grammar1.get(id);
                        String newSymbol = node.s;
                        String s1 = node.next.s;
                        List<String> list = Arrays.asList(s1.split(" "));
                        for (int i = 0; i < list.size(); i++) {
                            status.pop();
                            symbol.pop();
                        }
                        int index3 = status.peek() + 1;
                        int index4 = temp1.indexOf(newSymbol) + 1;
                        int newStatus = Integer.parseInt(GOTO[index3][index4]);
                        status.push(newStatus);
                        symbol.push(newSymbol);
                    } else {
                        System.out.println("错误");
                        break;
                    }
                } else {
                    System.out.println("错误");
                    break;
                }
            }
        }
    }

    private int indexOfGrammar1(List<String> list) {
        for (int i = 0; i < grammar1.size(); i++) {
            String RS = grammar1.get(i).next.s.split(" @ ")[0];

            List<String> list1 = Arrays.asList(RS.split(" "));
            if (isEquals(list, list1)) {
                return i;
            }
        }
        return -1;
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
                for (Node1 node : projectSet) { // 遍历项目集
                    String LS1 = node.list.get(0); // 得到项目集左部非终结符
                    if (LS.equals(LS1)) {
                        Node1 node1 = node;
                        while (node1.next != null) {
                            node1 = node1.next;
                            LinkedList<String> list = node1.list;

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

    private boolean isContainsProject(Status status, HashMap<String, LinkedList<String>> map) {
        for (HashMap<String, LinkedList<String>> map1 : status.list) {
            if (isMapEquals(map, map1)) {
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

    private void initGrammar1() {
        grammar1 = new ArrayList<>();
        for (Node head : grammar) {
            String LS = head.s;
            Node node = head;

            while (node.next != null) {
                node = node.next;
                String RS = node.s;
                Node node2 = new Node(RS, null);
                Node node1 = new Node(LS, node2);
                grammar1.add(node1);
            }
        }
    }

    private boolean addAndJudge(LinkedHashSet<String> set, String RS, String LS) {
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

    private boolean addAllAndJudge(LinkedHashSet<String> set, LinkedHashSet<String> temp, String LS) {
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

    private void getFirst() {
        if (grammar == null || terminal == null) {
            return;
        }

        // 初始化 first 集合
        for (Node head : grammar) {
            String LS = head.s;
            first.put(LS, new LinkedHashSet<>());
        }

        // 用于判断 first 有没有变化
        boolean status = true;

        // 循环遍历文法
        while (status) {
            status = false;

            // 遍历文法
            for (Node head : grammar) {
                flag = true;
                Node node = head;
                String LS = head.s;

                while (node.next != null) {
                    node = node.next;
                    String RS = node.s.split(" @ ")[0];
                    LinkedHashSet<String> set = first.get(LS);

                    String[] array = RS.split(" ");
                    if (terminal.contains(array[0])) {
                        if (addAndJudge(set, array[0], LS)) {
                            status = true;
                        }
                    } else if (!terminal.contains(array[0])) {
                        LinkedHashSet<String> temp = first.get(array[0]);
                        if (addAllAndJudge(set, temp, LS)) {
                            status = true;
                        }
                        changeSet.add(array[0]);
                    } else if ("null".equals(array[0])) {
                        if (addAndJudge(set, array[0], LS)) {
                            status = true;
                        }
                    }
                }
            }
        }
    }

    @Test
    public void create() {
        start();
    }
}
