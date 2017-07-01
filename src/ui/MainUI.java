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
	
	private static String username = null;			//记录当前登陆的用户
	private static String codeType = null;			//记录当前的代码类型,BF的为".bf",Ook的为".ook"
	private static String filename = null;			//当前文件的名字,包含后缀
	
	private static TabPane tabPane = null;
	
	private static TextArea codeArea = null;			//用来记录当前的tab页面
	private static Tab currentTab = null;
	private static ListView<String> versionList = null;
	
	private static Label stateLabel = null;
	private static TextArea console = null;
	
	private static ArrayList<UndoRedoManager> managerList = new ArrayList<UndoRedoManager>();			//用来保存之前的

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
		
		setStageTitle("Welcome");				//更改标题为登陆
		
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
	
	//设置标题
	public static void setStageTitle(String title){
		stage.setTitle(title);
	}
	
	//得到当前用户
	public static String getUsername(){
		return username;
	}
	
	//设置当前用户
	public static void setUsername(String name){
		username = name;
	}
	
	//得到当前代码类型
	public static String getCodeType(){
		return codeType;
	}
	
	//设置当前代码类型
	public static void setCodeType(String type){
		codeType = type;
	}
	
	//设置状态栏
	public static void setStateLabel(Label label){
		stateLabel = label;
	}
	
	//设置状态栏文字
	public static void setStateLabelText(String text){
		if(stateLabel != null){
			Platform.runLater(()->{
				stateLabel.setText(text);
			});
			
			//延时一段时间后更改为显示Linked
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
	
	//刷新当前文件的versionList
	public static void refreshVersionList(){
		if(MainUI.getFilename() != null){
			try {
				String[] version = RemoteHelper.getInstance().getIOService().readFileList(MainUI.getUsername()+"/"+MainUI.getFilename()+"/history");
				
				ObservableList<String> list = FXCollections.observableArrayList(version);
				FXCollections.reverse(list);			//倒序，最后保存的版本在最上
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
