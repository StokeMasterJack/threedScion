#!/usr/bin/env dart

#import('dart:io');



void main(){

  var options = new Options();
  List<String> args = options.arguments;
  var devMode = args.length==0?false:args.last()=="dev";
}

