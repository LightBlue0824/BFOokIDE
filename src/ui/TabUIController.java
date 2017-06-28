package ui;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import rmi.RemoteHelper;

public class TabUIController {
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();
	
	private static Stage tempStage = new Stage();				//临时弹窗
	
	@FXML
	private Tab tab;
	@FXML
	private TextArea codeArea;
	@FXML
	private ListView<String> versionList;
	
	@FXML
	private void initialize(){
		tempStage.setResizable(false);
		MainUI.addUndoRedoManager(new UndoRedoManager(codeArea));
	}
	
	public static void closeTempStage(){
		tempStage.close();
	}
	
	@FXML
	private void changeCurrentTab(){
		if(tab != null){				//防止关闭的时候再次改变
			//System.out.println("ga");
			
			MainUI.setCurrentTab(tab);
			//记录当前的文件名
			if(tab.getText().equals("new.bf") || tab.getText().equals("new.ook")){
				MainUI.setFilename(null);
			}
			else{
				MainUI.setFilename(tab.getText());
			}
			//记录当前的文本框
			MainUI.setCodeArea(codeArea);
			//记录当前的代码类型
			MainUI.setCodeType(tab.getText().substring(tab.getText().lastIndexOf(".")));
			
			//读取历史版本
			loadVersionList();
			
			//记录当前的版本列表
			MainUI.setVersionList(versionList);
			
		}
	}
	
	/*有问题的方法
	@FXML
	private void closeCurrentTab(){
		System.out.println("ha");
		MainUI.setCurrentTab(null);
		MainUI.setCodeArea(null);
		MainUI.setCodeType(null);
		MainUI.setVersionList(null);
		tab = null;
		codeArea = null;
		//System.out.println(MainUI.getCodeArea() == null);
	}
	*/
	
	//加载版本列表
	private void loadVersionList(){
		if(MainUI.getFilename() != null){
			try {
				String[] version = remoteHelper.getIOService().readFileList(MainUI.getUsername()+"/"+tab.getText()+"/history");
				
				ObservableList<String> list = FXCollections.observableArrayList(version);
				FXCollections.reverse(list);			//倒序，最后保存的版本在最上
				versionList.setItems(list);
				
				//如果没有选择过版本，默认选择为首个
				if(versionList.getSelectionModel().getSelectedIndex() == -1){
					versionList.getSelectionModel().selectFirst();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void chooseVersion(){
		if(MainUI.getFilename() == null){			//新建的文件还未保存时没有版本
			return;
		}
		String selectedVersion = versionList.getSelectionModel().getSelectedItem();
		
		if(selectedVersion != null){
			try {
				codeArea.setText(remoteHelper.getIOService().readHistoryVersion(MainUI.getUsername(), MainUI.getFilename(), selectedVersion));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void checkSaved(Event event){
		boolean isSaved = MainUIController.checkSame();
		
		if(!isSaved && !codeArea.getText().equals("")){			//如果改动还未保存，提示保存
			tempStage.setTitle("Save");
			
			try {
				GridPane noticePane = FXMLLoader.load(this.getClass().getResource("NoticeSaveUI.fxml"));
				
				Scene scene = new Scene(noticePane, 300, 150);
				
				tempStage.setScene(scene);
				tempStage.show();
				
				event.consume();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
