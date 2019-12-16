package bg.fmi.piss.course;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/firstScene.fxml"));
        loader.setController(new FirstScene());
        primaryStage.setTitle("Carpooly");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
    }
}
