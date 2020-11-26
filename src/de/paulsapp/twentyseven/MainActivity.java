package de.paulsapp.twentyseven;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class MainActivity extends Activity {
	private RadioGroup rg1;
	private RadioGroup rg2;
	private int[] connection1;
	private Long uhrzeit3;
	String richtung = "";
	static int wochentag;
	TimePicker mytp;
	Button restorebutton;
	Button now;
	int aktuell = 0;
	long aktuellzuletzt;
	public static Cursor haltestellen;
	private static Context context;
	String tag = "";
	long startTime = 0;
//	int orginalrefreshrate = 1000;				Kannste löschen wenn App funktioniert
//	int refreshrate = orginalrefreshrate;
//	int syncedrefreshrate = 60000;
//	Boolean timechangebyrefresher = false;
//	Boolean ersterdurchlauftimerefresher = true;
//	int testcounter = 0;
	long loadtime = 0;
	private static GoogleAnalytics mGa;
	private static Tracker mTracker;
	private static final String GA_PROPERTY_ID = "UA-49038435-1";
	private static final int GA_DISPATCH_PERIOD = 30;
	private static final String SCREEN_LABEL = "Start Screen";
	int uniqueid = 0;

//	Handler timerHandler = new Handler();
//	Runnable timerRunnable = new Runnable() {
//		@Override
//		public void run() {
//
//			timerHandler.postDelayed(this, refreshrate);
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadtime = System.currentTimeMillis();
		setContentView(R.layout.activity_main);
		setTitle("TwentySeven - Linie 27");
		connection1 = new int[2];
		MainActivity.context = getApplicationContext();

		try {

			Einlesen.checkaktuell(context);
		} catch (IOException e) {

			e.printStackTrace();
		}
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();

		Cursor uniqueidcursor = connection.rawQuery(
				"SELECT uniqueid FROM database", null);
		uniqueidcursor.moveToFirst();
		try {
			uniqueid = uniqueidcursor.getInt(uniqueidcursor
					.getColumnIndex("uniqueid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		uniqueidcursor.close();
		connection.close();
		database.close();
		haltestellenermitteln();
		ButtonListener();
		RadioButtonszuweisen();
		LastSearchzuweisen();
		TimePickererzuweisen();

		initializeGa();
		startTime = System.currentTimeMillis();
//		timerHandler.postDelayed(timerRunnable, 0);

		MainActivity.getGaTracker().set(Fields.SCREEN_NAME, SCREEN_LABEL);
		MainActivity.getGaTracker().send(MapBuilder.createAppView().build());

		loadtime = System.currentTimeMillis() - loadtime;
		MainActivity.getGaTracker().send(
				MapBuilder.createTiming("startduration", loadtime,
						"schnellstart", String.valueOf(uniqueid)).build());

	}

	@SuppressWarnings("deprecation")
	private void initializeGa() {
		mGa = GoogleAnalytics.getInstance(this);
		mTracker = mGa.getTracker(GA_PROPERTY_ID);

		// Set dispatch period.
		GAServiceManager.getInstance().setLocalDispatchPeriod(
				GA_DISPATCH_PERIOD);

	}

	public static Tracker getGaTracker() {
		return mTracker;
	}

	/*
	 * Returns the Google Analytics instance.
	 */
	public static GoogleAnalytics getGaInstance() {
		return mGa;
	}

	public void onStart() {
		super.onStart();

		// Send a screen view when the Activity is displayed to the user.
		MainActivity.getGaTracker().send(MapBuilder.createAppView().build());
	}

	public void onResume() {
		super.onResume();

	}

	public void haltestellenermitteln() {
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		haltestellen = connection.rawQuery("SELECT * FROM haltestellen", null);
		if (haltestellen != null) {
			haltestellen.moveToFirst();
		}
		connection.close();
		database.close();

	}

	public static String haltestellen(int id) {
		haltestellen.moveToPosition(id);
		return haltestellen.getString(haltestellen.getColumnIndex("name"));

	}

	public void RadioButtonszuweisen() {
		// Task: Aus DB erst einlesen...
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		Cursor resultpfad = connection.rawQuery(
				"SELECT name FROM haltestellen", null);

		if (resultpfad != null) {
			if (resultpfad.moveToFirst()) {

				rg1 = (RadioGroup) findViewById(R.id.radiogroup1);
				rg2 = (RadioGroup) findViewById(R.id.radiogroup2);
				RadioButton radioButton01 = (RadioButton) findViewById(R.id.RadioButton01);
				radioButton01.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton02 = (RadioButton) findViewById(R.id.RadioButton02);
				radioButton02.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton03 = (RadioButton) findViewById(R.id.RadioButton03);
				radioButton03.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton04 = (RadioButton) findViewById(R.id.RadioButton04);
				radioButton04.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton05 = (RadioButton) findViewById(R.id.radioButton05);
				radioButton05.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton06 = (RadioButton) findViewById(R.id.radioButton06);
				radioButton06.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton07 = (RadioButton) findViewById(R.id.radioButton07);
				radioButton07.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton08 = (RadioButton) findViewById(R.id.radioButton08);
				radioButton08.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToFirst();
				// first radiogroup
				RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
				radioButton1.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
				radioButton2.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
				radioButton3.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
				radioButton4.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton5 = (RadioButton) findViewById(R.id.radioButton5);
				radioButton5.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton6 = (RadioButton) findViewById(R.id.radioButton6);
				radioButton6.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton7 = (RadioButton) findViewById(R.id.radioButton7);
				radioButton7.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
				resultpfad.moveToNext();
				RadioButton radioButton8 = (RadioButton) findViewById(R.id.radioButton8);
				radioButton8.setText(resultpfad.getString(resultpfad
						.getColumnIndex("name")));
			}
		}
		database.close();
		connection.close();
		resultpfad.close();

	}

	public void LastSearchzuweisen() {
		// Task: Aus Datenbank einlesen
		// Task2: Zusammenfassen zu einer Cursor abfrage, und dann immer eins
		// weiter gehen oder so
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();

		Cursor anzahl = connection.rawQuery("SELECT COUNT(*) FROM lastsearch",
				null);
		anzahl.moveToFirst();
		int anzahllastsearch = anzahl.getInt(0);

		if (anzahllastsearch != 0) {

			Cursor lastsearch = connection.rawQuery(
					"SELECT start FROM lastsearch WHERE id="
							+ (anzahllastsearch) + "", null);
			lastsearch.moveToFirst();
			TextView lastsearchstart = (TextView) findViewById(R.id.lastsearchstart);
			lastsearchstart.setText(haltestellen(lastsearch.getInt(lastsearch
					.getColumnIndex("start"))));

			lastsearch = connection.rawQuery(
					"SELECT ende FROM lastsearch WHERE id=" + anzahllastsearch
							+ "", null);
			TextView lastsearchend = (TextView) findViewById(R.id.lastsearchend);
			lastsearch.moveToFirst();
			lastsearchend.setText(haltestellen(lastsearch.getInt(lastsearch
					.getColumnIndex("ende"))));

			lastsearch = connection.rawQuery(
					"SELECT date FROM lastsearch WHERE id=" + anzahllastsearch
							+ "", null);
			lastsearch.moveToFirst();
			TextView lastsearchtime = (TextView) findViewById(R.id.lastsearchtime);
			// Date date=new Date();
			long date2 = lastsearch.getLong(lastsearch.getColumnIndex("date"));
			Date date = new Date();
			date.setTime(date2);
			SimpleDateFormat langs = new SimpleDateFormat(
					"HH:mm' Uhr, 'EEEE, dd.MM.y", Locale.GERMAN);
			lastsearchtime.setText(langs.format(date));
		} else {

			TextView lastsearchstart = (TextView) findViewById(R.id.lastsearchstart);
			lastsearchstart.setText("Start");

			TextView lastsearchend = (TextView) findViewById(R.id.lastsearchend);
			lastsearchend.setText("Ziel");

			TextView lastsearchtime = (TextView) findViewById(R.id.lastsearchtime);
			Date date = new Date();
			SimpleDateFormat langs = new SimpleDateFormat(
					"HH:mm' Uhr, 'EEEE, dd.MM.y", Locale.GERMAN);
			lastsearchtime.setText(langs.format(date));
		}
		connection.close();
		database.close();

	}

	public void TimePickererzuweisen() {
		mytp = (TimePicker) findViewById(R.id.timePicker1);
		mytp.setIs24HourView(true);
		SimpleDateFormat hour = new SimpleDateFormat("HH", Locale.GERMAN);
		SimpleDateFormat minute = new SimpleDateFormat("mm", Locale.GERMAN);
		Date datetp = new Date();
		mytp.setCurrentHour(Integer.parseInt(hour.format(datetp)));
		mytp.setCurrentMinute(Integer.parseInt(minute.format(datetp)));

	}

	public void ButtonListener() {
		restorebutton = (Button) findViewById(R.id.lastsearchrestore);
		restorebutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SQLiteOpenHelper database = new FahrplanDatabaseHelper(context
						.getApplicationContext());
				SQLiteDatabase connection = database.getWritableDatabase();

				int anzahl = 0;
				Cursor anzahlcur = connection.rawQuery(
						"SELECT COUNT(id) FROM lastsearch", null);
				anzahlcur.moveToFirst();
				try {
					anzahl = anzahlcur.getInt(0);
				} catch (Exception e) {

				}
				if (anzahl != 0) {
					Cursor restore = connection.rawQuery(
							"SELECT date FROM lastsearch WHERE id=" + (anzahl)
									+ "", null);
					restore.moveToFirst();
					uhrzeit3 = restore.getLong(restore.getColumnIndex("date"));
					restore = connection.rawQuery(
							"SELECT start FROM lastsearch WHERE id=" + (anzahl)
									+ "", null);
					restore.moveToFirst();
					connection1[0] = restore.getInt(restore
							.getColumnIndex("start"));
					restore = connection.rawQuery(
							"SELECT ende FROM lastsearch WHERE id=" + (anzahl)
									+ "", null);
					restore.moveToFirst();
					connection1[1] = restore.getInt(restore
							.getColumnIndex("ende"));
					fahrplannrzuweisen(connection1);
					MainActivity.getGaTracker().send(
							MapBuilder.createEvent("buttonclick",
									"startrestore", "restorebutton",
									Long.valueOf(uniqueid)).build());
					startresult();
					restore.close();
				}
				anzahlcur.close();
				connection.close();
				database.close();
			}
		});

		Button startsearch = (Button) findViewById(R.id.startsearch);
		// startsearch.setVisibility(4);
		startsearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.getGaTracker()
						.send(MapBuilder.createEvent("buttonclick",
								"startsuche", "searchbutton",
								Long.valueOf(uniqueid)).build());

				getselectedRB();
				LastSearchzuweisen();
			}
		});

		Button bt_before10min = (Button) findViewById(R.id.before10min);
		bt_before10min.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mytp.getCurrentMinute() - 10 < 0) {
					mytp.setCurrentHour(mytp.getCurrentHour() - 1);
					mytp.setCurrentMinute(mytp.getCurrentMinute() - 10);
				} else {
					mytp.setCurrentMinute(mytp.getCurrentMinute() - 10);
				}
			}
		});

		bt_before10min.setLongClickable(true);
		bt_before10min.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				mytp.setCurrentHour(mytp.getCurrentHour() - 1);
				return true;
			}
		});

		Button bt_in30min = (Button) findViewById(R.id.in30min);
		bt_in30min.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mytp.getCurrentMinute() + 30 > 59) {
					mytp.setCurrentHour(mytp.getCurrentHour() + 1);
					mytp.setCurrentMinute(mytp.getCurrentMinute() + 30);
				} else {
					mytp.setCurrentMinute(mytp.getCurrentMinute() + 30);
				}
			}
		});

		bt_in30min.setLongClickable(true);
		bt_in30min.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				mytp.setCurrentHour(mytp.getCurrentHour() + 3);
				return true;
			}
		});

		Button bt_in1hour = (Button) findViewById(R.id.in1hour);
		bt_in1hour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mytp.setCurrentHour(mytp.getCurrentHour() + 1);
			}
		});

		bt_in1hour.setLongClickable(true);
		bt_in1hour.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				SimpleDateFormat hour = new SimpleDateFormat("HH",
						Locale.GERMAN);
				SimpleDateFormat minute = new SimpleDateFormat("mm",
						Locale.GERMAN);
				Date datenow = new Date();
				mytp.setCurrentHour(Integer.parseInt(hour.format(datenow)));
				mytp.setCurrentMinute(Integer.parseInt(minute.format(datenow)));
				return true;
			}
		});

		now = (Button) findViewById(R.id.Now);

		now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SimpleDateFormat hour = new SimpleDateFormat("HH",
						Locale.GERMAN);
				SimpleDateFormat minute = new SimpleDateFormat("mm",
						Locale.GERMAN);
				Date datenow = new Date();
				mytp.setCurrentHour(Integer.parseInt(hour.format(datenow)));
				mytp.setCurrentMinute(Integer.parseInt(minute.format(datenow)));
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "startsuche",
							"actionbar", Long.valueOf(uniqueid)).build());
			getselectedRB();
			LastSearchzuweisen();
			break;

		case R.id.item2: // Verlauf
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "verlauf",
							"actionbar", Long.valueOf(uniqueid)).build());
			Intent verlauf = new Intent(getApplicationContext(), Verlauf.class);
			startActivityForResult(verlauf, 0);
			break;

		case R.id.item3: // Statistik
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "statistik",
							"actionbar", Long.valueOf(uniqueid)).build());
			Intent statistik = new Intent(getApplicationContext(),
					Statistik.class);
			startActivityForResult(statistik, 0);
			break;

		case R.id.item4: // Einstellungen
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "einstellungen",
							"actionbar", Long.valueOf(uniqueid)).build());
			Intent settings = new Intent(getApplicationContext(),
					SettingsActivity.class);
			startActivityForResult(settings, 0);
			break;

		case R.id.item5: // Hilfe
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "hilfe", "actionbar",
							Long.valueOf(uniqueid)).build());
			Intent help = new Intent(getApplicationContext(), Help.class);
			startActivityForResult(help, 0);
			break;

		case R.id.item6: // Über
			MainActivity.getGaTracker().send(
					MapBuilder.createEvent("buttonclick", "ueber", "actionbar",
							Long.valueOf(uniqueid)).build());
			Intent about = new Intent(getApplicationContext(), About.class);
			startActivityForResult(about, 0);
			break;

		}

		return true;
	}

	public void getselectedRB() {
		// get selected radio button from radioGroup
		int selectedId = rg1.getCheckedRadioButtonId();
		int selectedId2 = rg2.getCheckedRadioButtonId();
		if (selectedId == -1 || selectedId2 == -1 || selectedId == selectedId2) {
			if (selectedId == -1 && selectedId2 == -1) {
				toast("Wählen Sie Start- und Zielort.");
			} else {
				if (selectedId == -1) {
					toast("Wählen Sie einen Startort.");
				}
				if (selectedId2 == -1) {
					toast("Wählen Sie einen Zielort.");
				}
				if (selectedId != selectedId2) {
					toast("Wählen Sie zwei verschiedene Orte.");
				}
			}
		} else {
			// radioButtonId wird die Zeile zugeteilt im Fahrplan
			if (selectedId == (findViewById(R.id.radioButton1)).getId()) {
				connection1[0] = 0;
			}
			if (selectedId == (findViewById(R.id.radioButton2)).getId()) {
				connection1[0] = 1;
			}
			if (selectedId == (findViewById(R.id.radioButton3)).getId()) {
				connection1[0] = 2;
			}
			if (selectedId == (findViewById(R.id.radioButton4)).getId()) {
				connection1[0] = 3;
			}
			if (selectedId == (findViewById(R.id.radioButton5)).getId()) {
				connection1[0] = 4;
			}
			if (selectedId == (findViewById(R.id.radioButton6)).getId()) {
				connection1[0] = 5;
			}
			if (selectedId == (findViewById(R.id.radioButton7)).getId()) {
				connection1[0] = 6;
			}
			if (selectedId == (findViewById(R.id.radioButton8)).getId()) {
				connection1[0] = 7;
			}
			// radiogroup2 Zuweisung
			if (selectedId2 == (findViewById(R.id.RadioButton01)).getId()) {
				connection1[1] = 0;
			}
			if (selectedId2 == (findViewById(R.id.RadioButton02)).getId()) {
				connection1[1] = 1;
			}
			if (selectedId2 == (findViewById(R.id.RadioButton03)).getId()) {
				connection1[1] = 2;
			}
			if (selectedId2 == (findViewById(R.id.RadioButton04)).getId()) {
				connection1[1] = 3;
			}
			if (selectedId2 == (findViewById(R.id.radioButton05)).getId()) {
				connection1[1] = 4;
			}
			if (selectedId2 == (findViewById(R.id.radioButton06)).getId()) {
				connection1[1] = 5;
			}
			if (selectedId2 == (findViewById(R.id.radioButton07)).getId()) {
				connection1[1] = 6;
			}
			if (selectedId2 == (findViewById(R.id.radioButton08)).getId()) {
				connection1[1] = 7;
			}
			if (connection1[0] == connection1[1]) {
				toast("Wählen Sie zwei unterschiedliche Haltestellen.");
			} else {
				haltestellen.moveToPosition(connection1[0]);
				MainActivity.getGaTracker().send(
						MapBuilder.createEvent(
								"haltestellen",
								"abfahrt",
								haltestellen.getString(haltestellen
										.getColumnIndex("name")),
								Long.valueOf(uniqueid)).build());
				haltestellen.moveToPosition(connection1[1]);
				MainActivity.getGaTracker().send(
						MapBuilder.createEvent(
								"haltestellen",
								"ankunft",
								haltestellen.getString(haltestellen
										.getColumnIndex("name")),
								Long.valueOf(uniqueid)).build());
				uhrzeit3 = getdatewotimeact(mytp.getCurrentHour(),
						mytp.getCurrentMinute());
				fahrplannrzuweisen(connection1);
				// findConnection(connection1);
				try {
					writerestore(connection1);
				} catch (IOException e) {
					toast(e.getMessage());
				}
				startresult();
			}
		}
	}

	public static long getdatewotimeact(int hour, int minute) {
		Calendar calendar = new GregorianCalendar();
		long calendarmilli = calendar.getTimeInMillis();
		long umrechnen = calendar.get(Calendar.HOUR_OF_DAY);
		umrechnen = umrechnen * (60 * 60 * 1000);
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.MINUTE);
		umrechnen = umrechnen * (60 * 1000);
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.SECOND);
		umrechnen = umrechnen * 1000;
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.MILLISECOND);
		calendarmilli = calendarmilli - umrechnen;
		hour = hour * 60 * 60 * 1000;
		minute = minute * 60 * 1000;
		calendarmilli = calendarmilli + hour + minute;
		calendar.setTimeInMillis(calendarmilli);
		return calendarmilli;
	}

	public void fahrplannrzuweisen(int[] connection) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(uhrzeit3);
		wochentag = calendar.get(Calendar.DAY_OF_WEEK);
		switch (wochentag) {
		case 1: // Sonntag
			tag = "sonntag";
			break;
		case 2: // Montag
			tag = "werktag";
			break;
		case 3: // Dienstag
			tag = "werktag";
			break;
		case 4: // Mittwoch
			tag = "werktag";
			break;
		case 5: // Donnerstag
			tag = "werktag";
			break;
		case 6: // Freitag
			tag = "werktag";
			break;
		case 7: // Samstag
			tag = "samstag";
			break;
		default:
			tag = "werktag";
			break;
		}
		if (connection[0] > connection[1]) {
			richtung = "hbfsch"; // hbf->sch
		} else {
			richtung = "schhbf"; // sch->hbf
		}

	}

	public void writerestore(int[] connection) throws IOException {
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(
				context.getApplicationContext());
		SQLiteDatabase con = database.getWritableDatabase();

		con.execSQL("INSERT INTO lastsearch (start,ende,date) VALUES ("
				+ connection[0] + "," + connection[1] + ",'" + uhrzeit3 + "')");

		//Statistik speichern
		con.execSQL("UPDATE database SET anzahlsuchen = anzahlsuchen+1 WHERE id=1");
		con.execSQL("UPDATE haltestellen SET anzahlstart = anzahlstart+1 WHERE id="+(connection[0]+1)+"");
		con.execSQL("UPDATE haltestellen SET anzahlend = anzahlend+1 WHERE id="+(connection[1]+1)+"");
	}

	public void startresult() {

		long[] searchtime = new long[1];
		searchtime[0] = uhrzeit3;
		Intent mIntent = new Intent(this, ResultActivity.class);
		Bundle korb = new Bundle();
		korb.putIntArray("connection", connection1);
		korb.putLongArray("uhrzeit31", searchtime);
		korb.putString("richtung", richtung);
		mIntent.putExtras(korb);
		startActivity(mIntent);
	}

	public static long getdatewotime(Long uhrzeit) {
		Calendar calendar = new GregorianCalendar();
		long calendarmilli = calendar.getTimeInMillis();
		long umrechnen = calendar.get(Calendar.HOUR_OF_DAY);
		umrechnen = umrechnen * (60 * 60 * 1000);
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.MINUTE);
		umrechnen = umrechnen * (60 * 1000);
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.SECOND);
		umrechnen = umrechnen * 1000;
		calendarmilli = calendarmilli - umrechnen;
		umrechnen = calendar.get(Calendar.MILLISECOND);
		calendarmilli = calendarmilli - umrechnen;
		calendar.setTimeInMillis(calendarmilli);
		// String umwandeln in zahl für add auf calendar.
		int stelleeins = 0;
		int stelledreizwei = 0;
		if (uhrzeit < 10000) {
			int stelledrei = (int) (uhrzeit % 10);
			int stellezwei = (int) ((uhrzeit - stelledrei) % 100) / 10;
			stelleeins = (int) (uhrzeit - (stellezwei * 10) - stelledrei) / 100;
			stelledreizwei = stelledrei + (stellezwei * 10);
			// toast(String.valueOf(stelleeins)+String.valueOf(stelledreizwei));
		}
		long mininmilli = stelledreizwei * 60 * 1000;
		long stundeninmilli = stelleeins * 60 * 60 * 1000;
		calendarmilli = calendarmilli + mininmilli + stundeninmilli;

		return calendarmilli;
	}

	public static void toast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static Context getAppContext() {
		return MainActivity.context;
	}

}