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
    public static void main (String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("PosTagger takes 1 arguments:  java FeatureBuilder training.pos-chunk");
            System.exit(1);
        }

        buildFeatures(args[0]);
    }

    private static void buildFeatures(String targetFilePath) throws IOException {
        File targetFile = new File(targetFilePath);
        List<String> lines = Files.readAllLines(targetFile.toPath(), StandardCharsets.UTF_8);

        List<String> sentence = new ArrayList<>();
        List<String> results = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            System.out.println("Processing line " + i);
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

        // print results to file
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("feature-enhanced", false));

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

        boolean training = false;
        for (int i = 0; i < sentence.size(); i++) {
            String[] fields = sentence.get(i).split("\t");
            if (fields.length >= 2) {
                tokens.add(fields[0]);
                posTags.add(fields[1]);
                if (fields.length > 2) {
                    training = true;
                    chunkTags.add(fields[2]);
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

            if (i > 0) {
                features.append("\tpreToken=" + tokens.get(i-1));
                features.append("\tprePosTag=" + posTags.get(i-1));
                features.append("\tpreChunkTag=" + (training? chunkTags.get(i-1):"@@"));
            }
            if (i < tokens.size() - 1) {
                features.append("\tnextToken=" + tokens.get(i+1));
                features.append("\tnextPosTag=" + posTags.get(i+1));
            }

            if (training) {
                if (i < chunkTags.size() - 1)
                    features.append("\tnextChunkTag=" + (chunkTags.get(i+1)));

                features.append("\t" + chunkTags.get(i));
            }

            results.add(features.toString());
        }
        return results;
    }

}
