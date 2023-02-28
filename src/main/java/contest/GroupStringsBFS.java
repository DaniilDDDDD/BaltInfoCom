package contest;

import library.ListMap;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class GroupStringsBFS {


    public static void main(String[] args) throws Throwable {

        long timestamp = System.currentTimeMillis();

        Pair<ArrayList<ArrayList<Long>>,
                Map<Integer, Pair<Boolean, Set<Integer>>>> pair = readToListOfIntegers(args[0]);

        ArrayList<ArrayList<Long>> data = pair.getLeft();

        List<Set<Integer>> groups = group(pair.getRight())
                .stream()
                .sorted((o1, o2) -> o2.size() - o1.size())
                .toList();

        System.out.println("Количество групп, в которых более одного элемента: " + groups.size() + "\n");

        for (int i = 0; i < groups.size(); i++) {

            System.out.println("Группа " + (i + 1) + "\n");

            for (Integer row : groups.get(i))
                System.out.println(data.get(row)
                        .stream().map(v -> {
                            if (v == null)
                                return "\"\"";
                            return "\"" + v + "\"";
                        })
                        .collect(Collectors.joining(";")) + "\n");

            System.out.println("...\n");
        }

        System.out.println("Время выполения программы: " + (System.currentTimeMillis() - timestamp) + " миллисекунд");

    }

    private static Pair<ArrayList<ArrayList<Long>>, Map<Integer, Pair<Boolean, Set<Integer>>>>
    readToListOfIntegers(String link) throws Throwable {

        URL url = new URL(link);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        System.setProperty("http.keepAlive", "false");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        InputStreamReader reader = new InputStreamReader(gzipInputStream);
        BufferedReader in = new BufferedReader(reader);


        ArrayList<ArrayList<Long>> allData = new ArrayList<>();
        ListMap<Long, Integer> data = new ListMap<>();
        Map<Integer, Pair<Boolean, Set<Integer>>> graph = new HashMap<>();

        int counter = 0;

        String readed;
        while ((readed = in.readLine()) != null) {

            try {

                ArrayList<Long> row = Arrays.stream(readed
                                .split(";"))
                        .map(s -> {
                            s = s.substring(1, s.length() - 1);
                            if (Objects.equals(s, ""))
                                return null;
                            return Long.parseLong(s);
                        })
                        .collect(Collectors.toCollection(ArrayList::new));

                allData.add(row);

                Set<Integer> sameRows = data.add(row, counter);

                if (sameRows.size() > 0) {

                    if (graph.containsKey(counter)) {
                        graph.get(counter).getRight().addAll(sameRows);
                    } else {
                        graph.put(counter, Pair.of(false, sameRows));
                    }

                    for (Integer sameRow : sameRows) {

                        if (graph.containsKey(sameRow))
                            graph.get(sameRow).getRight().add(counter);
                        else {
                            Set<Integer> set = new HashSet<>();
                            set.add(counter);
                            graph.put(sameRow, Pair.of(false, set));
                        }
                    }

                }

                counter++;

            } catch (Exception ignored) {
                // skit incorrect line
            }
        }

        return Pair.of(allData, graph);
    }


    private static List<Set<Integer>> group(Map<Integer, Pair<Boolean, Set<Integer>>> graph) {

        List<Set<Integer>> result = new LinkedList<>();

        for (Map.Entry<Integer, Pair<Boolean, Set<Integer>>> entry : graph.entrySet())
            if (!entry.getValue().getLeft())
                result.add(
                        bfs(graph, entry.getKey())
                );

        return result;
    }


    private static Set<Integer> bfs(Map<Integer, Pair<Boolean, Set<Integer>>> graph, Integer start) {

        Set<Integer> group = new HashSet<>();

        LinkedList<Integer> deque = new LinkedList<>();

        deque.push(start);

        while (!deque.isEmpty()) {

            Integer current = deque.pollLast();
            group.add(current);
            Set<Integer> children = graph.get(current).getRight();
            graph.put(current, Pair.of(true, children));

            for (Integer child : children)
                if (!graph.get(child).getLeft())
                    deque.push(child);

        }

        return group;
    }

}
