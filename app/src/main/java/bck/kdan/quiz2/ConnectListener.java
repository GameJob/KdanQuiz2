package bck.kdan.quiz2;

public interface ConnectListener
{
	public void onSendMessage(TalkMessage message);
	
	public void onReceiveMessage(TalkMessage message);
	
	public void onConnectStart();
	
	public void onConnectEnd();
	
	public void onConnectSuccess();
	
	public void onConnectFailed();
}
