1) Here are a few commands to run on the test corpus. If you want to run on the development corpus, simply change <WSJ_23> to <wSJ_24>

---- compile java files
        javac -cp maxent-3.0.0.jar:trove.jar *.java

---- build features for training corpus and target file (my FeatureBuilder only accept one file at a time)
        java FeatureBuilder CONLL_train.pos-chunk-name
        java FeatureBuilder CONLL_test.pos-chunk

---- train and tag
        java -cp maxent-3.0.0.jar:trove.jar:. MEtrain CONLL_train.enhanced MEModel
        java -cp maxent-3.0.0.jar:trove.jar:. MEtag CONLL_test.enhanced MEModel response.name


2) Below are all the feature set that I tried and results. The best result I got is F1=

---- Features: posTag, chunkTag, preToken, nextToken, prePosTag, nextPosTag, preChunkTag, nextChunkTag, preNameTag, nextNameTag, nameTag 
---- Without common name list

        48781 out of 51578 tags correct
          accuracy: 94.58
        5917 groups in key
        5686 groups in response
        4159 correct groups
          precision: 73.14
          recall:    70.29
          F1:        71.69

-------------------------------------------------------------------------------------------------------
