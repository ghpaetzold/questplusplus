# QuEst++
Pipelined quality estimation.

## Build
Requires `jdk 1.8`:
  
  `ant "-Dplatforms.JDK_1.8.home=/usr/lib/jvm/java-8-<<version>>" compile`
  
This will create all classes needed to use QuEst++.
You can also use NetBeans to build the project.

## Basic Usage
1.Document-level:

  `java -Xmx10g -XX:+UseConcMarkSweepGC -classpath build/classes:lib/commons-cli-1.2.jar:lib/stanford-postagger.jar:lib/BerkeleyParser-1.7.jar shef.mt.DocLevelFeatureExtractor -tok -case true -lang english spanish -input input/source.doc-level.en input/target.doc-level.es -config config/config.doc-level.properties`
  
Omit the option `-tok` if the input files are already tokenised.
The option `-case` can be `no` (no casing), `true` (truecase) or `lower` (lowercase)
