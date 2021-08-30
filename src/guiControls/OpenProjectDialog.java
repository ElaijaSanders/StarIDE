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
import javafx.stage.DirectoryChooser;

public class OpenProjectDialog {
	public File path;
	public void show(){
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Open a Chime project");
		HBox pane = new HBox(5);
		pane.setAlignment(Pos.CENTER_LEFT);
		pane.getChildren().add(new Label("Path:"));
		TextField pathInput = new TextField();
		pane.getChildren().add(pathInput);
		Button chooseInput = new Button("Browse");
		DirectoryChooser pathChooser = new DirectoryChooser();
		pathChooser.setTitle("Choose the path of a project.");
		chooseInput.setOnAction(e -> pathInput.setText(pathChooser.showDialog(null).getAbsolutePath()));
		pane.getChildren().add(chooseInput);
		dialog.getDialogPane().setContent(pane);
		ButtonType openBtn = new ButtonType("Open", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(openBtn);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Button affirmative = (Button)dialog.getDialogPane().lookupButton(openBtn);
		affirmative.setDisable(true);
		pathInput.textProperty().addListener((observer, oldV, newV) -> {
			affirmative.setDisable(pathInput.getText().isBlank());
		});
		dialog.showAndWait().ifPresent(result -> {
			if(result == openBtn) this.path = new File(pathInput.getText());
		});
	}
}
