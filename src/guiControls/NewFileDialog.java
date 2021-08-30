package guiControls;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class NewFileDialog {
	public String name;
	public File path;
	public void show(File file){
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Create new file");
		GridPane pane = new GridPane();
		pane.setVgap(10);
		pane.setHgap(10);
		pane.add(new Label("Name:"), 0, 0);
		TextField nameInput = new TextField();
		nameInput.setPromptText("E.g. subdir/file.chms");
		pane.add(nameInput, 1, 0, 2, 1);
		pane.add(new Label("Path:"), 0, 1);
		TextField pathLabel = new TextField(file.getAbsolutePath() + File.separator);
		pathLabel.positionCaret(pathLabel.getText().length());
		pathLabel.setEditable(false);
		pane.add(pathLabel, 1, 1);
		dialog.getDialogPane().setContent(pane);
		ButtonType createBtn = new ButtonType("Create", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(createBtn);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Button affirmative = (Button)dialog.getDialogPane().lookupButton(createBtn);
		affirmative.setDisable(true);
		nameInput.textProperty().addListener((observer, oldV, newV) -> {
			affirmative.setDisable(nameInput.getText().isBlank());
			pathLabel.setText(file.getAbsolutePath() + File.separator + nameInput.getText());
			pathLabel.positionCaret(pathLabel.getText().length());
		});
		dialog.showAndWait().ifPresent(result -> {
			if(result == createBtn){
				this.name = nameInput.getText();
				this.path = new File(pathLabel.getText());
			}
		});
	}
}
