package ui;

import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class SaveUIController {
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//获取RemoteHelper

	@FXML
	private TextField fileName;
	
	@FXML
	private void ok(){
		try {
			boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), fileName.getText()+MainUI.getCodeType());
			
			if(success){
				MainUIController.closeTempStage();
				MainUI.setStateLabelText("State: Save succeed");
				
				//设置当前文件的用户名
				MainUI.setFilename(fileName.getText()+MainUI.getCodeType());
				//设置当前标签的文件名
				MainUI.getTabPane().getSelectionModel().getSelectedItem().setText(fileName.getText()+MainUI.getCodeType());
				
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
}
