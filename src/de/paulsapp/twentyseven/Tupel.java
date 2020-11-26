package de.paulsapp.twentyseven;

public class Tupel {
	
	private String abfahrt;
	private String ankunft;
	private String dauer;
	
	
	public String getAbfahrt() {
		return abfahrt;
	}


	public String getAnkunft() {
		return ankunft;
	}



	public String getDauer() {
		return dauer;
	}

	
	
	
	public Tupel(String abfahrt, String ankunft, String dauer) {
		super();
		this.abfahrt = abfahrt;
		this.ankunft = ankunft;
		this.dauer = dauer;
	}
	
	
}
