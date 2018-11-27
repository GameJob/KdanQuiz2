package bck.kdan.quiz2.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import bck.kdan.quiz2.AbstractConnectFragment;
import bck.kdan.quiz2.ConnectListener;
import bck.kdan.quiz2.R;
import bck.kdan.quiz2.TalkMessage;

public class ClientFragment extends AbstractConnectFragment
{
	private String connectIp;
	private Thread thread;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	public void setConnectIp(String ip)
	{
		connectIp = ip;
	}

	@Override
	public void connect()
	{
		EditText etIp = layoutRoot.findViewById(R.id.etIp);
		EditText etPort = layoutRoot.findViewById(R.id.etPort);

		setConnectIp(etIp.getText().toString());
		if (!etPort.getText().toString().matches("[0-9]+"))
			return;
		setConnectPort(Integer.parseInt(etPort.getText().toString()));

		createConnectThread();
	}

	private void createConnectThread()
	{
		try
		{
			thread = new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						InetAddress serverIp = InetAddress.getByName(connectIp);
						socket = new Socket(serverIp, connectPort);
						outputStream = new ObjectOutputStream(socket.getOutputStream());
						inputStream = new ObjectInputStream(socket.getInputStream());
						
						connectSuccess();

						Object object;
						while ((object = inputStream.readObject()) != null)
						{
							if (object instanceof TalkMessage)
							{
								TalkMessage message = (TalkMessage)object;
								for (ConnectListener listener: listeners)
								{
									listener.onReceiveMessage(message);
								}
							}
						}
						
						connectEnd();
					}
					catch (Exception e)
					{
						showException(e);
						connectFailed();
					}
				}
			};
			thread.start();
		}
		catch (Exception e)
		{
			showException(e);
			connectFailed();
		}
	}
	
	private void connectSuccess()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				for (ConnectListener listener: listeners)
				{
					listener.onConnectSuccess();
				}
			}
		});
	}
	
	private void connectEnd()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				for (ConnectListener listener: listeners)
				{
					listener.onConnectEnd();
				}
			}
		});
	}
	
	private void connectFailed()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				for (ConnectListener listener: listeners)
				{
					listener.onConnectFailed();
				}
			}
		});
	}

	@Override
	public void disconnect()
	{
		if (thread != null)
		{
			thread.interrupt();
			thread = null;
		}
		if (outputStream != null)
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				showException(e);
			}
			outputStream = null;
		}
		if (inputStream != null)
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				showException(e);
			}
			inputStream = null;
		}
		if (socket != null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				showException(e);
			}
			socket = null;
		}
	}

	@Override
	public void send(TalkMessage message)
	{
		if (outputStream != null)
		{
			try
			{
				outputStream.writeObject(message);
				outputStream.flush();
			}
			catch (IOException e)
			{
				showException(e);
				connectFailed();
			}
		}
	}

	@Override
	public void showDisconnectUI()
	{
		View inflatedLayout = createUI(R.layout.view_client_disconnect, RelativeLayout.LayoutParams.WRAP_CONTENT);
		inflatedLayout.findViewById(R.id.btnConnect).setOnClickListener(ClientFragment.this);
	}

	@Override
	public void showConnectUI()
	{
		View inflatedLayout = createUI(R.layout.view_client_connect, RelativeLayout.LayoutParams.MATCH_PARENT);
		inflatedLayout.findViewById(R.id.btnDisconnect).setOnClickListener(ClientFragment.this);
		inflatedLayout.findViewById(R.id.btnSend).setOnClickListener(ClientFragment.this);

		final ScrollView scrollview = layoutRoot.findViewById(R.id.scrollview);
		scrollview.addOnLayoutChangeListener(new OnLayoutChangeListener()
		{
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
			{
				scrollview.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btnConnect:
			for (ConnectListener listener: listeners)
			{
				listener.onConnectStart();
			}
			break;
		case R.id.btnDisconnect:
			for (ConnectListener listener: listeners)
			{
				listener.onConnectEnd();
			}
			break;
		case R.id.btnSend:
			EditText etMessage = layoutRoot.findViewById(R.id.etMessage);
			String message = etMessage.getText().toString();
			if (!message.isEmpty())
			{
				etMessage.setText("");
				for (ConnectListener listener: listeners)
				{
					listener.onSendMessage(new TalkMessage(selfIp, message));
				}
			}
			break;
		}
	}
}
