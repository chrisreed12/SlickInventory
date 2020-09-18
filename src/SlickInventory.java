import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static utilities.DBHelper.createFiles;


public class SlickInventory extends Application {

    public static void main(String[] args) throws IOException {
        createFiles();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Slick Inventory");
        AnchorPane mainPane = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

}
