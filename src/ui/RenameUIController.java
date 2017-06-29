package ui;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import rmi.RemoteHelper;

public class RenameUIController {
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();
	
	@FXML
	private TextField newFileName;
	
	@FXML
	private void ok(){
		try {
			boolean success = remoteHelper.getIOService().renameFile(MainUI.getUsername(), MainUI.getFilename(), newFileName.getText()+MainUI.getCodeType());
			
			if(success){
				MainUI.setStateLabelText("State: Rename succeed");
				MainUIController.closeTempStage();
			}
			else{
				MainUI.setStateLabelText("State: Rename failed");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
