package library;

import java.util.*;

public class ListMap<K,V> {

    private final List<Map<K, List<V>>> listMap;

    public ListMap() {
        this.listMap = new LinkedList<>();
    }

    public Set<V> add(List<K> row, V currentIndex) {

        Set<V> result = new HashSet<>();

        for (int i = 0; i < row.size(); i++) {

            K current = row.get(i);

            if (current == null)
                continue;

            if (i >= listMap.size()) {
                Map<K, List<V>> map = new HashMap<>();
                List<V> list = new LinkedList<>();
                list.add(currentIndex);
                map.put(current, list);
                listMap.add(map);
                continue;
            }

            Map<K, List<V>> column = listMap.get(i);

            if (column.containsKey(current)) {
                result.addAll(column.get(row.get(i)));
                column.get(current).add(currentIndex);
            }
            else {
                List<V> list = new LinkedList<>();
                list.add(currentIndex);
                column.put(current, list);
            }

        }

        return result;
    }
}
