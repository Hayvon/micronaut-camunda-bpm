#Instructions for using GraalVM Native Image usage

####Deployment of BPM diagrams:
1. Create directory 'bpm' in src/main/resources/
    * All BPM diagrams must be located in this folder. It doesn't matter if they are located in one, none or multiple subfolders.
    * E.g src/main/resources/bpm/bpmn/HelloWorld.bpm
2. Create file 'diagrams.txt' in src/main/resources/bpm
    * In this file all used BPM diagrams including their paths starting from /bpm are noted
    * If a diagram is missing in this file, it will not be deployed
    * one diagram per line
    * E.g bpmn/HelloWorld.bpm
3. The same applies to tests

####Native Image Configurations:
 *  Due to Ahead-of-time compilation, native image builds relies on a statistic analysis of code. Therefore e.g reflection cannot be used 
    *  An automatic recognition of reflection exists, but does not recognize everything
    *  Undetected use of dynamic features must therefore be manually added
    
#####How to create manual configurations: 
* Create directory META-INF/native-image/your_groupID/your_artifactID
* In this directory create file 'native-image.properties' reflectionconfig.json' and 'resourceconfig.json'
* More information: https://github.com/oracle/graal/blob/master/substratevm/CONFIGURE.md
1. native-image.properties:
    * Used to define certain options for the building process of Native Image
    * More information and possible options: https://www.graalvm.org/docs/reference-manual/native-image/
2. reflectionconfig.json:
    * Used for undetected reflection usage
    * How to use: https://github.com/oracle/graal/blob/master/substratevm/REFLECTION.md
3. resourceconfig.json:
    * Used to load resources from the classpath into the native image.
    * How to use: https://github.com/oracle/graal/blob/master/substratevm/RESOURCES.md


   
    