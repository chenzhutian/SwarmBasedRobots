package com.cike.swarmbasedrobot;

import java.util.HashMap;

public class SubArea {
	private int State;
	private int S;
	private int centerX;//中心点的绝对横坐标
	private int centerY;//中心点的绝对纵坐标
	private HashMap<String, Integer> centerLocation = new HashMap<>();
	private int m;
	private int n;
	private int[][] grids;
	private int undetectedGrids;
	private double D; // distance between subarea and map center
	private double B;// something you know

	public SubArea() {
		this.State = 0;
		this.S = 10;
		this.grids = new int[10][10];
		for (int i = 0; i < this.S; ++i) {
			for (int j = 0; j < this.S; ++j) {
				this.grids[i][j] = 0;
			}
		}
		this.undetectedGrids = 10 * 10;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public HashMap<String, Integer> getCenterLocation() {
		return centerLocation;
	}

	public SubArea(int s,int m,int n) {
		this.State = 0;
		this.S = s;
		this.m = m;
		this.n = n;
		this.grids = new int[s][s];
		this.centerLocation = Map.subArea2Global(s/2, s/2, m, n, s);
		this.centerX = this.centerLocation.get("x");
		this.centerY = this.centerLocation.get("y");
		for (int i = 0; i < this.S; ++i) {
			for (int j = 0; j < this.S; ++j) {
				this.grids[i][j] = 0;
			}
		}
		this.undetectedGrids = s * s;
	}

	public double getUndetectedGridPrecentage() {
		return (double)this.undetectedGrids / (this.S * this.S);
	}

	public int detecedAGrid(int i, int j) {
		if (i > S - 1 || j > S - 1 || i < 0 || j < 0) {
			return -1;
		}
		this.grids[i][j] = 1;
		if (this.undetectedGrids == 0) {
			//System.out.print("All the grids of this subarea has been detected");
			return -1;
		} else {
			this.undetectedGrids--;
		}
		return this.undetectedGrids;
	}

	public int getUndetectedGrids() {
		return undetectedGrids;
	}

	public void setUndetectedGrids(int undetectedGrids) {
		this.undetectedGrids = undetectedGrids;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public int getS() {
		return S;
	}

	public void setS(int s) {
		S = s;
	}

	public int[][] getGrids() {
		return grids;
	}

	public void setGrids(int[][] grids) {
		this.grids = grids;
	}

	public double getD() {
		return D;
	}

	public void setD(double d) {
		D = d;
	}

	public double getB() {
		return B;
	}

	public void setB(double b) {
		B = b;
	}

}
