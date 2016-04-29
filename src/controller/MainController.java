/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;

/**
 * FXML Controller class
 *
 * @author PedroDavidLP
 */
public class MainController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    public void marks() throws JAXBException{
        File file=new File(System.getProperty("user.dir")+"/Tracks/20160105-114309-Ride.gpx");
        JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class,TrackPointExtensionT.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<Object> root = (JAXBElement<Object>)
        unmarshaller.unmarshal(file);
        GpxType gpx = (GpxType) root.getValue();
        TrackData trackData = new TrackData(new Track(gpx.getTrk().get(0)));
        ObservableList<Chunk> chunks = trackData.getChunks();
        System.out.println(chunks.get(0).getSpeed());
    }

    @FXML
    private void prueba(ActionEvent event) throws JAXBException {
        marks();
    }
    
}
