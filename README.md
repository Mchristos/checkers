# checkers

A checkers game written in Java/Swing. Requires Java 17 or higher.

AI implemented using the minimax algorithm and alpha-beta pruning.

## features 

- Human vs. AI interactive gameplay with GUI
- Minimax-based AI 
- Drag & drop  
- Various help facilities
- Multiple themes (dark / light from Flatlaf), see [pom.xml](pom.xml)


<img width="641" alt="checkers" src="./src/main/resources/screenshot_checkers.png">

## Build a runnable JAR

1. Fork or download the repo. Navigate to project directory.
2. Open terminal or Powershell, run the following command:
    ```bash
    ./mvnw clean package
    ```
3. The result JAR will be found in `target/` folder. Double click it to start.