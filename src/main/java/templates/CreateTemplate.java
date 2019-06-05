package templates;

import common.Constants;
import common.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import modelo.SesionTipo;

import java.util.List;
import java.util.stream.Collectors;


public class CreateTemplate extends MainWindowController {

    @FXML
    private TextField code;
    @FXML
    private TextField warmTime;
    @FXML
    private TextField nExercises;
    @FXML
    private TextField workTime;
    @FXML
    private TextField restTime;
    @FXML
    private TextField cRepetitions;
    @FXML
    private TextField cRest;


    @Override
    public void initStage(Stage primaryStage)
    {
        super.initStage(primaryStage);
        primaryStage.setTitle(bundle.getString("createtemplate.title"));
    }

    public void save(MouseEvent mouseEvent) {

        StringBuilder errors = new StringBuilder();
        SesionTipo template = new SesionTipo();
        List<String> codes = gym.getTiposSesion().stream().map(SesionTipo::getCodigo).collect(Collectors.toList());


        template.setCodigo(code.getText());
        if(template.getCodigo().isEmpty())
        {
            errors.append(bundle.getString("alerts.emptycode"));
        }
        if(codes.contains(template.getCodigo()))
        {
            errors.append(bundle.getString("alerts.notuniquecode"));
        }

        if (warmTime.getText().matches("[0-9]+"))
        {
            template.setT_calentamiento(Integer.parseInt(warmTime.getText()));
        }
        else
        {
            errors.append(bundle.getString("alerts.wrongwarmtime"));
        }

        if (nExercises.getText().matches("[0-9]+"))
        {
            template.setNum_ejercicios(Integer.parseInt(nExercises.getText()));

        }
        else
        {
            errors.append(bundle.getString("alerts.wrongnexercises"));
        }

        if (workTime.getText().matches("[0-9]+"))
        {
            template.setT_ejercicio(Integer.parseInt(workTime.getText()));
        }
        else
        {
            errors.append(bundle.getString("alerts.wrongworkingtime"));
        }

        if (restTime.getText().matches("[0-9]+"))
        {
            template.setD_ejercicio(Integer.parseInt(restTime.getText()));
        }
        else
        {
            errors.append(bundle.getString("alerts.wrongresttime"));
        }

        if (cRepetitions.getText().matches("[0-9]+"))
        {
            template.setNum_circuitos(Integer.parseInt(cRepetitions.getText()));
        }
        else
        {
            errors.append(bundle.getString("alerts.wrongncircuit"));
        }

        if (cRest.getText().matches("[0-9]+"))
        {
            template.setD_circuito(Integer.parseInt(cRest.getText()));
        }
        else
        {
            errors.append(bundle.getString("alerts.crest"));
        }

        if(errors.length() != 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("alerts.invalidtemplate"));
            alert.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert.setContentText(errors.toString());
            alert.show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(bundle.getString("alerts.message"));;
        alert.setTitle(bundle.getString("alerts.templatecreated"));;
        alert.getDialogPane().getStylesheets().add("styles/alerts.css");
        alert.setContentText(bundle.getString("template") + " " + code.getText() + bundle.getString("created"));
        alert.show();

        gym.getTiposSesion().add(template);
        loadScene(Constants.TEMPLATES);
    }

    public void cancel(MouseEvent mouseEvent) {
        loadScene(Constants.TEMPLATES);
    }
}
