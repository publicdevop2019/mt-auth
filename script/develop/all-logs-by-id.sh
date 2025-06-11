#!/bin/sh
cd ../../logs

grep -E "$1" proxy.log > analytics/all.$1.log

for filename in archived/proxy*.log; do
    grep -E "$1" $filename >> analytics/all.$1.log
done
echo "\n-----------------------------\n" >> analytics/all.$1.log
grep -E "$1" access.log >> analytics/all.$1.log

for filename in archived/access*.log; do
    grep -E "$1" $filename >> analytics/all.$1.log
done