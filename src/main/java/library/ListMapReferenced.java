package library;


import java.util.*;

public class ListMapReferenced<K> {

    private final List<Map<K, Integer>> listMap;

    private final ArrayList<Integer> references;


    public ListMapReferenced() {
        this.listMap = new ArrayList<>();
        this.references = new ArrayList<>();
    }

    public void add(List<K> row) {

        int root = references.size();
        Set<Integer> rootsOnChange = new HashSet<>();

        for (int i = 0; i < row.size(); i++) {

            K current = row.get(i);

            if (current == "" || current == null)
                continue;

            if (i >= listMap.size()) {
                Map<K, Integer> map = new HashMap<>();
                map.put(current, root);
                listMap.add(map);
                continue;
            }

            Map<K, Integer> column = listMap.get(i);

            Integer sameRow = column.get(current);

            if (sameRow != null) {

                if (root != sameRow) {
                    root = Math.min(root, sameRow);
                    rootsOnChange.add(sameRow);
                }

            }
            else {
                column.put(current, root);
            }

        }

        // set all elements root to lowest possible root value
        if (root != references.size())
            for (int i = 0; i < row.size(); i++)
                listMap.get(i).put(row.get(i), root);


        for (Integer rootOnChange : rootsOnChange)
            references.set(rootOnChange, root);

        references.add(root);
    }

    public List<Set<Integer>> group() {

        Map<Integer, Set<Integer>> map = new HashMap<>();

        for (int i = 0; i < references.size(); i++) {

            int currentRoot = references.get(i);
            int currentIndex = i;

            while (currentRoot != currentIndex) {
                currentIndex = currentRoot;
                currentRoot = references.get(currentIndex);

            }

            Set<Integer> group = map.get(currentRoot);
            if (group == null) {
                Set<Integer> set = new HashSet<>();
                set.add(i);
                map.put(currentRoot, set);
            }
            else
                group.add(i);

        }

        return map.values()
                .stream()
                .filter(set -> set.size() > 1)
                .sorted((o1, o2) -> o2.size() - o1.size())
                .toList();

    }

}
