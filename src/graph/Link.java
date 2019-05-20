package graph;

public class Link {
	
	public String fromRoomName, toRoomName, type;
	public int cost;
	public boolean isTwoWay;
	
	public Link(String fromRoomName, String toRoomName, String type, int cost, boolean isTwoWay) {
		this.fromRoomName = fromRoomName;
		this.toRoomName = toRoomName;
		this.type = type;
		this.cost = cost;
		this.isTwoWay = isTwoWay;
	}

}
