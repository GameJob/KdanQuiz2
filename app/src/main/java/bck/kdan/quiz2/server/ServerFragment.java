package bck.kdan.quiz2.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import bck.kdan.quiz2.AbstractConnectFragment;
import bck.kdan.quiz2.ConnectListener;
import bck.kdan.quiz2.R;
import bck.kdan.quiz2.TalkMessage;

public class ServerFragment extends AbstractConnectFragment
{
	private ServerSocket serverSocket;
	private List<ServerThread> clients = new Vector<ServerThread>();

	@Override
	public void connect()
	{
		EditText etPort = layoutRoot.findViewById(R.id.etPort);

		if (!etPort.getText().toString().matches("[0-9]+"))
			return;
		setConnectPort(Integer.parseInt(etPort.getText().toString()));

		createConnectThread();
	}

	private void createConnectThread()
	{
		EditText etPort = layoutRoot.findViewById(R.id.etPort);
		try
		{
			connectPort = Integer.parseInt(etPort.getText().toString());
			serverSocket = new ServerSocket(connectPort);
			Thread thread = new Thread()
			{
				@Override
				public void run()
				{
					while (serverSocket != null && !serverSocket.isClosed())
					{
						waitNewClient();
					}
				}
			};
			thread.start();

			for (ConnectListener listener: listeners)
			{
				listener.onConnectSuccess();
			}
		}
		catch (Exception e)
		{
			showException(e);
		}
	}

	public void waitNewClient()
	{
		try
		{
			Socket socket = serverSocket.accept();
			addNewClient(socket);
		}
		catch (Exception e)
		{
			showException(e);
		}
	}

	public void addNewClient(final Socket socket)
	{
		ServerThread runnable = new ServerThread(socket);
		Thread thread = new Thread(runnable);
		thread.start();
		clients.add(runnable);
	}

	@Override
	public void disconnect()
	{
		ServerThread [] array = new ServerThread [clients.size()];
		clients.toArray(array);
		for (ServerThread client: array)
		{
			client.close();
		}
		if (serverSocket != null)
		{
			try
			{
				serverSocket.close();
			}
			catch (IOException e)
			{
				showException(e);
			}
			serverSocket = null;
		}
	}

	@Override
	public void send(TalkMessage message)
	{
		broadcast(message);
	}

	private void broadcast(TalkMessage message)
	{
		ServerThread [] array = new ServerThread [clients.size()];
		clients.toArray(array);
		for (ServerThread client: array)
		{
			client.send(message);
		}
	}

	@Override
	public void showDisconnectUI()
	{
		View inflatedLayout = createUI(R.layout.view_server_close, RelativeLayout.LayoutParams.WRAP_CONTENT);
		inflatedLayout.findViewById(R.id.btnStart).setOnClickListener(ServerFragment.this);
	}

	@Override
	public void showConnectUI()
	{
		View inflatedLayout = createUI(R.layout.view_server_start, RelativeLayout.LayoutParams.MATCH_PARENT);
		inflatedLayout.findViewById(R.id.btnClose).setOnClickListener(ServerFragment.this);

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
		case R.id.btnStart:
			for (ConnectListener listener: listeners)
			{
				listener.onConnectStart();
			}
			break;
		case R.id.btnClose:
			for (ConnectListener listener: listeners)
			{
				listener.onConnectEnd();
			}
			break;
		}
	}

	class ServerThread implements Runnable
	{
		private Socket socket;
		private String socketIp;

		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;

		public ServerThread(Socket socket)
		{
			this.socket = socket;
		}

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
					close();
				}
			}
		}

		public String getSocketIp()
		{
			String ip = socket.getInetAddress().toString().substring(1);
			if (ip.equals("127.0.0.1"))
			{
				Socket tmpSocket = new Socket();
				try
				{
					tmpSocket.connect(new InetSocketAddress("google.com", 80));
					ip = tmpSocket.getLocalAddress().toString().substring(1);
				}
				catch (Exception e)
				{
					showException(e);
				}
				finally
				{
					try
					{
						tmpSocket.close();
					}
					catch (IOException e)
					{
						showException(e);
					}
				}
			}
			return ip;
		}

		@Override
		public void run()
		{
			try
			{
				socketIp = getSocketIp();
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());

				for (ConnectListener listener: listeners)
				{
					if (listener instanceof ServerConnectListener)
					{
						((ServerConnectListener)listener).onClientConnectSuccess(socketIp);
					}
				}

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
			}
			catch (Exception e)
			{
				showException(e);
				close();
			}
		}

		public void close()
		{
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

				for (ConnectListener listener: listeners)
				{
					if (listener instanceof ServerConnectListener)
					{
						((ServerConnectListener)listener).onClientConnectEnd(socketIp);
					}
				}
			}

			clients.remove(this);
		}
	}
}
