package bck.kdan.quiz2;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class AbstractConnectFragment extends Fragment implements ConnectView, OnClickListener
{
	protected Context mContext;
	protected RelativeLayout layoutRoot;

	protected List<ConnectListener> listeners = new ArrayList<ConnectListener>();

	protected String selfIp = getIPAddress(true);
	protected int connectPort;

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
		layoutRoot = new RelativeLayout(mContext);
		layoutRoot.setLayoutParams(params);
		showDisconnectUI();

		return layoutRoot;
	}
	
	protected View createUI(int resource, int h)
	{
		LayoutInflater inflater = LayoutInflater.from(mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, h);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);

		layoutRoot.removeAllViews();
		View inflatedLayout = inflater.inflate(resource, null, false);
		inflatedLayout.setLayoutParams(params);
		layoutRoot.addView(inflatedLayout);
		
		return inflatedLayout;
	}
	
	@Override
	public String getIp()
	{
		return selfIp;
	}

	@Override
	public int getConnectPort()
	{
		return connectPort;
	}

	public void setConnectPort(int port)
	{
		connectPort = port;
	}

	@Override
	public void display(final String text)
	{		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				final LinearLayout layout = layoutRoot.findViewById(R.id.layoutMessage);
				if (layout == null)
				{
					return;
				}
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 5, 0, 5);
				final TextView textview = new TextView(mContext);
				textview.setTextSize(16);
				textview.setLayoutParams(params);
				textview.setText(text);
				layout.addView(textview);
			}
		});
	}

	@Override
	public boolean addConnectListener(ConnectListener listener)
	{
		return listeners.add(listener);
	}

	@Override
	public boolean removeConnectListener(ConnectListener listener)
	{
		return listeners.remove(listener);
	}
	
	public String getIPAddress(boolean useIPv4)
	{
		try
		{
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf: interfaces)
			{
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr: addrs)
				{
					if (!addr.isLoopbackAddress())
					{
						String sAddr = addr.getHostAddress();
						boolean isIPv4 = sAddr.indexOf(':') < 0;

						if (useIPv4)
						{
							if (isIPv4)
							{
								return sAddr;
							}
						}
						else
						{
							if (!isIPv4)
							{
								int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
								return delim < 0? sAddr.toUpperCase(Locale.US): sAddr.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			showException(e);
		}
		return "";
	}

	protected void showException(Exception e)
	{
		e.printStackTrace();
		Log.e(getClass().getSimpleName(), e.toString());
	}
	
	protected boolean runOnUiThread(Runnable action)
	{
		return layoutRoot.post(action);
	}
}
