package common;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MenuController extends MainWindowController {

    @FXML
    private ChoiceBox<String> style;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        style.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            styles.get(newValue).accept(stage.getScene());
            styleName = newValue;
        });
    }

    @Override
    public void initStyles(Map<String, Consumer<Scene>> styles, String st) {
        super.initStyles(styles, st);
        style.setItems(FXCollections.observableArrayList(styles.keySet()));
        style.getSelectionModel().select(st);
    }
}

