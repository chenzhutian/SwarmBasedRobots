package com.cike.swarmbasedrobot;

import java.util.HashMap;

public class Map {
	public static final int ROBOT = 9;
	public static final int TARGET = 7;
	
	private int K = 1;
	private int M, N;
	private int S;
	private SubArea[][] SubAreaMap;
	public int[][] GlobalMap;

	public Map() {
		this.M = 10;
		this.N = 10;
		this.S = 10;
		this.SubAreaMap = new SubArea[10][10];
		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < 10; ++j) {
				this.SubAreaMap[i][j] = new SubArea();
			}
		}
		this.GlobalMap = new int[100][100];
		for (int i = 0; i < 100; ++i) {
			for (int j = 0; j < 100; ++j) {
				this.GlobalMap[i][j] = 0;
			}
		}
	}

	public Map(int m, int n, int s) {
		this.M = m;
		this.N = n;
		this.S = s;
		this.SubAreaMap = new SubArea[m][n];
		double sumD = 0;
		int mapCenterX = (m * s) / 2;
		int mapCenterY = (n * s) / 2;
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				this.SubAreaMap[i][j] = new SubArea(s,i,j);
				Pipe.allSubArea.add(this.SubAreaMap[i][j]);
				Pipe.setSubAreaByLocation(i, j, this.SubAreaMap[i][j]);
				HashMap<String, Integer> location;
				location = Map.subArea2Global(s / 2, s / 2, i, j, s);
				int tempx = location.get("x").intValue();
				int tempy = location.get("y").intValue();
				double tempD = Map.distance(tempx, tempy, mapCenterX, mapCenterY);
				this.SubAreaMap[i][j].setD(tempD);
				sumD += tempD;
			}
		}
		for(int i =0;i<m;++i){
			for(int j = 0;j<n;++j){
				double tempB = this.SubAreaMap[i][j].getD();
				tempB = tempB/sumD;
				this.SubAreaMap[i][j].setB(tempB);
			}
		}
		
		this.GlobalMap = new int[m * s][n * s];
		for (int i = 0; i < m * s; ++i) {
			for (int j = 0; j < n * s; ++j) {
				this.GlobalMap[i][j] = 0;
			}
		}
		Pipe.globalMap = this.GlobalMap;
		Pipe.m = m;
		Pipe.n = n;
		Pipe.S = s;
	}

	public static double distance(int x1,int y1,int x2,int y2){
		double dist;
		dist = Math.sqrt((x1 - x2) * (x1 - x2)
				+ (y1 - y2) * (y1 - y2));
		return dist;
	}
	public static HashMap<String, Integer> subArea2Global(int x,
			int y, int m, int n, int S) {
		HashMap<String, Integer> hp = new HashMap<>();
		int tempx = m * S + x;
		int tempy = n * S + y;
		hp.put("x", tempx);
		hp.put("y", tempy);
		return hp;
	}

	public static HashMap<String, Integer> inWhichSubArea(int x,
			int y, int S) {
		HashMap<String, Integer> hp = new HashMap<>();
		int tempx = x / S;
		int tempy = y / S;
		hp.put("m", tempx);
		hp.put("n", tempy);
		return hp;
	}

	public static HashMap<String, Integer> global2SubArea(int x,
			int y, int m, int n, int S) {
		HashMap<String, Integer> hp = new HashMap<>();
		int tempx = x - m * S;
		int tempy = y - n * S;
		hp.put("x", tempx);
		hp.put("y", tempy);
		return hp;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int getM() {
		return M;
	}

	public void setM(int m) {
		M = m;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getS() {
		return S;
	}

	public void setS(int s) {
		S = s;
	}

	public SubArea[][] getSubAreaMap() {
		return SubAreaMap;
	}

	public void setSubAreaMap(SubArea[][] subAreaMap) {
		SubAreaMap = subAreaMap;
	}

	public int[][] getGlobalMap() {
		return GlobalMap;
	}

	public void setGlobalMap(int[][] globalMap) {
		GlobalMap = globalMap;
	}

}
