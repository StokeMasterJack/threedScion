library pushTargetToLocalTomcat;

import 'dart:io';
import 'dart:async';

import 'daven_pom.dart';
import 'pom.dart';

void main(){
  var app = new C3iApiPom();
  pushTargetToLocalTomcat(app);
}

void pushTargetToLocalTomcat(DavenPom pom){
  
  var repo = ['repo','repo-1.0-SNAPSHOT','configurator-content-v2'];
  var api = ['api','api-1.0-SNAPSHOT','smartClient'];
  
  var cp = repo;
  String userHome = pom.userHome;
  String fromDir = '$userHome/repos/threed/threed-parent/${cp[0]}/target/${cp[1]}/';
  
  String destDir = '$userHome/p-java/apache-tomcat-6.0.10/webapps/${cp[2]}/';
  
  List<String> processArgs = [
                              '-m',
                              '--delete-after', 
                              '--exclude','.DS_Store',
                              '-v','-u','-a','-e',
                              '/usr/bin/ssh',
                              '$fromDir','$destDir'                     
                              ];
  
  
  print('processArgs: ');
  print(processArgs);
  
  Process.start('rsync',processArgs).then((process) {
    process.stdout.transform(new StringDecoder())
                  .transform(new LineTransformer())
                  .listen((String line) => print(line));
    
    process.stderr.transform(new StringDecoder())
      .transform(new LineTransformer())
        .listen((String line) => print(line));
    
    
    process.exitCode.then((exitCode) {
      print('exit code: $exitCode');
    });
  });
  
}






