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
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laboni.schooleducareparent.data.AnnouncementData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class Announcement extends Activity {
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv1,lv2;
	ImageButton backBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.announcement);
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

		lv1 = (ListView) findViewById(R.id.announcementlist1);
		lv2 = (ListView) findViewById(R.id.announcementlist2);
		new getAnnouncementTask().execute();
	}
	private class getAnnouncementTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Announcement.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.announcementData.clear();
			Constant.announcementData1.clear();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("Username", Username.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL+"retriveAnnouncement.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("announcement-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						////////////////
						if (i == 1) {
							JSONArray calssdetailsArray = c
									.getJSONArray("announcementUnSeenData");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								String subject = c1.getString("Subject");
								String announcement = c1.getString("Announcement");

								String date = c1.getString("Date");

								Constant.announcementData.add(new AnnouncementData(
										subject, announcement, date));
								
							
							}
						}
						////////////////
						
						if (i == 0) {
							JSONArray calssdetailsArray = c
									.getJSONArray("announcementSeenData");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								String subject = c1.getString("Subject");
								String announcement = c1.getString("Announcement");

								String date = c1.getString("Date");

								Constant.announcementData1.add(new AnnouncementData(
										subject, announcement, date));
								
							
							}
						}
						
						
						///////////////////

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
			if (Constant.announcementData.size() > 0 ||Constant.announcementData1.size() > 0) {
				lv1.setAdapter(new ListAdapter(Announcement.this));
				lv2.setAdapter(new ListAdapter1(Announcement.this));
			} else {
				Toast.makeText(getBaseContext(), "There is no announcement!",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	class ListAdapter extends BaseAdapter {

		// ImageView ib2;

		Context context;

		public ListAdapter(Context con) {
			context = con;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Constant.announcementData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			// pos=position;

			if (convertView == null) {

				LayoutInflater inf = getLayoutInflater();
				v = inf.inflate(R.layout.announcement_row, null);

			} else {
				v = convertView;
			}
			TextView tv2 = (TextView) v.findViewById(R.id.textSubject);
			TextView tv3 = (TextView) v.findViewById(R.id.textAnnouncement);
			TextView tv4 = (TextView) v.findViewById(R.id.textDate);

			tv2.setText(Constant.announcementData.get(position).getSubject()
					.toString());
			tv3.setText(Constant.announcementData.get(position)
					.getAnnouncement().toString());
			tv4.setText(Constant.announcementData.get(position).getDate()
					.toString());

			return v;
		}

	}

	class ListAdapter1 extends BaseAdapter {

		// ImageView ib2;

		Context context;

		public ListAdapter1(Context con) {
			context = con;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Constant.announcementData1.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v;
			// pos=position;

			if (convertView == null) {

				LayoutInflater inf = getLayoutInflater();
				v = inf.inflate(R.layout.announcement_row, null);

			} else {
				v = convertView;
			}
			TextView tv2 = (TextView) v.findViewById(R.id.textSubject);
			TextView tv3 = (TextView) v.findViewById(R.id.textAnnouncement);
			TextView tv4 = (TextView) v.findViewById(R.id.textDate);

			tv2.setText(Constant.announcementData1.get(position).getSubject()
					.toString());
			tv3.setText(Constant.announcementData1.get(position)
					.getAnnouncement().toString());
			tv4.setText(Constant.announcementData1.get(position).getDate()
					.toString());

			return v;
		}

	}
}
