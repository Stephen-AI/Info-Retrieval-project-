#!/bin/bash
EID=''
PROJNUM=""
CLASSLIST=""
MAINCLASS=""
CORPORA=""
while getopts e:n:l:m:c: o
do
    case $o in
        e) EID="$OPTARG";;
        n) PROJNUM="$OPTARG";;
        l) CLASSLIST="$OPTARG";;
        m) MAINCLASS="$OPTARG";;
        c) CORPORA="$OPTARG";;
    esac
done
shift $OPTIND-1
python3 submit.py -eid $EID -projnum $PROJNUM -classlist $CLASSLIST
trace_file=$(echo "proj${PROJNUM}_${EID}_experiment1_trace.txt" | tr -d "\r")
echo $trace_file
main_class=$(find ir -name "${MAINCLASS}")
IFS='.' # hyphen (-) is set as delimiter
read -ra ADDR <<< "$main_class"
class="${ADDR[0]}"
script -c "java ${class} -html ${CORPORA}" "${trace_file}"
