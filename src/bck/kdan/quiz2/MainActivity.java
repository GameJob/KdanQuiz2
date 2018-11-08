package bck.kdan.quiz2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import bck.kdan.quiz2.client.ClientFragment;
import bck.kdan.quiz2.client.ClientPresenter;
import bck.kdan.quiz2.server.ServerFragment;
import bck.kdan.quiz2.server.ServerPresenter;

public class MainActivity extends AppCompatActivity
{
	Presenter serverPresenter;
	Presenter clientPresenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initToolbar();
		initContent();
	}

	private void initToolbar()
	{
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);
	}

	private void initContent()
	{
		Fragment fragment = new ServerFragment();
		serverPresenter = new ServerPresenter((ServerFragment)fragment);
		navigateTo(R.id.layoutServer, fragment);

		fragment = new ClientFragment();
		clientPresenter = new ClientPresenter((ClientFragment)fragment);
		navigateTo(R.id.layoutClient, fragment);
	}
	
	private void navigateTo(int id, Fragment fragment)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(id, fragment);
		transaction.commit();
	}

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy()
	{
		serverPresenter.onDestroy();
		clientPresenter.onDestroy();
		super.onDestroy();
	}
}
