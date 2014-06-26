package com.laboni.schooleducareparent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.laboni.schooleducareparent.util.Config;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.NetworkUtilities;
import com.laboni.schooleducareparent.util.ServiceHandler;


public class MainActivity extends Activity implements OnClickListener {
	private String BASEURL = Constant.BASEURL;
	
	EditText userName, userPassword;
	Button btnsingIn;
	String regGCMId, jsonStr, uName, uPass;
	Context context;
	String uType;
	GoogleCloudMessaging gcm;
	public static final String REG_ID = "regId";
	static final String TAG = "Register Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		userName = (EditText) findViewById(R.id.editTextUsername);
		userPassword = (EditText) findViewById(R.id.editTextPassword);
		btnsingIn = (Button) findViewById(R.id.btnSignin);
		btnsingIn.setOnClickListener(this);
		regGCMId = registerGCM();

	}

	private String registerGCM() {
		// TODO Auto-generated method stub

		gcm = GoogleCloudMessaging.getInstance(this);
		regGCMId = getRegistrationId(context);
		if (TextUtils.isEmpty(regGCMId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regGCMId);
		} else {
			Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regGCMId,
					Toast.LENGTH_LONG).show();
		}
		return regGCMId;

	}

	private void registerInBackground() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regGCMId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regGCMId);
					msg = "Device registered, registration ID=" + regGCMId;

					storeRegistrationId(context, regGCMId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);

	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		// int appVersion = getAppVersion(context);
		// Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		// editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}

	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		// TODO Auto-generated method stub
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		/*
		 * int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		 * int currentVersion = getAppVersion(context); if (registeredVersion !=
		 * currentVersion) { Log.i(TAG, "App version changed."); return ""; }
		 */
		return registrationId;

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (userName.getText().toString().equals("")) {
			Toast.makeText(getBaseContext(), "Please enter Username!",
					Toast.LENGTH_SHORT).show();

		} else if (userPassword.getText().toString().equals("")) {
			Toast.makeText(getBaseContext(), "Please enter Password!",
					Toast.LENGTH_SHORT).show();
		} else if (NetworkUtilities.isInternet(getBaseContext())) {

			new userLoginTask().execute();

		} else {
			Toast.makeText(getBaseContext(), "No network", Toast.LENGTH_LONG)
					.show();
		}

	}

	private class userLoginTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			uName = userName.getText().toString();
			uPass = userPassword.getText().toString();
			uType = "Parent";

			List<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("userName", uName));
			params.add(new BasicNameValuePair("userPass", uPass));
			params.add(new BasicNameValuePair("userType", uType));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL+"login.php", ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			// mt(uName+uPass+uType);

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (jsonStr.equals("login successful")) {

				new storeRegidTask().execute();

				Intent i = new Intent(MainActivity.this, HomeActivity.class);

				i.putExtra("Username", uName);
				startActivity(i);
				finish();
			} else {

				mt("Incorrect username/password");
			}
		}

	}

	private class storeRegidTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			uType = "Parent";
			uName = userName.getText().toString().trim();
			final SharedPreferences prefs = getSharedPreferences(
					MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
			String registrationId = prefs.getString(REG_ID, "");
			List<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("regGCMId", registrationId));
			params.add(new BasicNameValuePair("userType", uType));
			params.add(new BasicNameValuePair("userName", uName));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL+"registerGCMId.php", ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			// mt(uName+uPass+uType);

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}

	}

	private void mt(String ms) {
		Toast.makeText(getApplicationContext(), ms, Toast.LENGTH_LONG).show();
	}
}
