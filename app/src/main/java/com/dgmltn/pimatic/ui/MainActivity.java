package com.dgmltn.pimatic.ui;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.accounts.AccountGeneral;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;
import com.dgmltn.pimatic.network.ConnectionOptions;
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
	public void onBackPressed() {
		if (vDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			vDrawerLayout.closeDrawer(GravityCompat.START);
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Events.register(this);
		setupNetwork();
	}

	private void setupNetwork() {
		// See if they've saved a connection in settings
		ConnectionOptions conOpts = ConnectionOptions.fromSettings(this);

		AccountManager am = AccountManager.get(this);

		// Verify that the connection stored in settings still exists in the AccountManager
		if (conOpts != null) {
			String authToken = conOpts.toAuthToken();
			boolean match = false;
			for (Account account : am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)) {
				String authUrl = am.getUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL);
				if (authUrl.equals(authToken)) {
					match = true;
					break;
				}
			}

			// A connection was found in preferences, but it doesn't match
			// any existing Account. So, don't use it, and erase it from preferences
			if (!match) {
				conOpts = null;
				ConnectionOptions.eraseSettings(this);
			}
		}

		// No existing connection was found, just use the first account
		if (conOpts == null) {
			for (Account account : am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)) {
				String authUrl = am.getUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL);
				conOpts = ConnectionOptions.fromAuthToken(authUrl);
				break;
			}
		}

		// All else failed, use the demo ConnectionOptions by default
		if (conOpts == null) {
			conOpts = ConnectionOptions.fromDemo(this);
		}

		Model.getInstance().configureNetwork(conOpts);
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
	public void otto(Events.AccountsChanged e) {
		setupNetwork();
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

		GroupPagerAdapter adapter = (GroupPagerAdapter) vPager.getAdapter();
		if (adapter == null) {
			adapter = new GroupPagerAdapter();
			vPager.setAdapter(adapter);
			vTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
			vTabLayout.setupWithViewPager(vPager);
		}
		else {
			adapter.clear();
			vPager.setAdapter(adapter);
		}
		if (model.getPages() != null && model.getDevices() != null) {
			for (Page p : model.getPages()) {
				if (p != null) {
					adapter.add(p);
				}
			}
		}
		vTabLayout.setTabsFromPagerAdapter(adapter);
	}

	static class GroupPagerAdapter extends PagerAdapter {
		private final List<Page> mPages = new ArrayList<>();
		private final List<PageView> mViews = new ArrayList<>();

		@Override
		public int getCount() {
			return mPages.size();
		}

		public void clear() {
			mPages.clear();
			mViews.clear();
			notifyDataSetChanged();
		}

		public void add(Page p) {
			mPages.add(p);
			mViews.add(null);
			notifyDataSetChanged();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Page p = mPages.get(position);
			PageView view = mViews.get(position);
			if (view == null) {
				view = (PageView) LayoutInflater
					.from(container.getContext())
					.inflate(R.layout.fragment_page, container, false);
				view.setPage(Model.getInstance(), p);
				mViews.set(position, view);
			}
			container.addView(view, 0);
			return p;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			for (int i = 0; i < container.getChildCount(); i++) {
				if (isViewFromObject(container.getChildAt(i), object)) {
					container.removeViewAt(i);
					break;
				}
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			boolean is = object == null || view == null
				? false
				: ((Page) object).id.equals(((PageView) view).getPage().id);
			return is;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPages.get(position).name;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
}
