package groups;

import common.Constants;
import common.MainWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import modelo.Grupo;

public class DetailGroup extends MainWindowController {

    @FXML
    private TextField code;
    @FXML
    private TextArea description;
    private Grupo group;

    public void cancel(MouseEvent mouseEvent) {
        loadScene(Constants.GROUPS);
    }

    public void initGroup(Grupo group) {
        this.group = group;
        code.setText(group.getCodigo());
        description.setText(group.getDescripcion());
    }
}
