package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ExecuteService extends Remote {
	
	/**
	 * ���ǽ�ͨ���˷���������Ľ��������ܣ��벻Ҫ�޸ķ��������������ͣ����ز�������
	 * @param code bfԴ����
	 * @return ���н��
	 * @throws RemoteException
	 */
	public String executeBF(String code, String param) throws RemoteException;
	
	public String executeOok(String code, String param) throws RemoteException;
	
	public void setMemorySize(int size) throws RemoteException;
	
	public int getMemorySize() throws RemoteException;
	
	public void setInfinite(boolean value) throws RemoteException;
	
	public boolean isInfinite() throws RemoteException;
}
