import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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
    // map a word to a list of tags
    private static Map<String, HashSet<String>> wordTagsMap = new HashMap<>();

    public static void main (String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("PosTagger takes 2 arguments:  java PosTagger training.pos document.word");
            System.exit(1);
        }

        trainTagger(args[0]);
        tagging(args[1]);
//        test();
    }

    private static void trainTagger(String trainingFilePath) throws IOException {
        File trainingFile = new File(trainingFilePath);
        List<String> wordTagPairs = Files.readAllLines(trainingFile.toPath(), StandardCharsets.UTF_8);

        String preTag = "";
        for (int i = 0; i < wordTagPairs.size(); i++) {
            System.out.println("Reading training corpus, line: " + i);
            String wordTagPair = wordTagPairs.get(i).trim();
            // empty tag or word means the beginning or the end of a sentence
            String word = "";
            String tag = "";
            if (!wordTagPair.equals("")) {
                String[] fields = wordTagPair.split("\t");
                if (fields.length != 2) {
                    System.err.println ("format error in word-tag pair at line " + i + ": " + wordTagPair);
                    System.exit(1);
                }
                word = fields[0];
                tag = fields[1];
            }

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

            if (!wordTagsMap.containsKey(word)) {
                wordTagsMap.put(word, new HashSet<>());
            }
            wordTagsMap.get(word).add(tag);

            if (!tagTagCntMap.containsKey(preTag)) {
                tagTagCntMap.put(preTag, new HashMap<>());
            }
            tempCntMap = tagTagCntMap.get(preTag);
            if (!tempCntMap.containsKey(tag)) {
                tempCntMap.put(tag, 1);
            } else {
                tempCntMap.put(tag, tempCntMap.get(tag) + 1);
            }

            preTag = tag;
        }
    }

    private static void tagging(String targetFilePath) throws IOException {
        File targetFile = new File(targetFilePath);
        List<String> words = Files.readAllLines(targetFile.toPath(), StandardCharsets.UTF_8);

        List<String> sentence = new ArrayList<>();
        Map<String, String> results = new HashMap<>();

        for (String word : words) {
            word = word.trim();
            if (word.equals("")) {
                // process sentence and do tagging
                results.putAll(runViterbi(sentence));
                results.put("", "");
                sentence.clear();
            } else {
                sentence.add(word);
            }
        }

        // process the last sentence
        if (!sentence.isEmpty()) {
            results.putAll(runViterbi(sentence));
        }

        // print results to file
        for (Map.Entry<String, String> entry : results.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    private static Map<String, String> runViterbi(List<String> sentence) {
        int size = sentence.size();
        List<HashMap<String, Double>> viterbi = new ArrayList<>();
        List<HashMap<String, String>> backpointer = new ArrayList<>();
        for (int i = 0; i <= size; i++) {
            viterbi.add(new HashMap<>());
            backpointer.add(new HashMap<>());
        }

        viterbi.get(0).put("", 1.0);
        backpointer.get(0).put("", "");

        for (int i = 1; i <= sentence.size(); i++) {
            String word = sentence.get(i-1);
            // if this is unknown word, ignore
            if (!wordTagsMap.containsKey(word)) {
                continue;
            }

            Set<String> tags = wordTagsMap.get(word);
            for (String tag : tags) {
                double max = 0.0;
                viterbi.get(i).put(tag, max);
                for (String preTag : viterbi.get(i-1).keySet()) {
                    double val = viterbi.get(i-1).get(preTag);
                    if (!tagTagCntMap.containsKey(preTag) || !tagCntMap.containsKey(preTag)) {
                        System.err.println ("Unknown tag: '" + preTag + "'");
                        System.exit(1);
                    }
                    if (!tagTagCntMap.get(preTag).containsKey(tag)
                            || !tagWordCntMap.containsKey(tag)
                            || !tagCntMap.containsKey(tag)) {
                        System.err.println ("Unknown tag: '" + tag + "'");
                        System.exit(1);
                    }
                    // transition possibility
                    double pSS = tagTagCntMap.get(preTag).get(tag) / tagCntMap.get(preTag);
                    // emission possibility
                    double pTokenS;
                    if (tagWordCntMap.get(tag).containsKey(word)) {
                       pTokenS = tagWordCntMap.get(tag).get(word) / tagCntMap.get(tag);
                    } else {
                        pTokenS = 0;
                    }

                    double total = val * pSS * pTokenS;
                    if (max < total) {
                        max = total;
                        backpointer.get(i).put(tag, preTag);
                        viterbi.get(i).put(tag, max);
                    }
                }
            }
        }

        // trace back from the end of the sentence
        Map<String, String> temp = new LinkedHashMap<>();
        Map<String, String> result = new LinkedHashMap<>();

        if (backpointer.get(size).size() > 1 || !backpointer.get(size).containsKey(".")) {
            System.err.println ("The sentence is not only ended with '.'");
            System.err.println ("The sentence is ended with:");
            for (String tag : backpointer.get(size).keySet()) {
                System.err.print("\t" + tag);
            }
            System.err.println();
            System.exit(1);
        }
        String tag = ".";
        String word = ".";
        temp.put(word, tag);
        while (!tag.equals("")) {
            tag = backpointer.get(size).get(tag);
            word = sentence.get(size-1);
            temp.put(word, tag);
            size--;
        }

        // reverse linked hashmap
        for (Map.Entry<String, String> entry : temp.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
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
