package de.paulsapp.twentyseven;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class Statistik extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistik);
		// Show the Up button in the action bar.
		setupActionBar();

		
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				getApplicationContext());
		SQLiteDatabase con = database.getWritableDatabase();

		
		Cursor anzahlsuchencursor = con.rawQuery(
				"SELECT anzahlsuchen FROM database WHERE id = 1", null);
		anzahlsuchencursor.moveToFirst();
		int anzahlsuchen = anzahlsuchencursor.getInt(anzahlsuchencursor
				.getColumnIndex("anzahlsuchen"));

		
		Cursor anzahlstartcursor = con.rawQuery(
				"SELECT MAX(anzahlstart), name FROM haltestellen", null);
		anzahlstartcursor.moveToFirst();
		int anzahlstartsuchen = anzahlstartcursor.getInt(0);
		String namestartsuchen = anzahlstartcursor.getString(1);
		
		
		Cursor anzahlendcursor = con.rawQuery(
				"SELECT MAX(anzahlend), name FROM haltestellen", null);
		anzahlendcursor.moveToFirst();
		int anzahlendsuchen = anzahlendcursor.getInt(0);
		String nameendsuchen = anzahlendcursor.getString(1);
		
		
		
		TextView stat = (TextView) findViewById(R.id.statistictv);
		String s = "Anzahl Suchen: " + anzahlsuchen+"\nBeliebteste Starthaltestelle: " +namestartsuchen +" (" +anzahlstartsuchen+"x)\nBeliebteste Endhaltestelle: " +nameendsuchen +" (" +anzahlendsuchen+"x)";     
		stat.setText(s);

		con.close();
		database.close();

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
		getMenuInflater().inflate(R.menu.statistik, menu);
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
