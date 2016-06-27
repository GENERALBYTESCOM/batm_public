#!/bin/bash
project="$1"
file="$2"
groupId="$3"
artifactId="$4"
version="$5"
directory="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo directory: $directory

if [ -z "$5" ] ; then
    echo 'This script is used to install dependencies to the local maven repository.'
    echo 'usage: install-lib.sh project file groupId artifactId version'
    echo ''
    echo 'project is either api, extra, or test'
    echo ''
    echo 'example: install-lib.sh api mail-1.4.7.jar javax.mail mail 1.4.7'
    echo ''
    exit 1
fi

if [ "$1" == "api" ] || [ "$1" == "extra" ] || [ "$1" == "test" ] ; then

    localRepositoryPath="$directory/../server_extensions_$1/libs"

    mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
      -Dfile=$file \
      -DgroupId=$groupId \
      -DartifactId=$artifactId \
      -Dversion=$version-BATM \
      -Dpackaging=jar \
      -DlocalRepositoryPath=$localRepositoryPath

    echo ''
    echo '...installed, now add following to the pom.xml:'
    echo ''
    echo '<dependency>'
    echo "  <groupId>$groupId</groupId>"
    echo "  <artifactId>$artifactId</artifactId>"
    echo "  <version>$version-BATM</version>"
    echo '</dependency>'
    echo ''

else
    echo 'project needs to be either api, extra, or test'
fi

exit 1

