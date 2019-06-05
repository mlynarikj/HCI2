package sessions;

import common.MenuController;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
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
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SessionRunning extends MenuController {
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
    private CountDown service;
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
    protected void session(MouseEvent mouseEvent) {
        clip.stop();
        service.cancel();
        super.session(mouseEvent);
    }

    @Override
    protected void groups(MouseEvent mouseEvent) {
        clip.stop();
        service.cancel();
        super.groups(mouseEvent);
    }

    @Override
    protected void templates(MouseEvent mouseEvent) {
        clip.stop();
        service.cancel();
        super.templates(mouseEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        clip = new AudioClip(getClass().getResource("/sounds/metronome.wav").toString());
        session = new Sesion();
        session.setFecha(LocalDateTime.now());
        time = new SimpleDoubleProperty(0);
        progressIndicator.progressProperty().bind(time);
        //service = new ScheduledService<String>() {
        //@Override
        //protected void succeeded() {
        //super.succeeded();
        //if (currentTime + 3 >= finalTime && !clip.isPlaying()) {
        //signal();
        //}
        //if (currentTime >= finalTime) {
        //cancel();
        //nextStage();
        //}
        //}
        //
        //
        //@Override
        //protected Task<String> createTask() {
        //return new Task<String>() {
        //@Override
        //protected String call() throws Exception {
        //if (currentTime < finalTime) {
        //currentTime++;
        //}
        //time.set(((double) currentTime) / finalTime);
        //return String.format("%02d:%02d", currentTime / 60, currentTime % 60);
        //}
        //};
        //
        //}
        //};


        service = new CountDown();
        service.setTimeString(timeLeft.textProperty());
        service.setTimeDouble(time);
    }


    class CountDown extends Service<Void> {

        private static final int DELAY = 100;
        private long lastTime = 0;
        private long startTime = 0;
        private long stoppedTime = 0;

        private boolean stopped = false;
        private long countDownMillis;


        @Override
        protected void succeeded() {
            super.succeeded();
            nextStage();
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            lastTime = System.currentTimeMillis();
            stopped = true;
            clip.stop();
        }

        public void clear() {

            lastTime = 0;
            startTime = 0;
            stoppedTime = 0;
            stopped = false;
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() {
                    if (!stopped) {
                        startTime = lastTime = System.currentTimeMillis();
                        stoppedTime = 0;
                    } else {
                        long elapsedTime = System.currentTimeMillis() - lastTime;
                        stoppedTime = stoppedTime + elapsedTime;
                        stopped = false;
                    }
                    while (true) {
                        try {
                            Thread.sleep(DELAY);
                        } catch (InterruptedException ex) {
                            if (isCancelled()) {
                                break;
                            }
                        }
                        if (isCancelled()) {
                            break;
                        }
                        if (countDown()) {
                            break;
                        }

                    }
                    return null;
                }

                private boolean countDown() {
                    lastTime = System.currentTimeMillis();
                    long totalTime = (lastTime - startTime) - stoppedTime;
                    java.time.Duration duration = java.time.Duration.ofMillis(countDownMillis - totalTime);
                    final long minutes = duration.toMinutes();
                    final long seconds = duration.minusMinutes(minutes).getSeconds();
                    final long nanos = duration.minusSeconds(seconds).toNanos();
                    if (seconds < 3) {
                        signal();
                    }
                    Platform.runLater(() -> {
                        timeString.setValue(String.format("%02d:%02d", minutes, seconds));
                        timeDouble.setValue(((double) totalTime) / countDownMillis);
                    });
                    return (seconds == 0) && (minutes == 0) && (nanos < 100000000);
                }
            };
        }

        private StringProperty timeString = new SimpleStringProperty();

        public String getTimeString() {
            return timeString.get();
        }

        public void setTimeString(StringProperty value) {
            timeString = value;
        }

        private SimpleDoubleProperty timeDouble = new SimpleDoubleProperty();

        public Double getTimeDouble() {
            return timeDouble.get();
        }

        public void setTimeDouble(SimpleDoubleProperty value) {
            timeDouble = value;
        }

        public void setCountDownMillis(long countDownMillis) {
            this.countDownMillis = countDownMillis;
        }
    }

    private void signal() {
        if (!clip.isPlaying()) {
            clip.play();
        }
        progressIndicator.setStyle("-fx-accent: red;");
    }

    private void nextStage() {
        clip.stop();
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

            exerciseNumber.setText("Session \ninformation:\nStart: " +
                    session.getFecha().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.", this.bundle.getLocale())) +
                    "\nDuration: " +
                    String.format("%02d:%02d", duration.getSeconds() / 60, duration.getSeconds() % 60)
                    );
            circuitNumber.setText("\nGroup:\n" +
                    group.getCodigo() +
                    "\nTemplate:\n" +
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
        service.setCountDownMillis(finalTime * 1000);
        service.cancel();
        service.clear();
        service.reset();
        service.start();
    }

    public void restart(MouseEvent mouseEvent) {
        clip.stop();
        setup();
        service.cancel();
        service.clear();
        service.reset();
        service.start();
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
        service.setCountDownMillis(finalTime * 1000);
        progressIndicator.setStyle("");
        exerciseNumber.setText("Exercises: \n" + (stage + 1) / 2 + "/" + template.getNum_ejercicios());
        circuitNumber.setText("Circuits: \n" + (circuit + 1) + "/" + template.getNum_circuitos());
    }

    public void pause(MouseEvent mouseEvent) {
        if (paused) {
            pause.setText("||");
            service.reset();
            service.start();
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
