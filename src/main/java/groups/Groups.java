package groups;

import common.Constants;
import common.MainWindowController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import modelo.Grupo;

import java.net.URL;
import java.util.ResourceBundle;

public class Groups extends MainWindowController {

    @FXML
    private TableView<Grupo> groupTable;
    @FXML
    private TableColumn<Grupo, String> code;
    @FXML
    private Button view;
    @FXML
    private Button modify;

    private ObservableList<Grupo> groups;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        groups = FXCollections.observableList(gym.getGrupos());
        code.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCodigo()));
        groupTable.setItems(groups);
        view.disableProperty().bind(
                Bindings.equal(-1,
                        groupTable.getSelectionModel().selectedIndexProperty()));
        modify.disableProperty().bind(
                Bindings.equal(-1,
                        groupTable.getSelectionModel().selectedIndexProperty()));

    }

    public void add(MouseEvent mouseEvent) {
        loadScene(Constants.NEW_GROUP);
    }

    public void view(MouseEvent mouseEvent) {
        this.<DetailGroup>loadScene(Constants.DETAIL_GROUP, param -> param.initGroup(groupTable.getSelectionModel().getSelectedItem()));

    }

    public void modify(MouseEvent mouseEvent) {
        this.<CreateUpdateGroup>loadScene(Constants.NEW_GROUP, param -> param.initGroup(groupTable.getSelectionModel().getSelectedItem()));
    }
}
