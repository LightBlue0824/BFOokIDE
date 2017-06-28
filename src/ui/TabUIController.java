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
	
	private static Stage tempStage = new Stage();				//��ʱ����
	
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
		if(tab != null){				//��ֹ�رյ�ʱ���ٴθı�
			//System.out.println("ga");
			
			MainUI.setCurrentTab(tab);
			//��¼��ǰ���ļ���
			if(tab.getText().equals("new.bf") || tab.getText().equals("new.ook")){
				MainUI.setFilename(null);
			}
			else{
				MainUI.setFilename(tab.getText());
			}
			//��¼��ǰ���ı���
			MainUI.setCodeArea(codeArea);
			//��¼��ǰ�Ĵ�������
			MainUI.setCodeType(tab.getText().substring(tab.getText().lastIndexOf(".")));
			
			//��ȡ��ʷ�汾
			loadVersionList();
			
			//��¼��ǰ�İ汾�б�
			MainUI.setVersionList(versionList);
			
		}
	}
	
	/*������ķ���
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
	
	//���ذ汾�б�
	private void loadVersionList(){
		if(MainUI.getFilename() != null){
			try {
				String[] version = remoteHelper.getIOService().readFileList(MainUI.getUsername()+"/"+tab.getText()+"/history");
				
				ObservableList<String> list = FXCollections.observableArrayList(version);
				FXCollections.reverse(list);			//������󱣴�İ汾������
				versionList.setItems(list);
				
				//���û��ѡ����汾��Ĭ��ѡ��Ϊ�׸�
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
		if(MainUI.getFilename() == null){			//�½����ļ���δ����ʱû�а汾
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
		
		if(!isSaved && !codeArea.getText().equals("")){			//����Ķ���δ���棬��ʾ����
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
