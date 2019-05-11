package sessions;

import common.MainWindowController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import modelo.Grupo;
import modelo.SesionTipo;

import java.net.URL;
import java.util.ResourceBundle;

public class SessionRunning extends MainWindowController {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label timeLeft;
    @FXML
    private Button pause;
    private Grupo group;
    private SesionTipo template;
    private boolean paused = false;

    public void initGroup(Grupo value) {
        group = value;
    }

    public void initTemplate(SesionTipo value) {
        template = value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ScheduledService<Void> s = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        return null;
                    }
                };
            }
        };
        s.
    }

    public void restart(MouseEvent mouseEvent) {

    }

    public void pause(MouseEvent mouseEvent) {
        if (paused) {
            pause.setText("||");
        }else {
            pause.setText("►");
        }
        paused = !paused;
    }

    public void advance(MouseEvent mouseEvent) {

    }

}
