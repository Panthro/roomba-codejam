#!/usr/bin/env bash

ROBOCODE_VERSION="1.9.2.6"

JAR_NAME="robocode-${ROBOCODE_VERSION}-setup.jar"

rm robocode-*setup.jar

wget "https://netcologne.dl.sourceforge.net/project/robocode/robocode/${ROBOCODE_VERSION}/${JAR_NAME}"

rm -rf bin

mkdir bin

unzip -o ${JAR_NAME} -d bin

cp -R target/classes/* bin/robots/

chmod +x ./bin/*.sh


sed -e "s/\${robot}/$1/" ./battles/sample.battle > battle.battle

sh -c "./bin/robocode.sh -battle ../battle.battle -nodisplay -nosound" > battle_result.txt

cat battle_result.txt

## The line that verifies if we win
grep "1st: com.gft.codejam.roomba.Roomba" battle_result.txt > /dev/null 2> /dev/null



