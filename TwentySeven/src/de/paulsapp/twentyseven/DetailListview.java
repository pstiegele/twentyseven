package de.paulsapp.twentyseven;

public class DetailListview {
	
		
		private String abfahrtszeit;
		private String haltestellenname;
		private int verbindungspunkt;
		
		
		
		
		
		
		






		public int getVerbindungspunkt() {
			return verbindungspunkt;
		}






		public String getAbfahrtszeit() {
			return abfahrtszeit;
		}






		public String getHaltestellenname() {
			return haltestellenname;
		}






		public DetailListview(String abfahrtszeit, String haltestellenname,int verbindungspunkt) {
			super();
			this.abfahrtszeit = abfahrtszeit;
			this.haltestellenname = haltestellenname;
			this.verbindungspunkt = verbindungspunkt;
		}






		
		
		
	}
