# Peculiar-Beer-Ducks-Benchmark
This is a tool that benchmarks the hdd/ssd by measuring the write/read speed for sequential/random reading.

## Running the app
There are some issues becuase of the old version of JavaFx that is used
* steps:
  * install javafx (version 11 would be ok)
  * have a java version newer than 8
  * to run the app go to the folder of the jar file and run the following
    * ```sh
    java --module-path /path/to/javafx/modules/ --add-modules javafx.controls,javafx.graphics,javafx.media,javafx.fxml,javafx.base,javafx.web,javafx.swing -jar PBD_benchmark.jar
    ```
    
