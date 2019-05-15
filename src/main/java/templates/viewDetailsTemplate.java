package templates;

import common.Constants;
import common.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import modelo.SesionTipo;

public class viewDetailsTemplate extends MainWindowController {

    @FXML
    private Label code;
    @FXML
    private Label warmTime;
    @FXML
    private Label nExercises;
    @FXML
    private Label workTime;
    @FXML
    private Label restTime;
    @FXML
    private Label nCircuit;
    @FXML
    private Label restCircuit;

    public void initTemplate(SesionTipo template)
    {
        code.setText(template.getCodigo());
        warmTime.setText(Integer.toString(template.getT_calentamiento()));
        nExercises.setText(Integer.toString(template.getNum_ejercicios()));
        workTime.setText((Integer.toString(template.getT_ejercicio())));
        restTime.setText(Integer.toString((template.getD_ejercicio())));
        nCircuit.setText(Integer.toString(template.getNum_circuitos()));
        restCircuit.setText((Integer.toString(template.getD_circuito())));


    }

    public void cancel(MouseEvent mouseEvent){
        loadScene(Constants.TEMPLATES);
    }
}
