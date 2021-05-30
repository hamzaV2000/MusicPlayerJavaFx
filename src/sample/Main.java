package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.DataModel.DataHandler;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("UI/Main.fxml"));
        primaryStage.setTitle("HM Player");
        primaryStage.setScene(new Scene(root));
      //  primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void init(){
        try {
            DataHandler.getInstance().loadSongs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stop() throws Exception {
        try {
            DataHandler.getInstance().storeSongs();

        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
