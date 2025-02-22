# COMP 512 - Phase 1

### Setup

1. Navigate to the `<source code root dir>/servercode/` directory
2. Execute `./gradlew clean compileJava` to compile the source code
3. Navigate to the `./bin/` directory
4. In the `java.policy` file make sure to put the correct path 
   ```
   grant codeBase "file:<YOUR PATH TO THE SOURCE ROOT DIRECTORY>/out_gradle" {
      permission java.security.AllPermission;
   };
   ```

### Usage on your local machine (TCP)
1. Navigate to `<source code root dir>/servercode/` 
2. Type in `./gradlew clean compileJava`
3. Go to the `./bin/` directory
4. Launch the RMs in the background `./startTCPRms.sh`. This will spawn 4 background java process, wait until 4 init messages.
5. Launch the middleware `./startTCPMiddleware.sh`
6. To launch the interactive TCP client `./startTCPClient.sh`

### Usage on your local machine (RMI)

1. Navigate to the `<source code root dir>/servercode/bin/` directory
2. Start up the registry with`./startRmiregistry.sh`
3. Start up the different resource manager servers in the background using `./startRms.sh`. 
   This will spawn 4 background java process, wait until you get 4 initialization messages to proceed to the next step.
4. Start up the middleware TCP server using `./startMiddleware.sh` wait until you get the initialization message.

### Running test cases
We use gradle in order to execute unit tests. Issue the following command in the `<source code root dir>/servercode/` directory to run the unit tests : (make sure to have all of the RMs running + the middleware of your choice (RMI or TCP))
 
 ```
 ./gradlew clean compileJava test
 ```
 
 An HTML report should then be generated and available at ```./build/reports/tests/test/index.html```
 
 