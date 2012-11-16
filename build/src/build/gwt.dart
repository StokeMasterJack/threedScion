#!/usr/bin/env dart

#import('dart:io');

String usage = "gwt.dart --mode=dev|compile --app=admin|gwtDemo|jsDemo";

Map<String,App> apps = {
  "gwtDemo":new App(contextPath:"gwtDemo",module:"c3i.gwtDemo.GwtDemo",startupPage:"GwtDemo.html"),
  "jsDemo":new App(contextPath:"smartClient",module:"c3i.smartClient.SmartClientExportJavaScript",startupPage:"demos.html"),
  "admin":new App(contextPath:"threed-admin-v2",module:"c3i.admin.ThreedAdmin",startupPage:"index.html")
};


void main(){
  
    List<String> modes = ["dev","compile"];
    
    
    Map<String,String> args = argsToMap(["app","mode"]);
    
    String mode = args["mode"];
    String app = args["app"];
    
    try{
    
      if(mode==null){
        throw new IllegalArgumentException("mode is required");
      }
     
      if(app==null){
        throw new IllegalArgumentException("app is required");
      }
  
      if(!apps.containsKey(app) && app != "all"){
        throw new IllegalArgumentException("Invalid appName: $app");
      }
      
      if(!modes.contains(mode)){
        throw new IllegalArgumentException("Invalid mode: $mode");
      }
    
    } on IllegalArgumentException catch(e){
      print(e.message);
      print("Usage: ${usage}");
      return;
    }
    
    bool devMode = mode=="dev";
    Collection<App> appsToProcess = app=="all"?apps.values:[apps[app]]; 
    appsToProcess.forEach( (App a) => doIt(devMode,a) );
}

Map<String,String> argsToMap(List<String> argNames){
  Options options = new Options();
  List<String> args = options.arguments;
  Map<String,String> retVal = {};
  args.forEach((arg) {
    argNames.forEach((argName){
      String s = "--${argName}=";
      if(arg.startsWith(s)){
        String argValue = arg.substring(s.length);
        retVal[argName] = argValue;
      }
    });
  });
  return retVal;
}

class App{
  String contextPath;
  String module;
  String startupPage;
  App({this.contextPath, this.module,this.startupPage});
}

void doIt(bool devMode,App a) {
  
  var modFullName = a.module;  //ex: c3i.smartClient.SmartClient
  
  var userHome = '/Users/dford';
  var cvsRoot = '$userHome/cvsCheckouts/CVSROOT';
  var repos = '$userHome/repos';
  
  var apps = '$cvsRoot/TMS/apps';
  var frameworks = '$cvsRoot/TMS/framework';
  var lib = '/Users/dford/daven-repository';
  
  var threedRepo = '$repos/threedScion';
  var utilRepo = '$repos/ut';
  
  
  List<String> srcFolders = [
    '$threedRepo/nfc/src/java',
    '$threedRepo/adminWebApp/src',
    '$threedRepo/gwtDemoWebApp/src',
    '$utilRepo/util/src/java',
  ];       
  
  var gwtVersion = '2.5.0';
//  var gwtVersion = '2.4.0';
  var gwtHome = '${userHome}/p-java/gwt-${gwtVersion}';
  
  List<String> libs = [
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
  
  String cpSrc = Strings.join(srcFolders, ":");
  String cpLibs = Strings.join(libs,':');
  
  var cp = "${cpSrc}:${cpLibs}";
  
  var gwtParamsCommon = {
       'war':             '$userHome/p-java/apache-tomcat-6.0.10/webapps/${a.contextPath}/',
       'logLevel':        'DEBUG',
       'extra':           '$userHome/temp/gwt/extra',
       'gen':             '$userHome/temp/gwt/gen',
       'workDir':         '$userHome/temp/gwt/workDir'
  
  };
  
  bool jetty = true;
  
  String startupUrl;
  if(jetty){
      startupUrl = '/${a.startupPage}';
  }else{ //tomcat
    startupUrl = 'http://localhost:8080//${a.contextPath}/${a.startupPage}';
  }
  
  Map<String,String> gwtParamsDevMode = {
//       'codeServerPort':     '9998',
  //         'bindAddress':     '10.211.55.2',
  //         'startupUrl':      'http://localhost:8080/${contextPath}/SmartClientTestJs.html',
       'startupUrl':      startupUrl,
       'logdir':          '$userHome/temp/gwt/logdir'
  };
  
  if(!jetty){
    gwtParamsDevMode['noserver'] = '';
  }
  
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

