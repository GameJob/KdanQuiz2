package bck.kdan.quiz2.server;

import bck.kdan.quiz2.ConnectView;
import bck.kdan.quiz2.TalkMessage;
import bck.kdan.quiz2.AbstractTalkModel;
import bck.kdan.quiz2.Presenter;

public class ServerPresenter implements Presenter, ServerConnectListener
{
	private ConnectView view;
	private AbstractTalkModel model;

	public ServerPresenter(ConnectView view)
	{
		this.view = view;
		this.model = new ServerModel();
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
		view.send(message);
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
		model.outputMessageRecord(System.out);
		model.clearMessageRecord();
	}

	@Override
	public void onConnectSuccess()
	{
		String text = model.addToMessageRecord("Ip: " + view.getIp() + " Port: " + view.getConnectPort());
		view.showConnectUI();
		view.display(text);
	}

	@Override
	public void onConnectFailed()
	{
		onConnectEnd();
	}

	@Override
	public void onClientConnectSuccess(String ip)
	{
		TalkMessage message = new TalkMessage("", "[/" + ip + "] 加入聊天室");
		String text = model.addToMessageRecord(message);
		view.display(text);
		view.send(message);
	}

	@Override
	public void onClientConnectEnd(String ip)
	{
		TalkMessage message = new TalkMessage("", "[/" + ip + "] 離開了");
		String text = model.addToMessageRecord(message);
		view.display(text);
		view.send(message);
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
