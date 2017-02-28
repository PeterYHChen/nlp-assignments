import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to tag words.
 * Input: a training corpus (words and tags), and a document with lines of words to be tagged
 * Output: a document with lines of word-tag pairs with tab separator
 */
public class PosTagger {
    // map a tag to its total occurrence
    private static Map<String, Integer> tagCntMap = new HashMap<>();
    // map a tag to a word and occurrence
    private static Map<String, HashMap<String, Integer>> tagWordCntMap = new HashMap<>();
    // map a tag to a following tag and occurrence
    private static Map<String, HashMap<String, Integer>> tagTagCntMap = new HashMap<>();

    public static void main (String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("PosTagger takes 2 arguments:  java PosTagger training.pos document.word");
            System.exit(1);
        }

        trainTagger(args[0]);
        tagging(args[1]);
        test();
    }

    private static void trainTagger(String trainingFilePath) throws IOException {
        File trainingFile = new File(trainingFilePath);
        List<String> wordTagPairs = Files.readAllLines(trainingFile.toPath(), StandardCharsets.UTF_8);

        String preTag = "";
        for (int i = 0; i < wordTagPairs.size(); i++) {
            System.out.println("Reading training corpus, line: " + i);
            String wordTagPair = wordTagPairs.get(i).trim();
            if (wordTagPair.equals("")) {
                continue;
            }
            String[] fields = wordTagPair.split("\t");
            if (fields.length != 2) {
                System.err.println ("format error in word-tag pair at line " + i + ": " + wordTagPair);
                System.exit(1);
            }

            String word = fields[0];
            String tag = fields[1];

            if (!tagCntMap.containsKey(tag)) {
                tagCntMap.put(tag, 1);
            } else {
                tagCntMap.put(tag, tagCntMap.get(tag) + 1);
            }

            if (!tagWordCntMap.containsKey(tag)) {
                tagWordCntMap.put(tag, new HashMap<>());
            }
            Map<String, Integer> tempCntMap = tagWordCntMap.get(tag);
            if (!tempCntMap.containsKey(word)) {
                tempCntMap.put(word, 1);
            } else {
                tempCntMap.put(word, tempCntMap.get(word) + 1);
            }

            if (preTag.equals("")) {
                continue;
            }
            if (!tagTagCntMap.containsKey(preTag)) {
                tagTagCntMap.put(preTag, new HashMap<>());
            }
            tempCntMap = tagTagCntMap.get(preTag);
            if (!tempCntMap.containsKey(tag)) {
                tempCntMap.put(tag, 1);
            } else {
                tempCntMap.put(tag, tempCntMap.get(tag) + 1);
            }
        }
    }

    private static void tagging(String targetFilePath) throws IOException {
        File targetFile = new File(targetFilePath);
        List<String> words = Files.readAllLines(targetFile.toPath(), StandardCharsets.UTF_8);

        for (String word : words) {
            word = word.trim();
            if (word.equals("")) {

            }
        }
    }

    private static void test() {
        for (String tag : tagCntMap.keySet()) {
            int totalCnt = tagCntMap.get(tag);
            System.out.println(tag + "\t" + totalCnt);

            int sum = 0;
            HashMap<String, Integer> tempMap = tagTagCntMap.get(tag);
            for (String tagtag : tempMap.keySet()) {
                int subCnt = tempMap.get(tagtag);
                sum += subCnt;
                System.out.println("\t" + tagtag + "\t" + subCnt);
            }

            System.out.println("totalCnt: " + totalCnt + "\tsum: " + sum);
            if (sum != totalCnt) {
                System.out.println("Tagtag Count not match!!!!!!!!!!!!!");
            }

            sum = 0;
            tempMap = tagWordCntMap.get(tag);
            for (String word : tempMap.keySet()) {
                int subCnt = tempMap.get(word);
                sum += subCnt;
//                System.out.println("\t" + word + "\t" + subCnt);
            }

            System.out.println("totalCnt: " + totalCnt + "\tsum: " + sum);
            if (sum != totalCnt) {
                System.out.println("TagWord sum not match!!!!!!!!!!!!!");
            }
        }
    }
}
