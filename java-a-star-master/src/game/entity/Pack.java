package game.entity;

import game.astar.Map;

public class Pack {
	
	private int x;
	private int y;
	
	private String typ;
	private String palnosc;
	private String wrazliwosc;
	private String odpornosc;
	private String termin;
	private String waga;
	private String wielkosc;
	private String rezultat;
	
	public Pack(int x, int y, String typ, String palnosc, String wrazliwosc, String odpornosc, String termin,String waga,String wielkosc, String rezultat ){
		this.x = x;
		this.y = y;
		this.typ = typ;
		this.palnosc = palnosc;		
		this.wrazliwosc = wrazliwosc;
		this.odpornosc = odpornosc;
		this.termin = termin;
		this.waga = waga;
		this.wielkosc = wielkosc;
		this.rezultat = rezultat;
	}
	
	public String getTyp(){
		return this.typ;
	}
	
	public String getPalnosc(){
		return this.palnosc;
	}
	
	public String getWrazliwosc(){
		return this.wrazliwosc;
	}
	
	public String getOdpornosc(){
		return this.odpornosc;
	}
	
	public String getTermin(){
		return this.termin;
	}
	
	public String getWaga(){
		return this.waga;
	}
	public String getWielkosc(){
		return this.wielkosc;
	}
	public String getRezultat(){
		return this.rezultat;
	}
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public double distanceTo(Pack pack){
		int distance = Map.findPath(this.getX(), this.getY(), pack.getX(), pack.getY()).size();
        //double distance = Math.abs(x-pack.getX()) + Math.abs(y-pack.getY());
        
        return distance;
    }
	
	 
    @Override
    public String toString(){
        return getX()+", "+getY();
    }

}
