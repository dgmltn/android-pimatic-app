package com.dgmltn.pimatic.network;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import com.dgmltn.pimatic.R;

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

    public static ConnectionOptions fromDemo(Resources res) {
        ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = res.getString(R.string.default_protocol);
        conOpts.host = res.getString(R.string.default_host);
        conOpts.port = res.getInteger(R.integer.default_port);
        conOpts.username = res.getString(R.string.default_username);
        conOpts.password = res.getString(R.string.default_password);
        return conOpts;
    }

    public static ConnectionOptions fromSettings(Resources res, SharedPreferences settings) {
        ConnectionOptions conOpts = new ConnectionOptions();
        conOpts.protocol = settings.getString("protocol", res.getString(R.string.default_protocol));
        conOpts.host = settings.getString("host", res.getString(R.string.default_host));
        conOpts.port = settings.getInt("port", res.getInteger(R.integer.default_port));
        conOpts.username = settings.getString("username", res.getString(R.string.default_username));
        conOpts.password = settings.getString("password", res.getString(R.string.default_password));
        return conOpts;
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
}


