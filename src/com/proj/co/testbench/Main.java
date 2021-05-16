package com.proj.co.testbench;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage stg;
    private FXMLLoader loader;

    private static Main instance;

    public Main(){
        instance= this;
    }

    public static Main getInstance(){
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stg= primaryStage;
        primaryStage.setResizable(false);
        loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Peculiar Beer Ducks");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.getIcons().add(new Image("/beer-duck.png"));
        primaryStage.show();
    }

    //method used in Controller files to change the window
    public void changeScene(String fxml, String title) throws IOException {
        loader= new FXMLLoader(getClass().getResource(fxml));
        Parent pane= loader.load();
        stg.setTitle(title);
        stg.getIcons().add(new Image("/beer-duck.png"));
        stg.getScene().setRoot(pane);
        stg.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
