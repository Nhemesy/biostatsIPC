/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;

/**
 * FXML Controller class
 *
 * @author PedroDavidLP
 */
public class MainController implements Initializable {

    @FXML
    private PieChart heartZonePieChart;
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
    ;
    @FXML
    private Label normalSpeed;
    @FXML
    private Label maxSpeed;
    @FXML
    private Label maxCadence;
    @FXML
    private Label normalCadence;
    @FXML
    private Label riseAltitude;
    @FXML
    private Label fallAltitude;
    ;
    @FXML
    private Label normalHeartRate;
    @FXML
    private Label minHeartRate;
    @FXML
    private Label maxHeartRate;
    @FXML
    private Text totalDistance;
    @FXML
    private ProgressBar normalHeartRateBar;
    @FXML
    private ProgressBar minHeartRateBar;
    @FXML
    private ProgressBar maxSpeedBar;
    @FXML
    private ProgressBar maxCadenceBar;
    @FXML
    private ProgressBar normalCadenceBar;
    @FXML
    private ProgressBar riseBar;
    @FXML
    private ProgressBar fallBar;
    @FXML
    private ProgressBar maxHeartRateBar;
    @FXML
    private ProgressBar normalSpeedBar;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SelectGPXController.controller = this;
        root.setDisable(true);
    }

    public void init() {
        root.setDisable(false);
        System.out.println(currentTrackData.getTotalDistance());
        totalDistance.setText("Distancia recorrida: " + roundDouble(currentTrackData.getTotalDistance() / 1000) + " Km");
        normalSpeed.setText(roundDouble(currentTrackData.getAverageSpeed()) + " km/h");
        setProgress(normalSpeedBar,currentTrackData.getAverageSpeed(),currentTrackData.getMaxSpeed());
        maxSpeed.setText(roundDouble(currentTrackData.getMaxSpeed()) + " km/h");
        setProgress(maxSpeedBar,currentTrackData.getMaxSpeed(),currentTrackData.getMaxSpeed());
        normalHeartRate.setText(currentTrackData.getAverageHeartrate() + " bpm");
        setProgress(normalHeartRateBar,currentTrackData.getAverageHeartrate(),currentTrackData.getMaxHeartrate());
        minHeartRate.setText(currentTrackData.getMinHeartRate() + " bpm");
        setProgress(minHeartRateBar,currentTrackData.getMinHeartRate(),currentTrackData.getMaxHeartrate());
        maxHeartRate.setText(currentTrackData.getMaxHeartrate() + " bpm");
        setProgress(maxHeartRateBar,currentTrackData.getMaxHeartrate(),currentTrackData.getMaxHeartrate());
        riseAltitude.setText(roundDouble(currentTrackData.getTotalAscent()) + " Km");
        setProgress(riseBar,7,14);
        fallAltitude.setText(roundDouble(currentTrackData.getTotalDescend()) + " Km");
        setProgress(fallBar,3,13);
        normalCadence.setText(currentTrackData.getAverageCadence() + " vpm");
        setProgress(normalCadenceBar,currentTrackData.getAverageCadence(),currentTrackData.getMaxCadence());
        maxCadence.setText(currentTrackData.getMaxCadence() + " vpm");
        setProgress(maxCadenceBar,currentTrackData.getMaxCadence(),currentTrackData.getMaxCadence());
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
        d = d * 100;
        d = Math.round(d);
        d = d / 100;
        return d;
    }

    @FXML
    private void changeChart(ActionEvent event) {
        ObservableList<Chunk> chuncks = currentTrackData.getChunks();
        switch (selectChartBox.getSelectionModel().getSelectedItem().charAt(0)) {
            case 'A':
                areaChart(altitudePerDistance(chuncks));
                break;
            case 'V':
                lineChart(speedPerDistance(chuncks), "Velocidad (km/h)", "Distancia (Km)");
                break;
            case 'F':
                lineChart(fcPerDistance(chuncks), "Frecuencia Cardíaca (bps)", "Distancia (Km)");
                break;
            case 'C':
                lineChart(cadencePerDistance(chuncks), "Cadencia (vps)", "Distancia (Km)");
                break;

        }
    }

    private void lineChart(ObservableList a, String y, String x) {
        chartContainer.getChildren().remove(1);
        LineChart lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        lineChart.setData(a);
        lineChart.getXAxis().setLabel(x);
        lineChart.getYAxis().setLabel(y);
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
        for (int i = 0; i < list.size(); i++) {
            series.getData().add(new XYChart.Data(list.get(i).getDistance(), list.get(i).getGrade()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> speedPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();

        for (int i = 0; i < list.size(); i++) {
            series.getData().add(new XYChart.Data(list.get(i).getSpeed(), list.get(i).getDistance()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> fcPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();

        for (int i = 0; i < list.size(); i++) {
            series.getData().add(new XYChart.Data(list.get(i).getDistance(), list.get(i).getAvgHeartRate()));

        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> cadencePerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        for (int i = 0; i < list.size(); i++) {
            series.getData().add(new XYChart.Data(list.get(i).getDistance(), list.get(i).getAvgCadence()));

        }
        series.setName("Cadence per Distance");
        res.addAll(series);
        return res;
    }

    @FXML
    private void openOtherGPX(ActionEvent event) {
        SelectGPXController.controller = this;
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectGPX.fxml"));
        loader.setResources(null);
        stage.setTitle("SeleccioneGPX");

        try {
            Parent root1 = (Parent) loader.load();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
        }
    }
    private void setProgress(ProgressBar bar,double valor,double max){
        double maxim=Math.random();
        if(maxim<0.7){
            maxim=0.78;
        }
       valor=valor*maxim/max;
       bar.setProgress(valor);
    }
}
