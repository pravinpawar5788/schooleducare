package com.laboni.schooleducareparent;



import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.laboni.schooleducareparent.data.CustomGridViewAdapter;
import com.laboni.schooleducareparent.data.Item;

public class HomeActivity extends Activity {
	TextView usernameTxt;
	GridView grid;
	ArrayList<Item> gridArray = new ArrayList<Item>();
	CustomGridViewAdapter customGridAdapter;
	Button logoutbtn;
	String Username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_after_login);

		Bundle b = getIntent().getExtras();
		Username = b.getString("Username");
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + Username + "]");

		logoutbtn = (Button) findViewById(R.id.buttonLogout);
		logoutbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent i = new Intent(HomeActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});
		Bitmap profile = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.profile);
		Bitmap Assignments = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.assignment1);
		Bitmap Communicator = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.communicator);
		Bitmap Homework = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.homework);
		Bitmap Announcements = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.announcement);
		Bitmap Bus = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bus);
		Bitmap Gallery = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.gallery);
		Bitmap Courses = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.courses);
		Bitmap Homework_Help = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.help);
		Bitmap Online_Assessment = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.assessment);
		Bitmap ClassWeeklyPlan = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.plan);
		
		gridArray.add(new Item(profile, "Authorized Persons Info"));
		gridArray.add(new Item(Assignments, "Assignments"));
		gridArray.add(new Item(Homework, "Homework"));
		gridArray.add(new Item(Communicator, "Communicator"));
		gridArray.add(new Item(Announcements, "Announcements"));
		gridArray.add(new Item(Bus, "Bus Details"));
		gridArray.add(new Item(Gallery, "Gallery"));
		

		gridArray.add(new Item(Online_Assessment, "Online Assessment"));
		gridArray.add(new Item(ClassWeeklyPlan, "Class Weekly Plan"));
		gridArray.add(new Item(Bus, "LocateBus"));
		grid = (GridView) findViewById(R.id.gridView1);
		customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid,
				gridArray);
		grid.setAdapter(customGridAdapter);

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub transaction

				Animator anim = AnimatorInflater.loadAnimator(HomeActivity.this, R.animator.flip_on_vertical);
				  anim.setTarget(arg1);
				  anim.start();
				
				Intent intent;

				switch (position) {

				case 0:
					intent = new Intent(getApplicationContext(),
							MyProfile.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
				case 1:

					intent = new Intent(getApplicationContext(),
							Assignments.class);
					intent.putExtra("Username", Username);
					startActivity(intent);

					break;
				
				case 2:
					intent = new Intent(getApplicationContext(), Homework.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(getApplicationContext(),
							Communicator.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;

				
				case 4:
					intent = new Intent(getApplicationContext(),
							Announcement.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;

					
				case 5:
					intent = new Intent(getApplicationContext(),
							Bus_details.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
					
				case 6:
					intent = new Intent(getApplicationContext(),
							GalleryList.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
					/*
				case 7:
					intent = new Intent(getApplicationContext(), Courses.class);
					intent.putExtra("Username", Username);
					intent.putExtra("type", "Courses");
					startActivity(intent);
					break;
				case 8:
					intent = new Intent(getApplicationContext(), Courses.class);
					intent.putExtra("Username", Username);
					intent.putExtra("type", "Homework Help");
					startActivity(intent);
					break;

				case 9:
					intent = new Intent(getApplicationContext(),Assessment.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
*/
				case 7:
					intent = new Intent(getApplicationContext(),Assessment.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
					
				case 8:
					intent = new Intent(getApplicationContext(),ClassWeeklyPlan.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
					
					
				case 9:
					intent = new Intent(getApplicationContext(),LocateBus.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;	
				default:
					break;
				}
			}
		});

	}

}
