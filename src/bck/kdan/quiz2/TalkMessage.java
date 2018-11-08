package bck.kdan.quiz2;

import java.io.Serializable;

public class TalkMessage implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8009322099354979332L;
	
	private String ip = "";
	private String content = "";

	public TalkMessage(String ip, String content)
	{
		this.ip = ip;
		this.content = content;
	}

//	public TalkMessage(TalkMessage other)
//	{
//		this.ip = other.ip;
//		this.content = other.content;
//	}

	public String getIp()
	{
		return ip;
	}

	public String getContent()
	{
		return content;
	}
}
