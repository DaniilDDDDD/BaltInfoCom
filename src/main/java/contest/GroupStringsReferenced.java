package contest;

import library.ListMapReferenced;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class GroupStringsReferenced {

    public static void main(String[] args) throws IOException {

        long timestamp = System.currentTimeMillis();


        Pair<ArrayList<ArrayList<String>>,
                ListMapReferenced<String>> pair = read(
                Objects.equals(args[0], "download"),
                args[1]
        );

        ArrayList<ArrayList<String>> data = pair.getLeft();


        List<Set<Integer>> groups = pair.getRight().group();

        if (args.length > 2) {

            BufferedWriter out = new BufferedWriter(new FileWriter(args[2]));

            out.write("Group number: " + groups.size() + "\n\n");
            out.write("Computation time: " + (System.currentTimeMillis() - timestamp) + " millisecond\n\n");

            for (int i = 0; i < groups.size(); i++) {

                out.write("Group " + (i + 1) + "\n");
                out.write("\n");

                for (Integer row : groups.get(i)) {
                    out.write(data.get(row)
                            .stream().map(v -> {
                                if (Objects.equals(v, ""))
                                    return "\"\"";
                                return "\"" + v + "\"";
                            })
                            .collect(Collectors.joining(";")) + "\n\n");
                }
                out.write("...\n\n");
            }

            out.close();

        } else {

            System.out.println("Group number: " + groups.size() + "\n");

            System.out.println("Computation time: " + (System.currentTimeMillis() - timestamp) + " millisecond\n");

            for (int i = 0; i < groups.size(); i++) {

                System.out.println("Group " + (i + 1) + "\n");

                for (Integer row : groups.get(i))
                    System.out.println(data.get(row)
                            .stream().map(v -> {
                                if (Objects.equals(v, ""))
                                    return "\"\"";
                                return "\"" + v + "\"";
                            })
                            .collect(Collectors.joining(";")) + "\n");

                System.out.println("...\n");
            }

        }

    }


    private static Pair<ArrayList<ArrayList<String>>, ListMapReferenced<String>>
    read(boolean download, String input) throws IOException {

        BufferedReader in;

        if (!download) {

            FileReader reader = new FileReader(input);
            in = new BufferedReader(reader);

        } else {

            URL url = new URL(input);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            System.setProperty("http.keepAlive", "false");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            InputStreamReader reader = new InputStreamReader(gzipInputStream);
            in = new BufferedReader(reader);

        }


        // read only unique lines in file
        Set<String> fileData = new HashSet<>();

        String readed;
        while ((readed = in.readLine()) != null)
            fileData.add(readed);


        ArrayList<ArrayList<String>> allData = new ArrayList<>();
        ListMapReferenced<String> data = new ListMapReferenced<>();


        for (String string : fileData) {

            try {

                ArrayList<String> row = Arrays.stream(string
                                .split(";"))
                        .map(s -> {
                            if (s.equals(""))
                                return s;
                            s = s.substring(1, s.length() - 1);
                            return s;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));

                allData.add(row);

                data.add(row);

            } catch (Exception ignored) {
                // skit incorrect line
            }
        }

        return Pair.of(allData, data);
    }

}
