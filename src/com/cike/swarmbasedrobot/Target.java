package com.cike.swarmbasedrobot;

import java.util.HashMap;
import java.util.Random;

public class Target {
	private int Id;
	private int K = 1;;
	private int mass;
	private HashMap<String, Integer> location = new HashMap<>();
	private int x;
	private int y;
    private Random rd = new Random();
    
	public Target() {
		this.mass = 10;
		this.x = 1;
		this.y = 1;
	}

	public Target(int x, int y) {
		this.mass = 10 + rd.nextInt(40);
		this.setLocation(x, y);
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setLocation(int x,int y){
		this.location = new HashMap<>();
		this.location.put("x", x);
		this.location.put("y", y);
		this.x = x;
		this.y = y;
	}
	
	public HashMap<String, Integer> getLocation() {
		return location;
	}

	public void setId(int id){
		this.Id = id;
	}
	public int getId(){
		return this.Id;
	}
	public int remove(){
		this.mass--;
		return this.mass;
	}
}
