userHome=/Users/dford

cvsRoot=/Users/dford/cvsCheckouts/CVSROOT

apps=$cvsRoot/TMS/apps
frameworks=$cvsRoot/TMS/framework
lib=$cvsRoot/lib

repos=$userHome/repos
repo=$repos/threed

src1=/Users/dford/repos/nfc/nfc/src/java

echo src1 $src1

src="$src1"

gwtVersion=2.2.0
gwtHome=$userHome/p-java/gwt-$gwtVersion



lib1=$gwtHome/gwt-dev.jar
lib2=$gwtHome/gwt-user.jar
lib3=$gwtHome/gwt-servlet.jar
lib4=$gwtHome/gwt-servlet-deps.jar
lib5=$lib/jsr305/1.0/jsr305.jar


modName=com.tms.threed.previewPaneAdapter.PreviewPaneAdapter

war=$userHome/p-java/apache-tomcat-6.0.10/webapps/previewPaneAdapter/
extra=$userHome/temp

g1="-war $war "
g3="-startupUrl $startupUrl "
g4="-noserver "
g5="-logLevel DEBUG "
g6="-extra /Users/dford/temp/extra "

j0="-Xmx512m "
j1="-DconfigDir=/Users/dford/temp/tmsConfig "
j2="-Dlog4j.configuration=file:///Users/dford/temp/tmsConfig/log4j/threed_framework_log4j.xml "

libs=$lib1:$lib2:$lib3:$lib4:$lib5:$lib6
cp="$src:$libs"

gwtDevModeParams="$g1 $g3 $g4 $g5 $g6 $modName"
gwtCompileParams="$g1 $g2 $g6 -style OBF -XdisableCastChecking $modName"

gwtParams=$gwtDevModeParams
mainClass=com.google.gwt.dev.DevMode
gwtParams=$gwtCompileParams
mainClass=com.google.gwt.dev.Compiler

jvmParams="$j3 $j0 $j1 $j2"

echo $jvmParams

JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home
export JAVA_HOME

finalCommand="java $jvmParams -classpath $cp $mainClass $gwtParams"

echo $finalCommand

$finalCommand