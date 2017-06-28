package ui;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import rmi.RemoteHelper;

public class NoticeSaveUIController {
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();
	
	private static Stage tempStage = new Stage();
	
	public static void closeTempStage(){
		tempStage.close();
	}
	
	@FXML
	private void yes(){
		//先保存文件
		if(MainUI.getFilename() == null){			//如果是新建的未保存过的文件
			TabUIController.closeTempStage();		//关闭提示保存的弹窗

			tempStage.setTitle("Save");
			
			try {
				//显示保存窗口
				GridPane savePane = FXMLLoader.load(MainUI.class.getResource("SaveUI2.fxml"));
				Scene saveScene = new Scene(savePane, 300, 150);
				
				tempStage.setScene(saveScene);
				tempStage.show();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try {
				boolean success = remoteHelper.getIOService().writeFile(MainUI.getCodeArea().getText(), MainUI.getUsername(), MainUI.getFilename());
				
				if(success){
					MainUI.setStateLabelText("State: Save succeed");
					
					//关闭
					//关闭（移除）当前的tab
					MainUI.getTabPane().getTabs().remove(MainUI.getCurrentTab());
					//关闭临时弹窗
					TabUIController.closeTempStage();
				}
				else{
					MainUI.setStateLabelText("State: Save failed");
					
					//关闭临时弹窗
					TabUIController.closeTempStage();
				}
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void no(){
		//关闭（移除）当前的tab
		MainUI.getTabPane().getTabs().remove(MainUI.getCurrentTab());
		//关闭临时弹窗
		TabUIController.closeTempStage();
	}
	
	@FXML
	private void cancel(){
		//关闭临时弹窗
		TabUIController.closeTempStage();
	}
}
