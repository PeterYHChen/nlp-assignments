/*
 * scorer for NLP class Spring 2016
 * ver.1.0
 *
 * score a key file against a response file
 * both should consist of lines of the form:   token \t tag
 * sentences are separated by empty lines
 */

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class Score {

    public static void main (String[] args) throws IOException {
    if (args.length != 2) {
        System.err.println("Score takes 2 arguments:  java Score key response");
        System.exit(1);
    }
    String keyFileName = args[0];
    File keyFile = new File(keyFileName);
    List<String> key = Files.readAllLines(keyFile.toPath(), StandardCharsets.UTF_8);
    String responseFileName = args[1];
    File responseFile = new File(responseFileName);
    List<String> response = Files.readAllLines(responseFile.toPath(), StandardCharsets.UTF_8);
    if (key.size() != response.size()) {
        System.err.println ("length mismatch between key and submitted file");
        System.exit(1);
    }
    int correct = 0;
    int incorrect = 0;
    for (int i = 0; i < key.size(); i++) {
        String keyLine = key.get(i).trim();
        String responseLine = response.get(i).trim();
        if (keyLine.equals("")) {
        if (responseLine.equals("")) {
            continue;
        } else {
            System.err.println ("sentence break expected at line " + i);
            System.exit(1);
        }
        }
        String[] keyFields = keyLine.split("\t");
        if (keyFields.length != 2) {
        System.err.println ("format error in key at line " + i + ":" + keyLine);
        System.exit(1);
        }
        String keyToken = keyFields[0];
        String keyPos = keyFields[1];
        String[] responseFields = responseLine.split("\t");
        if (responseFields.length != 2) {
            System.err.println ("format error in response at line " + i + ":" + responseLine);
            System.exit(1);
        }
        String responseToken = responseFields[0];
        String responsePos = responseFields[1];
        if (!responseToken.equals(keyToken)) {
        System.err.println ("token mismatch at line " + i);
        System.exit(1);
        }
        if (responsePos.equals(keyPos)) {
        correct = correct + 1;
        } else {
        incorrect = incorrect + 1;
        }
    }
    System.out.println (correct + " out of " + (correct + incorrect) + " tags correct");
    float accuracy = (float) 100.0 * correct / (correct + incorrect);
    System.out.printf ("  accuracy: %8.2f\n", accuracy);
    }
}
// java Score WSJ_POS_CORPUS_FOR_STUDENTS/WSJ_24.pos WSJ_24.pos