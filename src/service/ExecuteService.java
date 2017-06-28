package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExecuteService extends Remote {
	
	/**
	 * 我们将通过此方法测试你的解析器功能，请不要修改方法名，参数类型，返回参数类型
	 * @param code bf源代码
	 * @return 运行结果
	 * @throws RemoteException
	 */
	public String executeBF(String code, String param) throws RemoteException;
	
	public String executeOok(String code, String param) throws RemoteException;
	
	public void setMemorySize(int size) throws RemoteException;
	
	public int getMemorySize() throws RemoteException;
	
	public void setInfinite(boolean value) throws RemoteException;
	
	public boolean isInfinite() throws RemoteException;
}
