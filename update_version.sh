#!/bin/bash
echo Current version:
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
echo ""
echo Input new version:
read version
mvn versions:set -DnewVersion=${version}
mvn -N versions:update-child-modules
mvn versions:commit