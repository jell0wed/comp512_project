# COMP 512 - Phase 1

### Setup

1. Navigate to `<source code root>/servercode/`
2. Execute `./gradlew clean compileJava` to compile the soruce code
3. Navigate to the `./bin/` directory
4. In the `java.policy` file make sure to put the correct path 
   ```
   grant codeBase "file:<YOUR PATH TO THE SOURCE ROOT DIRECTORY>/build/classes/main/" {
      permission java.security.AllPermission;
   };
   ```
5. Similarly, fix paths in files `startMiddleware.sh`, `startRms.sh`, `startRmiregistry.sh`

### Usage on your local machine

1. Navigate to the `<source code root>/servercode/bin/` directory
2. Start up the RMI Registry with the appropriate CLASSPATH using `./startRmiregistry.sh`
3. Start up the different resource manager servers in the background using `./startRms.sh`. 
   This will spawn 4 background java process, wait until you get 4 initialization messages to proceed to the next step.
4. Start up the middleware TCP server using `./startMiddleware.sh` wait until you get the initialization message.

### Running test cases
We use gradle in order to execute unit tests. Issue the following command in `<source code root/servercode>` to run the unit tests : 
 
 ```
 ./gradlew clean test
 ```
 
 An HTML report should then be generated and available at ```./build/reports/tests/test/index.html```
 
 