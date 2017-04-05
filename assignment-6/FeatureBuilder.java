import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yonghong on 3/7/17.
 */
public class FeatureBuilder {
    private static boolean training = false;

    public static void main (String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("PosTagger takes 1 arguments:  java FeatureBuilder training.pos-chunk-name");
            System.exit(1);
        }

        buildFeatures(args[0]);
    }

    private static void buildFeatures(String targetFilePath) throws IOException {
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
                results.addAll(getFeatures(sentence));
                results.add("");
                sentence.clear();
            } else {
                sentence.add(word);
            }
        }

        // process the last sentence
        if (!sentence.isEmpty()) {
            results.addAll(getFeatures(sentence));
        }

        if (training) {
            // add a list of common names with nameTag
//            results.addAll(getCommonNameFeatures());
        }

        // print results to file
        PrintWriter pw = null;
        try {
            targetFilePath = targetFilePath.substring(0, targetFilePath.indexOf("."));
            pw = new PrintWriter(new FileWriter(targetFilePath + ".enhanced", false));

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (String result : results)
            sb.append(result + "\n");
        pw.append(sb.toString());
        pw.close();
    }

    private static List<String> getFeatures(List<String> sentence) {
        List<String> results = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        List<String> posTags = new ArrayList<>();
        List<String> chunkTags = new ArrayList<>();
        List<String> nameTags = new ArrayList<>();

        training = false;
        for (int i = 0; i < sentence.size(); i++) {
            String[] fields = sentence.get(i).split("\t");
            if (fields.length >= 3) {
                tokens.add(fields[0]);
                posTags.add(fields[1]);
                chunkTags.add(fields[2]);
                if (fields.length > 3) {
                    training = true;
                    nameTags.add(fields[3]);
                }
            }
            else {
                System.err.println ("data format error in at line " + i + ": " + sentence.get(i));
                System.exit(1);
            }
        }

        for (int i = 0; i < tokens.size(); i++) {
            StringBuffer features = new StringBuffer();
            features.append(tokens.get(i));
            features.append("\tposTag=" + posTags.get(i));
//            features.append("\tchunkTag=" + chunkTags.get(i));

            if (i > 0) {
                features.append("\tpreToken=" + tokens.get(i-1));
//                features.append("\tprePosTag=" + posTags.get(i-1));
//                features.append("\tpreChunkTag=" + chunkTags.get(i-1));

//                features.append("\tpreNameTag=" + (training? nameTags.get(i-1):"@@"));
            }
            if (i < tokens.size() - 1) {
                features.append("\tnextToken=" + tokens.get(i+1));
//                features.append("\tnextPosTag=" + posTags.get(i+1));
//                features.append("\tnextChunkTag=" + chunkTags.get(i+1));
            }

            if (training) {
//                if (i < nameTags.size() - 1)
//                    features.append("\tnextNameTag=" + (nameTags.get(i+1)));

                features.append("\t" + nameTags.get(i));
            }

            results.add(features.toString());
        }
        return results;
    }

    private static List<String> getCommonNameFeatures() throws IOException {
        List<String> results = new ArrayList<>();
        File nameFile = new File("names.txt");
        List<String> lines = Files.readAllLines(nameFile.toPath(), StandardCharsets.UTF_8);

        for (String line : lines) {
            String[] fields = line.trim().split("\\s+");
            if (fields.length > 0) {
                String name = fields[0];
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                results.add(name + "\tI-PER");
            }
        }
        return results;
    }

}
