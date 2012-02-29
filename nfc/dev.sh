userHome=/Users/dford

cvsRoot=$userHome/cvsCheckouts/CVSROOT

apps=$cvsRoot/TMS/apps
frameworks=$cvsRoot/TMS/framework
lib=$cvsRoot/lib

repos=$userHome/repos
repo=$repos/threedScion


echo src1 $src1

src1=$repo/nfc/src/java

src="$src1"

gwtVersion=2.4.0
gwtHome=$userHome/p-java/gwt-$gwtVersion



lib1=$gwtHome/gwt-dev.jar
lib2=$gwtHome/gwt-user.jar
lib3=$gwtHome/gwt-servlet.jar
lib4=$gwtHome/gwt-servlet-deps.jar
lib5=$gwtHome/validation-api-1.0.0.GA.jar
lib6=$gwtHome/validation-api-1.0.0.GA-sources.jar
lib7=$lib/jsr305/1.0/jsr305.jar


modName=com.tms.threed.threedAdmin.ThreedAdmin
startupUrl="http://localhost:8080/threed-admin/index.html"

bindAddress=10.211.55.2
war=$userHome/p-java/apache-tomcat-6.0.10/webapps/threed-admin/
extra=$userHome/temp

g0="-bindAddress $bindAddress "
g1="-war $war "
g3="-startupUrl $startupUrl "
g4="-noserver "
g5="-logLevel INFO "
g6="-extra /Users/dford/temp/extra "

j0="-Xmx512m "
j1="-DconfigDir=/Users/dford/temp/tmsConfig "
j2="-Dlog4j.configuration=file:///Users/dford/temp/tmsConfig/log4j/threed_framework_log4j.xml "

libs=$lib1:$lib2:$lib3:$lib4:$lib5:$lib6:$lib7
cp="$src:$libs"

gwtDevModeParams="$g1 $g3 $g4 $g5 $g6 $modName"
gwtCompileParams="$g1 $g2 $g6 -draftCompile -localWorkers 2 -style OBF -XdisableCastChecking $modName"

gwtParams=$gwtDevModeParams
mainClass=com.google.gwt.dev.DevMode

gwtParams=$gwtCompileParams
mainClass=com.google.gwt.dev.Compiler

jvmParams="$j3 $j0 $j1 $j2"

echo $jvmParams

#Java vars


JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
export JAVA_HOME

finalCommand="java $jvmParams -classpath $cp $mainClass $gwtParams"

echo $finalCommand

$finalCommand