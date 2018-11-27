package bck.kdan.quiz2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

public abstract class AbstractTalkModel
{
	// message record
	private List<TalkMessage> records;

	public AbstractTalkModel()
	{
		records = new Vector<TalkMessage>();
	}

	// record message and return text to display on the screen
	public String addToMessageRecord(TalkMessage message)
	{
		records.add(message);
		return getDisplayText(message);
	}

	// record message and return text to display on the screen
	public String addToMessageRecord(String text)
	{
		return addToMessageRecord(new TalkMessage("", text));
	}

	// get the text to display on the screen
	public abstract String getDisplayText(TalkMessage message);

	// output the message record to OutputStream
	public void outputMessageRecord(OutputStream outputStream)
	{
		synchronized (records)
		{
			try
			{
				for (TalkMessage message: records)
				{
					String text = getDisplayText(message) + "\n";
					outputStream.write(text.getBytes());
				}
				outputStream.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// clear records
	public void clearMessageRecord()
	{
		records.clear();
	}
}
