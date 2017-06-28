package ui;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class OpenUIController {
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();
	
	@FXML
	private ComboBox<String> comboBox;
	
	@FXML
	private void loadFileList(){
		try {
			String[] fileList = remoteHelper.getIOService().readFileList(MainUI.getUsername());
			
			comboBox.setItems(FXCollections.observableArrayList(fileList));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void ok(){
		//设为可编辑
		MainUI.getConsole().setEditable(true);
		
		try {
			//添加一个新的Tab
			Tab tab = FXMLLoader.load(MainUI.class.getResource("TabUI.fxml"));
			tab.setText(comboBox.getValue());
			MainUI.getTabPane().getTabs().add(tab);
			//设置选中新加的Tab
			MainUI.getTabPane().getSelectionModel().select(tab);
			
			//显示文件的内容
			String code = remoteHelper.getIOService().readFile(MainUI.getUsername(), comboBox.getValue());
			MainUI.getCodeArea().setText(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MainUI.setCodeType(comboBox.getValue().substring(comboBox.getValue().lastIndexOf(".")));
		
		MainUIController.closeTempStage();
		
		MainUI.setStateLabelText("State: Open succeed");
	}
}
