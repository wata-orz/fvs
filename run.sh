#!/bin/bash

base=$(cd $(dirname $0); pwd)
java -Xss256M -XX:+UseSerialGC -cp "$base/bin" Main "$@"

