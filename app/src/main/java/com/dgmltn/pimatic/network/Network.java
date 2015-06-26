package com.dgmltn.pimatic.network;

import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.Group;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;
import com.dgmltn.pimatic.util.JSONUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.okhttp.Credentials;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import timber.log.Timber;

public class Network {
	private ConnectionOptions connection;

	public Network(ConnectionOptions connection) {
		this.connection = connection;
		setupWebsocket();
	}

	public void teardown() {
		teardownWebsocket();
	}

	/////////////////////////////////////////////////////////////////////////
	// Websocket
	/////////////////////////////////////////////////////////////////////////

	private Socket socket;

	private void setupWebsocket() {
		if (socket == null) {
			try {
				socket = IO.socket(connection.getBaseUrl()
					+ "/?username=" + connection.username + "&password=" + connection.password);
			}
			catch (URISyntaxException e) {
				Timber.e(e.toString());
				return;
			}
		}

		socket.on("devices", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("devices " + args[0].toString());
				Model.getInstance().setDevices(JSONUtils.toFromJson((JSONArray) args[0], Device.class));
			}
		});

		socket.on("rules", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("rules " + args[0].toString());
			}
		});

		socket.on("variables", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("variables " + args[0].toString());
			}
		});

		socket.on("pages", new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				Timber.i("pages " + args[0].toString());
				Model.getInstance().setPages(JSONUtils.toFromJson((JSONArray) args[0], Page.class));
			}
		});

		socket.on("groups", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("groups " + args[0].toString());
				Model.getInstance().setGroups(JSONUtils.toFromJson((JSONArray) args[0], Group.class));
			}
		});

		socket.on("deviceAttributeChanged", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("deviceAttributeChanged " + args[0].toString());
			}
		});

		socket.connect();

	}

	private void teardownWebsocket() {
		if (socket == null) {
			return;
		}

		socket.disconnect();
		socket.off("devices");
		socket.off("rules");
		socket.off("variables");
		socket.off("pages");
		socket.off("groups");
		socket.off("deviceAttributeChanged");

		socket.disconnect();
		socket = null;
	}

	public void emit(String s) {
		try {
			emit(new JSONObject(s));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void emit(JSONObject o) {
		socket.emit("call", o);
	}

	/////////////////////////////////////////////////////////////////////////
	// REST
	/////////////////////////////////////////////////////////////////////////

	private PimaticService rest;

	public PimaticService getRest() {
		if (rest == null) {
			rest = generateRestService(connection);
		}
		return rest;
	}

	public static PimaticService generateRestService(final ConnectionOptions connection) {
		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade request) {
				String credential = Credentials.basic(connection.username, connection.password);
				request.addHeader("Authorization", credential);
			}
		};

		return new RestAdapter.Builder()
			.setEndpoint(connection.getBaseUrl())
			.setRequestInterceptor(requestInterceptor)
			.build()
			.create(PimaticService.class);
	}

}
