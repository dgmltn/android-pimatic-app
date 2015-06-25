package com.dgmltn.pimatic.network;

import java.net.URI;
import java.net.URISyntaxException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.dgmltn.pimatic.R;
import com.dgmltn.pimatic.accounts.AccountGeneral;

/**
 * Created by Oliver Schneider <oliverschneider89+sweetpi@gmail.com>
 */
public class ConnectionOptions {

    public static final String ARG_PROTOCOL = "com.dgmltn.pimatic.connection.protocol";
    public static final String ARG_HOST = "com.dgmltn.pimatic.connection.host";
    public static final String ARG_PORT = "com.dgmltn.pimatic.connection.port";
    public static final String ARG_USERNAME = "com.dgmltn.pimatic.connection.username";
    public static final String ARG_PASSWORD = "com.dgmltn.pimatic.connection.password";

    public String protocol;
    public String host;
    public int port;
    public String username;
    public String password;

    public static ConnectionOptions fromAuthToken(String authToken) {
        try {
            URI uri = new URI(authToken);
            ConnectionOptions conOps = new ConnectionOptions();
            conOps.protocol = uri.getScheme();
            conOps.host = uri.getHost();
            String auth = uri.getAuthority();
            int index = auth.indexOf(":");
            int index2 = auth.lastIndexOf("@");
            conOps.username = auth.substring(0, index);
            conOps.password = auth.substring(index + 1, index2);
            conOps.port = uri.getPort();
            return conOps;

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static ConnectionOptions fromAccount(AccountManager am, Account account) {
        final String authUrl = am.getUserData(account, AccountGeneral.ACCOUNT_USER_DATA_URL);
        return ConnectionOptions.fromAuthToken(authUrl);
    }

    public static ConnectionOptions fromDemo(Context context) {
        ConnectionOptions conOpts = new ConnectionOptions();
        Resources res = context.getResources();
        conOpts.protocol = res.getString(R.string.default_protocol);
        conOpts.host = res.getString(R.string.default_host);
        conOpts.port = res.getInteger(R.integer.default_port);
        conOpts.username = res.getString(R.string.default_username);
        conOpts.password = res.getString(R.string.default_password);
        return conOpts;
    }

    public static ConnectionOptions fromSettings(Context context) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(context.getApplicationContext());
        if (!settings.contains("host")) {
            return null;
        }
        ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = settings.getString("protocol", "");
        conOpts.host = settings.getString("host", "");
        conOpts.port = settings.getInt("port", 0);
        conOpts.username = settings.getString("username", "");
        conOpts.password = settings.getString("password", "");
        return conOpts;
    }

    /**
     * Store this ConnectionOptions object into SharedPreferences
     *
     * @param conOpts
     * @param context
     */
    public static void toSettings(ConnectionOptions conOpts, Context context) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("protocol", conOpts.protocol);
        editor.putString("host", conOpts.host);
        editor.putInt("port", conOpts.port);
        editor.putString("username", conOpts.username);
        editor.putString("password", conOpts.password);
        editor.commit();
    }

    /**
     * Clear out any previously saved ConnectionsOptions object from SharedPreferences
     * @param context
     */
    public static void eraseSettings(Context context) {
        SharedPreferences settings = PreferenceManager
            .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("protocol");
        editor.remove("host");
        editor.remove("port");
        editor.remove("username");
        editor.remove("password");
        editor.commit();
    }

    public static ConnectionOptions fromIntent(Intent intent) {
        return fromBundle(intent.getExtras());
    }

    public static ConnectionOptions fromBundle(Bundle bundle) {
        ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = bundle.getString(ARG_PROTOCOL);
        conOpts.host = bundle.getString(ARG_HOST);
        conOpts.port = bundle.getInt(ARG_PORT, 80);
        conOpts.username = bundle.getString(ARG_USERNAME);
        conOpts.password = bundle.getString(ARG_PASSWORD);
        return conOpts;
    }

    public String getBaseUrl() {
        return protocol + "://" + host + ":" + port;
    }

    public String getAccountName() {
        return username + "@" + host;
    }

    public String toAuthToken() {
        URI uri;
        try {
            uri = new URI(
                    protocol, //scheme
                    username + ":" + password, //userInfo
                    host, //host
                    port, //port
                    null, //path
                    null, //query
                    null  //fragment
            );
            return uri.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void putInIntent(Intent intent) {
        Bundle bundle = new Bundle();
        putInBundle(bundle);
        intent.putExtras(bundle);
    }


    public void putInBundle(Bundle bundle) {
        bundle.putString(ARG_PROTOCOL, protocol);
        bundle.putString(ARG_HOST, host);
        bundle.putInt(ARG_PORT, port);
        bundle.putString(ARG_USERNAME, username);
        bundle.putString(ARG_PASSWORD, password);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("protocol: ").append(protocol);
        builder.append(", host: ").append(host);
        builder.append(", port: ").append(port);
        builder.append(", username: ").append(username);
        builder.append(", password: ").append(password);
        return builder.toString();
    }

	@Override
	public boolean equals(Object o) {
		return o == null ? false : toString().equals(o.toString());
	}
}


