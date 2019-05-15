package groups;

import common.Constants;
import common.MainWindowController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Grupo;
import modelo.Sesion;
import modelo.SesionTipo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailGroup extends MainWindowController {

    @FXML
    private CheckBox realTime;
    @FXML
    private CheckBox workTime;
    @FXML
    private CheckBox restTime;
    @FXML
    private ChoiceBox<Integer> choiceBox;
    @FXML
    private TextField code;
    @FXML
    private TextArea description;


//    @FXML
//    private AreaChart timeGraph;
    @FXML
    private LineChart<String, Double> timeGraph;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private Grupo group;

    public void cancel(MouseEvent mouseEvent) {
        loadScene(Constants.GROUPS);
    }

    public void initGroup(Grupo group) {
        this.group = group;
        code.setText(group.getCodigo());
        description.setText(group.getDescripcion());

        choiceBox.setItems(FXCollections.observableArrayList(1,5,10));
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                graph());
        realTime.selectedProperty().addListener(((observable, oldValue, newValue) ->
                graph()));
        workTime.selectedProperty().addListener(((observable, oldValue, newValue) ->
                graph()));
        restTime.selectedProperty().addListener(((observable, oldValue, newValue) ->
                graph()));
        graph();


    }

    private void graph()
    {
        timeGraph.getData().clear();

        timeGraph.setTitle("Time graph");
        XYChart.Series<String, Double> workTime = new XYChart.Series<>();
        workTime.setName("Work time");
        XYChart.Series<String, Double> restTime = new XYChart.Series<>();
        restTime.setName("Rest time");
        XYChart.Series<String, Double> realTime = new XYChart.Series<>();
        realTime.setName("Total time");
        xAxis.setLabel("Session");
        yAxis.setLabel("Minutes");
        xAxis.setGapStartAndEnd(choiceBox.getValue() == 1);



        int iterations = choiceBox.getValue();

        ArrayList<Sesion> groupSessions = group.getSesiones();
        LocalDateTime date;
        Duration duration;
        SesionTipo template;

        String xTick = "";



        List<String> list = new ArrayList<>();

        int workSeconds;
        int restSeconds;
        int it = 0;
        for(Sesion s : groupSessions) {
            date = s.getFecha();
            duration = s.getDuracion();
            template = s.getTipo();
            workSeconds = template.getT_calentamiento()+template.getT_ejercicio()*template.getNum_ejercicios()*template.getNum_circuitos();
            restSeconds = template.getD_ejercicio()*(template.getNum_ejercicios()-1) + template.getD_circuito()*(template.getNum_circuitos()-1);

            xTick = date.format(DateTimeFormatter.ofPattern("HH.mm dd.MM.YY")) + " \n\t " + s.getTipo().getCodigo();

            realTime.getData().add(new XYChart.Data<>(xTick, duration.getSeconds()/60.0));
            workTime.getData().add(new XYChart.Data<>(xTick, workSeconds / 60.0));
            restTime.getData().add(new XYChart.Data<>(xTick, restSeconds/60.0));
            list.add(xTick);


            it++;
            if(it == iterations)
            {
                break;
            }
        }



        if (this.realTime.isSelected())
        {timeGraph.getData().add(realTime);}
        if(this.workTime.isSelected())
        {timeGraph.getData().add(workTime);}
        if(this.restTime.isSelected())
        {timeGraph.getData().add(restTime);}

        xAxis.getCategories().clear();
        xAxis.setAutoRanging(false);
        xAxis.setCategories(FXCollections.<String>observableArrayList(list));
        xAxis.invalidateRange(list);






    }
}
