#!/bin/bash
prefix=""
MAINCLASS=""
CORPORA=""
TRACENAME=""
U=""
C=""
D=""
WEIGHT=""
CORPORA=""
while getopts p:m:u:t:c:d:w:r:* o
do
    case $o in
        p) prefix="$OPTARG";;
        m) MAINCLASS="$OPTARG";;
        t) TRACENAME="$OPTARG";;
        u) U="-u $OPTARG";;
        c) C="-c $OPTARG";;
        d) D="-d $OPTARG";;
        w) WEIGHT="-weight $OPTARG";;
        r) CORPORA="$OPTARG";;
    esac
done
shift $OPTIND-1
trace_file="${prefix}_${TRACENAME}_trace.txt"
main_class=$(find ir -name "${MAINCLASS}")
IFS='.' # hyphen (-) is set as delimiter
read -ra ADDR <<< "$main_class"
class="${ADDR[0]}"
echo "java ${class} ${U} ${C} ${D} ${WEIGHT} ${CORPORA}"
script -c "java ${class} ${U} ${C} ${D} ${WEIGHT} ${CORPORA}" "${trace_file}"