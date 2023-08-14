#!/bin/bash
cd ../../logs
for filename in test-run*.log; do
    grep -E "$1" $filename >> analytics/test.$1.log
done