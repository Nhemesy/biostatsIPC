/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import controller.SelectGPXController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author PedroDavidLP
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        Stage stagetwo = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectGPX.fxml"));
        loader.setResources(null);
        stagetwo.setTitle("SeleccioneGPX");

        Parent root1 = (Parent) loader.load();
        stagetwo.initModality(Modality.APPLICATION_MODAL);
        stagetwo.setAlwaysOnTop(true);
        stagetwo.setScene(new Scene(root1));
        stagetwo.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
