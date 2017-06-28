#!/usr/bin/env bash

ROBOCODE_VERSION="1.9.2.6"


if [ ! -f robocode/robocode.sh ]; then

    JAR_NAME="robocode-${ROBOCODE_VERSION}-setup.jar"
    wget "https://netcologne.dl.sourceforge.net/project/robocode/robocode/${ROBOCODE_VERSION}/${JAR_NAME}"
    mkdir robocode
    unzip -o ${JAR_NAME} -d robocode
    chmod +x ./robocode/*.sh
fi

cp -R target/classes/* robocode/robots/

sed -e "s/\${robot}/$1/" ./battles/sample.battle > battle.battle

sh -c "./robocode/robocode.sh -battle ../battle.battle -nodisplay -nosound" > battle_result.txt

cat battle_result.txt

## The line that verifies if we win
grep "1st: com.gft.codejam.roomba.Roomba" battle_result.txt > /dev/null 2> /dev/null




