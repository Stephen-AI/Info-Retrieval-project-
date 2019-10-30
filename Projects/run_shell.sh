#Put your information here
EID="sa46979"
PROJNUM="2"

ZIPINDIR="proj2_sa46979_outputs"
GRAPHDIR="graphs"
rm proj2_sa46979_exp_trace.txt
./run-with-input.sh -e $EID -f "" -m InvertedIndexRated.java -n $PROJNUM -l sources.txt -t rated -c /u/mooney/ir-code/corpora/cs-faculty
for i in {1..5..2}
    do
        mkdir "n${i}"
        mkdir -p "${ZIPINDIR}/n${i}"
        mkdir -p "${GRAPHDIR}/n${i}"
        ./zip_shell.sh -e $EID -m ExperimentRelFeedbackRated.java -n $PROJNUM -l sources.txt -t ratedExp -f ${i}
        cp "n${i}/rated" "${ZIPINDIR}/n${i}/"
        cp "n${i}/rated.ndcg" "${ZIPINDIR}/n${i}/"
        ./zip_shell.sh -e $EID -m ExperimentRelFeedbackRated.java -b 0 -n 2 -l sources.txt -t binary -f ${i}
        cp "n${i}/binary" "${ZIPINDIR}/n${i}/"
        cp "n${i}/binary.ndcg" "${ZIPINDIR}/n${i}/"
        ./zip_shell.sh -e $EID -m ExperimentRelFeedbackRated.java -z 0 -n 2 -l sources.txt -t control -f ${i}
        #copy for submitting format
        cp "n${i}/control" "${ZIPINDIR}/n${i}/"
        cp "n${i}/control.ndcg" "${ZIPINDIR}/n${i}/"
        cd "n${i}"
        #plot graphs
        gnuplot "../combined.gplot" | ps2pdf - "../${GRAPHDIR}/n${i}/combined-n${i}.pdf"
        gnuplot "../combined.ndcg.gplot" | ps2pdf - "../${GRAPHDIR}/n${i}/combinedNDCG-n${i}.pdf"
        cd ..
    done
cd "${ZIPINDIR}"
zip -r "${ZIPINDIR}.zip" n1 n3 n5
mv "${ZIPINDIR}.zip" ..
cd ..
