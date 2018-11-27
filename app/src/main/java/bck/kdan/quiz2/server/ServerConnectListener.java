package bck.kdan.quiz2.server;

import bck.kdan.quiz2.ConnectListener;

public interface ServerConnectListener extends ConnectListener
{
	public void onClientConnectSuccess(String ip);
	
	public void onClientConnectEnd(String ip);
}
