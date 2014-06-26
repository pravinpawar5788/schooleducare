package com.laboni.schooleducareparent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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

import com.laboni.schooleducareparent.data.AssignmentData;
import com.laboni.schooleducareparent.data.StudentData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.FileOpen;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class Homework  extends Activity {
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv;
	ImageButton backBtn;
	Spinner sp;
	ArrayList<String> ass_down_path=new ArrayList<String>();
	ArrayList<String> ch_name=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homework);
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

		lv = (ListView) findViewById(R.id.homeworklist);
		sp=(Spinner)findViewById(R.id.spinner1);
		sp.setOnItemSelectedListener(new spChildListener());
	new getChildListTask().execute();
		
	}
	private class spChildListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			new getHomeworkTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class getHomeworkTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Homework.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.assignmentData.clear();
            int pos=sp.getSelectedItemPosition();
            String classdivId=Constant.childData.get(pos).getS_classdivId().toString();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("classdivId", classdivId.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "retrieveHomework.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("homework-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String ass_sub = c.getString("SubjectName");
						String ass_dec = c.getString("HomeworkDescription");
						assignment_path = c.getString("HomeworkAttachPath");
						String path[] = assignment_path.split("/");

						ass_path = path[path.length - 1];
						String ass_submissiondate = c
								.getString("SubmissionDate");

						Constant.assignmentData.add(new AssignmentData(ass_sub,
								ass_dec, ass_path, ass_submissiondate));
						ass_down_path.add(assignment_path);
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
			pDialog.dismiss();
            if(Constant.assignmentData.size()>0)
            {
            	lv.setAdapter(new ListAdapter(Homework.this));
            }
            else{
            	Toast.makeText(getBaseContext(), "There is no Homework!", Toast.LENGTH_SHORT).show();
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
			return Constant.assignmentData.size();
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
				v = inf.inflate(R.layout.assignment_row, null);

			} else {
				v = convertView;
			}
			TextView tv2 = (TextView) v.findViewById(R.id.textSubject);
			TextView tv3 = (TextView) v.findViewById(R.id.textAssignment);
			filename = (TextView) v.findViewById(R.id.textFile);
			TextView tv5 = (TextView) v.findViewById(R.id.textSubmissionDate);

			tv2.setText(Constant.assignmentData.get(position).getSubject()
					.toString());
			tv3.setText(Constant.assignmentData.get(position)
					.getAssignmentDecription().toString());
			filename.setText(Constant.assignmentData.get(position)
					.getAssignmentPath().toString());
			tv5.setText(Constant.assignmentData.get(position)
					.getAssignmentsubmissionDate().toString());
			filename.setOnClickListener(new downloadListener(position));
			return v;
		}

	}
	
	private class downloadListener implements OnClickListener {
        int pos;
		public downloadListener(int position) {
			// TODO Auto-generated constructor stub
			this.pos=position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Toast.makeText(getBaseContext(), "There is no file to display", Toast.LENGTH_LONG).show();
		   
			
			download_path = ass_down_path.get(pos).replaceAll("\\\\", "/");
			Log.e("Error", "Path" + download_path);
			new DownloadTask(Homework.this)
					.execute(BASEURL+"../../../"+ download_path);
		}
		
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private Context context;

		public DownloadTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// take CPU lock to prevent CPU from going off if the user
			// presses the power button during download
			pDialog = new ProgressDialog(Homework.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... sUrl) {
			InputStream input = null;
			OutputStream output = null;

			HttpURLConnection connection = null;
			try {
				URL url = new URL(sUrl[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP "
							+ connection.getResponseCode() + " "
							+ connection.getResponseMessage();
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				int fileLength = connection.getContentLength();

				// download the file
				input = connection.getInputStream();
				output = new FileOutputStream("/mnt/sdcard/" + ass_path);

				byte data[] = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
						publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// if we get here, length is known, now set indeterminate to false
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {

			pDialog.dismiss();
			if (result != null)
				// Toast.makeText(context,"Download error: ",
				// Toast.LENGTH_LONG).show();
				Log.e("Error",BASEURL+ download_path);
			else
				Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
						.show();
			File myFile = new File("/mnt/sdcard/" + ass_path);
			try {
				FileOpen.openFile(Homework.this, myFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ActivityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				Toast.makeText(getApplicationContext(), "There is no app install to view"+" "+ass_path+" file.", Toast.LENGTH_LONG).show();
			}
		}

	}
	private class getChildListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Homework.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.childData.clear();
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
				sp.setAdapter(new ArrayAdapter<String>(Homework.this,android.R.layout.simple_spinner_item,ch_name));
			}
			
		}

	}
	
}
