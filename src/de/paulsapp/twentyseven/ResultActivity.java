package de.paulsapp.twentyseven;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class ResultActivity extends Activity {

	int[]hstconnection;
	Bundle zielkorb;
	long uhrzeit3;
	long[] searchtimearr;
	int choice=0;
	NotificationManager notMan;
	private static Context context;
	String res="";
	String richtung="";
	String suchuhrzeit="";
	Cursor result=null;
	public List<Tupel> myList = new ArrayList<Tupel>();
	private static final String SCREEN_LABEL = "Result Screen";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		setTitle("TwentySeven - Ergebnis:");
		//importieren aus anderer klasse
		ResultActivity.context = getApplicationContext();
		Bundle zielkorb = getIntent().getExtras();
		if (getIntent().hasExtra("connection")) {
			hstconnection=zielkorb.getIntArray("connection");	
		}
		if (getIntent().hasExtra("uhrzeit31")) {
			searchtimearr=zielkorb.getLongArray("uhrzeit31");
			uhrzeit3=searchtimearr[0];
		}
		if (getIntent().hasExtra("richtung")) {
			richtung=zielkorb.getString("richtung");
		}
		
		MainActivity.getGaTracker().set(Fields.SCREEN_NAME, SCREEN_LABEL);
        MainActivity.getGaTracker().send(MapBuilder.createAppView().build());
        
        
		notMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//ende import
		//beschriftungen anpassen
		TextView resultstart=(TextView)findViewById(R.id.resultstart);
		resultstart.setText(MainActivity.haltestellen(hstconnection[0]));
		TextView resultend=(TextView)findViewById(R.id.resultend);
		resultend.setText(MainActivity.haltestellen(hstconnection[1]));
		Date date0=new Date();
		date0.setTime(uhrzeit3);
		SimpleDateFormat langs = new SimpleDateFormat("HH:mm' Uhr, 'EEEE, dd.MM.y",Locale.GERMAN);
		TextView resultsearchtime=(TextView)findViewById(R.id.resultsearchtime);
		resultsearchtime.setText(langs.format(date0));
		//beschriftungen ende
		
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		
		SimpleDateFormat hm = new SimpleDateFormat("HH:mm",Locale.GERMAN);
		
		
		
		Date date1=new Date();
		Date date2=new Date();
		int i=0;
		int diff=0;
		//Verbindungen finden
		String start="";
		String ende="";
		String dauer="";
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date0);
		suchuhrzeit=zweistellig(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
		suchuhrzeit=suchuhrzeit+zweistellig(String.valueOf(calendar.get(Calendar.MINUTE)));
		res="SELECT * FROM "+richtung+" WHERE hst"+(hstconnection[0]+1)+">"+Einlesen.gettimewodate(suchuhrzeit)+" AND hst"+(hstconnection[0]+1)+"<"+(Long.valueOf(Einlesen.gettimewodate(suchuhrzeit))+16000000)+" AND hst"+(hstconnection[1]+1)+" IS NOT NULL";
		String resanz="SELECT COUNT(id) FROM "+richtung+" WHERE hst"+(hstconnection[0]+1)+">"+Einlesen.gettimewodate(suchuhrzeit)+" AND hst"+(hstconnection[0]+1)+"<"+(Long.valueOf(Einlesen.gettimewodate(suchuhrzeit))+16000000)+" AND hst"+(hstconnection[1]+1)+" IS NOT NULL";
		result =connection.rawQuery(res, null); 
		Cursor resultanzahl =connection.rawQuery(resanz,null);
		if (result!=null) {
			result.moveToFirst();
		} else {
		}
		int anzahl;
		if (resultanzahl!=null) {
			resultanzahl.moveToFirst();
			anzahl=resultanzahl.getInt(0);
		} else {
			anzahl=-1;
		}
		
		
	//	meineListe.add(hm.format(result.getInt(result.getColumnIndex("hst"+connection[0]+1+""))))+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+hm.format(date2)+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+diffstr);
	//	myList.add((List) new de.paulsapp.twentyseven.List(hm.format(result.getInt(result.getColumnIndex("hst"+connection[0]+1+"")))), hm.format(date2), diffstr));
		while (i<anzahl) {	//solange i kleiner als anzahl an gefundenen Verbindungen
			date1.setTime(result.getInt(result.getColumnIndex("hst"+(hstconnection[0]+1))));
			start=hm.format(date1);
			date2.setTime(result.getInt(result.getColumnIndex("hst"+(hstconnection[1]+1))));
			ende=hm.format(date2);
			diff=result.getInt(result.getColumnIndex("hst"+(hstconnection[1]+1)))-result.getInt(result.getColumnIndex("hst"+(hstconnection[0]+1)));
			dauer=getdiff(diff);
			
			
			//meineListe.add(""+start+"\t\t\t\t\t\t\t\t\t\t\t\t"+ende+"\t\t\t\t\t\t\t\t\t\t\t\t"+dauer+"");
			myList.add(new Tupel(start, ende, dauer));
			i++;
			result.moveToNext();
		}
		if (i==0) {
			myList.add(new Tupel("Keine Verbindungen gefunden", "", ""));
		}
		
		resultanzahl.close();
		connection.close();
		database.close();
		//ListAdapter listenAdapter = new ArrayAdapter<String>(this, R.layout.custom_row_view, meineListe);
		// Assign adapter to ListView
		
		ArrayAdapter<Tupel> adapter = new myListAdapter();
		ListView myListView = (ListView) findViewById(R.id.listView1);
		myListView.setAdapter(adapter);
		
		
		
		//meineListView.setAdapter(listenAdapter); 
		myListView.setOnItemClickListener( new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    	 
		    	
		    	result.moveToPosition(arg2);
		    	int id=result.getInt(result.getColumnIndex("id"));
		    	//Starte DetailActivity
		    	Intent detailintent = new Intent(getApplicationContext(), DetailActivity.class);
		    	Bundle korb = new Bundle();
				korb.putInt("id", id);
				korb.putString("richtung", richtung);
				korb.putLong("uhrzeit3",uhrzeit3);
				korb.putIntArray("hstconnection", hstconnection);
				detailintent.putExtras(korb);
		    	startActivityForResult(detailintent, 0);
		    }
		    });
		
		myListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {
                // TODO Auto-generated method stub

				choice=pos;
		        showNotification("Beschte App überhaupt",false,0);

                return true;
            }
			
			
		});

	}

	
	
	
	public String zweistellig(String valueOf) {
		if (valueOf.length()==1) {
			valueOf="0"+valueOf;
		}
		
		return valueOf;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_result, menu);
		return true;
	}
	
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
			
			//setResult(RESULT_CANCELED);
            finish();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void showNotification(String text, boolean ongoing, int id){
		
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		
		Cursor wahl =connection.rawQuery("SELECT * FROM "+richtung+" WHERE hst"+(hstconnection[0]+1)+">"+Einlesen.gettimewodate(suchuhrzeit)+" AND hst"+(hstconnection[0]+1)+"<"+(Long.valueOf(Einlesen.gettimewodate(suchuhrzeit))+16000000)+"", null);
		wahl.moveToFirst();
		
		
		SimpleDateFormat hm = new SimpleDateFormat("HH:mm",Locale.GERMAN);
		Date date1=new Date();
		Date date2=new Date();
		wahl.moveToPosition(choice);
		date1.setTime(wahl.getLong(wahl.getColumnIndex("hst"+(hstconnection[0]+1)+"")));
		date2.setTime(wahl.getLong(wahl.getColumnIndex("hst"+(hstconnection[1]+1)+"")));
		PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    this,
			    0,
			    getIntent(),
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
		Notification notification = new Notification.Builder(this)      
		.setContentTitle("Ab: "+hm.format(date1)+" - "+MainActivity.haltestellen(hstconnection[0]))      
		.setContentText("An: "+hm.format(date2)+" - "+MainActivity.haltestellen(hstconnection[1]))    
		.setSmallIcon(R.drawable.ic_launcher)  
		.setLights(0xff0000ff, 300,1000)
		.setContentIntent(resultPendingIntent)
		.build();
				notMan.notify(id, notification);
	}
	public String getdiff(long diff){
		String diffstr;
		String zeit;
		diff=diff/(1000*60);
		if (diff<60) {
			if (diff<10) {
				zeit="0"+String.valueOf(diff);
			}else {
				zeit=String.valueOf(diff);
			}
			diffstr="00:"+zeit;
		} else {
			long diffs=diff;
			diff=diff/60;
			int h=(int)diff%10;
			diff=diffs-(60*h);
			if (diff<10) {
				zeit="0"+String.valueOf(diff);
			}else {
				zeit=String.valueOf(diff);
			}
			if (h<10) {
				diffstr="0"+String.valueOf(h)+":"+zeit;
			}else {
				diffstr=String.valueOf(h)+":"+zeit;	
			}
			
		}
		return diffstr;
	}
	
	public class myListAdapter extends ArrayAdapter<Tupel> {

		  public myListAdapter() {
		    super(ResultActivity.this, R.layout.custom_row_view, myList);
		  }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			View itemView = convertView;
			if (itemView==null) {
				itemView = getLayoutInflater().inflate(R.layout.custom_row_view, parent, false);
			}
			//find the Connection work with.
			Tupel currentTupel = myList.get(position);
			//Fill the view
			TextView abfahrt = (TextView) itemView.findViewById(R.id.item_abfahrt);
		    TextView ankunft = (TextView) itemView.findViewById(R.id.item_ankunft);
		    TextView dauer = (TextView) itemView.findViewById(R.id.item_dauer);
		    abfahrt.setText(currentTupel.getAbfahrt());
		    ankunft.setText(currentTupel.getAnkunft());
		    dauer.setText(currentTupel.getDauer());
			
			
			return itemView;
		}
	
	
}
}
