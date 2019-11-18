#!/bin/bash
prefix=""
MAINCLASS=""
CORPORA=""
TRACENAME=""
K=""
N=""

while getopts p:m:k:t:n:* o
do
    case $o in
        p) prefix="$OPTARG";;
        m) MAINCLASS="$OPTARG";;
        t) TRACENAME="$OPTARG";;
        k) K="-K $OPTARG";;
        n) N="-neg";;
    esac
done
shift $OPTIND-1
trace_file="${prefix}_${TRACENAME}_trace.txt"
main_class=$(find ir -name "${MAINCLASS}")
IFS='.' # hyphen (-) is set as delimiter
read -ra ADDR <<< "$main_class"
class="${ADDR[0]}"
echo "java ${class} ${K} ${N} ${WEIGHT} ${CORPORA}"
script -c "java ${class} ${K} ${N}" "${trace_file}"