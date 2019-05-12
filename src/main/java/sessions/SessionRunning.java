package sessions;

import common.MainWindowController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SessionRunning extends MainWindowController {
    @FXML
    private Button advance;
    @FXML
    private Button restart;
    @FXML
    private Label exerciseNumber;
    @FXML
    private Label interval;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label timeLeft;
    @FXML
    private Button pause;
    private Grupo group;
    private SesionTipo template;
    private boolean paused = false;
    private long startTime;
    private long currentTime = 0;
    private long finalTime;
    private SimpleDoubleProperty time;
    private ScheduledService<String> service;
    private Sesion session;

    private int stage = 0;
    private int circuit = 0;
    private Clip clip;


    public void initGroup(Grupo value) {
        group = value;
        ArrayList<Sesion> tmp = group.getSesiones();
        tmp.add(session);
        group.setSesiones(tmp);
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

        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    getClass().getResourceAsStream("/sounds/metronome.wav"));
            clip.open(inputStream);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        session = new Sesion();
        session.setFecha(LocalDateTime.now());

        time = new SimpleDoubleProperty(0);
        progressBar.progressProperty().bind(time);

        service = new ScheduledService<String>() {
            @Override
            protected void succeeded() {
                super.succeeded();
                if (currentTime + 3 >= finalTime && !clip.isRunning()) {
                    playSound();
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

    private void playSound() {
        clip.start();
        clip.setFramePosition(0);
    }

    private void nextStage() {

//        LAST EXERCISE IN THE CIRCUIT
        if (stage == template.getNum_ejercicios() * 2 - 1) {
            circuit++;
            stage = -1;
        }
//        THE LAST CIRCUIT
        if (circuit == template.getNum_circuitos()) {
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
            progressBar.setVisible(false);
            pause.setVisible(false);
            advance.setVisible(false);
            restart.setVisible(false);
            interval.setText("FINISHED");
            return;
        }

        stage++;
        if (stage == 0) {
//          CIRCUIT REST
            finalTime = template.getD_circuito();
            interval.setText("Rest between circuits");
        } else {
//          REGULAR EXERCISE/REST
            finalTime = stage % 2 == 0 ? template.getD_ejercicio() : template.getT_ejercicio();
            interval.setText(stage % 2 == 0 ? "Rest between exercises" : "Exercise");
        }
        currentTime = 0;
        exerciseNumber.setText("Exercises: " + (stage + 1) / 2 + "/" + template.getNum_ejercicios() + "\nCircuits: " + (circuit + 1) + "/" + template.getNum_circuitos());
        service.restart();
    }

    public void restart(MouseEvent mouseEvent) {
        setup();
        service.restart();
    }

    private void setup() {
        currentTime = 0;
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
        exerciseNumber.setText("Exercises: " + (stage + 1) / 2 + "/" + template.getNum_ejercicios() + "\nCircuits: " + (circuit + 1) + "/" + template.getNum_circuitos());
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
        pause.setText("||");
        paused = false;
        nextStage();
    }

}
