package bck.kdan.quiz2;

public interface ConnectView
{
	public String getIp();

	public int getConnectPort();
	
	public void connect();
	
	public void disconnect();

	public void send(TalkMessage message);

	public void display(String text);
	
	public void showDisconnectUI();

	public void showConnectUI();
	
	public boolean addConnectListener(ConnectListener listener);
	
	public boolean removeConnectListener(ConnectListener listener);
}
