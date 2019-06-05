import accesoBD.AccesoBD;
import common.Constants;
import common.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class CrossFit extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

//        AtomicReference<Locale> local = new AtomicReference<>(Locale.getDefault());
//        ButtonType english = new ButtonType("English");
//        ButtonType spanish = new ButtonType("Espa\u00F1ol");
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.getButtonTypes().setAll(english, spanish);
//        alert.setTitle("Choose language");
//        alert.getDialogPane().getStylesheets().add("styles/alerts.css");
//        alert.setHeaderText("Confirmation/Confirmaci\u00F3n");
//        alert.setContentText("Choose language\nElegir idioma");
//        alert.showAndWait().ifPresent(p -> {
//            if (p == spanish) {
//                local.set(new Locale("es"));
//            }
//        });

        ResourceBundle bundle = ResourceBundle.getBundle("languages", Locale.ENGLISH);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.MAIN_WINDOW), bundle);

        Parent root = loader.load();

        MainWindowController main = loader.<MainWindowController>getController();

        main.initStage(primaryStage);

        primaryStage.setOnCloseRequest(event -> {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert1.setHeaderText(bundle.getString("alert.message"));
            alert1.setTitle(bundle.getString("savingDB"));
            alert1.setContentText(bundle.getString("savingDB"));
            alert1.show();
            AccesoBD.getInstance().salvar();
            alert1.close();
        });
        Scene scene = new Scene(root);

        primaryStage.setTitle(bundle.getString("mainWindow.title"));
        primaryStage.setScene(scene);
        main.initStyles(new HashMap<String, Consumer<Scene>>(){{
            put("Bootstrap", scene -> {scene.getStylesheets().clear();scene.getStylesheets().add("styles/tables.css");});
            put("Flat red",scene -> {scene.getStylesheets().clear();scene.getStylesheets().add("styles/flatred.css");});
            put("Sky", scene -> {scene.getStylesheets().clear();scene.getStylesheets().add("styles/sky.css");});
        }
        },"Bootstrap");
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}