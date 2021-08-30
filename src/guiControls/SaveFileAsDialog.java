package guiControls;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class SaveFileAsDialog {
	public File path;
	public void show(){
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Save the opened file at...");
		HBox pane = new HBox(5);
		pane.setAlignment(Pos.CENTER_LEFT);
		pane.getChildren().add(new Label("Path:"));
		TextField pathInput = new TextField();
		pane.getChildren().add(pathInput);
		Button chooseInput = new Button("Browse");
		FileChooser pathChooser = new FileChooser();
		pathChooser.setTitle("Choose a file to write in.");
		chooseInput.setOnAction(e -> pathInput.setText(pathChooser.showSaveDialog(null).getAbsolutePath()));
		pane.getChildren().add(chooseInput);
		dialog.getDialogPane().setContent(pane);
		ButtonType saveBtn = new ButtonType("Save", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(saveBtn);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Button affirmative = (Button)dialog.getDialogPane().lookupButton(saveBtn);
		affirmative.setDisable(true);
		pathInput.textProperty().addListener((observer, oldV, newV) -> {
			affirmative.setDisable(pathInput.getText().isBlank());
		});
		dialog.showAndWait().ifPresent(result -> {
			if(result == saveBtn) this.path = new File(pathInput.getText());
		});
	}
}
