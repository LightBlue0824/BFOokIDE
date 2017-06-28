package ui;
import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class SaveUI2Controller {			//��NoticeSaveUI�е�����saveUI��Controller
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//��ȡRemoteHelper

	@FXML
	private TextField fileName;
	
	@FXML
	private void ok(){
		try {
			boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), fileName.getText()+MainUI.getCodeType());
			
			if(success){
				NoticeSaveUIController.closeTempStage();
				
				MainUI.setStateLabelText("State: Save succeed");
				
				//�رգ��Ƴ�����ǰ��tab
				MainUI.getTabPane().getTabs().remove(MainUI.getCurrentTab());
			}
			else{
				NoticeSaveUIController.closeTempStage();
				MainUI.setStateLabelText("State: Save failed");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
