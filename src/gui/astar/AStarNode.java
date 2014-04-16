package gui.astar;

import  java.util.*;
import gui.astar.base.Node;
import gui.astar.base.Position;


public class AStarNode implements Node {
	private Position pos; //last position in path ; redundant
	private double distTravelled;
	private double approxTotalDist;
	private List<Position> path; 

	public AStarNode(Position pos){
	    if (pos==null) System.out.println("AStarNode constructor, pos is null?");
	    this.pos = pos;
	}
	public Position getPosition() {
		return pos;		
	}
	
	@Override
	public String toString(){
	    String t = "";
	    for (Position p : path) t = t + p.toString();
	    return "("+ approxTotalDist +","+distTravelled+",("+t+")";		    
	}
	
	public double getDistTravelled() {
		return distTravelled;
	}
	
	public void setDistTravelled(double distTravelled){
		this.distTravelled = distTravelled;
	}
	
	public double getApproxTotalDist() {
		return approxTotalDist;
	}
	
	public void setApproxTotalDist(double newApprox){
		//distCity is the straight line distance to B
		approxTotalDist =  newApprox;
	}
	
	public List<Position> getPath() {
		return path;
	}
	
	public void setPath(List<Position> path) {
	    this.path=path;
	}
	
	public void updatePath(List<Position> x) {
		path = new ArrayList<Position>(x);
		path.add(pos);
	}
	
	@Override
	public void printNode() {
	    String ppos = printPath();
	    System.out.print("("+pos);
	    System.out.format(" %.2f,%.2f,",
			      approxTotalDist, distTravelled);
	    System.out.print(ppos + ")");
	}
	
	public String printPath() {
	    String pp = "(";
	    for (Position p:path) pp = pp + p;
	    return pp+")";
	}

	@Override
	public boolean goalTest(Object p){
	    return pos.equals((Position)p); 
	}	
}