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
	
	private static Stage tempStage = new Stage();			//������ʱ�����Ĵ���
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//��ȡRemoteHelper

	@FXML
	private TextArea console;
	@FXML
	private TextArea result;
	@FXML
	private Label stateLabel;				//״̬��
	
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
	
	//�ر���ʱ����
	public static void closeTempStage(){
		if(tempStage != null){
			tempStage.close();
		}
	}
	
	@FXML
	private void initialize(){				//��ʼ��������һЩ�ؼ�����¼��MainUI�У�
		MainUI.setTabPane(tabPane);
		MainUI.setStateLabel(stateLabel);		//��stateLabel����MainUI
		MainUI.setConsole(console);
		
		tempStage.setResizable(false);
	}
	
	@FXML
	private void repareFileMenu(){		//����File�˵�����ĿDisable����
		
		if(tabPane.getTabs().size() == 0){
			save.setDisable(true);
			saveAs.setDisable(true);
		}
		else{
			if(!checkSame()){				//�ļ����ݷ��������Ĳſɵ��save
				save.setDisable(false);
			}
			else{
				save.setDisable(true);
			}
			
			saveAs.setDisable(false);
		}
	}
	
	//��鵱ǰ�ļ��������Ƿ��и���
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
		
		//��Ϊ�ɱ༭
		console.setEditable(true);
		
		save.setDisable(false);			//��Ϊ�ɵ��
		saveAs.setDisable(false);
		execute.setDisable(false);
		
		try {
			//���һ���µ�Tab
			Tab tab = FXMLLoader.load(MainUI.class.getResource("TabUI.fxml"));
			tab.setText("new.bf");
			tabPane.getTabs().add(tab);
			//����ѡ���¼ӵ�Tab
			tabPane.getSelectionModel().select(tab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainUI.setCodeType(".bf");
	}
	
	@FXML
	private void newOok(){
		//��Ϊ�ɱ༭
		console.setEditable(true);
		
		save.setDisable(false);			//��Ϊ�ɵ��
		saveAs.setDisable(false);
		execute.setDisable(false);
		
		try {
			//���һ���µ�Tab
			Tab tab = FXMLLoader.load(MainUI.class.getResource("TabUI.fxml"));
			tab.setText("new.ook");
			tabPane.getTabs().add(tab);
			//����ѡ���¼ӵ�Tab
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
			//��ʾ���洰��
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
					
					//ˢ�°汾�б�
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
				//��ʾ���洰��
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
			if(MainUI.getFilename() != null){			//��ǰ��ʾ���ļ����Ѵ��ڵ��ļ�ʱ�ſ�ɾ��
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
		if(undo.getText().equals("Undo Replace")){			//������滻��ִ������undo����Ϊ�滻�ֳ�������װ��undoList��
			MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).undo();
		}
		MainUI.getUndoRedoManager(tabPane.getSelectionModel().getSelectedIndex()).undo();
	}

	@FXML
	private void redo(){
		if(redo.getText().equals("Redo Replace")){			//������滻��ִ������redo����Ϊ�滻�ֳ�������װ��redoList��
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
	private void loadSetMenu(){				//�������ò˵�
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
	
	//��������Ƿ���Ե��execute
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
				//�˳���ʾ��½����
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
				//�˳���ʾ��½����
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
	private void checkConsoleEditable(){		//������������̨ʱ��鲢���ÿ���̨�Ƿ���Ա༭
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
