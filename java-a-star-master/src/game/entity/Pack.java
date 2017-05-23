package game.entity;



public class Pack {
	
	private int x;
	private int y;
	
	private String type;
	private String size;
	private String time;
	
	public Pack(int x, int y, String type, String size, String time){
		this.x = x;
		this.y = y;
		this.type = type;
		this.size = size;		
		this.time = time;
	}
	
	public String getSize(){
		return this.size;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getTime(){
		return this.time;
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

}
