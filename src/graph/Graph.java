package graph;

import java.util.HashMap;

public class Graph {

	public HashMap<String, Room> myGraph = new HashMap<>();
	
	public String addRoom(Room room) {
		if(room == null || myGraph.containsKey(room.name)) {
			return "! Room with name " + room.name + " already exists.";
		}
		myGraph.put(room.name, room);
		return null;
	}
	
	public String addLink(String fromRoomName, String toRoomName, String type, int cost, boolean isTwoWay) {
		if(myGraph.containsKey(fromRoomName) && myGraph.containsKey(toRoomName)) {
			Room startRoom = myGraph.get(fromRoomName), endRoom = myGraph.get(toRoomName);
			startRoom.links.add(new Link(fromRoomName, toRoomName, type, cost, isTwoWay));
			if(isTwoWay) {
				endRoom.links.add(new Link(toRoomName, fromRoomName, type, cost, isTwoWay));
			}
		} else {
			if(!myGraph.containsKey(fromRoomName)) {
				return "Room with name " + fromRoomName + " doesn't exist.";
			}
			else {
				return "Room with name " + toRoomName + " doesn't exist.";
			}
		}
		return null;
	}
	
	public Room getRoom(String name) {
		return myGraph.get(name);
	}
	
	public boolean containsRoom(String name) {
		return myGraph.containsKey(name);
	}
	
}