#!/bin/sh
echo "versionFile : $versionFile"
echo "parameterFile : $configFile"
echo "versionFileName : $versionFileName"
echo "parameterFileName : $configFileName"
installWorkDir=/usr/local/workdir/winplan2
echo "clear workdir:"$installWorkDir
cd $installWorkDir
rm -rf * "$installWorkDir/*"
echo "unwar static war file"
cp /usr/local/workdir/winplan2.war $installWorkDir
jar -xvf $installWorkDir/winplan2.war
rm -rf winplan2.war
echo "unwar new war file"
cp $versionFile $installWorkDir
jar -xvf $versionFileName
rm -rf $versionFileName
cd $installWorkDir
jar -cvf winplan2.war ./
cp -a $installWorkDir/winplan2.war /usr/local/wars
wTomcat=/usr/local/tomcat8181
cd $wTomcat/bin
./shutdown.sh
rm -rf $wTomcat/webapps/*
rm -rf $wTomcat/work/*
rm -rf $wTomcat/logs/*
./startup.sh
unset wTomcat
unset installWorkDir
echo process complete.
