package com.cike.swarmbasedrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Robot {
	public static final int ROBOT_SEARCHING = 0;;
	public static final int ROBOT_REMOVING = 1;;
	public static final int ORIENTATION_E = 0;
	public static final int ORIENTATION_W = 1;
	public static final int ORIENTATION_S = 2;
	public static final int ORIENTATION_N = 3;
	public static final int ORIENTATION_ES = 4;
	public static final int ORIENTATION_EN = 5;
	public static final int ORIENTATION_WS = 6;
	public static final int ORIENTATION_WN = 7;

	private int K = 1;
	private SubArea currentSubArea; // 机器人储存当前subarea的情况，即grid的情况
	private int[][] subAreaOfGlobalMap; // 机器人储存整个地图subarea的情况
	// private int[][] globalMap; //开挂用的全地图...

	private HashMap<String, Integer> subAreaLocation = new HashMap<>(); // 机器人当前所处的subarea的坐标
	private HashMap<String, Integer> location = new HashMap<>(); // 机器人当前的坐标
	private HashMap<String, Integer> velocity = new HashMap<>(); // 机器人当前的速度
	private int S;
	private int x; // x轴坐标
	private int y;// y轴坐标
	private int orientation; // 机器人前进方向
	private int wifiR; // 通信半径
	private int detectedR; // 探测半径
	private int state; // 机器人当前状态
	public ArrayList<HashMap<String, Integer>> targetLocation = new ArrayList<>();// 目标坐标列表
	private HashMap<String, Integer> destination = new HashMap<>(); // 下一步前往的坐标
	private Random rd = new Random();

	public Robot() {
		HashMap<String, Integer> hp = new HashMap<>();
		hp.put("m", 0);
		hp.put("n", 0);
		this.subAreaLocation = hp;

		hp = new HashMap<>();
		this.x = 0;
		this.y = 0;
		hp.put("x", 0);
		hp.put("y", 0);
		this.location = hp;

		this.wifiR = 1;
		this.detectedR = this.wifiR;

		hp.put("x", 0);
		hp.put("y", 0);
		this.targetLocation.add(hp);

		this.currentSubArea = new SubArea();
		this.S = 10;
		this.subAreaOfGlobalMap = new int[10][10];
		this.state = Robot.ROBOT_SEARCHING;
	}

	public Robot(int x, int y, int wifi, int S) {
		HashMap<String, Integer> hp = Map.inWhichSubArea(x, y, S);
		this.subAreaLocation = hp;

		hp = new HashMap<>();
		this.x = x;
		this.y = y;
		hp.put("x", x);
		hp.put("y", y);
		this.location = hp;

		hp = new HashMap<>();
		hp.put("x", rd.nextInt(5));
		hp.put("y", rd.nextInt(5));
		this.velocity = hp;
		this.detectedR = this.wifiR = wifi;

		// hp.put("x", 0);
		// hp.put("y", 0);
		// this.targetLocation.add(hp);
		hp = Map.inWhichSubArea(x, y, S);
		this.currentSubArea = new SubArea(S, hp.get("m"), hp.get("n"));
		this.S = S;
		this.subAreaOfGlobalMap = new int[Pipe.m][Pipe.n];
		this.state = Robot.ROBOT_SEARCHING;
		this.orientation = rd.nextInt(7);
	}

	public int setOrientation() {
		double x1 = this.location.get("x").intValue();
		double y1 = this.location.get("y").intValue();
		double x2 = this.destination.get("x").intValue();
		double y2 = this.destination.get("y").intValue();
		int quadrant = 0;
		if (x2 == x1 && y2 == y1) {
			return rd.nextInt(7);
		}
		if (x2 >= x1) {
			if (y2 >= y1)
				quadrant = 4;
			else
				quadrant = 3;
		} else {
			if (y2 >= y1)
				quadrant = 1;
			else
				quadrant = 2;
		}
		double angle = Math.atan(-(x2 - x1) / (y2 - y1));
		switch (quadrant) {
		case 4:
		case 1: {
			if (angle >= (0.375 * Math.PI)) {
				return Robot.ORIENTATION_N;
			} else if (angle >= (0.125 * Math.PI)) {
				return Robot.ORIENTATION_EN;
			} else if (angle >= (-0.125 * Math.PI)) {
				return Robot.ORIENTATION_E;
			} else if (angle >= (-0.375 * Math.PI)) {
				return Robot.ORIENTATION_ES;
			} else {
				return Robot.ORIENTATION_S;
			}
		}
		case 2:
		case 3: {
			if (angle >= (0.375 * Math.PI)) {
				return Robot.ORIENTATION_S;
			} else if (angle >= (0.125 * Math.PI)) {
				return Robot.ORIENTATION_WS;
			} else if (angle >= (-0.125 * Math.PI)) {
				return Robot.ORIENTATION_W;
			} else if (angle >= (-0.375 * Math.PI)) {
				return Robot.ORIENTATION_WN;
			} else {
				return Robot.ORIENTATION_N;
			}
		}
		default:
			return -1;
		}

	}

	public void detected(int x, int y) {
		HashMap<String, Integer> tempLocation;
		HashMap<String, Integer> hash;
		switch (this.orientation) {
		case Robot.ORIENTATION_E: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x + k;
				int tempy = y + k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempx >= (x - k)) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempx--;
				}
				k++;
			}
		}
			break;
		case Robot.ORIENTATION_N: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x - k;
				int tempy = y + k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy >= (y - k)) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempy--;
				}
				k++;
			}
		}
			break;
		case Robot.ORIENTATION_S: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x + k;
				int tempy = y + k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy >= (y - k)) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempy--;
				}
				k++;
			}

		}
			break;
		case Robot.ORIENTATION_W: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x + k;
				int tempy = y - k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempx >= (x - k)) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempx--;
				}
				k++;
			}

		}
			break;
		case Robot.ORIENTATION_EN: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x;
				int tempy = y + k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy >= y) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempy--;
					tempx--;
				}
				k++;
			}

		}
			break;
		case Robot.ORIENTATION_ES: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x;
				int tempy = y + k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy >= y) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempy--;
					tempx++;
				}
				k++;
			}

		}
			break;
		case Robot.ORIENTATION_WN: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x;
				int tempy = y - k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy <= y) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempx--;
					tempy++;
				}
				k++;
			}

		}
			break;
		case Robot.ORIENTATION_WS: {
			int k = 1;
			while (k <= this.detectedR) {
				int tempx = x;
				int tempy = y - k;
				tempLocation = Map.inWhichSubArea(tempx, tempy, S);
				int tempm = tempLocation.get("m");
				int tempn = tempLocation.get("n");
				while (tempy <= y) {
					tempLocation = Map.global2SubArea(tempx, tempy, tempm,
							tempn, this.S);
					int i = tempLocation.get("x").intValue();
					int j = tempLocation.get("y").intValue();
					this.currentSubArea.detecedAGrid(i, j);
					if (Pipe.getGlobalMap(tempx, tempy) == Map.TARGET) {
						hash = new HashMap<>();
						hash.put("x", tempx);
						hash.put("y", tempy);
						this.targetLocation.add(hash);
						this.state = Robot.ROBOT_REMOVING;
					}
					tempy++;
					tempx++;
				}
				k++;
			}
		}
			break;
		default:
			System.out.println("探测出错了");
		}
	}

	public void pickDestinationByRandom() {
		int tempx = rd.nextInt(5);
		int tempy = rd.nextInt(5);
		if (rd.nextInt(2) == 1) {
			tempx = -tempx;
		}
		if (rd.nextInt(2) == 1) {
			tempy = -tempy;
		}
		this.velocity = new HashMap<>();
		this.velocity.put("x", tempx);
		this.velocity.put("y", tempy);
		this.destination = new HashMap<>();
		this.destination.put("x", this.x + tempx);
		this.destination.put("y", this.y + tempy);
	}

	public void pickDestination() {
		HashMap<String, Integer> hash;
		HashMap<String, Integer> maxLocation = null;
		int tempx = S;
		int tempy = S;
		double maxU = 0;
		double tempU;

		switch (this.state) {
		case Robot.ROBOT_REMOVING: {
			Target tempTarget;
			// 找效用函数最大的目标
			for (int i = 0; i < this.targetLocation.size(); ++i) {
				hash = this.targetLocation.get(i);
				tempTarget = Pipe.getTargetByLocation(hash.get("x"),
						hash.get("y"));
				if (tempTarget == null) {// 若目标不存在则在列表中删除目标
					this.targetLocation.remove(hash);
					continue;
				}
				Double D = Map.distance(this.x, this.y, tempTarget.getX(),
						tempTarget.getY());
				// if(tempTarget.getMass() == 0){
				// this.targetLocation.remove(hash);
				// Pipe.delTarget(tempTarget);
				// continue;
				// }
				tempU = tempTarget.getMass() / D;
				if (tempU >= maxU) {
					maxU = tempU;
					maxLocation = hash;
				}
			}
			if (maxLocation == null) {
				this.state = Robot.ROBOT_SEARCHING;
			} else {
				tempx = maxLocation.get("x") - this.x;
				tempy = maxLocation.get("y") - this.y;
				double temp = rd.nextFloat();
				tempx = (int) (2 * temp * tempx + 0.6 * velocity.get("x"));
				tempy = (int) (2 * temp * tempy + 0.6 * velocity.get("y"));
				if (!((Math.abs(tempx) > (S)) || (Math.abs(tempy) > (S)))) {
					this.velocity = new HashMap<>();
					this.velocity.put("x", tempx);
					this.velocity.put("y", tempy);
					this.destination = new HashMap<>();
					this.destination.put("x", this.x + tempx);
					this.destination.put("y", this.y + tempy);
				} else {
					tempx = this.x + this.velocity.get("x");
					tempy = this.y + this.velocity.get("y");
					this.destination = new HashMap<>();
					this.destination.put("x", tempx);
					this.destination.put("y", tempy);
				}
			}
			// TODO 偏移没写
		}
			break;
		case Robot.ROBOT_SEARCHING: {
			int m = this.subAreaLocation.get("m");
			int n = this.subAreaLocation.get("n");
			SubArea tempSubArea = Pipe.getSubAreaByLocation(m, n);
			if (tempSubArea.getUndetectedGridPrecentage() > 0.01) {
				for (int i = 0; i < S; ++i) {
					for (int j = 0; j < S; ++j) {
						if (tempSubArea.getGrids()[i][j] == 0) {
							hash = Map.subArea2Global(i, j, m, n, S);
							tempx = hash.get("x");
							tempy = hash.get("y");
							tempU = Map.distance(tempx, tempy, this.x, this.y);
							if (tempU >= maxU) {
								maxU = tempU;
								maxLocation = hash;
							}
						}
					}
				}
				tempx = maxLocation.get("x") - this.x;
				tempy = maxLocation.get("y") - this.y;
				double temp = rd.nextFloat();
				tempx = (int) (2 * temp * tempx + 0.6 * velocity.get("x"));
				tempy = (int) (2 * temp * tempy + 0.6 * velocity.get("y"));
				if (!((Math.abs(tempx) > (S / 2)) || (Math.abs(tempy) > (S / 2)))) {
					this.velocity = new HashMap<>();
					this.velocity.put("x", tempx);
					this.velocity.put("y", tempy);
					this.destination = new HashMap<>();
					this.destination.put("x", this.x + tempx);
					this.destination.put("y", this.y + tempy);
				} else {
					tempx = this.x + this.velocity.get("x").intValue();
					tempy = this.y + this.velocity.get("y").intValue();
					this.destination = new HashMap<>();
					this.destination.put("x", tempx);
					this.destination.put("y", tempy);
				}
			} else {
				this.subAreaOfGlobalMap[tempSubArea.getM()][tempSubArea.getN()] = 1;
				for (int i = 0; i < Pipe.allSubArea.size(); ++i) {
					tempSubArea = Pipe.allSubArea.get(i);
					if (this.subAreaOfGlobalMap[tempSubArea.getM()][tempSubArea
							.getN()] != 1) {
						hash = tempSubArea.getCenterLocation();
						double d = Map.distance(this.x, this.y,
								tempSubArea.getCenterX(),
								tempSubArea.getCenterY());
						tempU = (tempSubArea.getB() * tempSubArea
								.getUndetectedGrids()) / d;
						if (tempU > maxU) {
							maxU = tempU;
							maxLocation = hash;
						}
					}
				}
				if (maxLocation != null) {
					tempx = maxLocation.get("x") - this.x;
					tempy = maxLocation.get("y") - this.y;
					double temp = rd.nextFloat();
					tempx = (int) (temp * tempx + 0.6 * velocity.get("x"));
					tempy = (int) (temp * tempy + 0.6 * velocity.get("y"));
				}
				if (!((Math.abs(tempx) > (S / 2)) || (Math.abs(tempy) > (S / 2)))) {
					this.velocity = new HashMap<>();
					this.velocity.put("x", tempx);
					this.velocity.put("y", tempy);
					this.destination = new HashMap<>();
					this.destination.put("x", this.x + tempx);
					this.destination.put("y", this.y + tempy);

				} else {
					tempx = this.x + this.velocity.get("x");
					tempy = this.y + this.velocity.get("y");
					this.destination = new HashMap<>();
					this.destination.put("x", tempx);
					this.destination.put("y", tempy);
				}
			}
		}
		}
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int[][] getSubAreaOfGlobalMap() {
		return subAreaOfGlobalMap;
	}

	public void setSubAreaOfGlobalMap(int[][] subAreaOfGlobalMap) {
		this.subAreaOfGlobalMap = subAreaOfGlobalMap;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getWifiR() {
		return wifiR;
	}

	public void setWifiR(int wifiR) {
		this.wifiR = wifiR;
	}

	public int getDetectedR() {
		return detectedR;
	}

	public void setDetectedR(int detectedR) {
		this.detectedR = detectedR;
	}

	public void setLocation(int x, int y) {
		HashMap<String, Integer> hp = new HashMap<>();
		hp.put("x", x);
		hp.put("y", y);
		this.location = hp;
		// this.location.add(hp);
		this.x = x;
		this.y = y;
	}

	public void communication(int x, int y) {
		if (Pipe.getGlobalMap(x, y) == Map.ROBOT) {
			Robot tempRobot = Pipe.getRobotByLocation(x, y);
			if (tempRobot.getState() == ROBOT_REMOVING) {
				for (int j = 0; j < tempRobot.targetLocation.size(); ++j) {
					int x1 = tempRobot.targetLocation.get(j).get("x")
							.intValue();
					int y1 = tempRobot.targetLocation.get(j).get("y")
							.intValue();
					int flag = 1;
					for (int i = 0; i < this.targetLocation.size(); ++i) {
						int x2 = this.targetLocation.get(i).get("x").intValue();
						int y2 = this.targetLocation.get(i).get("y").intValue();
						if (x1 == x2 && y1 == y2) {
							flag = 0;
						}

					}
					if (flag == 1) {
						this.targetLocation
								.add(tempRobot.targetLocation.get(j));
					}
				}
			} else {
				if (tempRobot.subAreaLocation.equals(this.subAreaLocation)) {
					for (int i = 0; i < S; ++i) {
						for (int j = 0; j < S; ++j) {
							if (tempRobot.currentSubArea.getGrids()[i][j] == 1) {
								this.currentSubArea.getGrids()[i][j] = 1;
							}
						}
					}
				}

			}// else
			for (int i = 0; i < Pipe.m; ++i) {
				for (int j = 0; j < Pipe.n; ++j) {
					if (tempRobot.getSubAreaOfGlobalMap()[i][j] == 1) {
						this.getSubAreaOfGlobalMap()[i][j] = 1;
					}
				}
			}
		}
	}

	public void getNeighbour() {
		int k = 1;
		while (k <= this.wifiR) {
			int tempx = this.x - k;
			int tempy = this.y + k;
			while (tempx <= this.x + k) {
				communication(tempx, tempy);
				tempx++;
			}
			while (tempy >= this.y - k) {
				// if (Pipe.getGlobalMap(tempx, tempy) == Map.ROBOT) {
				// // for (int i = 0; i < Pipe.list_allRobots.size(); ++i) {
				// // if (Pipe.list_allRobots.get(i).x == tempx
				// // && Pipe.list_allRobots.get(i).y == tempy) {
				// Robot tempRobot = Pipe.getRobotByLocation(tempx, tempy);
				// if (tempRobot.getState() == ROBOT_REMOVING) {
				// for (int j = 0; j < tempRobot.targetLocation.size(); ++j) {
				// if (!this.targetLocation
				// .contains(tempRobot.targetLocation.get(j))) {
				// this.targetLocation
				// .add(tempRobot.targetLocation.get(j));
				// }
				// }
				// }
				// }
				communication(tempx, tempy);
				tempy--;
			}
			while (tempx >= this.x - k) {
				communication(tempx, tempy);
				tempx--;
			}
			while (tempy <= this.y + k) {
				communication(tempx, tempy);
				tempy++;
			}
			k++;
		}
	}

	public void move() {
		int tempx;
		int tempy;
		tempx = this.destination.get("x").intValue();
		tempy = this.destination.get("y").intValue();
		if (Pipe.getGlobalMap(tempx, tempy) != 0) {
			this.destination = this.findEmpty(tempx, tempy);
			tempx = this.destination.get("x").intValue();
			tempy = this.destination.get("y").intValue();
		}
		this.location = this.destination;
		Pipe.globalMap[x][y] = 0;
		Pipe.globalMap[tempx][tempy] = Map.ROBOT;
		this.x = tempx;
		this.y = tempy;
		Pipe.setRobotByLocation(tempx, tempy, this);
		this.orientation = this.setOrientation();
		this.subAreaLocation = Map.inWhichSubArea(this.x, this.y, this.S);
		tempx = this.subAreaLocation.get("m");
		tempy = this.subAreaLocation.get("n");
		this.currentSubArea = Pipe.getSubAreaByLocation(tempx, tempy);
	}

	public HashMap<String, Integer> findEmpty(int x, int y) {
		HashMap<String, Integer> hash = null;
		switch (Pipe.getGlobalMap(x, y)) {
		case Map.ROBOT:
		case Map.TARGET:
			hash = new HashMap<>();
			if (Pipe.getGlobalMap(x + 1, y) == 0) {
				hash.put("x", x + 1);
				hash.put("y", y);
				return hash;
			} else if (Pipe.getGlobalMap(x - 1, y) == 0) {
				hash.put("x", x - 1);
				hash.put("y", y);
				return hash;
			} else if (Pipe.getGlobalMap(x, y + 1) == 0) {
				hash.put("x", x);
				hash.put("y", y + 1);
				return hash;
			} else if (Pipe.getGlobalMap(x, y - 1) == 0) {
				hash.put("x", x);
				hash.put("y", y - 1);
				return hash;
			} else if (Pipe.getGlobalMap(x + 1, y + 1) == 0) {
				hash.put("x", x + 1);
				hash.put("y", y + 1);
				return hash;
			} else if (Pipe.getGlobalMap(x - 1, y + 1) == 0) {
				hash.put("x", x - 1);
				hash.put("y", y + 1);
				return hash;
			} else if (Pipe.getGlobalMap(x + 1, y - 1) == 0) {
				hash.put("x", x + 1);
				hash.put("y", y - 1);
				return hash;
			} else if (Pipe.getGlobalMap(x - 1, y - 1) == 0) {
				hash.put("x", x - 1);
				hash.put("y", y - 1);
				return hash;
			}
		}
		int tempx = this.velocity.get("x").intValue();
		int tempy = this.velocity.get("y").intValue();
		this.velocity = new HashMap<>();
		this.velocity.put("x", -tempx);
		this.velocity.put("y", -tempy);
		// this.destination = new HashMap<>();
		// this.destination.put("x", this.x - tempx);
		// this.destination.put("y", this.y - tempy);
		// return this.destination;
		return this.location;
	}

	public void remove(int x, int y) {
		Target tempTarget = Pipe.getTargetByLocation(x, y);
		if (tempTarget.getMass() == 0) {
			this.targetLocation.remove(tempTarget.getLocation());
			Pipe.globalMap[x][y] = 0;
			Pipe.delTarget(tempTarget);
			this.state = Robot.ROBOT_SEARCHING;
		} else
			tempTarget.remove();
	}

	public HashMap<String, Integer> tryToRemove() {
		if (this.state == Robot.ROBOT_SEARCHING) {
			return null;
		}
		HashMap<String, Integer> hash = new HashMap<>();
		if (Pipe.getGlobalMap(x + 1, y) == Map.TARGET) {
			hash.put("x", x + 1);
			hash.put("y", y);
			return hash;
		} else if (Pipe.getGlobalMap(x - 1, y) == Map.TARGET) {
			hash.put("x", x - 1);
			hash.put("y", y);
			return hash;
		} else if (Pipe.getGlobalMap(x, y + 1) == Map.TARGET) {
			hash.put("x", x);
			hash.put("y", y + 1);
			return hash;
		} else if (Pipe.getGlobalMap(x, y - 1) == Map.TARGET) {
			hash.put("x", x);
			hash.put("y", y - 1);
			return hash;
		} else if (Pipe.getGlobalMap(x + 1, y + 1) == Map.TARGET) {
			hash.put("x", x + 1);
			hash.put("y", y + 1);
			return hash;
		} else if (Pipe.getGlobalMap(x - 1, y + 1) == Map.TARGET) {
			hash.put("x", x - 1);
			hash.put("y", y + 1);
			return hash;
		} else if (Pipe.getGlobalMap(x + 1, y - 1) == Map.TARGET) {
			hash.put("x", x + 1);
			hash.put("y", y - 1);
			return hash;
		} else if (Pipe.getGlobalMap(x - 1, y - 1) == Map.TARGET) {
			hash.put("x", x - 1);
			hash.put("y", y - 1);
			return hash;
		}
		return null;
	}

	public String getOrientation() {
		switch (this.orientation) {
		case Robot.ORIENTATION_E:
			return "E";
		case Robot.ORIENTATION_EN:
			return "EN";
		case Robot.ORIENTATION_ES:
			return "ES";
		case Robot.ORIENTATION_N:
			return "N";
		case Robot.ORIENTATION_S:
			return "S";
		case Robot.ORIENTATION_W:
			return "W";
		case Robot.ORIENTATION_WN:
			return "WN";
		case Robot.ORIENTATION_WS:
			return "WS";
		default:
			return "WRONG!";

		}
	}
}
