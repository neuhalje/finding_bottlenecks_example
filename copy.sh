#!/bin/bash


# build

if [ ! -f ./build/libs/synthetic-io-emulator-1.0.1-all.jar ]
  then
  ./gradlew shadowJar
fi

if [ "x$3" == "x" ]
then
  echo "$0 [in_blocks|buffered|unbuffered] sourceFile destFile"
else
java -jar ./build/libs/synthetic-io-emulator-1.0.1-all.jar \
   "$1" \
   "$2" "$3"
fi

# verify
diff "$2" "$3" >/dev/null
[ $? -eq 0 ] || echo ERROR: The files are not identical!
