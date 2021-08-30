package guiControls;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public class NewProjectDialog {
	public String name;
	public String path;
	public void show(){
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("New project");
		dialog.setHeaderText("Create a new Chime project");
		GridPane pane = new GridPane();
		pane.setVgap(10);
		pane.setHgap(10);
		pane.add(new Label("Title:"), 0, 0);
		TextField nameInput = new TextField();
		pane.add(nameInput, 1, 0, 2, 1);
		pane.add(new Label("Path:"), 0, 1);
		TextField pathInput = new TextField();
		pane.add(pathInput, 1, 1);
		DirectoryChooser pathChooser = new DirectoryChooser();
		pathChooser.setTitle("Choose a clean directory for the new project.");
		Button chooseInput = new Button("Browse");
		chooseInput.setOnAction(e -> pathInput.setText(pathChooser.showDialog(null).getAbsolutePath()));
		pane.add(chooseInput, 2, 1);
		dialog.getDialogPane().setContent(pane);
		ButtonType createBtn = new ButtonType("Create", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(createBtn);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Button affirmative = (Button)dialog.getDialogPane().lookupButton(createBtn);
		affirmative.setDisable(true);
		ChangeListener<String> blockCreation = (observable, oldV, newV) -> {
			affirmative.setDisable(nameInput.getText().isBlank() | pathInput.getText().isBlank());
		}; nameInput.textProperty().addListener(blockCreation);
		pathInput.textProperty().addListener(blockCreation);
		dialog.showAndWait().ifPresent(result -> {
			if(result == createBtn){
				this.name = nameInput.getText();
				this.path = pathInput.getText();
			}
		});
	}
}
