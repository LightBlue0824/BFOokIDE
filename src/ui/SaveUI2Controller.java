package ui;
import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class SaveUI2Controller {			//在NoticeSaveUI中弹出的saveUI的Controller
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//获取RemoteHelper

	@FXML
	private TextField fileName;
	
	@FXML
	private void ok(){
		try {
			boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), fileName.getText()+MainUI.getCodeType());
			
			if(success){
				NoticeSaveUIController.closeTempStage();
				
				MainUI.setStateLabelText("State: Save succeed");
				
				//关闭（移除）当前的tab
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
