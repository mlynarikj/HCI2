/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import accesoBD.AccesoBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import modelo.Gym;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * FXML Controller class
 *
 * @author olemf
 */
public class MainWindowController implements Initializable {

    protected Stage stage;

    protected Gym gym;

    protected ResourceBundle bundle;
    protected Map<String, Consumer<Scene>> styles;
    protected String styleName;





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gym = AccesoBD.getInstance().getGym();
        bundle = resources;

    }

    public void initStage(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initStyles(Map<String, Consumer<Scene>> styles, String st){
        this.styles = styles;
        styleName =st;
    }

    @FXML
    protected void session(MouseEvent mouseEvent) {
        loadScene(Constants.SESSION);
    }

    @FXML
    protected void groups(MouseEvent mouseEvent) {
        loadScene(Constants.GROUPS);
    }

    @FXML
    protected void templates(MouseEvent mouseEvent) {
        loadScene(Constants.TEMPLATES);
    }




    protected <T extends MainWindowController> T loadScene(String fxml, Consumer<T> function) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), bundle);
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert.setTitle(bundle.getString("alerts.invalidFxml"));
            alert.setContentText(e.getMessage());
            alert.show();
            return null;
        }
        T controller = loader.getController();
        controller.initStage(stage);
        function.accept(controller);


        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
//        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        stage.setScene(scene);
        controller.initStyles(styles, styleName);
        styles.get(styleName).accept(scene);
        stage.show();
        return controller;
    }

    protected <T extends MainWindowController> T loadScene(String fxml) {
        return loadScene(fxml, t -> {
        });
    }
}



