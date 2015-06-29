package com.dgmltn.pimatic.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.activity.AppCompatAccountAuthenticatorActivity;
import com.dgmltn.pimatic.model.LoginResponse;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;
import com.dgmltn.pimatic.util.Events;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class PimaticAccountAuthenticatorActivity extends AppCompatAccountAuthenticatorActivity {

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

	private final String TAG = this.getClass().getSimpleName();

	@InjectView(R.id.preview)
	TextView vPreview;

	@InjectView(R.id.ssl)
	CheckBox vSsl;

	@InjectView(R.id.host)
	public EditText vHost;

	@InjectView(R.id.port)
	public EditText vPort;

	@InjectView(R.id.username)
	public EditText vUsername;

	@InjectView(R.id.password)
	public EditText vPassword;

	private AccountManager mAccountManager;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Timber.v("onCreate");
		setContentView(R.layout.activity_account);
		ButterKnife.inject(this);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
		}

		mAccountManager = AccountManager.get(getBaseContext());

		Intent intent = getIntent();
		ConnectionOptions conOpts = ConnectionOptions.fromIntent(intent);
		vSsl.setChecked(conOpts.protocol != null && conOpts.protocol.equals("https"));
		vPort.setText("" + conOpts.port);
		vHost.setText(conOpts.host != null ? conOpts.host : "");
		vUsername.setText(conOpts.username != null ? conOpts.username : "");
		vPassword.setText(conOpts.password != null ? conOpts.password : "");

		// http://stackoverflow.com/questions/24117178/android-typeface-is-changed-when-i-apply-password-type-on-edittext
		vPassword.setTypeface(vUsername.getTypeface());

		boolean isNew = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
		vHost.setEnabled(isNew);
		vUsername.setEnabled(isNew);

		vHost.addTextChangedListener(mHostPortChangedWatcher);
		vPort.addTextChangedListener(mHostPortChangedWatcher);

		updatePreview();
	}

	private TextWatcher mHostPortChangedWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// Intentionally left blank
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// Intentionally left blank
		}

		@Override
		public void afterTextChanged(Editable s) {
			updatePreview();
		}
	};

	@OnCheckedChanged(R.id.ssl)
	public void protocolItemSelected() {
		String port = vPort.getText().toString();
		if (vSsl.isChecked()) {
			vPort.setHint("443");
			if (port.length() == 0 || port.equals("80")) {
				vPort.setText("443");
			}
		}
		else {
			vPort.setHint("80");
			if (port.length() == 0 || port.equals("443")) {
				vPort.setText("80");
			}
		}
		updatePreview();
	}

	@OnClick(R.id.save)
	public void saveButtonClicked() {
		submit();
	}

	private void updatePreview() {
		vPreview.setText(
			(vSsl.isChecked() ? "https://" : "http://")
			+ (TextUtils.isEmpty(vHost.getText()) ? vHost.getHint() : vHost.getText())
			+ ":" + (TextUtils.isEmpty(vPort.getText()) ? vPort.getHint() : vPort.getText())
		);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// The sign up activity returned that the user has successfully created an account
		if (resultCode == RESULT_OK) {
			finishLogin(data);
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	public void submit() {
		Timber.v("submit()");
		final ConnectionOptions conOpts = new ConnectionOptions();
		conOpts.protocol = vSsl.isChecked() ? "https" : "http";
		conOpts.host = vHost.getText().toString();
		conOpts.port = Integer.parseInt(vPort.getText().toString());
		conOpts.username = vUsername.getText().toString();
		conOpts.password = vPassword.getText().toString();

		Network.generateRestService(conOpts)
			.login(conOpts.username, conOpts.password, new Callback<LoginResponse>() {
				@Override
				public void success(LoginResponse loginResponse, Response response) {
					Timber.v("login success: " + loginResponse.toString());
					final Intent res = new Intent();
					conOpts.putInIntent(res);
					finishLogin(res);
				}

				@Override
				public void failure(RetrofitError error) {
					Timber.v("login failure: " + error);
				}
			});
	}

	private void finishLogin(Intent intent) {
		Timber.v(TAG, "finishLogin");
		ConnectionOptions conOps = ConnectionOptions.fromIntent(intent);
		String accountName = conOps.getAccountName();
		String authToken = conOps.toAuthToken();
		final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);
		if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
			Timber.d("finishLogin > addAccountExplicitly");
			mAccountManager.addAccountExplicitly(account, conOps.password, null);
		}
		else {
			Timber.d("finishLogin > setPassword");
			mAccountManager.setPassword(account, conOps.password);
		}
		mAccountManager.setUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL, authToken);
		mAccountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_CONNECTION_URL, authToken);

		ConnectionOptions.toSettings(conOps, this);
		Events.post(new Events.AccountsChanged());

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

}