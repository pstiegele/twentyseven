package de.paulsapp.twentyseven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Einlesen {

static long loadtime=0;	
	
	public static void haltestelleneinlesen(Context context) throws IOException{
		//haltestellen.txt einlesen
		String[]haltestellen;
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(context.getAssets().open("haltestellen.txt"), "Cp1252")); //Cp1252 steht für den verwendeten Zeichensatz, da Umlaute sonst nicht dargestellt werden
        reader.readLine();
        String nextLine = reader.readLine();
        	haltestellen = nextLine.split(","); // –> splitten an den Zeichen..
        	//haltestellen.txt einlesen fertig
        	int i=0;
        	SQLiteOpenHelper database = new FahrplanDatabaseHelper(context.getApplicationContext());
    		SQLiteDatabase connection = database.getWritableDatabase();
    		do {
    			connection.execSQL("INSERT INTO haltestellen (name, anzahlstart, anzahlend) values ('"+haltestellen[i]+"',0,0)");
    			i++;
			} while (i<haltestellen.length);
    		
    		connection.close();
    		database.close();
	}	
	
	
	public static void checkaktuell(Context context) throws IOException{
		BufferedReader reader = new BufferedReader(
	            new InputStreamReader(context.getAssets().open("zuletztaktualisiert.txt")));
	        int txtversion=Integer.parseInt(reader.readLine());
	        
	        SQLiteOpenHelper database = new FahrplanDatabaseHelper(context.getApplicationContext());
    		SQLiteDatabase connection = database.getReadableDatabase();
    		
    		Cursor result = connection.rawQuery("SELECT version from database", null);
    		int dbversion=0;
    		if (result != null) {
				if (result.moveToLast()) {
					dbversion = result.getInt(result.getColumnIndex("version"));
				}
			}

    		
	        //test1234
    		if (txtversion>dbversion) { 
				//SQL ist alt->aktualisieren
    			loadtime=System.currentTimeMillis();
    			context.deleteDatabase("fahrplandb");
    			Random ran = new Random();
    			int uniqueid = ran.nextInt((99999999 - 999) + 1) + 999;
    			String s="INSERT INTO database (version, uniqueid, anzahlsuchen) values ("+String.valueOf(txtversion)+", "+uniqueid+",0)";
    			aktualisieresqlite(context,s);
	       
	}
    		
    		connection.close();
    		database.close();
    		}
	
	
	public static  void aktualisieresqlite(Context context, String s) throws IOException {
		SQLiteOpenHelper database = new FahrplanDatabaseHelper(context.getApplicationContext());
		SQLiteDatabase connection = database.getWritableDatabase();
		connection.execSQL(s);
		haltestelleneinlesen(context);
    	
		//FAHRPLANNAMEN EINLESEN
		BufferedReader reader = null;
		reader = new BufferedReader(
                new InputStreamReader(
                    context.getAssets().open("fahrplannamen.txt")));
		reader.readLine();
		String nextLine="";
		nextLine=reader.readLine();
		String[]richtung=new String[3];
		int i=0;
		do {
			i++;
			richtung=nextLine.split("_");
			richtung[2]=richtung[2].substring(0, 10);
			
			if (richtung[2].equals("hbfnachsch")) {
					connection.execSQL("insert into fahrplanpfad (richtung) values (1)"); //true(1)=hbf->sch
					
			} 
			if (richtung[2].equals("schnachhbf")) {
					connection.execSQL("insert into fahrplanpfad (richtung) values (0)"); //false(0)=sch->hbf
					
			}
			
			if (richtung[1].equals("werktag")) {
				connection.execSQL("UPDATE fahrplanpfad SET tag = 0 WHERE id = "+i+""); //0=>werktag
			}
			if (richtung[1].equals("samstags")) {
				connection.execSQL("UPDATE fahrplanpfad SET tag = 1 WHERE id = "+i+""); //1=>samstags
			}
			if (richtung[1].equals("sonntags")) {
				connection.execSQL("UPDATE fahrplanpfad SET tag = 2 WHERE id = "+i+""); //2=>sonntags
			}
			if (richtung[1].equals("feiertags")) {
				connection.execSQL("UPDATE fahrplanpfad SET tag = 3 WHERE id = "+i+""); //3=>feiertags
			}
			
			connection.execSQL("UPDATE fahrplanpfad SET pfad = '"+nextLine+"' WHERE id = "+i+"");
			nextLine=reader.readLine();
		} while (nextLine!=null);
		
				
		//FAHRPLAENE EINLESEN UND IN SQLITE SCHREIBEN
    	int fn=0;
    	s="";
    	BufferedReader reader3=null;
    	String[] laengefahrplan;
    	@SuppressWarnings("unused")
		int anzahlfahrplaene=0;
    	String tag="";
    	Cursor result=connection.rawQuery("SELECT COUNT(id) FROM fahrplanpfad", null);
    	if (result != null) {
    		result.moveToFirst();
    		anzahlfahrplaene=result.getInt(0);
		}
    	
    	
    	Cursor resultpfad =connection.rawQuery("SELECT pfad FROM fahrplanpfad", null);
    	Cursor resultrichtung =connection.rawQuery("SELECT richtung FROM fahrplanpfad", null);
    	Cursor resulttag =connection.rawQuery("SELECT tag FROM fahrplanpfad", null);
    	resultrichtung.moveToFirst();
    	resulttag.moveToFirst();
    	resultpfad.moveToFirst();
    	int j=0;
    	String richt="";
    	//while (fn<anzahlfahrplaene) {	
    	while (fn<2) {	//nur Werktags bis jetzt implementiert
        reader3 = new BufferedReader(
            new InputStreamReader(
                context.getAssets().open(resultpfad.getString(resultpfad.getColumnIndex("pfad")))));
        reader3.readLine();
        reader3.readLine();
        nextLine = reader3.readLine();
      
        if (resultrichtung.getInt(resultrichtung.getColumnIndex("richtung"))==1) {
			richt="hbfsch";
			} else {
				richt="schhbf";
			}
        
        switch (resulttag.getInt(resulttag.getColumnIndex("tag"))) {
		case 0:
			tag="werktag";
			break;

		case 1:
			tag="samstag";
			break;
		case 2:
			tag="sonntag";
			break;
			
		case 3:
			tag="feiertag";
			break;
		}
       
        
        	j=0;
        	i=0;
        	
        	while (nextLine!=null){
        		laengefahrplan=nextLine.split(",");
        		while (j<laengefahrplan.length){ 
        			if (laengefahrplan[j].equals("0000")) {
        				
        				if (i==0) {
    						connection.execSQL("insert into "+richt+" (hst"+(i+1)+", "+tag+") values (NULL,1)");
    						
    					} else {
    						s="UPDATE "+richt+" SET hst"+(i+1)+"=NULL ,"+tag+"=1 WHERE id="+(j+1)+"";
    						connection.execSQL(s); 
    						}
        			} else {
						
        			if (i==0) {
						connection.execSQL("insert into "+richt+" (hst"+(i+1)+","+tag+") values ("+gettimewodate(laengefahrplan[j])+",1)");
						
					} else {
						s="UPDATE "+richt+" SET hst"+(i+1)+"="+gettimewodate(laengefahrplan[j])+", "+tag+"=1 WHERE id="+(j+1)+"";
						connection.execSQL(s);
						}	
        			}
        		j++;
        		
        		}
        		j=0;
        		i++;
        		nextLine=reader3.readLine();
        	}
        	resultrichtung.moveToNext();
        	resultpfad.moveToNext();
        	fn++;
    	}
    	
    
    	result.close();
    	resultpfad.close();
    	connection.close();	
    	database.close();
    	loadtime=System.currentTimeMillis()-loadtime;
//    	MainActivity.getGaTracker().send(MapBuilder.createTiming("starttime", loadtime, "createsqldatabase", null).build());
    	
		}
	
public static long gettimewodate(String uhrzeit){
	long millis=0;
	int stunde=0;
	int minuten=0;
	if (uhrzeit.length()==4) {
		stunde=Integer.parseInt(uhrzeit.substring(0, 2));
		minuten=Integer.parseInt(uhrzeit.substring(2, 4));
	}
	if (uhrzeit.length()==3) {
		stunde=Integer.parseInt(uhrzeit.substring(0, 1));
		minuten=Integer.parseInt(uhrzeit.substring(1, 3));
	}	
	
	
	millis=stunde*3600000;
	millis=millis+minuten*60000;
	millis=millis-3600000; //UTC->MEZ
	
	
	return millis;
}

}
