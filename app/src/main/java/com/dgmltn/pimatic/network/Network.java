package com.dgmltn.pimatic.network;

import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dgmltn.pimatic.model.DeviceAttributeChange;
import com.dgmltn.pimatic.model.DevicesResponse;
import com.dgmltn.pimatic.model.Group;
import com.dgmltn.pimatic.model.Model;
import com.dgmltn.pimatic.model.PagesResponse;
import com.dgmltn.pimatic.model.RulesResponse;
import com.dgmltn.pimatic.model.VariablesResponse;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.squareup.okhttp.Credentials;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class Network {
	private ConnectionOptions connection;

	public Network(ConnectionOptions connection) {
		this.connection = connection;
		setupWebsocket();
		downloadDevices();
		downloadPages();
		//TODO: downloadRules();
		//TODO: downloadVariables();
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

		socket.on("groups", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String responseString = args[0].toString();
				Timber.i("groups (" + args.length + " args)" + responseString);
				Group[] groups = new Gson().fromJson(responseString, Group[].class);
				Model.getInstance().setGroups(groups);
			}
		});

		socket.on("deviceAttributeChanged", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				String responseString = args[0].toString();
				Timber.i("deviceAttributeChanged (" + args.length + " args)" + responseString);
				DeviceAttributeChange change = new Gson().fromJson(responseString, DeviceAttributeChange.class);
				Model.getInstance().updateDevice(change);
			}
		});

		socket.connect();

	}

	private void teardownWebsocket() {
		if (socket == null) {
			return;
		}

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

	private void downloadDevices() {
		getRest();
		rest.getDevices(new Callback<DevicesResponse>() {
			@Override
			public void success(DevicesResponse devicesResponse, Response response) {
				Timber.i("devices: " + new Gson().toJson(devicesResponse.devices));
				Model.getInstance().setDevices(devicesResponse.devices);
			}

			@Override
			public void failure(RetrofitError error) {
				Timber.e("DownloadDevices failure! " + error.getMessage());
			}
		});
	}

	private void downloadPages() {
		getRest();
		rest.getPages(new Callback<PagesResponse>() {
			@Override
			public void success(PagesResponse pagesResponse, Response response) {
				Timber.i("pages: " + new Gson().toJson(pagesResponse.pages));
				Model.getInstance().setPages(pagesResponse.pages);
			}

			@Override
			public void failure(RetrofitError error) {
				Timber.e("DownloadPages failure! " + error.getMessage());
			}
		});

	}

	private void downloadRules() {
		getRest();
		rest.getRules(new Callback<RulesResponse>() {
			@Override
			public void success(RulesResponse rulesResponse, Response response) {
				Timber.i("rules: " + new Gson().toJson(rulesResponse.rules));
				Model.getInstance().setRules(rulesResponse.rules);
			}

			@Override
			public void failure(RetrofitError error) {
				Timber.e("DownloadRules failure! " + error.getMessage());
			}
		});
	}

	private void downloadVariables() {
		getRest();
		rest.getVariables(new Callback<VariablesResponse>() {
			@Override
			public void success(VariablesResponse variablesResponse, Response response) {
				Timber.i("rules: " + new Gson().toJson(variablesResponse.variables));
				Model.getInstance().setVariables(variablesResponse.variables);
			}

			@Override
			public void failure(RetrofitError error) {
				Timber.e("DownloadRules failure! " + error.getMessage());
			}
		});
	}

}
