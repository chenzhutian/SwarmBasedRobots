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
	int t = 0;

	public static void main(String[] args) throws Exception {
		Expriment e = new Expriment();

		int p = 0;
		int tempSumMass;
		e.init(20, 10,400);
		e.computSumMass();
		tempSumMass = e.sumMass;

		while (e.sumMass > 0) {
			int i;
			Robot tempRobot = null;
			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				tempRobot.detected(tempRobot.getX(), tempRobot.getY());
			}

			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				tempRobot.getNeighbour();

			}

			for (i = 0; i < e.allRobots.size(); ++i) {
				tempRobot = e.allRobots.get(i);
				 tempRobot.pickDestinationByPSO();
				// tempRobot.pickDestinationByRandom();
				//tempRobot.pickDestinationByMotifyPSO();
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
			Thread.sleep(300);
			// e.computSumMass();
			if (e.sumMass == tempSumMass) {
				p++;
			} else {
				tempSumMass = e.sumMass;
				p = 0;
			}
			e.t++;
			if (p > 100)
				break;
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		Expriment e = new Expriment();
//		int sum = 0;
//		int k = 200;
//		int min = 33333333;
//		while (k > 0) {
//			e = new Expriment();
//			e.init(20, 10,400);
//			// int p = 0;
//			int t = 0;
//			// int tempSumMass;
//			// tempSumMass = e.sumMass;
//
//			while (e.sumMass > 0) {
//				// / e.printBounder();
//				int i;
//				Robot tempRobot = null;
//				for (i = 0; i < e.allRobots.size(); ++i) {
//					tempRobot = e.allRobots.get(i);
//					tempRobot.detected(tempRobot.getX(), tempRobot.getY());
//				}
//
////				for (i = 0; i < e.allRobots.size(); ++i) {
////					tempRobot = e.allRobots.get(i);
////					tempRobot.getNeighbour();
////				}
//
//				for (i = 0; i < e.allRobots.size(); ++i) {
//					tempRobot = e.allRobots.get(i);
//					//tempRobot.pickDestinationByPSO();
//					//tempRobot.pickDestinationByMotifyPSO();
//					tempRobot.pickDestinationByRandom();
//				}
//
//				for (i = 0; i < e.allRobots.size(); ++i) {
//					tempRobot = e.allRobots.get(i);
//					if (tempRobot.tryToRemove() != null) {
//						int tempx = tempRobot.tryToRemove().get("x").intValue();
//						int tempy = tempRobot.tryToRemove().get("y").intValue();
//						tempRobot.remove(tempx, tempy);
//					} else {
//						tempRobot.move();
//					}
//				}
//				// e.printMap();
//				// Thread.sleep(200);
//				e.computSumMass();
//				// if (e.sumMass == tempSumMass) {
//				// p++;
//				// } else {
//				// tempSumMass = e.sumMass;
//				// p = 0;
//				// }
//				t++;
//				// if (p > 100)break;
//			}
//			if (t < min)
//				min = t;
//			System.out.println(t);
//			sum += t;
//			k--;
//		}
//		System.out.println(sum + " and avg is " + sum / 200 + ", and min is "
//				+ min);
//	}

	public void computSumMass() {
		sumMass = 0;
		Target tempTarget;
		for (int i = 0; i < this.allTargets.size(); ++i) {
			tempTarget = this.allTargets.get(i);
			sumMass += tempTarget.getMass();
		}
	}

	public void init(int r, int t,int mass) {
		// step1
		// 初始化地图，随机分布机器人和目标
		Pipe.SumMass = mass;
		Random rd = new Random();
		map = new Map(4, 4, 10);
		for (int i = 0; i < t;) {
			int tempx = rd.nextInt(40);
			int tempy = rd.nextInt(40);
			if (map.GlobalMap[tempx][tempy] == 0) {
				map.GlobalMap[tempx][tempy] = Map.TARGET;
				Target tempTarget = new Target(tempx, tempy,i,10,1);
				allTargets.add(tempTarget);
				Pipe.setTargetByLocation(tempx, tempy, tempTarget);
				++i;
				System.out.println(tempTarget.getMass());
			}
		}

		for (int i = 0; i < r;) {
			int tempx = rd.nextInt(40);
			int tempy = rd.nextInt(40);
			if (map.GlobalMap[tempx][tempy] == 0) {
				map.GlobalMap[tempx][tempy] = Map.ROBOT;
				//Robot tempRobot = new Robot(tempx, tempy, 3, 10);
				Robot tempRobot = new Robot(tempx,tempy,3,4,10);
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

	// public void printBounder() {
	// sumMass = 0;
	// Target tempTarget;
	// for (int i = 0; i < this.allTargets.size(); ++i) {
	// tempTarget = this.allTargets.get(i);
	// sumMass += tempTarget.getMass();
	// }
	// System.out.println("<====================" + sumMass + "==========="
	// + t + "===============>");
	// }

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
		System.out.println("<====================" + sumMass + "==========="
				+ t + "===============>");
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
