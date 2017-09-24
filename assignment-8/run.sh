java -cp $JET_HOME/build/main:$JET_HOME/jet-all.jar:$JET_HOME/lib/* -Xmx1600m -server -DjetHome=$JET_HOME edu.nyu.jet.aceJet.Ace /home/yonghong/Apps/jet/props/tagLDP.jet reside-filelist reside-docs/ reside-triples/
echo "-------------------------------------------------------"
cat reside-triples/* 
echo "-------------------------------------------------------"
cat reside-triples/* | sort - | uniq | comm -12 - reside-key
