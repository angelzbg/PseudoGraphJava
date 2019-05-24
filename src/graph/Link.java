package graph;

public class Link {
	
	public String fromRoom, toRoom, type;
	public int cost;
	public boolean isTwoWay;
	
	public Link(String fromRoom, String toRoom, String type, int cost, boolean isTwoWay) {
		this.fromRoom = fromRoom;
		this.toRoom = toRoom;
		this.type = type;
		this.cost = cost;
		this.isTwoWay = isTwoWay;
	}

}
