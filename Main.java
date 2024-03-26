package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static ArrayList<Passenger> passengers = new ArrayList<Passenger>(); 
    @Override
    public void start(Stage primaryStage) {
       try {
    	   
    	Parent root = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Log In");
		primaryStage.setScene(scene);
        primaryStage.show();
        
	} catch (IOException e) {
		e.printStackTrace();
	}
    }

    public static void main(String[] args) {
        launch(args);
    }
}
