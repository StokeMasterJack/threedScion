import 'dart:io';

import 'package:path/path.dart' as path;

const String cp = "threed-admin-v2";

final local = new Local();

final remote = new Remote(
      user :'ssjsheehan',
      host:'ec2-184-73-157-153.compute-1.amazonaws.com',
      keyName:'ssjsheehan-dev.pem',
      webapps:'/www/threed/apache-tomcat-6.0.32/webapps',
      contextPath:cp);

class Local{
  
  String userHome;
  String repos;
  String host;
  String webapps;
  String contextPath;
  Local({this.repos:'admin',this.userHome:'/Users/dford', this.webapps:'/p-java/apache-tomcat-6.0.10/webapps',this.contextPath:cp});
  
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
  pushWar(local,remote);
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

