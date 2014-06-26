package com.laboni.schooleducareparent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laboni.schooleducareparent.util.Constant;
import com.laboni.schooleducareparent.util.GMapV2Direction;
import com.laboni.schooleducareparent.util.ServiceHandler;

@SuppressLint("NewApi")
public class LocateBus extends FragmentActivity {
	private String BASEURL = Constant.BASEURL;
	// Google Map
	private GoogleMap googleMap;
	double latitude = 0;
	double longitude = 0;
	Thread t;
	String route = null;
	String busId = "21";
	String routeFrom;
	boolean stopThread = false;
	List<NameValuePair> namevaluePairs = null;
	String result, lat, lng, Username, jsonStr = null;
	JSONArray jArray = null;
	double lastLat = 0;
	double lastLng = 0;
	double endlng = 0;
	double endlat = 0;
	Handler handler;
	MarkerOptions marker;
	GMapV2Direction md;
	Marker m;
	ArrayList<LatLng> directionPoint = new ArrayList<LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.locatemap);
		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		handler = new Handler();
		md = new GMapV2Direction();
		try {
			// Loading map

			initilizeMap();
			getLocation();
			new getRoutetask().execute();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class getRoutetask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			List<NameValuePair> params1 = new ArrayList<NameValuePair>(1);
			params1.add(new BasicNameValuePair("Username", Username));
			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(BASEURL + "getRoute.php",
					ServiceHandler.POST, params1);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					JSONObject json = new JSONObject(jsonStr);

					route = json.getString("route");

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the server");

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			new getLatLongFromAddressTask().execute(route);
		}

	}

	private class getLatLongFromAddressTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... youraddress) {

			youraddress[0] = youraddress[0].replaceAll(" ", "%20");
			String uri = "http://maps.google.com/maps/api/geocode/json?address="
					+ youraddress[0] + "&sensor=false";

			HttpGet httpGet = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());

				endlng = ((JSONArray) jsonObject.get("results"))
						.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("location").getDouble("lng");

				endlat = ((JSONArray) jsonObject.get("results"))
						.getJSONObject(0).getJSONObject("geometry")
						.getJSONObject("location").getDouble("lat");

				Log.d("latitude", "" + endlng);
				Log.d("longitude", "" + endlat);
				Log.d("youraddress", "" + youraddress);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			new GetDirectionsAsyncTask().execute();
		}

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}

			// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

			/*
			 * marker = new MarkerOptions().position( new LatLng(latitude,
			 * longitude)) .title("Bus is Here"); // Changing marker icon
			 * marker.icon(BitmapDescriptorFactory
			 * .fromResource(R.drawable.small_bus));
			 * 
			 * // adding marker googleMap.addMarker(marker);
			 */

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
		getLocation();
	}

	public void getLocation() {

		t = new Thread(new Runnable() {

			public void run() {

				HttpClient hc = new DefaultHttpClient();
				// Toast.makeText(getBaseContext(),"hello",Toast.LENGTH_LONG).show();
				// HttpGet hg = new HttpGet(getBusUrl + "/" + busId +
				// "/location");
				// Log.d("url", getBusUrl + "/" + busId + "/location");

				HttpPost hpost;
				hpost = new HttpPost(BASEURL + "getLatLng.php");
				namevaluePairs = new ArrayList<NameValuePair>(1);
				namevaluePairs
						.add(new BasicNameValuePair("Username", Username));
				// Toast.makeText(getBaseContext(),busId,Toast.LENGTH_LONG).show();
				while (true) {
					if (stopThread)
						break;
					try {

						hpost.setEntity(new UrlEncodedFormEntity(namevaluePairs));
						HttpResponse resp = hc.execute(hpost);

						InputStream inputStream = resp.getEntity().getContent();

						// Toast.makeText(getBaseContext(),resp.toString(),Toast.LENGTH_LONG).show();
						// Log.d("response", resp);

						try {
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(inputStream,
											"iso-8859-1"), 8);
							StringBuilder sb = new StringBuilder();
							String line = null;
							while ((line = reader.readLine()) != null) {
								sb.append(line + "\n");
							}
							inputStream.close();
							result = sb.toString();
							Log.e("show mapp result value", result);
							// Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							Log.e("log_tag",
									"Error converting result " + e.toString());
						}

						try {

							JSONObject json = new JSONObject(result);

							jArray = json.getJSONArray("result");
							Log.w("jsonfunction", jArray.toString());
							JSONObject json_data = null;

							for (int i = 0; i < jArray.length(); i++) {
								json_data = jArray.getJSONObject(i);
								lat = json_data.getString("Lat");
								lng = json_data.getString("Lng");
								Log.e("maplatitude", lat + " " + lng);
							}
						} catch (Exception e) {
							Log.e("log_tag",
									"Error parsing data " + e.toString());
						}

						// Toast.makeText(getBaseContext(),lat,Toast.LENGTH_LONG).show();

						latitude = (double) (Double.parseDouble(lat));

						longitude = (double) (Double.parseDouble(lng));

						// adding marker
						// googleMap.addMarker(marker);

						// Toast.makeText(getBaseContext(),latitude+longitude,Toast.LENGTH_LONG).show();

						Log.e("lattitude", "" + latitude);
						Log.e("logitude", "" + longitude);

						if (lastLat != latitude || lastLng != longitude) {

							lastLat = latitude;

							lastLng = longitude;

							handler.post(new Runnable() {

								public void run() {
									googleMap.clear();
									marker = new MarkerOptions()
											.position(
													new LatLng(latitude,
															longitude))
											.title("Bus is here")
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.small_bus));
									m = googleMap.addMarker(marker);
									googleMap.moveCamera(CameraUpdateFactory
											.newLatLng(new LatLng(latitude,
													longitude)));
									googleMap.animateCamera(CameraUpdateFactory
											.zoomTo(12));
									//new GetDirectionsAsyncTask().execute();
									/*
									 * Document doc = md.getDocument(new
									 * LatLng(18, 75), new LatLng(latitude,
									 * longitude),
									 * GMapV2Direction.MODE_DRIVING);
									 * 
									 * ArrayList<LatLng> directionPoint =
									 * md.getDirection(doc); PolylineOptions
									 * rectLine = new
									 * PolylineOptions().width(3).color(
									 * Color.RED);
									 * 
									 * for (int i = 0; i <
									 * directionPoint.size(); i++) {
									 * rectLine.add(directionPoint.get(i)); }
									 * Polyline polylin =
									 * googleMap.addPolyline(rectLine);
									 */
									// marker.position(new LatLng(latitude,
									// longitude) );

									// googleMap.addMarker(marker);

									// Instantiates a new Polyline object and
									// adds points to define a rectangle
									/*
									 * Polyline line = googleMap.addPolyline(new
									 * PolylineOptions() .add(new LatLng(18.33,
									 * 75.55), new LatLng(latitude, longitude))
									 * .width(25) .color(Color.BLUE)
									 * .geodesic(true));
									 */
								}
							});
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // catch (JSONException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					// }
					try {
						Thread.sleep(5000, 0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	private class GetDirectionsAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Document doc = md.getDocument(new LatLng(latitude, longitude),
					new LatLng(endlat, endlng), GMapV2Direction.MODE_DRIVING);
            
			directionPoint = (md.getDirection(doc));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			PolylineOptions rectLine = new PolylineOptions().width(5).color(
					Color.BLUE);
			for (int i = 0; i < directionPoint.size(); i++) {
				rectLine.add(directionPoint.get(i));
			}
			Polyline polyline = googleMap.addPolyline(rectLine);
		}

	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopThread = true;
	}

}