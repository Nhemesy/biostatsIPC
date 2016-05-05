/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import style.ColoredProgressBar;

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
    private GridPane activityContainer;
    @FXML
    private Text totalDuration;
    @FXML
    private Text totalMovement;
    @FXML
    private Text dateText;
    @FXML
    private Text introductionText;
    @FXML
    private Button distanceTimeButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

    public void init() {
        introductionText.setText("NeverStop! es una aplicación para organizarte con tu entrenamiento.\n\n"
                + " Carga tus archivos GPX para ver tu actividad y tus estadísticas. ¡Tambien puedes comparar tus actividades!\n\n"
                + " En la pestaña Actividad tendrás todo tipo de información de tu entrenamiento,"
                + " y en estadísticas podrás ver diferentes gráficas para ilustrar tu avance. \n \n \n YOU! NeverStop!");
        dateText.setText("Actividad del " + currentTrackData.getStartTime().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        totalDistance.setText("Distancia recorrida: " + roundDouble(currentTrackData.getTotalDistance() / 1000) + " Km");
        totalDuration.setText("Duracion total: " + formatDate(currentTrackData.getTotalDuration().getSeconds()));
        totalMovement.setText("Tiempo en movimiento: " + formatDate(currentTrackData.getMovingTime().getSeconds()));

        normalSpeed.setText(roundDouble(currentTrackData.getAverageSpeed()) + " km/h");
        setProgress(2, 3, currentTrackData.getAverageSpeed(), currentTrackData.getMaxSpeed());
        maxSpeed.setText(roundDouble(currentTrackData.getMaxSpeed()) + " km/h");
        setProgress(5, 3, currentTrackData.getMaxSpeed(), currentTrackData.getMaxSpeed());
        normalHeartRate.setText(currentTrackData.getAverageHeartrate() + " bpm");
        setProgress(2, 6, currentTrackData.getAverageHeartrate(), currentTrackData.getMaxHeartrate());
        minHeartRate.setText(currentTrackData.getMinHeartRate() + " bpm");
        setProgress(5, 6, currentTrackData.getMinHeartRate(), currentTrackData.getMaxHeartrate());
        maxHeartRate.setText(currentTrackData.getMaxHeartrate() + " bpm");
        setProgress(5, 7, currentTrackData.getMaxHeartrate(), currentTrackData.getMaxHeartrate());
        riseAltitude.setText(roundDouble(currentTrackData.getTotalAscent()) + " Km");
        setProgress(2, 5, 7, 14);
        fallAltitude.setText(roundDouble(currentTrackData.getTotalDescend()) + " Km");
        setProgress(5, 5, 3, 13);
        normalCadence.setText(currentTrackData.getAverageCadence() + " vpm");
        setProgress(2, 4, currentTrackData.getAverageCadence(), currentTrackData.getMaxCadence());
        maxCadence.setText(currentTrackData.getMaxCadence() + " vpm");
        setProgress(5, 4, currentTrackData.getMaxCadence(), currentTrackData.getMaxCadence());
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
        setupAnimation();
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
        ObservableList<Chunk> chunks = currentTrackData.getChunks();

        switch (selectChartBox.getSelectionModel().getSelectedItem().charAt(0)) {
            case 'A':
                Task<ObservableList> task = new Task<ObservableList>() {

                    @Override
                    protected ObservableList<XYChart.Series<Double, Double>> call() throws Exception {
                        Thread.sleep(5000);
                        return altitudePerDistance(chunks);
                    }
                };
                task.setOnSucceeded((WorkerStateEvent event1) -> {
                    areaChart(task.getValue(), "Altura (m)", "Distancia (Km)");
                });
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();

                break;
            case 'V':
                lineChart(speedPerDistance(chunks), "Velocidad (km/h)", "Distancia (Km)");
                break;
            case 'F':
                lineChart(fcPerDistance(chunks), "Frecuencia Cardíaca (bps)", "Distancia (Km)");
                break;
            case 'C':
                lineChart(cadencePerDistance(chunks), "Cadencia (vps)", "Distancia (Km)");
                break;

        }
    }

    private void setupAnimation() {
        heartZonePieChart.getData().stream().forEach(pieData -> {
            System.out.println(pieData.getName() + ": " + pieData.getPieValue());
            if (pieData.getPieValue() == 0) {
            } else {
                pieData.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    Bounds b1 = pieData.getNode().getBoundsInLocal();
                    double newX = (b1.getWidth()) / 2 + b1.getMinX();
                    double newY = (b1.getHeight()) / 2 + b1.getMinY();
                    pieData.getNode().setTranslateX(0);
                    pieData.getNode().setTranslateY(0);

                    TranslateTransition tt = new TranslateTransition(
                            Duration.millis(3000), pieData.getNode());
                    tt.setByX(newX);
                    tt.setByY(newY);
                    tt.setAutoReverse(true);
                    tt.setCycleCount(2);
                    tt.play();
                });
            }
        });
    }

    private void lineChart(ObservableList a, String y, String x) {

        chartContainer.getChildren().remove(2);
        LineChart lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        lineChart.setData(a);
        lineChart.getXAxis().setLabel(x);
        lineChart.getYAxis().setLabel(y);
        lineChart.setTitle(y + " por " + x);
        lineChart.setMaxSize(1000000, 100000);
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(true);
        chartContainer.add(lineChart, 0, 1);

    }

    private void areaChart(ObservableList a, String y, String x) {
        chartContainer.getChildren().remove(2);
        AreaChart areaChart = new AreaChart(new NumberAxis(), new NumberAxis());
        areaChart.getXAxis().setLabel(x);
        areaChart.getYAxis().setLabel(y);
        areaChart.setData(a);
        areaChart.setTitle(y + " por " + x);
        areaChart.setMaxSize(1000000, 100000);
        areaChart.setCreateSymbols(false);
        chartContainer.add(areaChart, 0, 1);
    }

    private ObservableList<XYChart.Series<Double, Double>> altitudePerDistance(ObservableList<Chunk> list) {
        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        double distance = 0;
        double grade = 0;
        for (int i = 0; i < list.size(); i++) {
            distance += list.get(i).getDistance();
            grade += list.get(i).getAscent() - list.get(i).getDescend();
            series.getData().add(new XYChart.Data(distance / 1000, grade));
        }
        series.setName("Altitude per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> speedPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        double distance = 0;
        for (int i = 0; i < list.size(); i++) {
            distance += list.get(i).getDistance();
            series.getData().add(new XYChart.Data(distance / 1000, list.get(i).getSpeed()));
            System.out.println(list.get(i).getDistance() + "acumulada: " + distance);

        }
        series.setName("Speed per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> speedPerTime(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        double time = 0;
        for (int i = 0; i < list.size(); i++) {
            time += list.get(i).getDuration().getSeconds();
            series.getData().add(new XYChart.Data(time, list.get(i).getSpeed()));
            System.out.println(list.get(i).getDistance() + "acumulada: " + time);

        }
        series.setName("Speed per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> fcPerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        double distance = 0;
        for (int i = 0; i < list.size(); i++) {
            distance += list.get(i).getDistance();
            series.getData().add(new XYChart.Data(distance / 1000, list.get(i).getAvgHeartRate()));

        }
        series.setName("Heart Rate per Distance");
        res.addAll(series);
        return res;
    }

    private ObservableList<XYChart.Series<Double, Double>> cadencePerDistance(ObservableList<Chunk> list) {

        ObservableList<XYChart.Series<Double, Double>> res = FXCollections.observableArrayList();
        Series<Double, Double> series = new Series<>();
        double distance = 0;
        for (int i = 0; i < list.size(); i++) {
            distance += list.get(i).getDistance();
            series.getData().add(new XYChart.Data(distance / 1000, list.get(i).getAvgCadence()));

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

    private void setProgress(int column, int row, double value, double max) {
        double maxim = Math.random();
        if (maxim < 0.7) {
            maxim = 0.78;
        }
        value = value * maxim / max;
        String color = "";
        if (value > 0.78) {
            color = "high-bar";
        } else if (value > 0.50) {
            color = "mid-bar";
        } else if (value > 0.25) {
            color = "poor-bar";
        } else {
            color = "zero-bar";
        }

        ColoredProgressBar bar = new ColoredProgressBar(color, value);
        bar.setMinSize(130, 20);
        bar.setMaxSize(130, 20);
        if (row == 6 && column == 2) {
            GridPane.setRowSpan(bar, 2);
        }
        activityContainer.add(bar, column, row);
    }

    @FXML
    private void changeTimeDistance(ActionEvent event) {
        switch (selectChartBox.getSelectionModel().getSelectedItem().charAt(0)) {
            case 'A':
                AreaChart chart = (AreaChart) chartContainer.getChildren().get(2);
                if ("Distancia (Km)".equals(chart.getXAxis().getLabel())) {
                  chart.setData(speedPerTime(currentTrackData.getChunks()));
                }else{
                  chart.setData(speedPerDistance(currentTrackData.getChunks()));
                }
                break;
            case 'V':
                ((LineChart) chartContainer.getChildren().get(2)).setData(speedPerTime(currentTrackData.getChunks()));
                break;
            case 'F':
                ((LineChart) chartContainer.getChildren().get(2)).setData(speedPerTime(currentTrackData.getChunks()));
                break;
            case 'C':
                ((LineChart) chartContainer.getChildren().get(2)).setData(speedPerTime(currentTrackData.getChunks()));
                break;
        }

    }
}
