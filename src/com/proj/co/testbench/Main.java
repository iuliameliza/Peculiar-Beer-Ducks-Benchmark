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

    @Override
    public void start(Stage primaryStage) throws Exception{
        stg= primaryStage;
        primaryStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/primary.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Peculiar Beer Ducks");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.getIcons().add(new Image("/beer-duck.png"));
        primaryStage.show();
    }

    //method used in Controller files to change the window
    public void changeScene(String fxml, String title) throws IOException {
        Parent pane= FXMLLoader.load(getClass().getResource(fxml));
        stg.setTitle(title);
        stg.getIcons().add(new Image("/beer-duck.png"));
        stg.getScene().setRoot(pane);
    }

    public static void main(String[] args){
        launch(args);
    }
}
