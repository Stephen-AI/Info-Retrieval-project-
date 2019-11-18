EID="sa46979"
PROJNUM="4"
PREFIX="proj${PROJNUM}_${EID}"
MAIN_TRACE="${PREFIX}_trace.txt"
TEMP_TRACE="${PREFIX}_temp_trace.txt"

python3 submit.py -eid $EID -projnum $PROJNUM -classlist sources.txt
rm $MAIN_TRACE
touch $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestNaiveBayes.java -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestKNN.java -k 1 -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestKNN.java -k 3 -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestKNN.java -k 5 -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestRocchio.java -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
./zip_shell.sh -p $PREFIX -m TestRocchio.java -n 0 -t temp
cat $TEMP_TRACE >> $MAIN_TRACE
gnuplot "combineGraphs/combined.gplot" | ps2pdf - "test-graph.pdf"
gnuplot "combineGraphs/combinedTrain.gplot" | ps2pdf - "train-graph.pdf"
rm $TEMP_TRACE
rm *.data
rm *.gplot
notify-send "Project 4 script done"
