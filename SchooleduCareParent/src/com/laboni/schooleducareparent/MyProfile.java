package com.laboni.schooleducareparent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laboni.schooleducareparent.Assignments.ListAdapter;
import com.laboni.schooleducareparent.data.AssignmentData;
import com.laboni.schooleducareparent.data.ParentData;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyProfile extends Activity{
	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv;
	Uri currImageURI;
	String realpath,path;
	String p_Name,p_MobileNo,p_Relation,p_PhotoPath;
	String w_Name,w_MobileNo,w_Relation,w_PhotoPath;
	String m_Name,m_MobileNo,m_Relation,m_PhotoPath;
	ImageButton backBtn;
	TextView p_name,w_name,m_name;
	TextView p_mobileno,w_mobileno,m_mobileno;
	TextView p_relation,w_relation,m_relation;
	Button e_edit,w_edit,m_edit;
	ImageView imageParent,imagewife,imagemaid;
	DisplayImageOptions options;
	EditText e_name,e_mobile,e_relation;
	 int k;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.authorized_persons_info);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		/*p_name=(TextView)findViewById(R.id.textp_name);
		w_name=(TextView)findViewById(R.id.textw_name);
		m_name=(TextView)findViewById(R.id.textm_name);
		p_mobileno=(TextView)findViewById(R.id.textp_mobile);
		w_mobileno=(TextView)findViewById(R.id.textw_mobile);
		m_mobileno=(TextView)findViewById(R.id.textm_mobile);
		p_relation=(TextView)findViewById(R.id.textp_relation);
		w_relation=(TextView)findViewById(R.id.textw_relation);
		m_relation=(TextView)findViewById(R.id.textm_relation);
		imageParent=(ImageView)findViewById(R.id.imageParent);
		imagewife=(ImageView)findViewById(R.id.imagewife);
		imagemaid=(ImageView)findViewById(R.id.imagemaid);*/
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				//Constant.assignmentData.clear();
				finish();
			}
		});

		lv = (ListView) findViewById(R.id.auth_person_list);
		
		
		new getParentDetailsTask().execute();
	}

	
	
	private class getParentDetailsTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MyProfile.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			Constant.parentData.clear();
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("Username", Username.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "viewparentprofile.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("parentDetails-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						 p_Name = c.getString("PhotoName");
						 p_MobileNo = c.getString("PhotoNo");
						 p_Relation = c.getString("PhotoRelation");
						 p_PhotoPath = c.getString("Photo");
						 Constant.parentData.add(new ParentData(p_Name, p_Relation, p_MobileNo, p_PhotoPath));
						
						 w_Name = c.getString("WifePhotoName");
						 w_MobileNo = c.getString("WifePhotoNo");
						 w_Relation = c.getString("WifePhotoRelation");
						 w_PhotoPath = c.getString("WifePhoto");
						
						Constant.parentData.add(new ParentData(w_Name, w_Relation, w_MobileNo, w_PhotoPath));
						 m_Name = c.getString("MaidPhotoname");
						 m_MobileNo = c.getString("MaidPhotoNo");
						 m_Relation = c.getString("MaidPhotoRelation");
						 m_PhotoPath = c.getString("MaidPhoto");
						Constant.parentData.add(new ParentData(m_Name, m_Relation, m_MobileNo, m_PhotoPath));
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
			lv.setAdapter(new ListAdapter(MyProfile.this));
			/*p_name.setText(p_Name);
			p_mobileno.setText(p_MobileNo);
			p_relation.setText(p_Relation);
			w_name.setText(w_Name);
			w_mobileno.setText(w_MobileNo);
			w_relation.setText(w_Relation);
			m_name.setText(m_Name);
			m_mobileno.setText(m_MobileNo);
			m_relation.setText(m_Relation);*/
			
			
			
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
			return Constant.parentData.size();
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
				v = inf.inflate(R.layout.authorized_persons_list_child, null);

			} else {
				v = convertView;
			}
			TextView pName = (TextView) v.findViewById(R.id.textView1);
			TextView pRelation = (TextView) v.findViewById(R.id.textView4);
			TextView pMobile = (TextView) v.findViewById(R.id.textView5);
            ImageView imageView=(ImageView)v.findViewById(R.id.imageuser);
            Button edit=(Button)v.findViewById(R.id.btnEdit);
            
			pName.setText(Constant.parentData.get(position).getP_Name()
					.toString());
			pRelation.setText(Constant.parentData.get(position)
					.getP_Relation().toString());
			
					
			pMobile.setText(Constant.parentData.get(position)
					.getP_MobileNo().toString());
			imageLoader.clearMemoryCache();
			imageLoader.clearDiscCache();
			imageLoader.displayImage(BASEURL+"../"+Constant.parentData.get(position).getP_ImagePath().toString(), imageView, options);
			edit.setOnClickListener(new EditbuttonListener(position));
			
			return v;
		}

	}
	
	private class EditbuttonListener implements OnClickListener{
    int pos;
		public EditbuttonListener(int position) {
			// TODO Auto-generated constructor stub
			pos=position;
		
			
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			k=pos;
			//Toast.makeText(getBaseContext(), ""+pos+"k"+k, Toast.LENGTH_SHORT).show();
			final Dialog dialog2 = new Dialog(MyProfile.this);
			dialog2.setContentView(R.layout.update_parent_details);
			dialog2.setTitle("Update Parent Details");
			Button update = (Button) dialog2.findViewById(R.id.btnupdate);
			 e_name = (EditText) dialog2.findViewById(R.id.editTextname);
			 e_name.setText(Constant.parentData.get(pos).getP_Name().toString());
			 
			 e_mobile = (EditText) dialog2.findViewById(R.id.editTextmobile);
			 e_mobile.setText(Constant.parentData.get(pos).getP_MobileNo().toString());
			 e_relation = (EditText) dialog2.findViewById(R.id.editTextrelation);
			 e_relation.setText(Constant.parentData.get(pos).getP_Relation().toString());
			 filename=(TextView)dialog2.findViewById(R.id.filename);
            Button chooseBtn=(Button)dialog2.findViewById(R.id.btnChoose);
            chooseBtn.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				// TODO Auto-generated method stub
    			
    				
    				Intent intent = new Intent();
    			    intent.setType("image/*");
    			    intent.setAction(Intent.ACTION_GET_CONTENT);
    			    startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);

    				
    				
    				
    			}
    		});
            
			update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				      
					new Updatetask().execute();
					dialog2.dismiss();
				}
			});

			dialog2.show();
			
		}
		
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 

    	if (resultCode == RESULT_OK) {

    		if (requestCode == 1) {

    			// currImageURI is the global variable I'm using to hold the content:// URI of the image
    			currImageURI = data.getData();
    			Log.e("get",currImageURI.toString());
    		    realpath=this.getRealPathFromURI(currImageURI);
    		    String imagepath[]=realpath.split("/");
    		    path=imagepath[imagepath.length-1];
    		    filename.setText(path);
    			
    			
    			
    			Log.e("get",realpath);
    			//iv.setImageURI(currImageURI);
    			
    		
    		}
    	}
    }

	public String getRealPathFromURI(Uri contentUri) {

    	// can post image
    	String [] proj={MediaStore.Images.Media.DATA};
    	Cursor cursor = managedQuery( contentUri,
    			proj, // Which columns to return
    			null,       // WHERE clause; which rows to return (all rows)
    			null,       // WHERE clause selection arguments (none)
    			null); // Order-by clause (ascending by name)
    	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	cursor.moveToFirst();
        Log.e("getit",cursor.getString(column_index));
    	return cursor.getString(column_index);
    }
	
	private class  Updatetask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MyProfile.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String u_name=e_name.getText().toString();
			String u_mobile=e_mobile.getText().toString();
			String u_relation=e_relation.getText().toString();
			String key=String.valueOf(k);
			String prevImagepath=Constant.parentData.get(k).getP_ImagePath().toString();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(6);
			params.add(new BasicNameValuePair("Username", Username.toString()));
			 params.add(new BasicNameValuePair("realpath", path));
			 params.add(new BasicNameValuePair("u_name", u_name));
			 params.add(new BasicNameValuePair("u_mobile", u_mobile));
			 params.add(new BasicNameValuePair("u_relation", u_relation));
			 params.add(new BasicNameValuePair("key", key));
			 params.add(new BasicNameValuePair("prevImagepath", prevImagepath));
			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "update_parent_details.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			new imageuploadtask().execute();
		}

		
	}

	
	private class imageuploadtask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MyProfile.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			//String newrealpath1=n+"/"+n+"."+"jpg";
			String urlServer = BASEURL+"imageupload.php";
			String key=String.valueOf(k);
			RequestParams params1 = new RequestParams();
			
			params1.put("fileName",path);
			params1.put("Username",Username.toString());
			params1.put("key",key);
			// here write your parameter name and its value
			try {
				params1.put("uploadedfile", new File(realpath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Upload a File
			// params.put("profile_picture2", someInputStream); // Upload an
			// InputStream
			// params.put("profile_picture3", new ByteArrayInputStream(someBytes));
			// // Upload some bytes

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(urlServer, params1, new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String arg0) {
			        super.onSuccess(arg0);
			        Log.v("from response", arg0);
			    }
			});
			
			
			
			
			
		
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			Constant.parentData.clear();
			new getParentDetailsTask().execute();
		}
		
	}	
	
	
}
