#!/bin/bash
cd ../../logs
grep -E "$1" proxy.log > analytics/proxy.$1.log

for filename in archived/proxy*.log; do
    grep -E "$1" $filename >> analytics/proxy.$1.log
done