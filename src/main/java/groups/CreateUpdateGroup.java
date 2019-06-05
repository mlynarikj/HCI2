package groups;

import common.Constants;
import common.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Grupo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateUpdateGroup extends MainWindowController {
    @FXML
    private TextField code;
    @FXML
    private TextArea description;

    private Grupo group;

    public void initGroup(Grupo group) {
        this.group = group;
        code.setText(group.getCodigo());
        description.setText(group.getDescripcion());
    }

    public void save(MouseEvent mouseEvent) {
        boolean create = false;
        String prevCode = "";
        if (group == null) {
            group = new Grupo();
            create = true;
        } else {
            prevCode = group.getCodigo();
        }
        StringBuilder errors = new StringBuilder();
        List<String> codes = gym.getGrupos().stream().map(Grupo::getCodigo).collect(Collectors.toList());
        group.setCodigo(code.getText());
        if (group.getCodigo().isEmpty()) {
            errors.append(bundle.getString("alerts.emptycode"));
        }
        if (!prevCode.equals(group.getCodigo()) && codes.contains(group.getCodigo())) {
            errors.append(bundle.getString("alerts.notuniquecode"));
        }
        group.setDescripcion(description.getText());
        if (errors.length() != 0) {
            group = create?null:group;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert.setTitle(bundle.getString("alerts.invalidgroup"));
            alert.setContentText(errors.toString());
            alert.show();
            return;
        }
        if (create) {
            ArrayList<Grupo> grupos = gym.getGrupos();
            grupos.add(group);
            gym.setGrupos(grupos);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert.setTitle(bundle.getString("alerts.groupcreated"));
            alert.setContentText(bundle.getString("group") + " " + code.getText() + bundle.getString("created"));
            alert.show();
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add("styles/alerts.css");
            alert.setTitle(bundle.getString("alerts.groupchanged"));
            alert.setContentText(bundle.getString("group") + " " + code.getText() + bundle.getString("changed"));
            alert.show();

        }
        loadScene(Constants.GROUPS);

    }

    public void cancel(MouseEvent mouseEvent) {
        loadScene(Constants.GROUPS);
    }


}
