#!/bin/sh

echo "[Dependency installer]"
read -p "What is your dependency's file name? " filename

if [ "$filename" = "" ]; then
    return
fi
    
read -p "What is your dependency's group ID? " groupID
read -p "What is your dependency's artifact ID? " artifactID
read -p "What is your dependency's version? " version

mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=$filename -DgroupId=$groupID -DartifactId=$artifactID -Dversion=$version

echo "[Dependency installed!]"