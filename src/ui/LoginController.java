package ui;

import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import rmi.RemoteHelper;

public class LoginController {
	
	private RemoteHelper remoteHelper = RemoteHelper.getInstance();			//获取RemoteHelper
	
	@FXML
	private TextField userName;			//用于输入用户名
	@FXML
	private PasswordField password;		//用于输入密码
	@FXML
	private Label notice;			//底部的公告
	
	@FXML
	private void signIn(){
		try {
			boolean succes = remoteHelper.getUserService().login(userName.getText(), password.getText());
			
			if(succes){
				MainUI.setUsername(userName.getText());
				MainUI.setStageTitle("Client - User: "+MainUI.getUsername());
				MainUI.setMainScene();
			}
			else{
				notice.setText("User name or password is wrong.");
			}
		} catch (RemoteException e) {
			notice.setText("Error！");
		}
	}
	
	@FXML
	private void register(){
		try{
			boolean success = remoteHelper.getUserService().register(userName.getText(), password.getText());
			
			if(!success){
				notice.setText("The user name is already exist.");
			}
			else{
				notice.setText("Register success. Please sign in.");
			}
		} catch(RemoteException e){
			notice.setText("Error!");
		}
	}
}
