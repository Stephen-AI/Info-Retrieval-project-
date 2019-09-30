#!/bin/bash
EID=''
PROJNUM=""
CLASSLIST=""
MAINCLASS=""
CORPORA=""
TRACENAME=""
while getopts e:n:l:m:c:t: o
do
    case $o in
        e) EID="$OPTARG";;
        n) PROJNUM="$OPTARG";;
        l) CLASSLIST="$OPTARG";;
        m) MAINCLASS="$OPTARG";;
        c) CORPORA="$OPTARG";;
        t) TRACENAME="$OPTARG";;
    esac
done
shift $OPTIND-1
python3 submit.py -eid $EID -projnum $PROJNUM -classlist $CLASSLIST
trace_file=$(echo "proj${PROJNUM}_${EID}_${TRACENAME}_trace.txt" | tr -d "\r")
echo $trace_file
main_class=$(find ir -name "${MAINCLASS}")
IFS='.' # hyphen (-) is set as delimiter
read -ra ADDR <<< "$main_class"
class="${ADDR[0]}"
script -c "java ${class} -html -proximity ${CORPORA}" "${trace_file}"
