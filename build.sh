#!/usr/bin/env bash

set -e

BASE_DIR=$(pwd)
echo "base dir: $BASE_DIR"
OUTPUT_ROOT=$BASE_DIR
mkdir -p dist


function buildCloudRasp {
	cd "$BASE_DIR" || exit 1
	echo "mvn clean package -DskipTests"
	mvn clean package -DskipTests || exit 1
	cp $BASE_DIR/rasp-core/target/rasp-core.jar $OUTPUT_ROOT/dist || exit 1
	cp $BASE_DIR/rasp-loader/target/rasp-loader.jar $OUTPUT_ROOT/dist || exit 1

}

buildCloudRasp