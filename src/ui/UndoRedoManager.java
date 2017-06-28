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

	private boolean isOther = false;			//��¼�ǳ���������������ı��仯
	private boolean isReplacing = false;		//��¼�滻�Ķ����Ƿ�ִ������
	
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
				
				//�ж��Ƿ�����Ϊ������undo��redo��ճ���ȣ�����������ݱ仯
				if(!isOther){
					//���ж���ɾ�����Ǽ���
					//����Ǽ���
					if(oldValue.length() == newValue.length()-1){
						if(tempUndo == null){
							if(isReplacing){			//���ڴ����滻
								tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.TYPING, true);
								isReplacing = false;		//�Ѵ������滻����¼Ϊfalse
							}
							else{
								tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.TYPING, false);
							}
							addUndo(tempUndo);
						}
						tempUndo.addChange(newValue.charAt(codeArea.getCaretPosition()));
						tempUndo.changeEndPosition(1);
					}
					else if(oldValue.length() == newValue.length()+1){			//��ɾ��
						if(tempUndo == null){
							tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition(), "", Type.DELETE, false);
							addUndo(tempUndo);
						}
						//�ж�����ǰɾ���������ɾ��
						//�������ǰɾ��
						if(newValue.length() < codeArea.getCaretPosition() || !oldValue.substring(0, codeArea.getCaretPosition()).equals(newValue.substring(0, codeArea.getCaretPosition()))){
							tempUndo.changeStartPosition(-1);
							tempUndo.addChange(oldValue.charAt(codeArea.getCaretPosition()-1));
						}
						else{			//���ɾ��
							tempUndo.addChange(oldValue.charAt(codeArea.getCaretPosition()));
						}
					}
					else if(oldValue.length() > newValue.length()){				//����ѡ��ɾ����
						tempUndo = null;
						if(timer != null){			//��֮ǰ�ļ�ʱȡ��
							timer.cancel();
						}
						tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), oldValue.substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, false);
						addUndo(tempUndo);
					}
					
					//���û�г���ʱ�䣬ÿ�μ��붼һֱˢ��ʱ��
					if(timer != null){
						timer.cancel();
					}
					timer = new Timer();
					timer.schedule(new MyTask(), 1000);
				}//if(!isOther)����
				else{
					isOther = false;			//������Other���¼��ѽ���
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
	 * ���ĳЩ����İ�����ճ�����������backspace��delete��
	 */
	private class KeyPressed implements EventHandler<KeyEvent>{
		private KeyCode last = null;
		@Override
		public void handle(KeyEvent event) {
			if(event.isControlDown() && event.getCode() == KeyCode.C){		//�������϶�ѡ��Ĭ��ӳ�䵽ctrl+c��ȡ����ӳ��
				return;
			}
			if(event.getCode() == KeyCode.CONTROL){		//����controlʱ����������
				return;
			}
			
			//��ֹ���ּ���ǰɾ��Ҳ���ɾ��
			if(event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE){
				if(last != event.getCode()){
					tempUndo = null;
				}
			}
			
			if(event.isControlDown() && event.getCode() == KeyCode.V){		//�Ƿ���ctrl+v
				tempUndo = null;
				if(timer != null){			//��֮ǰ�ļ�ʱȡ��
					timer.cancel();
				}

				isOther = true;
				//ճ���������⴦��
				//����Ƿ�ѡ��������
				if(codeArea.getSelection().getLength() != 0){
					isOther = true;

					tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), codeArea.getText().substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, true);
					addUndo(tempUndo);
					codeArea.deleteText(codeArea.getSelection());		//��ɾ��ѡ�в���
				}
				
				isOther = true;

				TextArea tempArea = new TextArea();			//��ʱ���ı���������ȡҪճ�����ı�
				tempArea.paste();
				tempUndo = new Undo(codeArea.getCaretPosition(), codeArea.getCaretPosition()+tempArea.getText().length(), tempArea.getText(), Type.TYPING, true);
				addUndo(tempUndo);
				
				//�������
				tempUndo = null;
			}
			else if(event.getCode() == KeyCode.UP
					 || event.getCode() == KeyCode.DOWN
					 || event.getCode() == KeyCode.LEFT
					 || event.getCode() == KeyCode.RIGHT){			//���·�����ı���λ�ú��ÿ�tempUndo
				if(timer != null){			//��֮ǰ�ļ�ʱȡ��
					timer.cancel();
				}
				tempUndo = null;
			}
			else if(codeArea.getSelection().getLength() != 0 && event.getCode() != KeyCode.BACK_SPACE
					&& event.getCode() != KeyCode.DELETE
					&& !(event.isControlDown() && event.getCode() == KeyCode.X)){
				//���ѡ���˲������֣��Ұ��µĲ���Backspace,Delete,ctrl+x,���滻��������
				tempUndo = null;
				if(timer != null){			//��֮ǰ�ļ�ʱȡ��
					timer.cancel();
				}

				isOther = true;

				tempUndo = new Undo(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd(), codeArea.getText().substring(codeArea.getSelection().getStart(), codeArea.getSelection().getEnd()), Type.DELETE, true);
				addUndo(tempUndo);
				codeArea.deleteText(codeArea.getSelection());		//��ɾ��ѡ�в���
				
				//ֻ��ɾ����ѡ�в��֣�ʣ�µ����벿�ֽ���changeListener
				isReplacing = true;		//��¼Ϊ�����滻
				
				//�������
				tempUndo = null;
			}
			
			//��¼�˴εİ���
			last = event.getCode();
		}
	}
	
	class Undo{
		private int startPosition;			//��¼��ʼλ��
		private int endPosition;			//��¼��ֹλ��
		private String changes;				//��¼�ı�
		private Type type;							//��¼��ʲô���͵ĸı�
		private boolean isReplacement;				//��¼�ǲ�����Ϊ���滻������undo
		
		/**
		 * ���췽��
		 * @param start ��ʼλ��
		 * @param end ��ֹλ��
		 * @param c �ı�Ĳ���
		 * @param t �ı������
		 * @param value �Ƿ�����Ϊ���滻������
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
		 * �޸�startPosition��ֵ��startPosition += value;
		 * @param value
		 */
		public void changeStartPosition(int value){
			startPosition += value;
		}
		public int getEndPosition(){
			return endPosition;
		}
		/**
		 * �޸�endPosition��ֵ��endPosition += value;
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
		private int startPosition;			//��¼��ʼλ��
		private int endPosition;			//��¼��ֹλ��
		private String changes;				//��¼�ı�
		private Type type;							//��¼��ʲô���͵ĸı�
		private boolean isReplacement;				//��¼�ǲ�����Ϊ���滻������undo

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
	
	private void addUndo(Undo toAdd){			//���Undo��undoListʱ����
		//���undo֮���Ҫ��֮ǰ��redoList���
		undoList.add(toAdd);
		redoList.clear();
	}
	
	private void removeUndo(){				//�Ƴ�undoList�е�Undoʱ����
		//�Ƴ�undoʱ��redoList�����һ��redo
		redoList.add(new Redo(undoList.get(undoList.size()-1)));
		undoList.remove(undoList.size()-1);
	}
	
	private void removeRedo(){				//�Ƴ�redoList�е�Redoʱ����
		//�Ƴ�redoʱ��undoList�����һ��undo
		undoList.add(new Undo(redoList.get(redoList.size()-1)));
		redoList.remove(redoList.size()-1);
	}
	
	public void undo(){
		tempUndo = null;
		if(timer != null){			//��֮ǰ�ļ�ʱȡ��
			timer.cancel();
		}

		isOther = true;			//����Ϊtrue����Ϊ��undo��������ݱ仯
		
		Undo toUndo = undoList.get(undoList.size()-1);
		
		if(toUndo.getType() == Type.TYPING){			//���Ҫ�������Ǽ���
			codeArea.selectRange(toUndo.getEndPosition(), toUndo.getStartPosition());
			codeArea.deleteText(codeArea.getSelection());
		}
		else{				//���Ҫ��������ɾ��
			if(toUndo.getStartPosition() == toUndo.getEndPosition()){			//��������ɾ��
				//codeArea.positionCaret(toUndo.getStartPosition());
				String changes = toUndo.getChanges();
				int start = toUndo.getStartPosition();
				codeArea.insertText(start, changes);
				codeArea.selectRange(start, start+changes.length());		//ѡ�лָ��Ĳ���
			}
			else{				//�������ǰɾ��
				String changes = toUndo.getChanges();
				String toAddStr = "";
				int start = toUndo.getStartPosition();
				
				//����
				for(int i = changes.length()-1;i >= 0;i--){
					toAddStr += changes.charAt(i);
				}
				codeArea.insertText(start, toAddStr);
				codeArea.selectRange(start, start+toAddStr.length());		//ѡ�лָ��Ĳ���
			}
		}
		
		//�Ƴ����һ��undo�����������Ӧ��redo
		removeUndo();
	}
	
	public void redo(){
		isOther = true;			//����Ϊtrue����Ϊ��redo��������ݱ仯
		
		Redo toRedo = redoList.get(redoList.size()-1);
		
		if(toRedo.getType() == Type.TYPING){			//���Ҫ�������Ǽ���
			codeArea.insertText(toRedo.getStartPosition(), toRedo.getChanges());
			codeArea.selectRange(toRedo.getStartPosition(), toRedo.getEndPosition());
		}
		else{				//���Ҫ��������ɾ��
			String changes = toRedo.getChanges();
			int start = toRedo.getStartPosition();
			codeArea.selectRange(start, start+changes.length());		//ѡ��Ҫɾ���Ĳ���
			codeArea.deleteText(codeArea.getSelection());
		}
		
		//�Ƴ����һ��redo�����������Ӧ��undo
		removeRedo();
	}
	
	//ö����ÿ���޸ĵ�����
	enum Type{			//��ΪTYPING��DELETE����
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
