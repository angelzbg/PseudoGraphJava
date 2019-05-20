package graph;

import java.util.ArrayList;

public class Room {
	
	public String name, type;
	public int x, y, floor;
	
	public ArrayList<Link> links = new ArrayList<>();
	
	public Room(String name, int x, int y, int floor, String type) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.floor = floor;
		this.type = type;
	}

}
