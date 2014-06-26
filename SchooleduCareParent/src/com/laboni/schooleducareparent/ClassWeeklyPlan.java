package com.laboni.schooleducareparent;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.laboni.schooleducareparent.data.StudentData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class ClassWeeklyPlan extends Activity{
	//private String BASEURL = "http://114.143.188.5/smarteduclass/AndroidDb/Parent/";
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv;
	ImageButton backBtn;
	Spinner sp;
	WebView web;
	ArrayList<String> ch_name=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.class_weekly_plan);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				//Constant.assignmentData.clear();
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.assignmentlist);
		sp=(Spinner)findViewById(R.id.spinnerChild);
		web = (WebView) findViewById(R.id.webView1);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setAllowFileAccess(true);
		web.getSettings().setPluginState(PluginState.ON);
		
		sp.setOnItemSelectedListener(new spChildListener());
		new getChildListTask().execute();
		
	}
	
	
	
	
	
	
	
	
	
	
	private class spChildListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			//new getAssignmentTask().execute();
			 int pos=sp.getSelectedItemPosition();
	            String classdivId=Constant.childData.get(pos).getS_classdivId().toString();
	            web.loadUrl(BASEURL+"archiveClassWeeklyPlan.php"+"?ClassDivId="+classdivId);
	            
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	private class getChildListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ClassWeeklyPlan.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.childData.clear();
			ch_name.clear();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("UserName", Username.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "retrieveChildrens.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("childList-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String c_studentId = c.getString("StudentId");
						String c_classdivId = c.getString("ClassDivId");
						String c_firstName = c.getString("FirstName");
						String c_lastName = c.getString("LastName");
						ch_name.add(c_firstName+" "+c_lastName);
						Constant.childData.add(new StudentData(c_studentId, c_classdivId, c_firstName, c_lastName));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if(Constant.childData.size()>0)
			{
				sp.setAdapter(new ArrayAdapter<String>(ClassWeeklyPlan.this,android.R.layout.simple_spinner_item,ch_name));
			}
			
		}

	}
	

}
