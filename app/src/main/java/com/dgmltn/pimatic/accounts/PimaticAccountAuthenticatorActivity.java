package com.dgmltn.pimatic.accounts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.model.LoginResponse;
import com.dgmltn.pimatic.network.ConnectionOptions;
import com.dgmltn.pimatic.network.Network;

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

    private AccountManager mAccountManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.account_settings);
        mAccountManager = AccountManager.get(getBaseContext());

        final Spinner protocolView = (Spinner) findViewById(R.id.protocol);
        final EditText hostView = (EditText) findViewById(R.id.host);
        final EditText portView = (EditText) findViewById(R.id.port);
        final EditText usernameView = (EditText) findViewById(R.id.username);
        final EditText passwordView = (EditText) findViewById(R.id.password);
        final CheckBox delTrustedView = (CheckBox) findViewById(R.id.deleteCert);

        Intent intent = getIntent();
        ConnectionOptions conOpts = ConnectionOptions.fromIntent(getIntent());
        protocolView.setSelection(conOpts.protocol == null || conOpts.protocol.equals("http") ? 0 : 1);
        portView.setText("" + conOpts.port);
        hostView.setText(conOpts.host != null ? conOpts.host : "");
        usernameView.setText(conOpts.username != null ? conOpts.username : "");
        passwordView.setText(conOpts.password != null ? conOpts.password : "");

        boolean isNew = intent.getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false);
        hostView.setEnabled(isNew);
        usernameView.setEnabled(isNew);

        protocolView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProtocol = protocolView.getSelectedItem().toString();
                String port = portView.getText().toString();
                if (selectedProtocol.equals("https") && (port.length() == 0 || port.equals("80"))) {
                    portView.setText("443");
                } else if (selectedProtocol.equals("http") && (port.length() == 0 || port.equals("443"))) {
                    portView.setText("80");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {

        final Spinner protocolView = (Spinner) findViewById(R.id.protocol);
        final EditText hostView = (EditText) findViewById(R.id.host);
        final EditText portView = (EditText) findViewById(R.id.port);
        final EditText usernameView = (EditText) findViewById(R.id.username);
        final EditText passwordView = (EditText) findViewById(R.id.password);
        final CheckBox delTrustedView = (CheckBox) findViewById(R.id.deleteCert);

        final ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = protocolView.getSelectedItem().toString();
        conOpts.host = hostView.getText().toString();
        conOpts.port = Integer.parseInt(portView.getText().toString());
        conOpts.username = usernameView.getText().toString();
        conOpts.password = passwordView.getText().toString();

        Network.getRest().login(conOpts.username, conOpts.password, new Callback<LoginResponse>() {
            @Override
            public void success(LoginResponse loginResponse, Response response) {
                Timber.v(TAG, "login success: " + loginResponse.toString());
                final Intent res = new Intent();
                conOpts.putInIntent(res);
                finishLogin(res);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.v(TAG, "login failure: " + error);
            }
        });
    }

    private void finishLogin(Intent intent) {
        Log.v(TAG, "finishLogin");
        ConnectionOptions conOps = ConnectionOptions.fromIntent(intent);
        String accountName = conOps.getAccountName();
        String authToken = conOps.toAuthToken();
        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "finishLogin > addAccountExplicitly");
            mAccountManager.addAccountExplicitly(account, conOps.password, null);
        } else {
            Log.d(TAG, "finishLogin > setPassword");
            mAccountManager.setPassword(account, conOps.password);
        }
        mAccountManager.setUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL, authToken);
        mAccountManager.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_CONNECTION_URL, authToken);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}