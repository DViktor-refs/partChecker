package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Controller.Controller;

public class Main extends Application {

    Controller c = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/sample.fxml"));
        Scene scene = new Scene(root, 1100, 600);
        scene.getStylesheets().add("sample/CSS/style.css");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        c.setStage(primaryStage);
        c.setParentInController(root);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
