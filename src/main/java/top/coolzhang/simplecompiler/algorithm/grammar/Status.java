package top.coolzhang.simplecompiler.algorithm.grammar;

import java.util.*;

public class Status implements Comparable<Status>{
    LinkedList<HashMap<String, LinkedList<String>>> list = new LinkedList<>();
    Edge next;

    public Status(HashMap<String, LinkedList<String>> map, Edge next) {
        this.list.add(map);
        this.next = next;
    }

    @Override
    public int compareTo(Status o) {
        LinkedList<HashMap<String, LinkedList<String>>> list = this.list;
        LinkedList<HashMap<String, LinkedList<String>>> list1 = o.list;

        if (list.size() == list1.size()) {
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, LinkedList<String>> map = list.get(i);
                HashMap<String, LinkedList<String>> map1 = list1.get(i);

                String key = "";
                String key1 = "";
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    key = iterator.next();
                }
                Iterator<String> iterator1 = map1.keySet().iterator();
                while (iterator1.hasNext()) {
                    key1 = iterator1.next();
                }

                if (key.equals(key1)) {
                    LinkedList<String> list2 = map.get(key);
                    LinkedList<String> list3 = map1.get(key1);
                    if (list2.size() == list3.size()) {
                        List<String> list4 = list2.subList(0, list2.indexOf("$"));
                        List<String> list5 = list3.subList(0, list3.indexOf("$"));
                        if (list4.size() != list5.size()) {
                            return 0;
                        }
                        for (int j = 0; j < list4.size(); j++) {
                            if (!list4.get(j).equals(list5.get(j))) {
                                return 0;
                            }
                        }
                        HashSet<String> set1 = new HashSet<>(list2.subList(list2.indexOf("$") + 1, list2.size()));
                        HashSet<String> set2 = new HashSet<>(list3.subList(list3.indexOf("$") + 1, list3.size()));
                        if (set1.size() == set2.size()) {
                            if (!set1.containsAll(set2)) {
                                return 0;
                            }
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }

            }
        } else {
            return 0;
        }
        return 1;
    }
}
