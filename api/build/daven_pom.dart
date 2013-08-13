library daven_pom;

class DavenPom {
  
  String userHome = '/Users/dford';
  String contextPath = 'smartClient';
  String get repos => '$userHome/repos';
  String get webapps => '$userHome/p-java/apache-tomcat-6.0.10/webapps';
  
  String get projectRoot => '$repos/threed/threed-parent';
  
  String get moduleRoot => '$projectRoot/api';
  
  String gwtModule = 'c3i.smartClient.SmartClientExportJavaScript';
  String startupPage = 'demos.html';
  
  String gwtVersion = '2.5.1';
  
  String get m2 => '$userHome/.m2/repository/';
  String get lib => '$userHome/daven-repository/';
  
  String get gwtHome => '$userHome/p-java/gwt-$gwtVersion';
  

  
//  String get warDir => '$moduleRoot/target/src/main/webapp';
  String get warDir => '$webapps/$contextPath';
  
  String get srcDir => '$moduleRoot/src/main/java';
  
  List<String> get importedSrcDirs => [
    '$repos/ssutil/src',
    '$projectRoot/nfc/src/main/java'
  ]; 
  
  List<String> get allSrcDirs => [srcDir]..addAll(importedSrcDirs);
  
  List<String> get gwtJars  =>  [
   '$gwtHome/gwt-dev.jar',
   '$gwtHome/gwt-user.jar',
   '$gwtHome/gwt-servlet.jar',
   '$gwtHome/gwt-servlet-deps.jar',
   '$gwtHome/validation-api-1.0.0.GA.jar',
   '$gwtHome/validation-api-1.0.0.GA-sources.jar'
   ];
  
  List<String> get otherJars =>  [
  '$lib/jsr305/1.0/jsr305.jar',
  '$lib/gwtexporter/gwtexporter-2.5.0-SNAPSHOT.jar',
  '$m2/com/google/guava/guava/11.0.2/guava-11.0.2.jar',
  '$m2/com/google/guava/guava-gwt/11.0.2/guava-gwt-11.0.2.jar'
 ];
  
  List<String> get allJars => []..addAll(gwtJars)..addAll(otherJars); 
  
  List<String> get allClasspathEntries => []..addAll(allJars)..addAll(allSrcDirs);  
  
  String get classpath => allClasspathEntries.join(':');
  
  String get gwtTmp => '$userHome/temp/gwt';

  List<String> get jvmArgs =>  [
             '-Xms512m',
             '-Xmx2048m',
             '-XX:MaxPermSize=512m',
             '-Xss1024k',
             '-classpath',classpath,
             '-Dgwt.persistentunitcachedir=$gwtTmp/cache'
             ];
  
  //'$userHome/temp/gwt/workDir'
  
  String logLevel = 'DEBUG'; //  [WARN | DEBUG] 
  
  get gwtCommonArgs => {
    'logLevel': logLevel,
     'extra':           '$gwtTmp/extra',
     'deploy':          '$gwtTmp/deploy',
     'gen':             '$gwtTmp/gen',
     'workDir':         '$gwtTmp/workDir'
  };
  
  bool pretty = false;
  bool disableCastChecking = false;
  
  String get startupUrl => 'http://localhost:8080/$contextPath/$startupPage';
  
  Map<String,String> get gwtCompileArgs  {
    var m = {
        'style': pretty?'PRETTY':'OBF',
        'war': warDir
    };
    if(disableCastChecking){
      m['XdisableCastChecking'] = '';
    }
    return m;
  }
  
  Map<String,String> get gwtDevModeArgs => {
       'startupUrl':      startupUrl,
       'logdir':          '$userHome/temp/gwt/logdir',
      'war': warDir,
  };

  Map<String,String> get gwtSuperDevArgs => {
        'startupUrl':      startupUrl,
        'logdir':          '$userHome/temp/gwt/logdir'
   };
  
  
}
