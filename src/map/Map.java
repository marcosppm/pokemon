package map;

import java.util.Random;

public class Map extends Object {
	private final static int COMPRIMENTO = 10;
	private final static int LARGURA = 10;
	
	private static int[][] map = new int[COMPRIMENTO][LARGURA];

	public static void generateMap() {
		Random random = new Random();
		int randomNumber = 0;
		for (int i = 0; i < Map.COMPRIMENTO; i++) {
			for (int j = 0; j < Map.LARGURA; j++) {
				randomNumber = random.nextInt(2);
				map[i][j] = randomNumber;
			}
		}
	}
	
	public static Direction randomDirection() {
		Random random = new Random();
		int randomNumber = random.nextInt(3);
		return Direction.values()[randomNumber];
	}
	
	public static Floor getFloor(int x, int y) {
		return Floor.values()[map[x][y]];
	}
	
	public static void printMap(int xPlayer, int yPlayer) {
		for (int i = 0; i < Map.COMPRIMENTO; i++) {
			for (int j = 0; j < Map.LARGURA; j++) {
				if (i == xPlayer && j == yPlayer) {
					System.out.print("P ");
				} else {
					System.out.print(map[i][j] + " ");
				}
			}
			System.out.println();
		}
	}
	
	public static int getComprimento() {
		return COMPRIMENTO;
	}

	public static int getLargura() {
		return LARGURA;
	}
	
	public enum Direction {
		UP, RIGHT, DOWN, LEFT
	}

	public enum Floor {
		GROUND, GRASS
	}
}
