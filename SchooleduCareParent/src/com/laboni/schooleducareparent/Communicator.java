package com.laboni.schooleducareparent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laboni.schooleducareparent.data.CommuincationData;
import com.laboni.schooleducareparent.data.TeacherList;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;

public class Communicator extends Activity {
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	ListView lv, teacherChatList,teacherList;
	ImageButton backBtn;
	Spinner sp, spTeacher;
	EditText message;
	String messageReply = null;
	ListAdapter adapter, adapterT;
	Button adminBtn,teacherBtn;
	String type;
	String teacherId ;
	Button send;
	String categery[] = { "Admin", "Teacher" };
	public static ArrayList<String> messages = new ArrayList<String>();
	public static ArrayList<String> teacherNameList = new ArrayList<String>();
	LinearLayout teacherlayout;
	Boolean flag=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/*
		 * LayoutInflater inf =
		 * ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE
		 * )); View userActivity=inf.inflate(R.layout.communicator, null,false);
		 * mDrawerLayout.addView(userActivity,0);
		 */
		setContentView(R.layout.communicator);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		adapter = new ListAdapter(getApplicationContext());
		adapterT = new ListAdapter(getApplicationContext());
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Constant.communicatorData.clear();
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.chatListAdmin);
		teacherList = (ListView) findViewById(R.id.Teacherlist);
		// lv.setAdapter(adapter);
		teacherChatList = (ListView) findViewById(R.id.chatListTeacher);
		// teacherChatList.setAdapter(adapter);
		teacherlayout = (LinearLayout) findViewById(R.id.replyLayout);
	
		teacherList.setOnItemClickListener(new spTeacherListener()); 
		
		
		message = (EditText) findViewById(R.id.editTextMessage);
	
		
		adminBtn=(Button)findViewById(R.id.textAdmin);
		teacherBtn=(Button)findViewById(R.id.textTeacher);
		
		
		adminBtn.setOnClickListener(new AdminListener());
		
		teacherList.setVisibility(View.INVISIBLE);
		lv.setVisibility(View.VISIBLE);

		teacherChatList.setVisibility(View.GONE);
		Constant.communicatorData.clear();
		new getMessageTask().execute();
		flag=true;
		
		teacherBtn.setOnClickListener(new TeacherListener());
		send = (Button) findViewById(R.id.btnSend);
		
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				messageReply = message.getText().toString();
				String type = "Parent";
				String seenFlag="0";
				if (flag==true) {

					if (message.getText().toString().trim().equals("")) {
						Toast.makeText(getBaseContext(),
								"Please enter a Message!", Toast.LENGTH_SHORT)
								.show();

					} else {

						Constant.communicatorData.add(new CommuincationData(
								messageReply, type,seenFlag));
						adapter.notifyDataSetChanged();
						new insertAdminParent().execute();
						message.setText("");

					}
				} else if (flag==false ) {

					if (message.getText().toString().trim().equals("")) {
						Toast.makeText(getBaseContext(),
								"Please enter a Message!", Toast.LENGTH_SHORT)
								.show();

					} else if(teacherId.equals("")){
						Toast.makeText(getBaseContext(),
								"Please select the Teacher from list!", Toast.LENGTH_SHORT)
								.show();
						

					}
					else{
						Constant.communicatorData.add(new CommuincationData(
								messageReply, type,seenFlag));

						adapter.notifyDataSetChanged();

						new insertTeacherStudentCom().execute();
						message.setText("");
					}

				}
			}
		});

		
	}
	
	private class TeacherListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			lv.setVisibility(View.GONE);
			teacherList.setVisibility(View.VISIBLE);
			teacherlayout.setVisibility(View.INVISIBLE);
			teacherChatList.setVisibility(View.VISIBLE);
			Constant.communicatorData.clear();
			new getTeacherListTask().execute();
			flag=false;
				
		}
	
	}
	
	private class AdminListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			teacherList.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.VISIBLE);
			teacherlayout.setVisibility(View.VISIBLE);
			teacherChatList.setVisibility(View.GONE);
			Constant.communicatorData.clear();
			new getMessageTask().execute();
			flag=true;
			
		
			
		}
	
	}


	private class insertTeacherStudentCom extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
		//	int pos = spTeacher.getSelectedItemPosition();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("UserName", Username.toString()));
			params.add(new BasicNameValuePair("reply", messageReply));
			params.add(new BasicNameValuePair("TeacherID", teacherId));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL
					+ "insertTeacherParentCom.php", ServiceHandler.POST,
					params);

			Log.d("Response: ", "> " + jsonStr);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// Toast.makeText(getBaseContext(), "Message sent!",
			// Toast.LENGTH_SHORT).show();

			new getTeacherStudentCom().execute();
		}

	}

	private class spTeacherListener implements  OnItemClickListener {

		

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			teacherlayout.setVisibility(View.VISIBLE);
			teacherId = Constant.teacherList.get(arg2).getTeacherd()
					.toString();
			new getTeacherStudentCom().execute();
			
		
		}

	}

	private class getTeacherStudentCom extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(Communicator.this);
			 * pDialog.setMessage("Please wait...");
			 * pDialog.setCancelable(false); pDialog.show();
			 */

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.communicatorData.clear();
			
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("UserName", Username.toString()));
			params.add(new BasicNameValuePair("TeacherID", teacherId));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL
					+ "retrieveTeacherParentCom.php", ServiceHandler.POST,
					params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("communicator-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String massage = c.getString("Message");
						String fromflag = c.getString("FromFlag");
						String seenflag = c.getString("SeenFlag");
						Constant.communicatorData.add(new CommuincationData(
								massage, fromflag,seenflag));

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
			/*
			 * if (pDialog.isShowing()) pDialog.dismiss();
			 */
			// if (Constant.announcementData.size() > 0) {
			// lv.setAdapter(new ListAdapter(Communicator.this));
			teacherChatList.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			teacherChatList.setSelection(adapter.getCount() - 1);
			// } else {
			// Toast.makeText(getBaseContext(), "There is no announcement!",
			// Toast.LENGTH_SHORT).show();
			// }
		}

	}

	private class getTeacherListTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(Communicator.this);
			 * pDialog.setMessage("Please wait...");
			 * pDialog.setCancelable(false); pDialog.show();
			 */

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			teacherNameList.clear();
			Constant.teacherList.clear();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			// params.add(new BasicNameValuePair("Username",
			// Username.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "teacherList.php",
					ServiceHandler.POST, null);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json.getJSONArray("teacher-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String fName = c.getString("First_Name");
						String lName = c.getString("Last_Name");
						String tId = c.getString("TeacherId");
						String name = fName + " " + lName;
						teacherNameList.add(name);
						Constant.teacherList.add(new TeacherList(tId, fName,
								lName));

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
			/*
			 * if (pDialog.isShowing()) pDialog.dismiss();
			 */
			// if (Constant.announcementData.size() > 0) {
			// lv.setAdapter(new ListAdapter(Communicator.this));
			ArrayAdapter<String> teacherlistadapter = new ArrayAdapter<String>(
					Communicator.this, R.layout.gallery_list_child,R.id.textGalleryName,
					teacherNameList);
			teacherList.setAdapter(teacherlistadapter);
			teacherlistadapter.notifyDataSetChanged();
			teacherList.setSelection(0);
			
			
			//////////////////////////////////
			/*ArrayAdapter<String> teacherlistadapter = new ArrayAdapter<String>(
					Communicator.this, android.R.layout.simple_spinner_item,
					teacherNameList);
			spTeacher.setAdapter(teacherlistadapter);
			teacherlistadapter.notifyDataSetChanged();
			spTeacher.setSelection(0);*/
			/////////////////////////////////////////
			/*
			 * teacherChatList.setAdapter(adapter);
			 * adapter.notifyDataSetChanged();
			 */
			// } else {
			// Toast.makeText(getBaseContext(), "There is no announcement!",
			// Toast.LENGTH_SHORT).show();
			// }
		}

	}

	private class insertAdminParent extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("UserName", Username.toString()));
			params.add(new BasicNameValuePair("reply", messageReply));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "insertAdminParentCom.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// Toast.makeText(getBaseContext(), "Message sent!",
			// Toast.LENGTH_SHORT).show();

			new getMessageTask().execute();
		}

	}

	private class getMessageTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(Communicator.this);
			 * pDialog.setMessage("Please wait...");
			 * pDialog.setCancelable(false); pDialog.show();
			 */

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.communicatorData.clear();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("UserName", Username.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL
					+ "retrieveAdminParentCom.php", ServiceHandler.POST,
					params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("communicator-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String massage = c.getString("Message");
						String fromflag = c.getString("FromFlag");
						String seenflag = c.getString("SeenFlag");
						Constant.communicatorData.add(new CommuincationData(
								massage, fromflag,seenflag));

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
			/*
			 * if (pDialog.isShowing()) pDialog.dismiss();
			 */
			// if (Constant.announcementData.size() > 0) {
			// lv.setAdapter(new ListAdapter(Communicator.this));
			lv.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			lv.setSelection(adapter.getCount() - 1);
			// } else {
			// Toast.makeText(getBaseContext(), "There is no announcement!",
			// Toast.LENGTH_SHORT).show();
			// }
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
			return Constant.communicatorData.size();
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
				v = inf.inflate(R.layout.communicator_list_row, null);

			} else {
				v = convertView;
			}
			LinearLayout mainLayout = (LinearLayout) v
					.findViewById(R.id.mainLayout);
			LinearLayout container = (LinearLayout) v
					.findViewById(R.id.container);

			TextView tv2 = (TextView) v.findViewById(R.id.message);
			TextView tv3 = (TextView) v.findViewById(R.id.seenFlag);
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();

			container.setMinimumWidth(width / 2);

			if (Constant.communicatorData.get(position).getFromflag()
					.toString().equals("Admin")||Constant.communicatorData.get(position).getFromflag()
					.toString().equals("Teacher") ) {

				container.setBackgroundResource(R.drawable.incoming_message_bg);
				mainLayout.setGravity(Gravity.LEFT);

			} else {

				mainLayout.setGravity(Gravity.RIGHT);
				container.setBackgroundResource(R.drawable.outgoing_message_bg);

			}

			tv2.setText(Constant.communicatorData.get(position).getMessage()
					.toString());
			if(Constant.communicatorData.get(position).getSeenflag().toString().equals("0"))
			{
			tv3.setText("Unseen");
			} else{
				tv3.setText("Seen");
			}
			return v;
		}

	}
}