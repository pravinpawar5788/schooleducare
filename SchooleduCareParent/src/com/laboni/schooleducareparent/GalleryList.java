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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laboni.schooleducareparent.data.StudentData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class GalleryList extends Activity{
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	ListView lv;
	ImageButton backBtn;
	Spinner sp;
	String u_classDivId=null;
	public static ArrayList<String> GalleryName = new ArrayList<String>();
	ArrayList<String> ch_name=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout. gallery);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GalleryName.clear();
				
				finish();
			}
		});

		sp=(Spinner)findViewById(R.id.spinnerChild);
		sp.setOnItemSelectedListener(new spChildListener());
		lv = (ListView) findViewById(R.id.gallerylist);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String galleryname=GalleryName.get(arg2);
				
				Intent i=new Intent(GalleryList.this,GalleryView.class);
				i.putExtra("Username", Username);
				i.putExtra("GalleryName",galleryname);
				i.putExtra("classDivId",u_classDivId);
				
				startActivity(i);
				
			}
			
			
		});

		new getChildListTask().execute();
		
	}
	
	
	private class getChildListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(GalleryList.this);
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
				sp.setAdapter(new ArrayAdapter<String>(GalleryList.this,android.R.layout.simple_spinner_item,ch_name));
			}
			
		}

	}
	
	private class spChildListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			new getGalleyNameTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class getGalleyNameTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(GalleryList.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			GalleryName.clear();
			int k=sp.getSelectedItemPosition();
		    u_classDivId=Constant.childData.get(k).getS_classdivId().toString();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("ClassDivId", u_classDivId.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL+"parentchildclassGallery.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("gallery-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String galleryName = c.getString("GalleryName");
						
						GalleryName.add(galleryName);
						

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
			if (GalleryName.size() > 0) {
			lv.setAdapter(new ArrayAdapter<String>(GalleryList.this,
					android.R.layout.simple_list_item_1, GalleryName));
			} else {
				Toast.makeText(getBaseContext(), "There is no Gallery!",
						Toast.LENGTH_SHORT).show();
			}
			
			
		}

	}


	

	
}
