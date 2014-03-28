# !/bin/bash

LOC=`find ./ -name "*.java" -type f | xargs cat | wc -l`
echo "Project LOC = $LOC"

