package ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import rmi.RemoteHelper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class MainUIController {
	
	private static Stage tempStage = new Stage();			//用于临时弹窗的窗口
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//获取RemoteHelper

	@FXML
	private TextArea console;
	@FXML
	private TextArea result;
	@FXML
	private Label stateLabel;				//状态栏
	
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem saveAs;
	
	@FXML
	private MenuItem undo;
	@FXML
	private MenuItem redo;
	@FXML
	private MenuItem delete;
	
	@FXML
	private MenuItem memorySizeItem;
	@FXML
	private CheckMenuItem isInfinite;
	
	@FXML
	private MenuItem execute;
	@FXML
	private TabPane tabPane;
	
	//关闭临时弹窗
	public static void closeTempStage(){
		if(tempStage != null){
			tempStage.close();
		}
	}
	
	@FXML
	private void initialize(){				//初始化，传递一些控件，记录在MainUI中，
		MainUI.setTabPane(tabPane);
		MainUI.setStateLabel(stateLabel);		//将stateLabel传给MainUI
		MainUI.setConsole(console);
		
		tempStage.setResizable(false);
	}
	
	@FXML
	private void repareFileMenu(){		//设置File菜单中项目Disable属性
		
		if(tabPane.getTabs().size() == 0){
			save.setDisable(true);
			saveAs.setDisable(true);
		}
		else{
			if(!checkSame()){				//文件内容发生过更改才可点击save
				save.setDisable(false);
			}
			else{
				save.setDisable(true);
			}
			
			saveAs.setDisable(false);
		}
	}
	
	//检查当前文件的内容是否有更改
	public static boolean checkSame(){
		if(MainUI.getFilename() == null){
			return false;
		}
		try {
			String oldCode = RemoteHelper.getInstance().getIOService().readFile(MainUI.getUsername(), MainUI.getFilename());
			
			if(oldCode.equals(MainUI.getCodeArea().getText())){
				return true;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	@FXML
	private void newBF(){
		
		//设为可编辑
		console.setEditable(true);
		
		save.setDisable(false);			//设为可点击
		saveAs.setDisable(false);
		execute.setDisable(false);
		
		try {
			//添加一个新的Tab
			Tab tab = FXMLLoader.load(MainUI.class.getResource("TabUI.fxml"));
			tab.setText("new.bf");
			tabPane.getTabs().add(tab);
			//设置选中新加的Tab
			tabPane.getSelectionModel().select(tab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainUI.setCodeType(".bf");
	}
	
	@FXML
	private void newOok(){
		//设为可编辑
		console.setEditable(true);
		
		save.setDisable(false);			//设为可点击
		saveAs.setDisable(false);
		execute.setDisable(false);
		
		try {
			//添加一个新的Tab
			Tab tab = FXMLLoader.load(MainUI.class.getResource("TabUI.fxml"));
			tab.setText("new.ook");
			tabPane.getTabs().add(tab);
			//设置选中新加的Tab
			tabPane.getSelectionModel().select(tab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainUI.setCodeType(".ook");
	}

	@FXML
	private void openFile(){
		tempStage.setTitle("Open");
		
		try {
			//显示保存窗口
			GridPane openPane = FXMLLoader.load(MainUI.class.getResource("OpenUI.fxml"));
			Scene openScene = new Scene(openPane, 300, 150);
			
			tempStage.setScene(openScene);
			tempStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void saveFile(){
		if(MainUI.getFilename() != null){
			
			try {
				boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), MainUI.getFilename());
				
				if(success){
					MainUI.setStateLabelText("State: Save succeed");
					
					//刷新版本列表
					MainUI.refreshVersionList();
				}
				else{
					MainUI.setStateLabelText("State: Save failed");
				}
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			tempStage.setTitle("Save");
			
			try {
				//显示保存窗口
				GridPane savePane = FXMLLoader.load(MainUI.class.getResource("SaveUI.fxml"));
				Scene saveScene = new Scene(savePane, 300, 150);
				
				tempStage.setScene(saveScene);
				tempStage.show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void saveFileAs(){
		tempStage.setTitle("Save As");
		
		try {
			GridPane savePane = FXMLLoader.load(MainUI.class.getResource("SaveUI.fxml"));
			Scene saveScene = new Scene(savePane, 300, 150);
			
			tempStage.setScene(saveScene);
			tempStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void exit(){
		System.exit(0);
	}

	@FXML
	private void repareEditMenu(){
		if(tabPane.getTabs().size() == 0){
			delete.setDisable(true);
			undo.setDisable(true);
			redo.setDisable(true);
		}
		else{
			if(MainUI.getFilename() != null){			//当前显示的文件是已存在的文件时才可删除
				delete.setDisable(false);
			}
			
			if(MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getUndoList().size() == 0){
				undo.setDisable(true);
				undo.setText("Undo");
			}
			else{
				undo.setDisable(false);
				
				String tempStr = "";
				if(MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getTheLastOfUndoList().isReplacement()){
					tempStr = " Replace";
				}
				else{
					UndoRedoManager.Type type = MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getTheLastOfUndoList().getType();
					if(type == UndoRedoManager.Type.TYPING){
						tempStr = " Typing";
					}
					if(type == UndoRedoManager.Type.DELETE){
						tempStr = " Delete";
					}
				}
				undo.setText("Undo"+tempStr);
			}
			
			if(MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getRedoList().size() == 0){
				redo.setDisable(true);
				redo.setText("Redo");
			}
			else{
				redo.setDisable(false);
				
				String tempStr = "";
				if(MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getTheLastOfRedoList().isReplacement()){
					tempStr = " Replace";
				}
				else{
					UndoRedoManager.Type type = MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).getTheLastOfRedoList().getType();
					if(type == UndoRedoManager.Type.TYPING){
						tempStr = " Typing";
					}
					if(type == UndoRedoManager.Type.DELETE){
						tempStr = " Delete";
					}
				}
				redo.setText("Redo"+tempStr);
			}
		}
	}
	
	@FXML
	private void undo(){
		if(undo.getText().equals("Undo Replace")){			//如果是替换，执行两步undo，因为替换分成了两步装进undoList里
			MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).undo();
		}
		MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).undo();
	}

	@FXML
	private void redo(){
		if(redo.getText().equals("Redo Replace")){			//如果是替换，执行两步redo，因为替换分成了两步装进redoList里
			MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).redo();
		}
		MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).redo();
	}

	@FXML
	private void delete(){
		try {
			boolean success = remoteHelper.getIOService().deleteFile(MainUI.getUsername(), MainUI.getFilename()+MainUI.getCodeType());
			
			if(success){
				tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
				MainUI.setStateLabelText("State: Delete succeed");
			}
			else{
				MainUI.setStateLabelText("State: Delete failed");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void loadSetMenu(){				//加载设置菜单
		try {
			if(remoteHelper.getExecuteService().isInfinite()){
				memorySizeItem.setDisable(true);
				isInfinite.setSelected(true);
			}
			else{
				memorySizeItem.setDisable(false);
				isInfinite.setSelected(false);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void setMemorySize(){
		tempStage.setTitle("Set Memory Size");
		
		try {
			GridPane setPane = FXMLLoader.load(this.getClass().getResource("SetMemorySizeUI.fxml"));
			
			Scene setScene = new Scene(setPane, 300, 150);
			
			tempStage.setScene(setScene);
			tempStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void setInfiniteMemory(){
		try {
			remoteHelper.getExecuteService().setInfinite(isInfinite.isSelected());
			
			MainUI.setStateLabelText("State: Set succeed. isInfinite: "+remoteHelper.getExecuteService().isInfinite());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//用来检查是否可以点击execute
	@FXML
	private void checkRunnable(){
		if(tabPane.getTabs().size() == 0){
			execute.setDisable(true);
		}
		else{
			execute.setDisable(false);
		}
	}
	
	@FXML
	private void execute(){
		try {
			if(MainUI.getCodeType().equals(".bf")){
				result.setText(remoteHelper.getExecuteService().executeBF(MainUI.getCodeArea().getText(), console.getText()));
			}
			else if(MainUI.getCodeType().equals(".ook")){
				result.setText(remoteHelper.getExecuteService().executeOok(MainUI.getCodeArea().getText(), console.getText()));
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void changeUser(){
		try {
			boolean success = remoteHelper.getUserService().logout(MainUI.getUsername());
			
			if(success){
				//退出显示登陆界面
				MainUI.setUsername(null);
				MainUI.signIn();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void deleteUser(){
		try {
			boolean success = remoteHelper.getUserService().delete(MainUI.getUsername());
			
			if(success){
				//退出显示登陆界面
				MainUI.setUsername(null);
				MainUI.signIn();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void about(){
		
	}

	@FXML
	private void checkConsoleEditable(){		//当鼠标移入控制台时检查并设置控制台是否可以编辑
		if(tabPane.getTabs().size() == 0){
			console.setEditable(false);
		}
		else{
			console.setEditable(true);
		}
	}
	
	@FXML
	private void clearConsole(){
		console.setText("");
	}
	
	@FXML
	private void clearResult(){
		result.setText("");
	}
}
