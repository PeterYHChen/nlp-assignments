# compile java files
javac -cp maxent-3.0.0.jar:trove.jar *.java

# build features for training corpus and target file
java FeatureBuilder CONLL_train.pos-chunk-name
java FeatureBuilder CONLL_dev.pos-chunk

# train and tag
java -cp maxent-3.0.0.jar:trove.jar:. MEtrain CONLL_train.enhanced MEModel
java -cp maxent-3.0.0.jar:trove.jar:. MEtag CONLL_dev.enhanced MEModel response.name

# scoring
python score.name.py

# # adjust name tag and score again
# java NameTagAdjuster response.name
# python score.name.py
