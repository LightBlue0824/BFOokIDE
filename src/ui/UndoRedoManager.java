package ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class UndoRedoManager {
	private Undo tempUndo = null;
	
	private Timer timer = null;

	private boolean isOther = false;			//记录是撤销等其他引起的文本变化
	private boolean isReplacing = false;		//记录替换的动作是否执行完了
	
	private TextArea codeArea = null;
	private ArrayList<Undo> undoList = new ArrayList<Undo>();
	private ArrayList<Redo> redoList = new ArrayList<Redo>();
	
	public UndoRedoManager(TextArea area){
		codeArea = area;
		go();
	}
	
	private void go(){
		codeArea.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				//判断是否是因为其他（undo，redo，粘贴等）而引起的内容变化
				if(!isOther){
					//先判断是删除还是键入
					//如果是键入
					if(oldValue.length() == newValue.length()-1){
						if(tempUndo == null){
							if(isReplacing){			//还在处理替换
								tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.TYPING, true);
								isReplacing = false;		//已处理完替换，记录为false
							}
							else{
								tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.TYPING, false);
							}
							addUndo(tempUndo);
						}
						tempUndo.addChange(newValue.charAt(codeArea.getCaretPosition()));
						tempUndo.changeEndPosition(1);
					}
					else if(oldValue.length() == newValue.length()+1){			//是删除
						if(tempUndo == null){
							tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.DELETE, false);
							addUndo(tempUndo);
						}
						//判断是向前删除还是向后删除
						//如果是向前删除
						if(newValue.length() < codeArea.getCaretPosition() || !oldValue.substring(0, codeArea.getCaretPosition()).equals(newValue.substring(0, codeArea.getCaretPosition()))){
							tempUndo.changeStartPosition(-1);
							tempUndo.addChange(oldValue.charAt(codeArea.getCaretPosition()-1));
						}
						else{			//向后删除
							tempUndo.addChange(oldValue.charAt(codeArea.getCaretPosition()));
						}
					}
					else if(oldValue.length() > newValue.length()){				//批量选择删除的
						tempUndo = null;
						if(timer != null){			//将之前的计时取消
							timer.cancel();
						}
						tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), oldValue.substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, false);
						addUndo(tempUndo);
					}
					
					//如果没有超过时间，每次键入都一直刷新时间
					if(timer != null){
						timer.cancel();
					}
					timer = new Timer();
					timer.schedule(new MyTask(), 1000);
				}//if(!isOther)结束
				else{
					isOther = false;			//代表本次Other的事件已结束
				}
			}
		});
		
		codeArea.setOnKeyPressed(new KeyPressed());
	}
	
	private class MyTask extends TimerTask{

		@Override
		public void run() {
			tempUndo = null;
		}
	}
	
	/**
	 * 监控某些特殊的按键，粘贴，方向键，backspace，delete等
	 */
	private class KeyPressed implements EventHandler<KeyEvent>{
		private KeyCode last = null;
		@Override
		public void handle(KeyEvent event) {
			if(event.isControlDown() && event.getCode() == KeyCode.C){		//鼠标左键拖动选中默认映射到ctrl+c；取消该映射
				return;
			}
			if(event.getCode() == KeyCode.CONTROL){		//按下control时不产生动作
				return;
			}
			
			//防止出现既向前删除也向后删除
			if(event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE){
				if(last != event.getCode()){
					tempUndo = null;
				}
			}
			
			if(event.isControlDown() && event.getCode() == KeyCode.V){		//是否按下ctrl+v
				tempUndo = null;
				if(timer != null){			//将之前的计时取消
					timer.cancel();
				}

				isOther = true;
				//粘贴操作特殊处理
				//检查是否选中了文字
				if(codeArea.getSelection().getLength() != 0){
					isOther = true;

					tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), codeArea.getText().substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, true);
					addUndo(tempUndo);
					codeArea.deleteText(codeArea.getSelection());		//先删除选中部分
				}
				
				isOther = true;

				TextArea tempArea = new TextArea();			//暂时的文本框，用来获取要粘贴的文本
				tempArea.paste();
				tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition()+tempArea.getText().length(), tempArea.getText(), Type.TYPING, true);
				addUndo(tempUndo);
				
				//处理结束
				tempUndo = null;
			}
			else if(event.getCode() == KeyCode.UP
					 || event.getCode() == KeyCode.DOWN
					 || event.getCode() == KeyCode.LEFT
					 || event.getCode() == KeyCode.RIGHT){			//按下方向键改变光标位置后，置空tempUndo
				if(timer != null){			//将之前的计时取消
					timer.cancel();
				}
				tempUndo = null;
			}
			else if(codeArea.getSelection().getLength() != 0 && event.getCode() != KeyCode.BACK_SPACE
					&& event.getCode() != KeyCode.DELETE
					&& !(event.isControlDown() && event.getCode() == KeyCode.X)){
				//如果选中了部分文字，且按下的不是Backspace,Delete,ctrl+x,则按替换操作处理
				tempUndo = null;
				if(timer != null){			//将之前的计时取消
					timer.cancel();
				}

				isOther = true;

				tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), codeArea.getText().substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, true);
				addUndo(tempUndo);
				codeArea.deleteText(codeArea.getSelection());		//先删除选中部分
				
				//只是删除了选中部分，剩下的输入部分交由changeListener
				isReplacing = true;		//记录为还在替换
				
				//处理结束
				tempUndo = null;
			}
			
			//记录此次的按键
			last = event.getCode();
		}
	}
	
	class Undo{
		private int startPosition;			//记录起始位置
		private int endPosition;			//记录终止位置
		private String changes;				//记录改变
		private Type type;							//记录是什么类型的改变
		private boolean isReplacement;				//记录是不是因为被替换产生的undo
		
		/**
		 * 构造方法
		 * @param start 起始位置
		 * @param end 终止位置
		 * @param c 改变的部分
		 * @param t 改变的类型
		 * @param value 是否是因为被替换产生的
		 */
		private Undo(int start, int end, String c, Type t, boolean value){
			startPosition = start;
			endPosition = end;
			changes = c;
			type = t;
			isReplacement = value;
		}
		private Undo(Redo haveRedoed){
			startPosition = haveRedoed.getStartPosition();
			endPosition = haveRedoed.getEndPosition();
			changes = haveRedoed.getChanges();
			type = haveRedoed.getType();
			isReplacement = haveRedoed.isReplacement();
		}

		public int getStartPosition(){
			return startPosition;
		}
		/**
		 * 修改startPosition的值，startPosition += value;
		 * @param value
		 */
		public void changeStartPosition(int value){
			startPosition += value;
		}
		public int getEndPosition(){
			return endPosition;
		}
		/**
		 * 修改endPosition的值，endPosition += value;
		 * @param value
		 */
		public void changeEndPosition(int value){
			endPosition += value;
		}
		public Type getType(){
			return type;
		}
		public String getChanges(){
			return changes;
		}
		public void addChange(char change){
			changes = changes + change;
		}
		public boolean isReplacement(){
			return isReplacement;
		}
	}
	
	class Redo{
		private int startPosition;			//记录起始位置
		private int endPosition;			//记录终止位置
		private String changes;				//记录改变
		private Type type;							//记录是什么类型的改变
		private boolean isReplacement;				//记录是不是因为被替换产生的undo

		private Redo(Undo haveUndoed){
			startPosition = haveUndoed.getStartPosition();
			endPosition = haveUndoed.getEndPosition();
			changes = haveUndoed.getChanges();
			type = haveUndoed.getType();
			isReplacement = haveUndoed.isReplacement();
		}
		
		public int getStartPosition(){
			return startPosition;
		}
		public int getEndPosition(){
			return endPosition;
		}
		public Type getType(){
			return type;
		}
		public String getChanges(){
			return changes;
		}
		public boolean isReplacement(){
			return isReplacement;
		}
	}
	
	private void addUndo(Undo toAdd){			//添加Undo到undoList时调用
		//添加undo之后就要把之前的redoList清空
		undoList.add(toAdd);
		redoList.clear();
	}
	
	private void removeUndo(){				//移除undoList中的Undo时调用
		//移除undo时在redoList中添加一个redo
		redoList.add(new Redo(undoList.get(undoList.size()-1)));
		undoList.remove(undoList.size()-1);
	}
	
	private void removeRedo(){				//移除redoList中的Redo时调用
		//移除redo时在undoList中添加一个undo
		undoList.add(new Undo(redoList.get(redoList.size()-1)));
		redoList.remove(redoList.size()-1);
	}
	
	public void undo(){
		tempUndo = null;
		if(timer != null){			//将之前的计时取消
			timer.cancel();
		}

		isOther = true;			//设置为true，因为是undo引起的内容变化
		
		Undo toUndo = undoList.get(undoList.size()-1);
		
		if(toUndo.getType() == Type.TYPING){			//如果要撤销的是键入
			codeArea.selectRange(toUndo.getEndPosition(), toUndo.getStartPosition());
			codeArea.deleteText(codeArea.getSelection());
		}
		else{				//如果要撤销的是删除
			if(toUndo.getStartPosition() == toUndo.getEndPosition()){			//如果是向后删除
				//codeArea.positionCaret(toUndo.getStartPosition());
				String changes = toUndo.getChanges();
				int start = toUndo.getStartPosition();
				codeArea.insertText(start, changes);
				codeArea.selectRange(start, start+changes.length());		//选中恢复的部分
			}
			else{				//如果是向前删除
				String changes = toUndo.getChanges();
				String toAddStr = "";
				int start = toUndo.getStartPosition();
				
				//反序
				for(int i = changes.length()-1;i >= 0;i--){
					toAddStr += changes.charAt(i);
				}
				codeArea.insertText(start, toAddStr);
				codeArea.selectRange(start, start+toAddStr.length());		//选中恢复的部分
			}
		}
		
		//移除最后一个undo，并且添加相应的redo
		removeUndo();
	}
	
	public void redo(){
		isOther = true;			//设置为true，因为是redo引起的内容变化
		
		Redo toRedo = redoList.get(redoList.size()-1);
		
		if(toRedo.getType() == Type.TYPING){			//如果要重做的是键入
			codeArea.insertText(toRedo.getStartPosition(), toRedo.getChanges());
			codeArea.selectRange(toRedo.getStartPosition(), toRedo.getEndPosition());
		}
		else{				//如果要重做的是删除
			String changes = toRedo.getChanges();
			int start = toRedo.getStartPosition();
			codeArea.selectRange(start, start+changes.length());		//选中要删除的部分
			codeArea.deleteText(codeArea.getSelection());
		}
		
		//移除最后一个redo，并且添加相应的undo
		removeRedo();
	}
	
	//枚举了每次修改的类型
	enum Type{			//分为TYPING和DELETE两种
		TYPING,
		DELETE
	}
	
	public ArrayList<Undo> getUndoList(){
		return undoList;
	}
	public ArrayList<Redo> getRedoList(){
		return redoList;
	}
	public Undo getTheLastOfUndoList(){
		return undoList.get(undoList.size()-1);
	}
	public Redo getTheLastOfRedoList(){
		return redoList.get(redoList.size()-1);
	}
}
