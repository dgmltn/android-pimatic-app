package com.dgmltn.pimatic.ui;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.accounts.AccountGeneral;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

	@InjectView(R.id.toolbar)
	Toolbar vToolbar;

	@InjectView(R.id.drawer_layout)
	DrawerLayout vDrawerLayout;

	@InjectView(R.id.tabs)
	TabLayout vTabLayout;

	@InjectView(R.id.viewpager)
	ViewPager vPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		setSupportActionBar(vToolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			vDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		case R.id.action_settings:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Events.register(this);

		// This just always chooses the first account
		// TODO: store the chosen account in settings
		AccountManager am = AccountManager.get(this);
		for (Account account : am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)) {
			final String authUrl = am.getUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL);
			ConnectionOptions conOpts = ConnectionOptions.fromAuthToken(authUrl);
			Model.getInstance().configureNetwork(conOpts);
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Model.getInstance().deconfigureNetwork();
		Events.unregister(this);
	}

	@Subscribe
	public void otto(Events.DevicesChanged e) {
		setupViewPager();
	}

	@Subscribe
	public void otto(Events.PagesChanged e) {
		setupViewPager();
	}

	@Subscribe
	public void otto(Events.DesiredGroupTab e) {
		vDrawerLayout.closeDrawers();
		vPager.setCurrentItem(e.tab, true);
	}

	private void setupViewPager() {
		Model model = Model.getInstance();

		Adapter adapter = new Adapter(getSupportFragmentManager());
		if (model.pages != null && model.devices != null) {
			for (Page p : model.pages) {
				if (p != null) {
					adapter.addFragment(p);
				}
			}
		}
		vPager.setAdapter(adapter);

		vTabLayout.removeAllTabs();
		vTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		vTabLayout.setupWithViewPager(vPager);
	}

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		public void addFragment(Page p) {
			mFragments.add(PageFragment.getInstance(p));
			mFragmentTitles.add(p.name);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitles.get(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
}
