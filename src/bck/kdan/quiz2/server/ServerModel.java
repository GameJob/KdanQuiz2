package bck.kdan.quiz2.server;

import bck.kdan.quiz2.TalkMessage;
import bck.kdan.quiz2.AbstractTalkModel;

public class ServerModel extends AbstractTalkModel
{
	public ServerModel()
	{
		super();
	}

	@Override
	public String getDisplayText(TalkMessage message)
	{
		if (!message.getIp().isEmpty())
		{
			return "[/" + message.getIp() + "] : " + message.getContent();
		}
		else
		{
			return message.getContent();
		}
	}
}
