package com.cike.swarmbasedrobot;

import java.util.ArrayList;
import java.util.HashMap;

public class Pipe {
	public static int m;
	public static int n;
	public static int S;
	public static ArrayList<Robot> list_allRobots = new ArrayList<>();
	public static ArrayList<Target> allTargets = new ArrayList<>();
	public static ArrayList<SubArea> allSubArea = new ArrayList<>();
	public static int[][] globalMap;
	public static HashMap<String, Robot> hashmap_allRobots = new HashMap<>();
	public static HashMap<String, Target> hashmap_allTargets = new HashMap<>();
	public static HashMap<String, SubArea> hashmap_allSubArea = new HashMap<>();
	private static HashMap<Target, String> targetHelper = new HashMap<>();
	private static HashMap<Robot, String> robotsHelper = new HashMap<>();
	private static HashMap<SubArea, String> subAreaHelper = new HashMap<>();
	public static int getGlobalMap(int x,int y){
		if(x<0||y<0||x>(m*S-1)||y>(n*S-1)){
			return -1;
		}
		return globalMap[x][y];
	}
	public static void setGlobalMap(int x,int y,int t){
		if(x<0||y<0||x>(m*S-1)||y>(n*S-1)){
			return;
		}
		globalMap[x][y] = t;
	}
	public static Robot getRobotByLocation(int x,int y){
		return hashmap_allRobots.get(String.valueOf(x*100+y));
	}
	public static void setRobotByLocation(int x, int y, Robot r){
		if(hashmap_allRobots.containsValue(r)){
			String id = Pipe.robotsHelper.get(r);
			hashmap_allRobots.remove(id);
		}
		hashmap_allRobots.put(String.valueOf(x*100+y), r);
		robotsHelper.put(r, String.valueOf(x*100+y));
	}
	
	public static Target getTargetByLocation(int x,int y){
		return hashmap_allTargets.get(String.valueOf(x*100+y));
	}
	public static void setTargetByLocation(int x,int y,Target t){
		if(hashmap_allTargets.containsValue(t)){
			String id = Pipe.targetHelper.get(t);
			hashmap_allTargets.remove(id);
		}
		hashmap_allTargets.put(String.valueOf(x*100+y), t);
		targetHelper.put(t, String.valueOf(x*100+y));
	}
	public static void delTarget(Target t){
		Pipe.allTargets.remove(t);
		String id = Pipe.targetHelper.get(t);
		Pipe.hashmap_allTargets.remove(id);
		Pipe.targetHelper.remove(t);
	}
	public static SubArea getSubAreaByLocation(int m,int n){
		return hashmap_allSubArea.get(String.valueOf(m*100+n));
	}
	public static void setSubAreaByLocation(int m,int n,SubArea s){
		if(hashmap_allSubArea.containsValue(s)){
			String id = Pipe.subAreaHelper.get(s);
			hashmap_allSubArea.remove(id);
		}
		hashmap_allSubArea.put(String.valueOf(m*100+n), s);
		subAreaHelper.put(s, String.valueOf(m*100+n));
	}
}
