import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yonghong on 4/4/17.
 */
public class NameTagAdjuster {
    private static Map<String, Double> firstNames = new HashMap<>();
    private static Map<String, Double> lastNames = new HashMap<>();
    private static int tagChangedCount = 0;

    public static void main (String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("NameTagAdjuster takes 1 arguments:  java NameTagAdjuster response.name");
            System.exit(1);
        }

        initNames();
        adjustTag(args[0]);
    }

    private static void initNames() throws IOException {
        File nameFile = new File("firstNames.txt");
        List<String> lines = Files.readAllLines(nameFile.toPath(), StandardCharsets.UTF_8);

        for (String line : lines) {
            String[] fields = line.trim().split("\\s+");
            if (fields.length > 1) {
                String name = fields[0];
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                firstNames.put(name,  Double.parseDouble(fields[1]));
            }
        }

        nameFile = new File("lastNames.txt");
        lines = Files.readAllLines(nameFile.toPath(), StandardCharsets.UTF_8);

        for (String line : lines) {
            String[] fields = line.trim().split("\\s+");
            if (fields.length > 0) {
                String name = fields[0];
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                lastNames.put(name, Double.parseDouble(fields[1]));
            }
        }
    }

    private static void adjustTag(String targetFilePath) throws IOException {
        File targetFile = new File(targetFilePath);
        List<String> lines = Files.readAllLines(targetFile.toPath(), StandardCharsets.UTF_8);

        List<String> sentence = new ArrayList<>();
        List<String> results = new ArrayList<>();

        System.out.println("Processing file " + targetFilePath);
        for (int i = 0; i < lines.size(); i++) {
//            System.out.println("Processing line " + i);
            String word = lines.get(i).trim();
            if (word.equals("")) {
                // process sentence and get features
                results.addAll(getAdjustedResult(sentence));
                results.add("");
                sentence.clear();
            } else {
                sentence.add(word);
            }
        }

        // process the last sentence
        if (!sentence.isEmpty()) {
            results.addAll(getAdjustedResult(sentence));
        }


        System.out.println("In total " + tagChangedCount +  " name tags have been changed");

        // print results to file
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(targetFilePath + ".adjusted", false));

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (String result : results)
            sb.append(result + "\n");
        pw.append(sb.toString());
        pw.close();
    }

    private static List<String> getAdjustedResult(List<String> sentence) {
        List<String> results = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        List<String> nameTags = new ArrayList<>();

        for (int i = 0; i < sentence.size(); i++) {
            String[] fields = sentence.get(i).split("\t");
            if (fields.length == 2) {
                tokens.add(fields[0]);
                nameTags.add(fields[1]);
            } else {
                System.err.println ("data format error in at line " + i + ": " + sentence.get(i));
                System.exit(1);
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String nameTag = nameTags.get(i);

            boolean isName = false;
            boolean isLocation = false;
//            if (lastNames.containsKey(token) && lastNames.get(token) >= 0.005) {
//                if (i > 0 && firstNames.containsKey(tokens.get(i - 1))) {
//                    isName = true;
//                }
//            }
//            if (firstNames.containsKey(token) && firstNames.get(token) >= 0.005) {
//                if (i < tokens.size() - 1 && lastNames.containsKey(tokens.get(i + 1))) {
//                    isName = true;
//                }
//            }
//
//            if (isName && !nameTag.equals("I-PER")) {
//                System.out.print("(" + (i>0? tokens.get(i-1) + ", " + nameTags.get(i-1):"") + ")");
//                System.out.print("(" + token + ", " + nameTag + ")");
//                System.out.println("(" + (i<tokens.size()-1? tokens.get(i+1) + ", " + nameTags.get(i+1):"") + ")");
//                tagChangedCount++;
//                nameTag = "I-PER";
//            }

            if (i > 0 && i < tokens.size() - 1) {
                if (!nameTags.get(i-1).equals("0") && !nameTags.get(i-1).equals(nameTag) && !nameTag.equals("0") && !nameTags.get(i+1).equals(nameTag)) {
                    System.out.print("(" + (i>0? tokens.get(i-1) + ", " + nameTags.get(i-1):"") + ")");
                    System.out.print("(" + token + ", " + nameTag + ")");
                    System.out.println("(" + (i<tokens.size()-1? tokens.get(i+1) + ", " + nameTags.get(i+1):"") + ")");
                    tagChangedCount++;
                    nameTag = nameTags.get(i-1);
                }
            }


            results.add(token + "\t" + nameTag);
        }

        return results;
    }
}
