# !/bin/bash

if [[ -z $1 ]]; then
  echo "Provide previous version as first command line argument."
  exit -1
fi

if [[ -z $2 ]]; then
  echo "Provide current version as second command line argument."
  exit -1
fi

echo "Using previous version=$1 and current version=$2"

find ./ -name "*.xml" | xargs sed -i '' "s/$1/$2/g"

