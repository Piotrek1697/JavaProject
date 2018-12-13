package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.time.Season;

import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("Weather app");
        Scene scene = new Scene(root, 582, 610);

        //added different styles in various seasons
        scene.getStylesheets().addAll(getStyleFileName(LocalDate.now()));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //added to close all threads and stage
        primaryStage.setOnCloseRequest(event -> {
            controller.exitApplication();
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

    private String getStyleFileName(LocalDate localDate){
        String styleFileName = null;
        Season season = Season.of(localDate);

        switch (season) {
            case SPRING:
                styleFileName = "styles/springStyle.css";
                break;
            case SUMMER:
                styleFileName = "styles/summerStyle.css";
                break;
            case AUTUMN:
                styleFileName = "styles/autumnStyle.css";
                break;
            case WINTER:
                styleFileName = "styles/winterStyle.css";
                break;
        }

        return styleFileName;
    }
}
