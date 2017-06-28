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
		//�ȱ����ļ�
		if(MainUI.getFilename() == null){			//������½���δ��������ļ�
			TabUIController.closeTempStage();		//�ر���ʾ����ĵ���

			tempStage.setTitle("Save");
			
			try {
				//��ʾ���洰��
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
					
					//�ر�
					//�رգ��Ƴ�����ǰ��tab
					MainUI.getTabPane().getTabs().remove(MainUI.getCurrentTab());
					//�ر���ʱ����
					TabUIController.closeTempStage();
				}
				else{
					MainUI.setStateLabelText("State: Save failed");
					
					//�ر���ʱ����
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
		//�رգ��Ƴ�����ǰ��tab
		MainUI.getTabPane().getTabs().remove(MainUI.getCurrentTab());
		//�ر���ʱ����
		TabUIController.closeTempStage();
	}
	
	@FXML
	private void cancel(){
		//�ر���ʱ����
		TabUIController.closeTempStage();
	}
}
