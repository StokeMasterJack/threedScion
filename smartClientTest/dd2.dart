#!/usr/bin/env dart

#import('dart:io');

var devMode = false;

var contextPath =  "smartClientTest";
var modName = 'smartClientTest.SmartClientTest';

void main(){

    var userHome = '/Users/dford';
    var cvsRoot = '$userHome/cvsCheckouts/CVSROOT';
    var repos = '$userHome/repos';

    var apps = '$cvsRoot/TMS/apps';
    var frameworks = '$cvsRoot/TMS/framework';
    var lib = '/Users/dford/p-java/daven-repository';

    var repo1 = '$repos/threedScion';
    var repo2 = '$repos/util';

    var src1 = '$repo1/nfc/src/java';
    var src2 = '$repo2/util/src/java';
    var src3 = '$repo1/smartClientTest/src';
    var src = '$src1:$src2:$src3';

    var gwtVersion = '2.4.0';
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
      '$lib/guava/11.0/guava-gwt-11.0.2.jar'
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
//         'bindAddress':     '10.211.55.2',
         'startupUrl':      'http://localhost:8080/${contextPath}/SmartClientTest.html',
         'noserver':        '',
         'logdir':          '$userHome/temp/gwt/logdir'
    };

    var gwtParamsCompile = {
        'style':                   'PRETTY'
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
      '-Xmx512m',
      '-DconfigDir=/Users/dford/temp/tmsConfig',
      '-Dlog4j.configuration=file:///Users/dford/temp/tmsConfig/log4j/threed_framework_log4j.xml',
      '-classpath',
      cp
    ];

    var mainClass = devMode?'com.google.gwt.dev.DevMode':'com.google.gwt.dev.Compiler';

    var args = [];
    args.addAll(jvmParams);
    args.add(mainClass);

    gwtParams.forEach((k,v) {
        args.add('-${k}');
        if(v.length!=0){
            args.add(v);
        }

    });

    args.add(modName);

    var p = new Process.start('java',args);

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

