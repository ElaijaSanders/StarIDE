package staridefx;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.undo.UndoManager;

import data.Project;
import guiControls.NewFileDialog;
import guiControls.NewProjectDialog;
import guiControls.OpenProjectDialog;
import guiControls.SFXScrollPane;
import guiControls.SaveFileAsDialog;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import mjson.Json;

public class Controller {
	@FXML TabPane openedFiles;
	@FXML TextArea console;
	@FXML TreeView<String> explorer;
	@FXML TreeView<String> nodes;
	Project currProject = null;
	HashMap<File, String> buffer = new HashMap<>();
	@FXML private void onMenuNew(){
		NewProjectDialog npd = new NewProjectDialog(); npd.show();
		if(npd.name == null | npd.path == null) return;
		this.currProject = new Project(npd.name, new File(npd.path));
		File dir = this.currProject.folder;
		if(dir.listFiles().length != 0){
			Alert dlg = new Alert(AlertType.CONFIRMATION);
			dlg.setTitle("Proceed?");
			dlg.setContentText("The selected directory is not empty and will be cleared. Are you sure?");
			dlg.showAndWait().ifPresent(selected -> { if(selected == ButtonType.OK) this.intlDeleteFolder(dir); });
		} if(!dir.exists()) dir.mkdirs();
		this.revalidateExplorer();
	}
	@FXML private void onMenuOpen() throws IOException{
		if(this.currProject != null){
			Alert dlg = new Alert(AlertType.CONFIRMATION);
			dlg.setTitle("Proceed?");
			dlg.setContentText("There is a project already opened; any unsaved changes will be removed. Are you sure?");
			ButtonType saveBtn = new ButtonType("Save, then open", ButtonData.APPLY);
			dlg.getButtonTypes().add(saveBtn);
			dlg.showAndWait().ifPresent(selected -> {
				if(selected == saveBtn){ try{ this.onMenuSave(); } catch (IOException e){ e.printStackTrace(); } }
				else if(selected == ButtonType.CANCEL) return;
				this.openedFiles.getTabs().clear();
				this.buffer.clear();
				this.currProject = null;
				this.explorer.setRoot(null);
			});
		} OpenProjectDialog opd = new OpenProjectDialog();
		opd.show(); if(opd.path == null) return;
		File config = new File(opd.path, ".chm-project");
		if(!config.exists()){
			new Alert(AlertType.ERROR, "The selected folder does not contain a Chime project.", ButtonType.CLOSE).show();
			return;
		} Json data = Json.read(Files.readString(config.toPath()));
		this.currProject = new Project(data.at("title").asString(), new File(data.at("rootDirectory").asString()));
		this.revalidateExplorer();
	}
	@FXML private void onMenuSave() throws IOException{
		if(this.currProject == null) return;
		File config = new File(this.currProject.folder, ".chm-project");
		if(!config.exists()) config.createNewFile();
		Json data = Json.object().set("title", this.currProject.name)
			.set("rootDirectory", this.currProject.folder.getAbsolutePath());
		Files.writeString(config.toPath(), data.toString(),
			StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		for(Tab tab : this.openedFiles.getTabs()){
			SFXScrollPane embedded = (SFXScrollPane)((SwingNode)tab.getContent()).getContent();
			JTextPane editor = (JTextPane)embedded.getViewport().getView();
			Files.writeString(((File)embedded.getClientProperty("path")).toPath(), editor.getText(), StandardCharsets.UTF_8);
		} for(Entry<File, String> entry : this.buffer.entrySet()){
			Files.writeString(entry.getKey().toPath(), entry.getValue(), StandardCharsets.UTF_8);
		} this.buffer.clear();
		new Alert(AlertType.INFORMATION, "The project has been successfully saved.", ButtonType.OK).show();
		this.revalidateExplorer();
	}
	@FXML private void onMenuSaveFileAs() throws IOException{
		if(this.currProject == null || this.openedFiles.getTabs().isEmpty()) return;
		SaveFileAsDialog sfad = new SaveFileAsDialog();
		sfad.show();
		if(sfad.path == null) return;
		SFXScrollPane embedded = (SFXScrollPane)((SwingNode)this.openedFiles.getSelectionModel().getSelectedItem()
			.getContent()).getContent();
		Files.writeString(sfad.path.toPath(), ((JTextPane)embedded.getViewport().getView()).getText(), StandardCharsets.UTF_8);
		new Alert(AlertType.INFORMATION, "The file has been successfully exported.", ButtonType.OK).show();
		this.revalidateExplorer();
	}
	@FXML private void onMenuClose(){
		if(this.currProject == null) return;
		Alert dlg = new Alert(AlertType.CONFIRMATION);
		dlg.setTitle("Proceed?");
		dlg.setContentText("Any unsaved changes will be removed. Are you sure?");
		dlg.getButtonTypes().clear();
		dlg.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		dlg.showAndWait().ifPresent(selected -> {
			if(selected == ButtonType.YES){
				this.openedFiles.getTabs().clear();
				this.buffer.clear();
				this.currProject = null;
				this.explorer.setRoot(null);
			}
		});
	}
	@FXML private void onMenuQuit(){
		if(this.currProject == null) System.exit(0);
		Alert dlg = new Alert(AlertType.CONFIRMATION);
		dlg.setTitle("Proceed?");
		dlg.setContentText("Any unsaved changes will be removed. Are you sure?");
		dlg.getButtonTypes().clear();
		dlg.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		dlg.showAndWait().ifPresent(selected -> {
			if(selected == ButtonType.YES) System.exit(0);
		});
	}
	@FXML private void onMenuUndo(){
		UndoManager um = (UndoManager)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getClientProperty("undoer");
		SwingUtilities.invokeLater(() -> { if(um.canUndo()) um.undo(); });
	}
	@FXML private void onMenuRedo(){
		UndoManager um = (UndoManager)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getClientProperty("undoer");
		SwingUtilities.invokeLater(() -> { if(um.canRedo()) um.redo(); });
	}
	@FXML private void onMenuCut(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		if(tp.getSelectedText() != null){ int c = tp.getSelectionStart(); tp.cut(); tp.select(c, c); }
	}
	@FXML private void onMenuCopy(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		if(tp.getSelectedText() != null) tp.copy();
	}
	@FXML private void onMenuPaste(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		tp.paste();
	}
	@FXML private void onMenuDelete(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		if(tp.getSelectedText() != null){ int c = tp.getSelectionStart(); tp.replaceSelection(""); tp.select(c, c); }
	}
	@FXML private void onMenuSelectAll(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		tp.selectAll();
	}
	@FXML private void onMenuUnselectAll(){
		JTextPane tp = (JTextPane)this.intlGetPane(this.openedFiles.getSelectionModel().getSelectedItem()).getViewport().getView();
		tp.select(0, 0);
	}
	@FXML private void onMenuAbout(){} //TODO
	@FXML private void onNewFile() throws IOException{
		if(this.currProject == null) return;
		NewFileDialog nfd = new NewFileDialog();
		nfd.show(this.currProject.folder);
		if(nfd.name == null | nfd.path == null) return;
		if(nfd.path.exists()) return;
		nfd.path.getParentFile().mkdirs();
		nfd.path.createNewFile();
		this.revalidateExplorer();
		this.intlNewEditor(nfd.path);
	}
	@FXML private void onExplorerClick(MouseEvent e){
		if(e.getClickCount()==2){
			if(this.explorer.getSelectionModel().isEmpty()) return;
			String file = "";
			for(TreeItem<String> selected = this.explorer.getSelectionModel().getSelectedItem();
				selected.getParent() != null; selected = selected.getParent()) file = File.separator + selected.getValue() + file;
			File f = new File(this.currProject.folder.getAbsolutePath() + file);
			if(f.isFile()) this.intlNewEditor(f);
			else if(f.isDirectory() && !this.explorer.getSelectionModel().getSelectedItem().getChildren().isEmpty())
				this.explorer.getSelectionModel().getSelectedItem().setExpanded(!this.explorer.getSelectionModel()
					.getSelectedItem().isExpanded());
		} else if(e.getButton() == MouseButton.SECONDARY) this.explorer.getSelectionModel().clearSelection();
	}
	private void revalidateExplorer(){
		if(this.currProject == null){ this.explorer.setRoot(null); return; }
		this.explorer.setRoot(this.intlBuildTree(this.currProject.folder));
	}
	@FXML private void onNodeClick(){}
	private TreeItem<String> intlBuildTree(File dir){
		TreeItem<String> root = new TreeItem<>(dir.getName());
		for(File f : dir.listFiles()){
			if(f.isDirectory()) root.getChildren().add(this.intlBuildTree(f));
			else root.getChildren().add(new TreeItem<String>(f.getName()));
		} return root;
	} private void intlDeleteFolder(File dir){
		for(File f : dir.listFiles()) if(f.isDirectory()) this.intlDeleteFolder(f); else f.delete();
		if(dir.listFiles().length == 0) dir.delete();
	} private void intlNewEditor(File file) {
		SwingNode embeddable = new SwingNode();
		embeddable.setOnMouseReleased(e -> embeddable.requestFocus());
		JTextPane editor = new JTextPane();
		SFXScrollPane scrollPane = new SFXScrollPane(editor);
		SwingUtilities.invokeLater(() -> {
			scrollPane.putClientProperty("path", file);
			scrollPane.putClientProperty("undoer", new UndoManager());
			try { if(file != null){
				if(this.buffer.containsKey(file)){ editor.setText(this.buffer.get(file)); this.buffer.remove(file); }
				else editor.setText(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
			}} catch (IOException e){ e.printStackTrace(); }
			editor.getDocument().addUndoableEditListener(e -> ((UndoManager)scrollPane.getClientProperty("undoer")).addEdit(e.getEdit()));
			embeddable.setContent(scrollPane);
		}); Tab tab = new Tab(file.getName(), embeddable);
		tab.setOnCloseRequest(e -> this.buffer.put((File)scrollPane.getClientProperty("path"), editor.getText()));
		this.openedFiles.getTabs().add(tab);
	} private SFXScrollPane intlGetPane(Tab tab){
		return (SFXScrollPane)(((SwingNode)tab.getContent()).getContent());
	}
}
