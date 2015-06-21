package com.dgmltn.pimatic.ui;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.accounts.AccountGeneral;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.util.Events;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;


/**
 * Created by doug on 6/20/15.
 */
public class NavigationView extends android.support.design.widget.NavigationView
	implements android.support.design.widget.NavigationView.OnNavigationItemSelectedListener {

	// Arbitrary number from which to start the id's inside the group
	// (where "group" in this case is the sole <group> inside
	// drawer_view.xml or drawer_accounts.xml)
	private static final int GROUP_START_ID = 1;

	@InjectView(R.id.username)
	public TextView vUsername;

	private boolean isDisplayingAccounts;

	public NavigationView(Context context) {
		super(context);
		init(context);
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		setNavigationItemSelectedListener(this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.inject(this);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		Events.register(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		Events.unregister(this);
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		if (menuItem.getGroupId() == R.id.accounts) {
			Timber.e("navigation: accounts!");
		}
		else if (menuItem.getGroupId() == R.id.groups) {
			Timber.e("navigation: group # " + menuItem.getItemId());
			Events.post(new Events.DesiredGroupTab(menuItem.getItemId() - GROUP_START_ID));
			return true;
		}
		else if (menuItem.getItemId() == R.id.add_account) {
			Timber.e("navigation: add account!");
			addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_CONNECTION_URL);
			return true;
		}
		else if (menuItem.getItemId() == R.id.manage_accounts) {
			Timber.e("navigation: manage accounts!");
			String[] authorities = {AccountGeneral.ACCOUNT_TYPE};
			Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
			intent.putExtra(Settings.EXTRA_AUTHORITIES, authorities);
			getContext().startActivity(intent);
			return true;
		}
		else if (menuItem.getItemId() == R.id.rules) {
			Timber.e("navigation: rules!");
		}
		else if (menuItem.getItemId() == R.id.variables) {
			Timber.e("navigation: variables!");
		}
		else if (menuItem.getItemId() == R.id.messages) {
			Timber.e("navigation: messages!");
		}
		else if (menuItem.getItemId() == R.id.events) {
			Timber.e("navigation: events!");
		}
		else if (menuItem.getItemId() == R.id.connections) {
			Timber.e("navigation: connections!");
		}
		return false;
	}

	@Subscribe
	public void otto(Events.AccountsChanged e) {
		bind();
	}

	@Subscribe
	public void otto(Events.DevicesChanged e) {
		bind();
	}

	@Subscribe
	public void otto(Events.PagesChanged e) {
		bind();
	}

	@OnClick(R.id.username)
	public void usernameOnClick() {
		isDisplayingAccounts = !isDisplayingAccounts;
		bind();
	}

	private void bind() {
		Menu menu = getMenu();
		menu.clear();

		vUsername.setText(Model.getInstance().getConnection().getAccountName());

		if (isDisplayingAccounts) {
			inflateMenu(R.menu.drawer_accounts);
			AccountManager am = AccountManager.get(getContext());
			int i = GROUP_START_ID;
			for (Account account : am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)) {
				MenuItem existingItem = menu.findItem(i);
				if (existingItem == null) {
					menu.add(R.id.accounts, i, i, account.name);
				}
				else {
					existingItem.setTitle(account.name);
				}
				i++;
			}
		}
		else {
			inflateMenu(R.menu.drawer_view);
			Model model = Model.getInstance();
			if (model.pages != null && model.devices != null) {
				int i = GROUP_START_ID;
				for (Page p : model.pages) {
					if (p != null) {
						Timber.i("page: " + p.id + ": " + p.name);
						MenuItem existingItem = menu.findItem(i);
						if (existingItem == null) {
							menu.add(R.id.groups, i, i, p.name);
						}
						else {
							existingItem.setTitle(p.name);
						}
						i++;
					}
				}
			}
		}
	}

	/**
	 * Add new account to the account manager
	 *
	 * @param accountType
	 * @param authTokenType
	 */
	private void addNewAccount(String accountType, String authTokenType) {
		AccountManager am = AccountManager.get(getContext());
		am.addAccount(accountType, authTokenType, null, null, null, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle bundle = future.getResult();
					Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
					getContext().startActivity(intent);
				}
				catch (OperationCanceledException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				catch (AuthenticatorException e) {
					e.printStackTrace();
				}

				Events.post(new Events.DevicesChanged());
			}
		}, null);
	}
}
