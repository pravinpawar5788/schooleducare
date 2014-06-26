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



public class Bus_details extends Activity{
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	ListView lv;
	ImageButton backBtn;
	Spinner sp;
	String boardingpoints, attendentname, drivername, routefrom, busno,pickTime,dropTime;
	public static ArrayList<String> busNo = new ArrayList<String>();
	public static ArrayList<String> driverName = new ArrayList<String>();
	public static ArrayList<String> attenentData = new ArrayList<String>();
	public static ArrayList<String> Route = new ArrayList<String>();
	public static ArrayList<String> Pick_time = new ArrayList<String>();
	public static ArrayList<String> BoardingPoints = new ArrayList<String>();
	public static ArrayList<String> drop_time = new ArrayList<String>();
	public static ArrayList<String> student_name = new ArrayList<String>();
	ArrayList<String> ch_name=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bus_details);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.buslist);
		sp=(Spinner)findViewById(R.id.spinnerChild);
		sp.setOnItemSelectedListener(new spChildListener());
		new getChildListTask().execute();
		
	}

	
	private class getChildListTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Bus_details.this);
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
				sp.setAdapter(new ArrayAdapter<String>(Bus_details.this,android.R.layout.simple_spinner_item,ch_name));
			}
			
		}

	}
	
	
	
	private class spChildListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			new getBusDetailsTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	private class getBusDetailsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(Bus_details.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			busNo.clear();
			driverName.clear();
			attenentData.clear();
			Route.clear();
			BoardingPoints.clear();
			Pick_time.clear();
			drop_time.clear();
            int p=sp.getSelectedItemPosition();
            String s_id=Constant.childData.get(p).getS_id().toString();
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("Username", Username.toString()));
			params.add(new BasicNameValuePair("StudentId", s_id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "retrieveBusDetails.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json.getJSONArray("bus-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);

						if (i == 0) {
							JSONArray calssdetailsArray = c
									.getJSONArray("busdetails");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								busno = c1.getString("BusNo");
								routefrom = c1.getString("RouteFrom");
							
							
								busNo.add(busno);
								Route.add(routefrom);
							}
						}

						if (i == 1) {
							JSONArray calssdetailsArray = c
									.getJSONArray("driverdata");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								drivername = c1.getString("FirstName") + " "
										+ c1.getString("LastName");
								driverName.add(drivername);
							}
						}
						if (i == 2) {
							JSONArray calssdetailsArray = c
									.getJSONArray("attendentdata");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								attendentname = c1.getString("FirstName") + " "
										+ c1.getString("LastName");
								attenentData.add(attendentname);
							}
						}

						if (i == 3) {
							JSONArray calssdetailsArray = c
									.getJSONArray("boardingpoints");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);

								boardingpoints = c1.getString("BoardingPoints");
								pickTime = c1.getString("PickTime");
								dropTime = c1.getString("DropTime");
                                BoardingPoints.add(boardingpoints);
                                Pick_time.add(pickTime);
                                drop_time.add(dropTime);
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
			if (busNo.size()> 0 && driverName.size()>0 && attenentData.size()>0) {
				lv.setAdapter(new ListAdapter(Bus_details.this));
			} else {
				Toast.makeText(getBaseContext(), "There is no Bus Details Available!",
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
			return busNo.size();
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
				v = inf.inflate(R.layout.bus_details_row, null);

			} else {
				v = convertView;
			}
			TextView tv2 = (TextView) v.findViewById(R.id.textBusno);
			TextView tv3 = (TextView) v.findViewById(R.id.textDrivername);
			TextView tv4 = (TextView) v.findViewById(R.id.textAttendentname);
			TextView tv5 = (TextView) v.findViewById(R.id.textRoutefrom);
			TextView tv6 = (TextView) v.findViewById(R.id.textBoardingpoint);
			TextView tv7 = (TextView) v.findViewById(R.id.textPickTime);
			TextView tv8 = (TextView) v.findViewById(R.id.textDropTime);
			

			tv2.setText(busNo.get(position).toString());
			tv3.setText(driverName.get(position)
					.toString());
			tv4.setText(attenentData.get(position)
					.toString());
			tv5.setText(Route.get(position).toString());
			tv6.setText(BoardingPoints.get(position)
					.toString());
			tv7.setText(Pick_time.get(position).toString());
			tv8.setText(drop_time.get(position).toString());
			
			return v;
		}

	}

}
