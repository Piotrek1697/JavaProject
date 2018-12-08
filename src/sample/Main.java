package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Weather app");
        Scene scene = new Scene(root, 700, 530);

        //added different styles in various seasons
        scene.getStylesheets().addAll(getStyleFileName(LocalDate.now()));
        primaryStage.setScene(scene);
        primaryStage.show();

        //added to close all threads and stage
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

    private String getStyleFileName(LocalDate localDate){
        String styleFileName = null;

        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();

        if ((month >= 12 && day > 20) || (month <= 3 && day < 21)){
            styleFileName = "styles/winterStyle.css";
        }else if(month <= 6 && day <= 21){
            styleFileName = "styles/springStyle.css";
        }else if (month <= 9 && day <= 22){
            styleFileName = "styles/summerStyle.css";
        }else {
            styleFileName = "styles/autumnStyle.css";
        }

        return styleFileName;
    }
}
