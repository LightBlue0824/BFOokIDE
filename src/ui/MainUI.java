package ui;
	
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rmi.RemoteHelper;
import runner.ClientRunner;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainUI extends Application {
	
	private static Stage stage = null;
	private static Scene scene = null;
	
	private static String username = null;			//��¼��ǰ��½���û�
	private static String codeType = null;			//��¼��ǰ�Ĵ�������,BF��Ϊ".bf",Ook��Ϊ".ook"
	private static String filename = null;			//��ǰ�ļ�������,������׺
	
	private static TabPane tabPane = null;
	
	private static TextArea codeArea = null;			//������¼��ǰ��tabҳ��
	private static Tab currentTab = null;
	private static ListView<String> versionList = null;
	
	private static Label stateLabel = null;
	private static TextArea console = null;
	
	private static ArrayList<UndoRedoManager> managerList = new ArrayList<UndoRedoManager>();			//��������֮ǰ��

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setResizable(false);
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent e) {
				System.exit(0);
			}
		});
		Group group = new Group();
		
		BorderPane root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		group.getChildren().add(root);
		
		scene = new Scene(group, 800, 600);

		signIn();
		
		stage.show();
	}
	
	public static void main(String[] args) {
		ClientRunner cr = new ClientRunner();
		cr.linkToServer();

		launch(args);
	}
	
	public static void signIn(){
		
		setStageTitle("Welcome");				//���ı���Ϊ��½
		
		GridPane loginPane = null;
		try{
			loginPane = FXMLLoader.load(MainUI.class.getResource("Login.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Scene signScene = new Scene(loginPane);
		
		stage.setScene(signScene);
	}
	
	//setMainScene
	public static void setMainScene(){
		stage.setScene(scene);
	}
	
	//���ñ���
	public static void setStageTitle(String title){
		stage.setTitle(title);
	}
	
	//�õ���ǰ�û�
	public static String getUsername(){
		return username;
	}
	
	//���õ�ǰ�û�
	public static void setUsername(String name){
		username = name;
	}
	
	//�õ���ǰ��������
	public static String getCodeType(){
		return codeType;
	}
	
	//���õ�ǰ��������
	public static void setCodeType(String type){
		codeType = type;
	}
	
	//����״̬��
	public static void setStateLabel(Label label){
		stateLabel = label;
	}
	
	//����״̬������
	public static void setStateLabelText(String text){
		if(stateLabel != null){
			Platform.runLater(()->{
				stateLabel.setText(text);
			});
			
			//��ʱһ��ʱ������Ϊ��ʾLinked
			new Thread(()->{
				try {
					Thread.sleep(2500);
					Platform.runLater(()->{
						stateLabel.setText("State: Linked");
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	public static void setFilename(String name){
		filename = name;
	}
	
	public static String getFilename(){
		return filename;
	}
	
	public static void setCodeArea(TextArea area){
		codeArea = area;
	}
	
	public static TextArea getCodeArea(){
		return codeArea;
	}
	
	public static void setCurrentTab(Tab tab){
		currentTab = tab;
	}
	
	public static Tab getCurrentTab(){
		return currentTab;
	}
	
	public static void setTabPane(TabPane pane){
		tabPane = pane;
	}
	
	public static TabPane getTabPane(){
		return tabPane;
	}
	
	public static void setConsole(TextArea area){
		console = area;
	}
	
	public static TextArea getConsole(){
		return console;
	}

	public static void setVersionList(ListView<String> list){
		versionList = list;
	}
	
	public static ListView<String> getVersionList(){
		return versionList;
	}
	
	//ˢ�µ�ǰ�ļ���versionList
	public static void refreshVersionList(){
		if(MainUI.getFilename() != null){
			try {
				String[] version = RemoteHelper.getInstance().getIOService().readFileList(MainUI.getUsername()+"/"+MainUI.getFilename()+"/history");
				
				ObservableList<String> list = FXCollections.observableArrayList(version);
				FXCollections.reverse(list);			//������󱣴�İ汾������
				versionList.setItems(list);
				
				versionList.getSelectionModel().select(0);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void addUndoRedoManager(UndoRedoManager manager){
		managerList.add(manager);
	}
	
	public static UndoRedoManager getUndoRedoManager(int index){
		return managerList.get(index);
	}
}
