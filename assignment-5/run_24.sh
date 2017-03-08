# compile java files
javac -cp maxent-3.0.0.jar:trove.jar *.java

# build features for training corpus and target file
java FeatureBuilder WSJ_02-21.pos-chunk
java FeatureBuilder WSJ_24.pos 

# train and tag
java -cp maxent-3.0.0.jar:trove.jar:. MEtrain WSJ_02-21.enhanced MEModel
java -cp maxent-3.0.0.jar:trove.jar:. MEtag WSJ_24.enhanced MEModel response.chunk

# scoring
python score.chunk.py