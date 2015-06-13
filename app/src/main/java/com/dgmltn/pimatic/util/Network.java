package com.dgmltn.pimatic.util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dgmltn.pimatic.model.Device;
import com.dgmltn.pimatic.model.Group;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.Page;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import timber.log.Timber;

public class Network {

	//TODO: make these user configurable
	private static final String USERNAME = "demo";
	private static final String PASSWORD = "demo";
	private static final String SERVER = "demo.pimatic.org";
	private static final int PORT = 8080;

	private Network() {
		// Singleton - Cannot be instantiated
	}

	public static void setup() {
		setupWebsocket();
		setupRest();
	}

	public static void teardown() {
		teardownWebsocket();
		teardownRest();
	}

	/////////////////////////////////////////////////////////////////////////
	// Websocket
	/////////////////////////////////////////////////////////////////////////

	private static Socket sSocket;

	private static void setupWebsocket() {
		if (sSocket == null) {
			try {
				sSocket = IO.socket("http://" + SERVER + ":" + PORT
					+ "/?username=" + USERNAME + "&password=" + PASSWORD);
			}
			catch (URISyntaxException e) {
				Timber.e(e.toString());
				return;
			}
		}

		sSocket.on("devices", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Model.getInstance().devices = JSONUtils.toFromJson((JSONArray) args[0], Device.class);
				Timber.i("devices " + Model.getInstance().devices);
				Events.post(new Events.DevicesChanged());
			}
		});

		sSocket.on("rules", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("rules " + args[0].toString());
			}
		});

		sSocket.on("variables", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("variables " + args[0].toString());
			}
		});

		sSocket.on("pages", new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				Timber.i("pages " + args[0].toString());
				Model.getInstance().pages = JSONUtils.toFromJson((JSONArray) args[0], Page.class);
				Events.post(new Events.PagesChanged());
			}
		});

		sSocket.on("groups", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("groups " + args[0].toString());
				Model.getInstance().groups = JSONUtils.toFromJson((JSONArray) args[0], Group.class);
			}
		});

		sSocket.on("deviceAttributeChanged", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Timber.i("deviceAttributeChanged " + args[0].toString());
			}
		});

		sSocket.connect();

	}

	private static void teardownWebsocket() {
		if (sSocket == null) {
			return;
		}

		sSocket.disconnect();
		sSocket.off("devices");
		sSocket.off("rules");
		sSocket.off("variables");
		sSocket.off("pages");
		sSocket.off("groups");
		sSocket.off("deviceAttributeChanged");
	}

	public static void emit(String s) {
		try {
			emit(new JSONObject(s));
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void emit(JSONObject o) {
		sSocket.emit("call", o);
	}

	/////////////////////////////////////////////////////////////////////////
	// REST
	/////////////////////////////////////////////////////////////////////////

	private static OkHttpClient sHttp;

	private static void setupRest() {
		if (sHttp == null) {
			sHttp = new OkHttpClient();
		}
	}

	private static void teardownRest() {
		// Nothing to do here
	}

	public static void rest(String path) {
		String credential = Credentials.basic(USERNAME, PASSWORD);
		Request request = new Request.Builder()
			.url("http://" + SERVER + ":" + PORT + path)
			.header("Authorization", credential)
			.build();

		sHttp.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if (!response.isSuccessful()) {
					throw new IOException("Unexpected code " + response);
				}

				Headers responseHeaders = response.headers();
				for (int i = 0; i < responseHeaders.size(); i++) {
					System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
				}

				System.out.println(response.body().string());

				// TODO: better response handling
				// example response:
				// {
				// "result": true,
				// "success": true
				// }
			}
		});
	}
}
