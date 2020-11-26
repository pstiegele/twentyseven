package de.paulsapp.twentyseven;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class DetailActivity extends Activity {

	int countpositions=0;
	private static final String SCREEN_LABEL = "Detail Screen";
	public List<DetailListview> myList = new ArrayList<DetailListview>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		long uhrzeit3=0;
		Bundle zielkorb = getIntent().getExtras();
		if (getIntent().hasExtra("uhrzeit3")) {
			uhrzeit3=zielkorb.getLong("uhrzeit3");	
		}
		
		MainActivity.getGaTracker().set(Fields.SCREEN_NAME, SCREEN_LABEL);
        MainActivity.getGaTracker().send(MapBuilder.createAppView().build());
		
		
		int id=0;
		zielkorb = getIntent().getExtras();
		if (getIntent().hasExtra("id")) {
			id=zielkorb.getInt("id");	
		}
		
		int[] hstconnection=null;
		zielkorb = getIntent().getExtras();
		if (getIntent().hasExtra("hstconnection")) {
			hstconnection=zielkorb.getIntArray("hstconnection");	
		}
		
		String richtung="";
		zielkorb = getIntent().getExtras();
		if (getIntent().hasExtra("richtung")) {
			richtung=zielkorb.getString("richtung");	
		}
		
		
		
		
		
		Context context=getApplicationContext();
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(context);
		SQLiteDatabase connection = database.getWritableDatabase();
		
		SimpleDateFormat hm = new SimpleDateFormat("HH:mm",Locale.GERMAN);
		
		
		String dc="SELECT * FROM "+richtung+" WHERE id="+id+"";
		Cursor detailcursor = connection.rawQuery(dc, null);
		
		
		String hc="SELECT * FROM haltestellen";
		Cursor haltestellen = connection.rawQuery(hc, null);
		
		haltestellen.moveToPosition(hstconnection[0]);
		
		TextView datum = (TextView)findViewById(R.id.datum);
		SimpleDateFormat langs = new SimpleDateFormat("EEEE, dd.MM.y",Locale.GERMAN);
		datum.setText(langs.format(uhrzeit3));
		
		TextView abfahrt = (TextView)findViewById(R.id.start);
		abfahrt.setText(haltestellen.getString(haltestellen.getColumnIndex("name")));
		
		haltestellen.moveToPosition(hstconnection[1]);
		TextView ziel = (TextView)findViewById(R.id.ziel);
		ziel.setText(haltestellen.getString(haltestellen.getColumnIndex("name")));
		
		
		
		
		Date date=new Date();
		int i=1;
		String az="";
		String hsn="";
		int aktstelle=0;
		if (richtung.equals("schhbf")) {
			detailcursor.moveToFirst();
			haltestellen.moveToPosition(hstconnection[0]);
		do {
			
		
			date.setTime(detailcursor.getInt(detailcursor.getColumnIndex("hst"+(hstconnection[0]+i)+"")));
			az=hm.format(date);
			hsn=haltestellen.getString(haltestellen.getColumnIndex("name"));
		
			if (!(az.equals("01:00"))) {
				countpositions++;
				myList.add(new DetailListview(az,hsn,0));
			}
			
			i++;
			aktstelle=haltestellen.getInt(haltestellen.getColumnIndex("id"));
			if ((haltestellen.getInt(haltestellen.getColumnIndex("id")))!=8) {
				haltestellen.moveToNext();
			}
			
		} while ((hstconnection[1]+1)>aktstelle);
		} else {
			
			detailcursor.moveToFirst();
			haltestellen.moveToPosition(hstconnection[0]);
			
			do {
				
			
				
				date.setTime(detailcursor.getInt(detailcursor.getColumnIndex("hst"+(hstconnection[0]+i)+"")));
				az=hm.format(date);
				hsn=haltestellen.getString(haltestellen.getColumnIndex("name"));
			
				if (!(az.equals("01:00"))) {
					countpositions++;
					myList.add(new DetailListview(az,hsn,0));
				}
				i--;
				
				aktstelle=haltestellen.getInt(haltestellen.getColumnIndex("id"));
				
				if ((haltestellen.getInt(haltestellen.getColumnIndex("id")))!=1) {
					haltestellen.moveToPrevious();
				}
				
				
				
			} while ((hstconnection[1]+1)<aktstelle);
			
			
		}
		
		
		
		ArrayAdapter<DetailListview> adapter = new myDetailListAdapter();
		ListView myListView = (ListView) findViewById(R.id.detail_lview);
		myListView.setAdapter(adapter);
		
		
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
		//getMenuInflater().inflate(R.menu.tupel, menu);
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
			//NavUtils.navigateUpFromSameTask(this);
			
			setResult(RESULT_CANCELED);
            finish();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public class myDetailListAdapter extends ArrayAdapter<DetailListview> {

		  public myDetailListAdapter() {
		    super(DetailActivity.this, R.layout.detail_row_view, myList);
		  }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			View itemView = convertView;
			if (itemView==null) {
				itemView = getLayoutInflater().inflate(R.layout.detail_row_view, parent, false);
			}
			//find the Connection work with.
			DetailListview currentDetail = myList.get(position);
			//Fill the view
			
			TextView abfahrtszeit = (TextView) itemView.findViewById(R.id.listview_abfahrtszeit);
			ImageView verbindungspunkt  = (ImageView) itemView.findViewById(R.id.listview_Haltestellenpunkt);
		    TextView haltestellenname = (TextView) itemView.findViewById(R.id.listview_haltestellenname);
		    
		    
		    if (position==0) {
		    	verbindungspunkt.setImageResource(R.drawable.detailverbindungstart);
		    	abfahrtszeit.setTextAppearance(getBaseContext(), R.style.wichtigeabfahrtszeit);
		    	haltestellenname.setTextAppearance(getBaseContext(), R.style.wichtigehaltestelle);
		    	
			} else {
				
				if (position==(countpositions-1)) {
					haltestellenname.setTextAppearance(getBaseContext(), R.style.wichtigehaltestelle);
					abfahrtszeit.setTextAppearance(getBaseContext(), R.style.wichtigeabfahrtszeit);
					verbindungspunkt.setImageResource(R.drawable.detailverbindungende);
				} else {
					haltestellenname.setTextAppearance(getBaseContext(), R.style.zwischenhaltestelle);
					abfahrtszeit.setTextAppearance(getBaseContext(), R.style.zwischenabfahrtsuhrzeit);
					verbindungspunkt.setImageResource(R.drawable.detailverbindungmitte);
				}
				
			}
		    abfahrtszeit.setText(currentDetail.getAbfahrtszeit());
		    
		    haltestellenname.setText(currentDetail.getHaltestellenname());			
			
			return itemView;
		}
	
	
}
	
	
	
	

}
