1) Here are a few commands to run on the test corpus. If you want to run on the development corpus, simply change <WSJ_23> to <wSJ_24>

---- compile java files
        javac -cp maxent-3.0.0.jar:trove.jar *.java

---- build features for training corpus and target file (my FeatureBuilder only accept one file at a time)
        java FeatureBuilder WSJ_02-21.pos-chunk
        java FeatureBuilder WSJ_23.pos 

---- train and tag
        java -cp maxent-3.0.0.jar:trove.jar:. MEtrain WSJ_02-21.enhanced MEModel
        java -cp maxent-3.0.0.jar:trove.jar:. MEtag WSJ_23.enhanced MEModel response.chunk


2) Below are all the feature set that I tried and results. The best result I got is F1=91.04, and using previous token, posTag, or chunkTag for training is better than using the next one.

---- Features: posTag, chunkTag

        28792 out of 32853 tags correct
          accuracy: 87.64
        8378 groups in key
        9725 groups in response
        6928 correct groups
          precision: 71.24
          recall:    82.69
          F1:        76.54

---- Features: posTag, preToken, nextToken, chunkTag

        31360 out of 32853 tags correct
          accuracy: 95.46
        8378 groups in key
        8752 groups in response
        7621 correct groups
          precision: 87.08
          recall:    90.96
          F1:        88.98

---- Features: posTag, prePosTag, nextPosTag, chunkTag

        31371 out of 32853 tags correct
          accuracy: 95.49
        8378 groups in key
        8769 groups in response
        7567 correct groups
          precision: 86.29
          recall:    90.32
          F1:        88.26

---- Features: posTag, preChunkTag, nextChunkTag, chunkTag

        30562 out of 32853 tags correct
          accuracy: 93.03
        8378 groups in key
        8616 groups in response
        7110 correct groups
          precision: 82.52
          recall:    84.87
          F1:        83.68

---- Features: posTag, preToken, prePosTag, preChunkTag, chunkTag

        31492 out of 32853 tags correct
          accuracy: 95.86
        8378 groups in key
        8627 groups in response
        7669 correct groups
          precision: 88.90
          recall:    91.54
          F1:        90.20

---- Features: posTag, nextToken, nextPosTag, nextChunkTag, chunkTag

        28972 out of 32853 tags correct
          accuracy: 88.19
        8378 groups in key
        10118 groups in response
        6519 correct groups
          precision: 64.43
          recall:    77.81
          F1:        70.49

-------------------------------------------------------------------------------------------------------
---- Features: posTag, preToken, nextToken, prePosTag, nextPosTag, preChunkTag, nextChunkTag, chunkTag

        31637 out of 32853 tags correct
          accuracy: 96.30
        8378 groups in key
        8454 groups in response
        7662 correct groups
          precision: 90.63
          recall:    91.45
          F1:        91.04
-------------------------------------------------------------------------------------------------------
