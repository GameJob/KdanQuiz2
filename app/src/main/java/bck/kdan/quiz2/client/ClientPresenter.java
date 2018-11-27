package bck.kdan.quiz2.client;

import bck.kdan.quiz2.ConnectListener;
import bck.kdan.quiz2.ConnectView;
import bck.kdan.quiz2.TalkMessage;
import bck.kdan.quiz2.AbstractTalkModel;
import bck.kdan.quiz2.Presenter;

public class ClientPresenter implements Presenter, ConnectListener
{
	private ConnectView view;
	private AbstractTalkModel model;
	
	public ClientPresenter(ConnectView view)
	{
		this.view = view;
		this.model = new ClientModel();
		this.view.addConnectListener(this);
	}

	@Override
	public void onSendMessage(TalkMessage message)
	{
		view.send(message);
	}

	@Override
	public void onReceiveMessage(TalkMessage message)
	{
		String text = model.addToMessageRecord(message);
		view.display(text);
	}

	@Override
	public void onConnectStart()
	{
		view.connect();
	}

	@Override
	public void onConnectEnd()
	{
		view.disconnect();
		view.showDisconnectUI();
	}

	@Override
	public void onConnectSuccess()
	{
		String text = model.addToMessageRecord("Connected to " + view.getIp() + ":" + view.getConnectPort());
		view.showConnectUI();
		view.display(text);
	}

	@Override
	public void onConnectFailed()
	{
		onConnectEnd();
	}

	@Override
	public void onCreate()
	{
		
	}

	@Override
	public void onPause()
	{
		
	}

	@Override
	public void onResume()
	{
		
	}

	@Override
	public void onDestroy()
	{
		onConnectEnd();
	}
}
