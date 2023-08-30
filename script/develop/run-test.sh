cd ../../mt-integration-test
rm -rf ../logs/analytics
mkdir ../logs/analytics
for VARIABLE in 1 2 3 4 5
# for VARIABLE in 1
do
    mvn clean test > ../logs/test-run.$VARIABLE.log
done