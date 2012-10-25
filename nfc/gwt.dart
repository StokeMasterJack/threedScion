#!/usr/bin/env dart

#import('dart:io');

var appName;
var modName;
var startupPage;
var contextPath;

void main(){

    appName = "c3i";




    //gwt demo
    contextPath = "smartClient";
    modName = "smartClient.SmartClientDemoGwt";
    startupPage =  "DemoGwt";


    //SmartClientExportJavaScript
    contextPath = "smartClient";
    modName = "smartClient.SmartClientExportJavaScript";
    startupPage =  "demo/widget/toyota/demo";

    //threed admin
    contextPath = "threed-admin-v2";
    modName = "admin.ThreedAdmin";
    startupPage =  "index";


    var options = new Options();
    List<String> args = options.arguments;
    var devMode = args.length==0?false:args.last()=="dev";

    var modFullName = '${appName}.${modName}';  //threed.smartClient.SmartClient

    var userHome = '/Users/dford';
    var cvsRoot = '$userHome/cvsCheckouts/CVSROOT';
    var repos = '$userHome/repos';

    var apps = '$cvsRoot/TMS/apps';
    var frameworks = '$cvsRoot/TMS/framework';
    var lib = '/Users/dford/daven-repository';

    var repo1 = '$repos/threedScion';
    var repo2 = '$repos/util';

    var src1 = '$repo1/nfc/src/java';
    var src2 = '$repo2/util/src/java';
    var src = '$src1:$src2';

    var gwtVersion = '2.5.0.rc1';
    var gwtHome = '${userHome}/p-java/gwt-${gwtVersion}';
    
    var libs = [
      '$gwtHome/gwt-dev.jar',
      '$gwtHome/gwt-user.jar',
      '$gwtHome/gwt-servlet.jar',
      '$gwtHome/gwt-servlet-deps.jar',
      '$gwtHome/validation-api-1.0.0.GA.jar',
      '$gwtHome/validation-api-1.0.0.GA-sources.jar',
      '$lib/jsr305/1.0/jsr305.jar',
      '$lib/guava/11.0/guava-11.0.2.jar',
      '$lib/guava/11.0/guava-gwt-11.0.2.jar',
      '$lib/gwtexporter/SNAPSHOT/gwtexporter-2.4.0-M2-SNAPSHOT.jar',


    ];

    var cp = "${src}:${Strings.join(libs,':')}";



    var gwtParamsCommon = {
         'war':             '$userHome/p-java/apache-tomcat-6.0.10/webapps/${contextPath}/',
         'logLevel':        'DEBUG',
         'extra':           '$userHome/temp/gwt/extra',
         'gen':             '$userHome/temp/gwt/gen',
         'workDir':         '$userHome/temp/gwt/workDir'

    };

    var gwtParamsDevMode = {
         'codeServerPort':     '9998',
//         'bindAddress':     '10.211.55.2',
//         'startupUrl':      'http://localhost:8080/${contextPath}/SmartClientTestJs.html',
         'startupUrl':      'http://localhost:8080/${contextPath}/${startupPage}.html',
         'noserver':        '',
         'logdir':          '$userHome/temp/gwt/logdir'
    };

    var gwtParamsCompile = {
//        'style':                   'PRETTY'
        'style':                   'OBF'
//        'draftCompile':            '',
//        'localWorkers':            '2',
//        'XdisableCastChecking':    '',
    };

    var gwtParams = new Map.from(gwtParamsCommon);
    if(devMode){
        gwtParamsDevMode.forEach( (k,v) => gwtParams[k] = v );
    }else{
        gwtParamsCompile.forEach( (k,v) => gwtParams[k] = v );
    }




    var jvmParams = [
      '-Xms512m',
      '-Xmx2048m',
      '-XX:MaxPermSize=512m',
      '-Xss1024k',
      '-DconfigDir=/Users/dford/temp/tmsConfig',
      '-Dlog4j.configuration=file:///Users/dford/temp/tmsConfig/log4j/threed_framework_log4j.xml',
      '-classpath',
      cp
    ];

    var mainClass = devMode?'com.google.gwt.dev.DevMode':'com.google.gwt.dev.Compiler';

    var processArgs = [];
    processArgs.addAll(jvmParams);
    processArgs.add(mainClass);

    gwtParams.forEach((k,v) {
        processArgs.add('-${k}');
        if(v.length!=0){
            processArgs.add(v);
        }

    });

    processArgs.add(modFullName);

    var p = Process.start('java',processArgs);

    var stdoutStream = new StringInputStream(p.stdout);
    var stderrStream = new StringInputStream(p.stderr);

    stdoutStream.onLine = () {
        print(stdoutStream.readLine());
    };

    stderrStream.onLine = () {
        print(stderrStream.readLine());
    };

    p.onExit = (exitCode) {
        print('exit code: $exitCode');
        p.close();
    };

}

