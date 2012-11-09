#!/usr/bin/env dart

#import('dart:io');

//usage: ./gwt.dart --app=admin|smartClient|gwtDemo|[all] --mode=dev|[compile]

String appName = "c3i";

void main(){

    bool devMode = false;
    String app = null;

    //process command line args
    Options options = new Options();
    List<String> args = options.arguments;

    args.forEach((arg) {
        if(arg.startsWith("--mode=") && arg.endsWith("dev")){
           devMode = true;
        }
        if(arg.startsWith("--app=") ){
            if(arg.endsWith("admin")) app = "admin";
            else if(arg.endsWith("smartClient")) app = "smartClient";
            else if(arg.endsWith("gwtDemo")) app = "gwtDemo";
        }
    });

    App gwtDemo =  new App("smartClient","smartClient.SmartClientDemoGwt","DemoGwt");
    App smartClient =  new App("smartClient","smartClient.SmartClientExportJavaScript","demo/widget/toyota/demo");
    App admin =  new App("threed-admin-v2","admin.ThreedAdmin","index");
    
    if(app == "gwtDemo") {
        doIt(devMode,gwtDemo);
    }
    else if(app == "smartClient") {
        doIt(devMode,smartClient);
    }
    else if(app == "admin") {
        doIt(devMode,admin);
    }
    else{
      doIt(devMode,gwtDemo);
      doIt(devMode,smartClient);
      doIt(devMode,admin);
    }

}

class App{
  
  String contextPath;
  String modName;
  String startupPage;
  
  App(this.contextPath, this.modName,this.startupPage);
  
}

void doIt(bool devMode,App a) {
  
  var modFullName = '${appName}.${a.modName}';  //threed.smartClient.SmartClient
  
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
  var src3 = '$repo1/adminWebApp/src';
  var src = '$src1:$src2:$src3';
  
  //var gwtVersion = '2.5.0.rc1';
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
    '$lib/guava/11.0/guava-gwt-11.0.2.jar',
    '$lib/gwtexporter/SNAPSHOT/gwtexporter-2.4.0-M2-SNAPSHOT.jar',
  
  
  ];
  
  var cp = "${src}:${Strings.join(libs,':')}";
  
  
  
  var gwtParamsCommon = {
       'war':             '$userHome/p-java/apache-tomcat-6.0.10/webapps/${a.contextPath}/',
       'logLevel':        'DEBUG',
       'extra':           '$userHome/temp/gwt/extra',
       'gen':             '$userHome/temp/gwt/gen',
       'workDir':         '$userHome/temp/gwt/workDir'
  
  };
  
  var gwtParamsDevMode = {
       'codeServerPort':     '9998',
  //         'bindAddress':     '10.211.55.2',
  //         'startupUrl':      'http://localhost:8080/${contextPath}/SmartClientTestJs.html',
       'startupUrl':      'http://localhost:8080/${a.contextPath}/${a.startupPage}.html',
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
  
  Process.start('java',processArgs).then((Process p){
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
    };
    
  });
  
  
  
}

