package bck.kdan.quiz2.client;

import bck.kdan.quiz2.TalkMessage;
import bck.kdan.quiz2.AbstractTalkModel;

public class ClientModel extends AbstractTalkModel
{
	public ClientModel()
	{
		super();
	}

	@Override
	public String getDisplayText(TalkMessage message)
	{
		if (!message.getIp().isEmpty())
		{
			return "[/" + message.getIp() + "] 說: " + message.getContent();
		}
		else
		{
			return message.getContent();
		}
	}
}
