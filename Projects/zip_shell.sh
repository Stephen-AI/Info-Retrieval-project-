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
HTML=""
while getopts p:m:u:t:c:d:w:r:h:* o
do
    case $o in
        p) prefix="$OPTARG";;
        m) MAINCLASS="$OPTARG";;
        t) TRACENAME="$OPTARG";;
        u) U="-u $OPTARG";;
        c) C="-c $OPTARG";;
        d) D="-d $OPTARG";;
        w) WEIGHT="-weight $OPTARG";;
        h) HTML="-html";;
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
script -c "java ${class} ${U} ${C} ${D} ${WEIGHT} ${HTML} ${CORPORA}" "${trace_file}"