#!/bin/sh
cd ../../logs
grep -E "$1" access.log > analytics/access.$1.log

for filename in archived/access*.log; do
    grep -E "$1" $filename >> analytics/access.$1.log
done