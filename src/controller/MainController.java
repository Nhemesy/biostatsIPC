/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private ComboBox<String> selectChartBox;
    public static TrackData currentTrackData;
    @FXML
    private Label percentageText;
    @FXML
    private StackPane stackPane;
    @FXML
    private AnchorPane root;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SelectGPXController.controller=this;
         Stage stage = new Stage();
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectGPX.fxml"));
            loader.setResources(null);
            stage.setTitle("SeleccioneGPX");
            
            try {
                Parent root1 = (Parent) loader.load();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setAlwaysOnTop(true);
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    public void init() {
        root.setDisable(false);
        dayText.setText("Actividad realizada el dia " + currentTrackData.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
        durationText.setText("Tiempo total: " + formatDate(currentTrackData.getTotalDuration().getSeconds()));
        movementTimeText.setText("Tiempo en movimiento: " + formatDate(currentTrackData.getMovingTime().getSeconds()));
        distanceText.setText("Distancia recorrida: " + roundDouble(currentTrackData.getTotalDistance()) + " m");
        altitudeText.setText("Desnivel acumulado: " + currentTrackData.getAverageHeight());
        velocityText.setText("Velocidad-> Media: " + roundDouble(currentTrackData.getAverageSpeed()) + " km/h" + " Maxima: " + roundDouble(currentTrackData.getMaxSpeed()) + " km/h");
        maxHeartFrecuencyText.setText("Maxima frecuencia cardiaca:" + currentTrackData.getMaxHeartrate());
        minHeartFrecuencyText.setText("Minima frecuencia cardica: " + currentTrackData.getMinHeartRate());
        normalHeartFrecuencyText.setText("Minima frecuencia cardiaca: " + currentTrackData.getAverageHeartrate());
        maxCadenceText.setText("Maxima cadencia: " + currentTrackData.getMaxCadence());
        minCadenceText.setText("Cadencia media: " + currentTrackData.getAverageCadence());
        updateHeartChart(35);
        selectChartBox.setItems(FXCollections.observableArrayList(new String[]{"Altura x Distancia", "Velocidad x Distancia",
            "FC x Distancia", "Cadencia x Distancia"}));
    }

    public void updateHeartChart(int age) {
        ObservableList<Chunk> list = currentTrackData.getChunks();
        int[] heartZones = new int[]{0, 0, 0, 0, 0};
        for (Chunk list1 : list) {
            double heartRate = list1.getAvgHeartRate();
            double maxFC = 220 - age;
            if (heartRate < maxFC * 0.60) {
                heartZones[0]++;
            } else if (heartRate < maxFC * 0.70) {
                heartZones[1]++;
            } else if (heartRate < maxFC * 0.80) {
                heartZones[2]++;
            } else if (heartRate < maxFC * 0.90) {
                heartZones[3]++;
            } else {
                heartZones[4]++;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Z1 Recuperación", heartZones[0] * 100 / list.size()),
                new PieChart.Data("Z2 Fondo", heartZones[1] * 100 / list.size()),
                new PieChart.Data("Z3 Tempo", heartZones[2] * 100 / list.size()),
                new PieChart.Data("Z4 Umbral", heartZones[3] * 100 / list.size()),
                new PieChart.Data("Z5 Anaérobico", heartZones[4] * 100 / list.size()));
        heartZonePieChart.setData(pieChartData);
        heartZonePieChart.setLabelLineLength(10);
        heartZonePieChart.setLegendSide(Side.LEFT);
        heartZonePieChart.setClockwise(false);
        heartZonePieChart.setLabelsVisible(true);

        for (final PieChart.Data data : heartZonePieChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            percentageText.setText(String.valueOf(data.getPieValue()) + "%");
                            percentageText.setVisible(true);
                            percentageText.setVisible(false);
                        }
                    });
        }

    }



    public static String formatDate(long s) {
        String res = "";
        if (s / 3600 > 0) {
            res += (int) s / 3600 + " horas, ";
            s = s - ((int) s / 3600) * 3600;
        }
        if (s / 60 > 0) {
            res += (int) s / 60 + " minutos y ";
            s = s - ((int) s / 60) * 60;
        }
        res += s + " segundos.";

        return res;
    }

    public static double roundDouble(double d) {
        return (Math.round(d * 100)) / 100;
    }

    @FXML
    private void changeChart(ActionEvent event) {
        ObservableList<Chunk> chuncks = currentTrackData.getChunks();
        switch (selectChartBox.getSelectionModel().getSelectedItem().charAt(0)) {
            case 'A':
                areaChart(altitudePerDistance(chuncks));
                break;
            case 'V':
                lineChart(speedPerDistance(chuncks));
                break;
            case 'F':
                lineChart(fcPerDistance(chuncks));
                break;
            case 'C':
                lineChart(cadencePerDistance(chuncks));
                break;

        }
    }

    private void lineChart(ObservableList a) {
        chartContainer.getChildren().remove(1);
        LineChart lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        lineChart.setData(a);
        lineChart.setTitle("Hey");
        lineChart.setMaxSize(1000000, 100000);
        lineChart.setCreateSymbols(false);
        GridPane.setColumnSpan(lineChart, 2);
        chartContainer.add(lineChart, 0, 1);

    }

    private void areaChart(ObservableList a) {
        chartContainer.getChildren().remove(1);
        AreaChart areaChart = new AreaChart(new NumberAxis(), new NumberAxis());
        areaChart.setData(a);
        areaChart.setTitle("que tal");
        areaChart.setMaxSize(1000000, 100000);
        areaChart.setCreateSymbols(false);
        GridPane.setColumnSpan(areaChart, 2);
        chartContainer.add(areaChart, 0, 1);
    }

    private ObservableList<XYChart.Series<Double, Double>> altitudePerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        for (int i = 0; i < list.size(); i += list.size() / 50) {
            series.getData().add(new XYChart.Data(list.get(i).getGrade(), list.get(i).getDistance()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> speedPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();

        for (int i = 0; i < list.size(); i += list.size() / 50) {
            series.getData().add(new XYChart.Data(list.get(i).getSpeed(), list.get(i).getDistance()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> fcPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();

        for (int i = 0; i < list.size(); i += list.size() / 500) {
            series.getData().add(new XYChart.Data(list.get(i).getDistance(), list.get(i).getAvgHeartRate()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> cadencePerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        for (int i = 0; i < list.size(); i += list.size() / 50) {
            series.getData().add(new XYChart.Data(list.get(i).getDistance(), list.get(i).getAvgCadence()));

        }
        series.setName("Cadence per Distance");
        res.addAll(series);
        return res;
    }
}
