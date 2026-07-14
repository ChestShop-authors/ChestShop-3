#!/bin/bash

build_release() {
  platform=$1
  echo "Creating jar for $platform"
  mkdir -p target/releases/$platform
  rm -f target/releases/$platform/ChestShop.jar
  cp target/ChestShop.jar target/releases/$platform/ChestShop.jar
  unzip -o target/releases/$platform/ChestShop.jar "META-INF/MANIFEST.MF" -d .
  sed -i -E "s/Distribution-Type: \w+/Distribution-Type: $platform/g" META-INF/MANIFEST.MF
  zip -m --update target/releases/$platform/ChestShop.jar META-INF/MANIFEST.MF
  rm -d META-INF
}

echo Current version:
version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo $version
if [[ "$version" == *"-SNAPSHOT" ]] ; then
  echo "Not a release"
  exit 0;
fi

echo "Detected release. Building platform release jars..."

if [[ ! -f target/ChestShop.jar ]]; then
  echo "No built jar file found at target/ChestShop.jar! Aborting."
  exit 0;
fi

build_release 'spigot'
build_release 'devbukkit'
build_release 'modrinth'
build_release 'github'
build_release 'hangar'

echo "Release jar files created in target/releases/"