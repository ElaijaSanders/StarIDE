package staridefx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
		Scene scene = new Scene(root, 900, 600);
		stage.setScene(scene);
		stage.setTitle("Star IDE");
		stage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
