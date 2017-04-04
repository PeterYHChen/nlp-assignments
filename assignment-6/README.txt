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

---- Features: token, posTag, chunkTag, nameTag, (preToken, nextToken), (prePosTag, nextPosTag), (preChunkTag, nextChunkTag) 
---- Without common name list

        49456 out of 51578 tags correct
          accuracy: 95.89
        5917 groups in key
        5834 groups in response
        4403 correct groups
          precision: 75.47
          recall:    74.41
          F1:        74.94

---- Features: token, posTag, chunkTag, nameTag 
---- Without common name list

        48926 out of 51578 tags correct
          accuracy: 94.86
        5917 groups in key
        6443 groups in response
        4102 correct groups
          precision: 63.67
          recall:    69.33
          F1:        66.38

---- Features: token, posTag, chunkTag, nameTag, (preToken, nextToken)
---- Without common name list

        49625 out of 51578 tags correct
          accuracy: 96.21
        5917 groups in key
        6023 groups in response
        4538 correct groups
          precision: 75.34
          recall:    76.69
          F1:        76.01

---- Features: token, posTag, chunkTag, nameTag, (prePosTag, nextPosTag)
---- Without common name list

        49116 out of 51578 tags correct
          accuracy: 95.23
        5917 groups in key
        6304 groups in response
        4200 correct groups
          precision: 66.62
          recall:    70.98
          F1:        68.73

---- Features: token, posTag, chunkTag, nameTag, (preChunkTag, nextChunkTag)
---- Without common name list

        49047 out of 51578 tags correct
          accuracy: 95.09
        5917 groups in key
        6521 groups in response
        4209 correct groups
          precision: 64.55
          recall:    71.13
          F1:        67.68

---- Features: token, nameTag, (preToken, nextToken)
---- Without common name list

        49062 out of 51578 tags correct
          accuracy: 95.12
        5917 groups in key
        5201 groups in response
        4108 correct groups
          precision: 78.98
          recall:    69.43
          F1:        73.90

---- Features: token, posTag, nameTag, (preToken, nextToken)
---- Without common name list

        49686 out of 51578 tags correct
          accuracy: 96.33
        5917 groups in key
        6015 groups in response
        4586 correct groups
          precision: 76.24
          recall:    77.51
          F1:        76.87

---- Features: token, chunkTag, nameTag, (preToken, nextToken)
---- Without common name list

        49003 out of 51578 tags correct
          accuracy: 95.01
        5917 groups in key
        5018 groups in response
        4031 correct groups
          precision: 80.33
          recall:    68.13
          F1:        73.73

---- Features: token, posTag, nameTag, (preToken, nextToken), (prePosTag, nextPosTag)
---- Without common name list

        49578 out of 51578 tags correct
          accuracy: 96.12
        5917 groups in key
        5885 groups in response
        4502 correct groups
          precision: 76.50
          recall:    76.09
          F1:        76.29

-------------------------------------------------------------------------------------------------------
