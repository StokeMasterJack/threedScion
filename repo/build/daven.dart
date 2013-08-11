library daven;

import 'dart:io';
import 'dart:async';

import 'daven_pom.dart';

void compile(DavenPom app){
  Compile command = new Compile(app);
  command.run();
}

void devmode(DavenPom app){
  DevMode command = new DevMode(app);
  command.run();
}

void compileEmpty(DavenPom app){
  CompileEmpty command = new CompileEmpty(app);
  command.run();
}

abstract class  Command{
  
  DavenPom app;
  String get mainClass;
  
  Command(this.app);
  
  void run(){
    ProcessArgs  processArgs = buildProcessArgs();
    startProcess('java', processArgs.list);
  }
  
  ProcessArgs buildProcessArgs(){
    ProcessArgs a = new ProcessArgs();
    a.addAll(app.jvmArgs);
    a.add(mainClass);
    a.addMap(app.gwtCommonArgs);
    a.addMap(gwtCommandArgs);
    a.add(app.gwtModule);
    return a;
  }  
  
  Map<String,String> get gwtCommandArgs;
  
}

class ProcessArgs{
  
  List<String> a = [];
  
  void add(String arg){
    a.add(arg);
  }
  
  void addAll(List<String> args){
    a.addAll(args);
  }
  
  void addMap(Map<String,String> map){
    map.forEach((k,v) {
      a.add('-${k}');
      if(v.length!=0){
          a.add(v);
      }
    });
  }
  
  List<String> get list => a; 
  
}


class Compile extends Command{
  final String mainClass = 'com.google.gwt.dev.Compiler';
  Compile(DavenPom app): super(app);
  Map<String, String> get gwtCommandArgs =>app.gwtCompileArgs;
}

class DevMode extends Command{
  static const String mainClass = 'com.google.gwt.dev.DevMode';
  DevMode(DavenPom app): super(app);
  Map<String, String> get gwtCommandArgs => app.gwtDevModeArgs;
}

class CompileEmpty extends Command{
  
  final String mainClass = 'com.google.gwt.dev.Compiler';
  CompileEmpty(DavenPom app): super(app);
  Map<String, String> get gwtCommandArgs =>app.gwtCompileArgs;
  
  ProcessArgs buildProcessArgs(){
    ProcessArgs a = new ProcessArgs();
    a.addAll(app.jvmArgs);
    a.add(mainClass);
    return a;
  }  
  
}


void startProcess(String command,List<String> processArgs){
  print('About to start process');
  Process.start('java',processArgs).then((Process p){
    
    Stream<List<int>> stdoutStream = p.stdout;
    Stream<List<int>> stderrStream = p.stderr;
    
    stdoutStream
    .transform(new StringDecoder())
      .transform(new LineTransformer())
      .listen((String line) { print(line); },
          onDone: () { print('no more lines'); },
          onError: (e) { print('onError: $e'); });
    
    stderrStream
    .transform(new StringDecoder())
      .transform(new LineTransformer())
      .listen((String line) { print(line); },
          onDone: () { print('no more lines'); },
          onError: (e) { print('onError: $e'); });
    
    
    Future<int>  exitCode = p.exitCode;
    exitCode.then( (exitCode) {
      print('exit code: $exitCode');
    });
    
  });
  
  print('Process started!');
}



  
 
  






