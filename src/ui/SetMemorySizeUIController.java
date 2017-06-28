package ui;

import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import rmi.RemoteHelper;

public class SetMemorySizeUIController {
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();
	
	@FXML
	private TextField size;
	
	@FXML
	private void initialize(){
		try {
			size.setText(""+remoteHelper.getExecuteService().getMemorySize());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void ok(){
		try {
			remoteHelper.getExecuteService().setMemorySize(Integer.parseInt(size.getText()));
			
			MainUIController.closeTempStage();
			MainUI.setStateLabelText("State: Set memory size succeed. Memory size: "+remoteHelper.getExecuteService().getMemorySize());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
