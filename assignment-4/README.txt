To run my code in Java, 
    javac PosTagger.java
    java PosTagger WSJ_02-21.pos WSJ_23.words

NOTE:
1) PosTagger class accepts 2 arguments, the first one is training corpus (<WSJ_02-21.pos>), the second argument is the document to be tagged (<WSJ_23.words>).
1) File <WSJ_23.pos> will be generated for you to score.

-------------------------------------------------------------------------CONCLUSION----------------------------------------------------------------------
I successfully implemented and trained a part-of-speech tagger using the Viterbi algorithm.

After running on WSJ_24.words, I got an output file WSJ_24.pos, and I'm able to calculate the score.
By treating unknown words as NNP, I got the following scores:
        30860 out of 32853 tags correct
          accuracy:    93.93

By treating unknown words as NNP, I got the following scores:
        31108 out of 32853 tags correct
          accuracy:    94.69

To achieve this accuracy, I accomendate additional features:
1)  If the viterbi value of a word A in a sentence is 0, all following words of A in this sentence will get 0 viterbi value because this is the only path. 
    To avoid bad tagging, I pick the most possible tag for this word A based on the traning corpus (the occurrences of different tags for the same word).

2)  After tagging the words, I compute P(tag|word) for each word, and apply a weight to adjust the tagging results.