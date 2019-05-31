package sessions;

import common.MainWindowController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SessionRunning extends MainWindowController {
    @FXML
    private Label circuitNumber;
    @FXML
    private Button advance;
    @FXML
    private Button restart;
    @FXML
    private Label exerciseNumber;
    @FXML
    private Label interval;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label timeLeft;
    @FXML
    private Button pause;
    private Grupo group;
    private SesionTipo template;
    private boolean paused = false;
    private long startTime;
    private long currentTime = -1;
    private long finalTime;
    private SimpleDoubleProperty time;
    private ScheduledService<String> service;
    private Sesion session;

    private int stage = 0;
    private int circuit = 0;
    private AudioClip clip;


    public void initGroup(Grupo value) {
        group = value;
    }

    public void initTemplate(SesionTipo value) {
        template = value;
        session.setTipo(template);
        setup();
        service.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        clip = new AudioClip(getClass().getResource("/sounds/metronome.wav").toString());
        session = new Sesion();
        session.setFecha(LocalDateTime.now());

        time = new SimpleDoubleProperty(0);
        progressIndicator.progressProperty().bind(time);
        service = new ScheduledService<String>() {
            @Override
            protected void succeeded() {
                super.succeeded();
                if (currentTime + 3 >= finalTime && !clip.isPlaying()) {
                    signal();
                }
                if (currentTime >= finalTime) {
                    cancel();
                    nextStage();
                }
            }


            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        if (currentTime < finalTime) {
                            currentTime++;
                        }
                        time.set(((double) currentTime) / finalTime);
                        return String.format("%02d:%02d", currentTime / 60, currentTime % 60);
                    }
                };

            }
        };
        service.setPeriod(Duration.seconds(1));

        timeLeft.textProperty().bind(service.lastValueProperty());
    }

    private void signal() {
        clip.play();
        progressIndicator.setStyle("-fx-accent: red;");
    }

    private void nextStage() {
        progressIndicator.setStyle("");
//        LAST EXERCISE IN THE CIRCUIT
        if (stage == template.getNum_ejercicios() * 2 - 1) {
            circuit++;
            stage = -1;
        }
//        THE LAST CIRCUIT
        if (circuit == template.getNum_circuitos()) {

            ArrayList<Sesion> tmp = group.getSesiones();
            tmp.add(session);
            group.setSesiones(tmp);
            service.cancel();
            java.time.Duration duration = java.time.Duration.between(session.getFecha(), LocalDateTime.now());
            session.setDuracion(duration);
            group.setDefaultTipoSesion(template);

            exerciseNumber.setText("Session information:\nStart: " +
                    session.getFecha().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.", this.bundle.getLocale())) +
                    "\nDuration: " +
                    String.format("%02d:%02d", duration.getSeconds() / 60, duration.getSeconds() % 60) +
                    "\nGroup: " +
                    group.getCodigo() +
                    "\nTemplate: " +
                    template.getCodigo());
            timeLeft.setVisible(false);
            progressIndicator.setVisible(false);
            pause.setVisible(false);
            advance.setVisible(false);
            restart.setVisible(false);
            interval.setText("FINISHED");
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
            bc.setTitle("Session Summary");
            xAxis.setLabel("Session stage");
            yAxis.setLabel("Time");
            bc.setLegendVisible(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            XYChart.Data real = new XYChart.Data<>("Real time", duration.getSeconds());
            XYChart.Data work = new XYChart.Data<>("Work time", template.getNum_ejercicios() * template.getNum_circuitos() * template.getT_ejercicio() + template.getT_calentamiento());
            XYChart.Data rest = new XYChart.Data<>("Rest time", (template.getNum_ejercicios() - 1) * template.getD_ejercicio() * template.getNum_circuitos() + (template.getNum_circuitos() - 1) * template.getD_circuito());
            

            series.getData().add(real);
            series.getData().add(work);
            series.getData().add(rest);
            bc.getData().add(series);
            ((VBox) interval.getParent()).getChildren().add(bc);
            real.getNode().setStyle("-fx-bar-fill: blue;");
            work.getNode().setStyle("-fx-bar-fill: red;");
            rest.getNode().setStyle("-fx-bar-fill: green;");
            return;
        }

        stage++;
        if (stage == 0) {
//          CIRCUIT REST
            finalTime = template.getD_circuito();
            interval.setText("Circuit rest");
        } else {
//          REGULAR EXERCISE/REST
            finalTime = stage % 2 == 0 ? template.getD_ejercicio() : template.getT_ejercicio();
            interval.setText(stage % 2 == 0 ? "Rest" : "Exercise");
        }
        currentTime = -1;
        exerciseNumber.setText("Exercises: \n" + (stage + 1) / 2 + "/" + template.getNum_ejercicios());
        circuitNumber.setText("Circuits: \n" + (circuit + 1) + "/" + template.getNum_circuitos());
        service.restart();
    }

    public void restart(MouseEvent mouseEvent) {
        clip.stop();
        setup();
        service.restart();
    }

    private void setup() {
        currentTime = -1;
        stage = 0;
        circuit = 0;
        pause.setText("||");
        paused = false;
        if (template.getT_calentamiento() != 0) {
            finalTime = template.getT_calentamiento();
            interval.setText("Warm up");
        } else {
            interval.setText("Exercise");
            finalTime = template.getT_ejercicio();
            stage++;
        }
        progressIndicator.setStyle("");
        exerciseNumber.setText("Exercises: \n" + (stage + 1) / 2 + "/" + template.getNum_ejercicios());
        circuitNumber.setText("Circuits: \n" + (circuit + 1) + "/" + template.getNum_circuitos());
    }

    public void pause(MouseEvent mouseEvent) {
        if (paused) {
            pause.setText("||");
            service.restart();
        } else {
            pause.setText("►");
            service.cancel();
        }
        paused = !paused;
    }

    public void advance(MouseEvent mouseEvent) {
        clip.stop();
        pause.setText("||");
        paused = false;
        nextStage();
    }

}
