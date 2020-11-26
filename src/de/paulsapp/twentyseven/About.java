package de.paulsapp.twentyseven;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(MainActivity.getAppContext().getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		
		Cursor db =connection.rawQuery("SELECT * FROM database", null);
    	db.moveToFirst();
    	int uniqueid=0;
    	try {
    		uniqueid=db.getInt(db.getColumnIndex("uniqueid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	int dbversion=0;
    	try {
    		dbversion=db.getInt(db.getColumnIndex("version"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	db.close();
		connection.close();
		database.close();
		
		
		
		
		
		TextView version = (TextView)findViewById(R.id.version);
		try {
			version.setText("Version: "+getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName+"\n\nUnique-ID: "+uniqueid+"\n\nDatabase-Version: "+dbversion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
