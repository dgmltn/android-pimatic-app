package com.dgmltn.pimatic.accounts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.LoginResponse;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class PimaticAccountAuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private final String TAG = this.getClass().getSimpleName();

	@InjectView(R.id.protocol)
	public Spinner vProtocol;

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
        Timber.v("login onCreate");
        setContentView(R.layout.account_settings);
		ButterKnife.inject(this);
		
        mAccountManager = AccountManager.get(getBaseContext());

        Intent intent = getIntent();
        ConnectionOptions conOpts = ConnectionOptions.fromIntent(intent);
        vProtocol.setSelection(conOpts.protocol == null || conOpts.protocol.equals("http") ? 0 : 1);
        vPort.setText("" + conOpts.port);
        vHost.setText(conOpts.host != null ? conOpts.host : "");
        vUsername.setText(conOpts.username != null ? conOpts.username : "");
        vPassword.setText(conOpts.password != null ? conOpts.password : "");

        boolean isNew = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        vHost.setEnabled(isNew);
        vUsername.setEnabled(isNew);
    }

	@OnItemSelected(R.id.protocol)
	public void protocolItemSelected() {
		String selectedProtocol = vProtocol.getSelectedItem().toString();
		String port = vPort.getText().toString();
		if (selectedProtocol.equals("https") && (port.length() == 0 || port.equals("80"))) {
			vPort.setText("443");
		}
		else if (selectedProtocol.equals("http") && (port.length() == 0 || port.equals("443"))) {
			vPort.setText("80");
		}
	}

    @OnClick(R.id.save)
    public void saveButtonClicked() {
        submit();
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
		Timber.v("login submit()");
        final ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = vProtocol.getSelectedItem().toString();
        conOpts.host = vHost.getText().toString();
        conOpts.port = Integer.parseInt(vPort.getText().toString());
        conOpts.username = vUsername.getText().toString();
        conOpts.password = vPassword.getText().toString();

        Network.getRest().login(conOpts.username, conOpts.password, new Callback<LoginResponse>() {
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

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}