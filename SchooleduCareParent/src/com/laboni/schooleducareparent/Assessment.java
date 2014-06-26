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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laboni.schooleducareparent.data.StudentData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class Assessment extends Activity{
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv;
	ImageButton backBtn;
	Spinner sp;
	
	ArrayList<String> ch_name=new ArrayList<String>();
	ArrayList<String> chName=new ArrayList<String>();
	ArrayList<String> subName=new ArrayList<String>();
	ArrayList<String> t_score=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.assessment);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ch_name.clear();
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.assessmentlist);
		sp=(Spinner)findViewById(R.id.spinnerChild);
		sp.setOnItemSelectedListener(new spChildListener());
		new getChildListTask().execute();
		
	}

	private class spChildListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			new getAssessmentTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class getAssessmentTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Assessment.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			chName.clear();
			subName.clear();
			t_score.clear(); 
            int k=sp.getSelectedItemPosition();
            String classDivId=Constant.childData.get(k).getS_classdivId().toString();
            String studentDivId=Constant.childData.get(k).getS_id().toString();
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("ClassDivId", classDivId.toString()));
			 params.add(new BasicNameValuePair("StudentId", studentDivId.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL+"onlineTopicAssessment.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("Assessment-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						if(i==0)
						{
							JSONArray chapterdetails = c
									.getJSONArray("subjectName");
							for (int k1 = 0; k1 < chapterdetails.length(); k1++) {

								JSONObject c1 = chapterdetails.getJSONObject(k1);
							String subjectName = c1.getString("SubjectName");
							subName.add(subjectName);
							
							
							}
						}

						if(i==1)
						{
							
							JSONArray pathdetails = c
									.getJSONArray("chapterName");
							for (int k1 = 0; k1 < pathdetails.length(); k1++) {

								JSONObject c1 = pathdetails.getJSONObject(k1);
							String chapterName = c1.getString("ChapterName");
							
							chName.add(chapterName);
							
							}
							
							
						}
						if(i==2)
						{
							
							JSONArray pathdetails = c
									.getJSONArray("score");
							for (int k1 = 0; k1 < pathdetails.length(); k1++) {

								JSONObject c1 = pathdetails.getJSONObject(k1);
							String score = c1.getString("Score");
							
						t_score.add(score);
							
							}
							
							
						}

						
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
			if (subName.size() > 0) {
				lv.setAdapter(new ListAdapter(Assessment.this));
			} else {
				Toast.makeText(getBaseContext(), "There is no Assessment!",
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
			return subName.size();
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

			tv2.setText(subName.get(position)
					.toString());
			tv3.setText(chName.get(position)
					.toString());
			tv4.setText(t_score.get(position).toString());
			
            
			return v;
		}

	}

	
	
	private class getChildListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Assessment.this);
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
				sp.setAdapter(new ArrayAdapter<String>(Assessment.this,android.R.layout.simple_spinner_item,ch_name));
			}
			
		}

	}
}
