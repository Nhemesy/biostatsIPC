/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
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
    
    @FXML
    private PieChart heartZonePieChart;
    @FXML
    private Text dayText;
    @FXML
    private Text durationText;
    @FXML
    private Text movementTimeText;
    @FXML
    private Text distanceText;
    @FXML
    private Text altitudeText;
    @FXML
    private Text velocityText;
    @FXML
    private Text maxHeartFrecuencyText;
    @FXML
    private Text minHeartFrecuencyText;
    @FXML
    private Text normalHeartFrecuencyText;
    @FXML
    private Text maxCadenceText;
    @FXML
    private Text minCadenceText;
    @FXML
    private GridPane chartContainer;
    @FXML
    private ComboBox<String> selectCharBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }
    
    private void init() {
        TrackData trackData = loadGpx("/Tracks/20160203-094110-Ride.gpx");
        dayText.setText("Actividad realizada el dia " + trackData.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        durationText.setText("Tiempo total: "+formatDate(trackData.getTotalDuration().getSeconds()));
        movementTimeText.setText("Tiempo en movimiento: "+formatDate(trackData.getMovingTime().getSeconds()));
        distanceText.setText("Distancia recorrida: "+roundDouble(trackData.getTotalDistance())+" m");
        altitudeText.setText("Desnivel acumulado: "+trackData.getAverageHeight());
        velocityText.setText("Velocidad-> Media: "+roundDouble(trackData.getAverageSpeed())+" km/h"+" Maxima: "+roundDouble(trackData.getMaxSpeed())+" km/h");
        maxHeartFrecuencyText.setText("Maxima frecuencia cardiaca:"+trackData.getMaxHeartrate());
        minHeartFrecuencyText.setText("Minima frecuencia cardica: "+trackData.getMinHeartRate());
        normalHeartFrecuencyText.setText("Minima frecuencia cardiaca: "+trackData.getAverageHeartrate());
        maxCadenceText.setText("Maxima cadencia: "+trackData.getMaxCadence());
        minCadenceText.setText("Cadencia media: "+trackData.getAverageCadence());
        updateHeartChart(18);
        selectCharBox.setItems(FXCollections.observableArrayList(new String[]{"Altura x Distancia","Velocidad x Distancia",
           "FC x Distancia","Cadencia x Distancia"}));
    }
    
    public TrackData loadGpx(String file) {
        TrackData trackData = null;
        try {
            File f = new File(System.getProperty("user.dir") + file);
            JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(f);
            GpxType gpx = (GpxType) root.getValue();
            trackData = new TrackData(new Track(gpx.getTrk().get(0)));
            ObservableList<Chunk> chunks = trackData.getChunks();
        } catch (JAXBException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trackData;
    }
    public void updateHeartChart(int age){
        
     ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Z1 Recuperación", 13),
            new PieChart.Data("Z2 Fondo", 25),
            new PieChart.Data("Z3 Tempo", 10),
            new PieChart.Data("Z4 Umbral", 22),
            new PieChart.Data("Z5 Anaérobico", 30));
     heartZonePieChart.setData(pieChartData);
    }
    
    @FXML
    private void prueba(ActionEvent event) throws JAXBException, IOException {

        /**
         * areachart.setTitle("Temperature Monitoring (in Degrees C)");
         * XYChart.Series seriesApril = new XYChart.Series();
         * seriesApril.setName("Distance per speed");
         *
         * for (int i = 0; i < 100; i++) {
         * seriesApril.getData().add(new XYChart.Data("" + duration(i), speed(i)));
         * }
         * areachart.getData().addAll(seriesApril);
         * Files.walk(Paths.get(System.getProperty("user.dir") + "/Tracks")).forEach(filePath ->
         * { if (Files.isRegularFile(filePath)) { System.out.println(filePath);
         * }
        });
         */
    }
    
    
    public static String formatDate(long s){
        String res="";
        if(s/3600>0){
            res+=(int)s/3600+" horas, ";
            s=s-((int)s/3600)*3600;
        }
        if(s/60>0){
            res+=(int)s/60+" minutos y ";
            s=s-((int)s/60)*60;
        }
        res+=s+" segundos.";
        
        return res;
    }
    public static double roundDouble(double d){
        return (Math.round(d*100))/100;
    }
}
