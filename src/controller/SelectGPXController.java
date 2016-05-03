/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
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
    @FXML
    private Label label;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
    
    @FXML
        private void load(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
            if (file == null) {
                return;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
            GpxType gpx = (GpxType) root.getValue();
            
            if (gpx != null) {
                trackData = new TrackData(new Track(gpx.getTrk().get(0)));
                MainController.currentTrackData=trackData;
                Stage current=(Stage) loadButton.getScene().getWindow();
                controller.init();
                current.close();
            } else {
                label.setVisible(true);
                label.setText("Error cargando GPX de " + file.getName());
            }
        } catch (JAXBException ex) {
            label.setVisible(true);
            label.setText("Error cargando archivo");
        }
    }
    
    
}
