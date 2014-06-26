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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.ServiceHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryView extends Activity {

	private String BASEURL = Constant.BASEURL;
	String Username, jsonStr = null;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	ListView lv;
	ImageButton backBtn;
	String galleryName, image_Name = null,classDivId=null;
	Gallery gallery;
	ImageView i;
	Bitmap bimage = null;

	ImageView selectedImage;

	DisplayImageOptions options;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_image_gallery);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		galleryName = b.getString("GalleryName");
		classDivId=b.getString("classDivId");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");
		backBtn = (ImageButton) findViewById(R.id.imageBackbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Constant.ImagePath.clear();
				finish();
			}
		});

		gallery = (Gallery) findViewById(R.id.gallery);

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				startImagePagerActivity(position);
			}
		});

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		new getGalleryImageTask().execute();

	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);

		intent.putExtra("position", position);
		startActivity(intent);
	}

	// //////////////////////////////////////////////////////////////////

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Constant.ImagePath.clear();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constant.ImagePath.clear();
		finish();
	}

	private class ImageGalleryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Constant.ImagePath.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(
						R.layout.item_gallery_image, parent, false);
			}
			// imageLoader.displayImage(imageUrls[position], imageView,
			// options);
			imageLoader.displayImage(
					BASEURL + Constant.ImagePath.get(position), imageView,
					options);
			return imageView;
		}
	}

	// ////////////////////////////////////////////////

	private class getGalleryImageTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(GalleryView.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("GalleryName", galleryName
					.toString()));
			params.add(new BasicNameValuePair("ClassDivId", classDivId.toString()));

			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "retriveImages.php",
					ServiceHandler.POST, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					JSONArray calssdetails = json
							.getJSONArray("galleryImage-data");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);
						String galleryName = c.getString("Photo");

						Constant.ImagePath.add(galleryName);

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

			gallery.setAdapter(new ImageGalleryAdapter());
		}

	}

}
