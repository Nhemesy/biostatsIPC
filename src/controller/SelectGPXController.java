/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;

/**
 * FXML Controller class
 *
 * @author PedroDavidLP
 */
public class SelectGPXController implements Initializable {

    final FileChooser fileChooser = new FileChooser();
    @FXML
    private Button loadButton;
    private TrackData trackData;
    public static MainController controller;
    private Label label;
    @FXML
    private Text loadingText;
    @FXML
    private ProgressIndicator loadingIndicator;
    private static File file;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void load(ActionEvent event) throws IOException {
        Task<Boolean> task = new Task() {

            @Override
            protected Boolean call() throws Exception {
                Thread.sleep(5000);
                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
                GpxType gpx = (GpxType) root.getValue();
                if (gpx != null) {
                trackData = new TrackData(new Track(gpx.getTrk().get(0)));
                }
                return true;
            }
        };

        task.onSucceededProperty();
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        

        task.setOnRunning((WorkerStateEvent event1) -> {
            loadButton.setVisible(false);
            loadingText.setText("Cargando archivo GPX...");
            loadingIndicator.setVisible(true);
        });
        task.setOnSucceeded((WorkerStateEvent event1) -> {
            if (task.getValue()) {
            
            MainController.currentTrackData = trackData;
            Stage current = (Stage) loadButton.getScene().getWindow();
            current.close();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            loader.setResources(null);
            stage.setTitle("SeleccioneGPX");
            Parent root1 = null;
                try {
                    root1 = (Parent) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(SelectGPXController.class.getName()).log(Level.SEVERE, null, ex);
                }
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root1));
            stage.show();
            
        } else {
            label.setVisible(true);
            label.setText("Error cargando GPX de " + file.getName());
        }
        });
        
    }

}
