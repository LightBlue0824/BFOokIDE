package ui;

import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class SaveUIController {
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//��ȡRemoteHelper

	@FXML
	private TextField fileName;
	
	@FXML
	private void ok(){
		try {
			boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), fileName.getText()+MainUI.getCodeType());
			
			if(success){
				MainUIController.closeTempStage();
				MainUI.setStateLabelText("State: Save succeed");
				
				//���õ�ǰ�ļ����û���
				MainUI.setFilename(fileName.getText()+MainUI.getCodeType());
				//���õ�ǰ��ǩ���ļ���
				MainUI.getTabPane().getSelectionModel().getSelectedItem().setText(fileName.getText()+MainUI.getCodeType());
				
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
}
