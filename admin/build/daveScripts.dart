#!/usr/bin/env dart

import 'dart:io';

import 'package:path/path.dart' as path;

const String cpRepo = "configurator-content-v2";
const String cpApi = "smartClient";
const String cpAdmin = "threed-admin-v2";

const String cp = cpRepo;
final remote = tms;
final local = new Local();

final Remote ss = new Remote(
    user :'ec2-user',
    host:'smartsoftdev.net',
    keyName:'StokeMasterJack.pem',
    webapps:'/usr/local/apache-tomcat-6.0.33/webapps',
    contextPath:cp);

final Remote tms = new Remote(
      user :'ssjsheehan',
//      host:'ec2dev3dimagerepo.toyota.com',
//      host:'ec2dev3dimagerepo',
      host:'ec2-184-73-157-153.compute-1.amazonaws.com',
      keyName:'ssjsheehan-dev.pem',
      webapps:'/www/threed/apache-tomcat-6.0.32/webapps',
      contextPath:cp);


//ssjsheehan@ec2dev3dimagerepo
//ssh -i /Users/dford/amazonKeyFiles/ssjsheehan-dev.pem ssjsheehan@ec2-184-73-157-153.compute-1.amazonaws.com 

//ssh -i /Users/dford/amazonKeyFiles/ssjsheehan-dev.pem ssjsheehan@ec2-184-73-157-153.compute-1.amazonaws.com

class Local{
  
  String userHome;
  String repos;
  String host;
  String webapps;
  String contextPath;
  Local({this.repos:'repos',this.userHome:'/Users/dford', this.webapps:'/p-java/apache-tomcat-6.0.10/webapps',this.contextPath:cp});
  
  String fromDirTomcat(){
    return  path.normalize('$userHome/$webapps/$contextPath/');
  }
  String fromDirRepos(){
    return path.normalize('$userHome/$repos/$contextPath/target/$contextPath');
  }
  
  String fromDir(){
    return fromDirTomcat();
  }
}

class Remote{
  
  String user;
  String host;
  String webapps;
  String contextPath;
  String keyDir;
  String keyName;
  
  Remote({this.user, this.host,this.webapps,this.keyDir:'/Users/dford/amazonKeyFiles/',this.keyName,this.contextPath:cp});
  
  String destDir(){
    return path.normalize('$user@$host:$webapps/$contextPath/');
  }
  
  String keyFile(){
    return  path.normalize('$keyDir/$keyName');
  }
  
}


void main(){
//  pushWar(local,remote);
  
  pushTargetToLocalTomcat();
}

void pushTargetToLocalTomcat(){
  
  var repo = ['repo','repo-1.0-SNAPSHOT','configurator-content-v2'];
  var api = ['api','api-1.0-SNAPSHOT','smartClient'];
  
  var cp = api;
  String userHome = '/Users/dford';
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

void pushWar(Local local,Remote remote){
  print('L: $local');
  print('L: $remote');
  
  String fromDir = local.fromDir() + "/";
  String destDir = remote.destDir() + "/";
  
  
  print('fromDir $fromDir');
  print('destDir $destDir');
  
  
  String keyFile = remote.keyFile();
  print('keyFile $keyFile');
  
  List<String> processArgs = [
    '-m',
    '--delete-after', 
    '--exclude','.DS_Store',
    '-v','-u','-a','-e',
    '/usr/bin/ssh -i ${keyFile}',
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

