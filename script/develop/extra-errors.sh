ERROR_EXP='WARN|ERROR|^\tat |Exception|^Caused by: |\t... \d+ more'

#!/bin/sh
cd ../../logs
grep -E "$ERROR_EXP" access.log > analytics/access-error.log

for filename in archived/access*.log; do
    grep -E "$1" $filename >> analytics/access.$1.log
done