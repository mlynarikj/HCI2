package sessions;

import common.Constants;
import common.MainWindowController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import modelo.Grupo;
import modelo.SesionTipo;

import java.net.URL;
import java.util.ResourceBundle;

public class Session extends MainWindowController {
    @FXML
    private Button start;
    @FXML
    private ChoiceBox<Grupo> group;
    @FXML
    private ChoiceBox<SesionTipo> template;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        group.setItems(FXCollections.observableList(gym.getGrupos()));
        template.setItems(FXCollections.observableList(gym.getTiposSesion()));
        group.setConverter(new StringConverter<Grupo>() {
            @Override
            public String toString(Grupo object) {
                return object.getCodigo();
            }

            @Override
            public Grupo fromString(String string) {
                return gym.getGrupos().stream().filter(p -> p.getCodigo().equals(string)).findFirst().get();
            }
        });
        template.setConverter(new StringConverter<SesionTipo>() {
            @Override
            public String toString(SesionTipo object) {
                return object.getCodigo();
            }

            @Override
            public SesionTipo fromString(String string) {
                return gym.getTiposSesion().stream().filter(p -> p.getCodigo().equals(string)).findFirst().get();
            }
        });

        group.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue.getDefaultTipoSesion() != null) {
                template.getSelectionModel()
                        .select(
                                template.getItems()
                                        .stream()
                                        .filter(p -> p.getCodigo().equals(newValue.getDefaultTipoSesion().getCodigo()))
                                        .findFirst()
                                        .get());
            } else {
                template.getSelectionModel().selectFirst();
            }
        });
        start.disableProperty().bind(
                Bindings.or(
                        Bindings.equal(-1, group.getSelectionModel().selectedIndexProperty()),
                        Bindings.equal(-1, template.getSelectionModel().selectedIndexProperty())));

    }

    public void start(MouseEvent mouseEvent) {
        this.<SessionRunning>loadScene(Constants.SESSION_RUNNING, p -> {
            p.initTemplate(template.getValue());
            p.initGroup(group.getValue());
        });
    }
}
