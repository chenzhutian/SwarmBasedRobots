package com.cike.swarmbasedrobot;

import java.util.ArrayList;
import java.util.Random;

public class Expriment {

	/**
	 * @param args
	 */
	public ArrayList<Target> allTargets = new ArrayList<>();
	public ArrayList<Robot> allRobots = new ArrayList<>();
	public Map map;
	public int sumMass = 0;
	public static int t = 0;
	
	public static void main(String[] args) throws Exception {
		Expriment e = new Expriment();
		e.init(30, 30);
		//e.printBounder();
		while (e.sumMass > 0) {
///			e.printBounder();
			int i;
			Robot tempRobot = null;
			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				tempRobot.detected(tempRobot.getX(), tempRobot.getY());
				// System.out.println("Robot("+tempRobot.getX()+","+tempRobot.getY()+")'s state is "+tempRobot.getState());
			}
			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				tempRobot.getNeighbour();
			}
			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				tempRobot.pickDestination();
			}
			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				if (tempRobot.tryToRemove() != null) {
					int tempx = tempRobot.tryToRemove().get("x").intValue();
					int tempy = tempRobot.tryToRemove().get("y").intValue();
					tempRobot.remove(tempx, tempy);
				} else {
					tempRobot.move();
				}
			}
			e.printMap();
			Thread.sleep(500);
			t++;
		}
		// e.map.GlobalMap[5][5] = 1;
		// System.out.println(e.map.GlobalMap[5][5]);
		// Pipe.globalMap[5][5] = 2;
		// System.out.println(e.map.GlobalMap[5][5]);
		// for (int i = 0; i < e.allRobots.size(); ++i) {
		// Robot tempRobot = e.allRobots.get(i);
		// if (tempRobot.targetLocation.size() != 0)
		// System.out.println("robot(" + tempRobot.getX() + ","
		// + tempRobot.getY() + ")'s Orientation is "
		// + tempRobot.getOrientation()
		// + " and its target list is :");
		// for (int j = 0; j < tempRobot.targetLocation.size(); ++j) {
		// int tempx = tempRobot.targetLocation.get(j).get("x");
		// int tempy = tempRobot.targetLocation.get(j).get("y");
		// System.out.println("(" + tempx + "," + tempy + ")");
		// }
		// Robot tempRobot = e.allRobots.get(i);
		// tempRobot.pickDestination();
		//
		// }
		// ArrayList<HashMap<String, Integer>> list = new ArrayList<>();
		// HashMap<String, Integer> hp = new HashMap<>();
		// hp.put("1", 2);
		// hp.put("3", 4);
		// list.add(hp);
		// if(list.contains(hp)){
		// System.out.println("asdfsdf");
		// }
		// if(hp.containsValue(2)){
		// System.out.println("dddddddddddddddd");
		// }
		// hp.remove(2);
		// if(hp.containsValue(2)){
		// System.out.println("dddddddddddddddd");
		// }

	}

	public void init(int r, int t) {
		// step1
		// 初始化地图，随机分布机器人和目标
		Random rd = new Random();
		map = new Map(4, 4, 10);
		for (int i = 0; i < t;) {
			int tempx = rd.nextInt(40);
			int tempy = rd.nextInt(40);
			if (map.GlobalMap[tempx][tempy] == 0) {
				map.GlobalMap[tempx][tempy] = Map.TARGET;
				Target tempTarget = new Target(tempx, tempy);
				tempTarget.setId(i);
				allTargets.add(tempTarget);
				Pipe.setTargetByLocation(tempx, tempy, tempTarget);
				++i;
			}
		}

		for (int i = 0; i < r;) {
			int tempx = rd.nextInt(40);
			int tempy = rd.nextInt(40);
			if (map.GlobalMap[tempx][tempy] == 0) {
				map.GlobalMap[tempx][tempy] = Map.ROBOT;
				Robot tempRobot = new Robot(tempx, tempy, 2, 10);
				tempRobot.setID(i);
				allRobots.add(tempRobot);
				Pipe.setRobotByLocation(tempx, tempy, tempRobot);
				++i;
			}
		}
		sumMass = 0;
		Target tempTarget;
		for (int i = 0; i < this.allTargets.size(); ++i) {
			tempTarget = this.allTargets.get(i);
			sumMass += tempTarget.getMass();
		}
		Pipe.list_allRobots = this.allRobots;
		Pipe.allTargets = this.allTargets;
	}


	public void printBounder() {
		sumMass = 0;
		Target tempTarget;
		for (int i = 0; i < this.allTargets.size(); ++i) {
			tempTarget = this.allTargets.get(i);
			sumMass += tempTarget.getMass();
		}
		System.out.println("<====================" + sumMass
				+ "==========="+t+"===============>");
	}

	public void printMap() {
		int m = map.getM();
		int n = map.getN();
		int s = map.getS();
		sumMass = 0;
		Target tempTarget;
		for (int i = 0; i < this.allTargets.size(); ++i) {
			tempTarget = this.allTargets.get(i);
			sumMass += tempTarget.getMass();
		}
		// int[][] screen = new int[m * s + 1][n * s + 1];
		System.out.println("<====================" + sumMass
				+ "==========="+t+"===============>");
		for (int i = 1; i < (m * s + 1); ++i) {
			for (int j = 0; j < (n * s + 2); ++j) {
				if (j == (n * s + 1)) {
					System.out.print("|" + (i - 1));
				} else if (j == 0) {
					if (i - 1 < 10)
						System.out.print(" " + (i - 1) + "|");
					else
						System.out.print((i - 1) + "|");
				} else {
					if (this.map.getGlobalMap()[i - 1][j - 1] == 0) {
						System.out.print("_");
					} else {
						System.out.print(this.map.getGlobalMap()[i - 1][j - 1]);
					}
				}
			}
			System.out.println();
		}
	}
}
