package templates;

import common.Constants;
import common.MainWindowController;
import common.MenuController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import modelo.Gym;
import modelo.SesionTipo;

import java.net.URL;
import java.util.ResourceBundle;

public class Templates extends MenuController {


    @FXML
    private TableView<SesionTipo> templateTable;
    @FXML
    private TableColumn<SesionTipo, String> code;
    @FXML
    private TableColumn<SesionTipo, Integer> nExercises;
    @FXML
    private Button createTemplate;
    @FXML
    private Button viewDetails;
    @FXML
    private Button sessions;
    @FXML
    private Button groups;
    @FXML
    private Button templates;

    private ObservableList<SesionTipo> obsList = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        viewDetails.disableProperty().bind(Bindings.equal(-1, templateTable.getSelectionModel().selectedIndexProperty()));
        code.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCodigo()));
        nExercises.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getNum_ejercicios()).asObject());

        obsList = FXCollections.observableList(gym.getTiposSesion());
        templateTable.setItems(obsList);
    }

    @Override
    public void initStage(Stage primaryStage) {
        super.initStage(primaryStage);
        primaryStage.setTitle(bundle.getString("template"));
    }


    public void createTemplate(MouseEvent mouseEvent) {
        loadScene(Constants.CREATE_TEMPLATE);
    }

    public void viewDetails(MouseEvent mouseEvent) {
        this.<viewDetailsTemplate>loadScene(Constants.VIEW_DETAILS_TEMPLATE, param -> param.initTemplate(templateTable.getSelectionModel().getSelectedItem()));
    }


}
