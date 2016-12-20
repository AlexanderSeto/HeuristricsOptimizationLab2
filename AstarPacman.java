import java.lang.Math;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.File;

public class AstarPacman {
    private static int n, height, width;
    private static final int WALL = 0, EMPTY = 1, CAPSULE = 2, GHOST = 4, PACMAN = 8, VISTED = 16;
    public static void main(String [] args) {
	if (args.length < 2) {
	    //throw some sort of exception
	}
	String fileName = args[0];
	String heuristic = args[1];
		    
	if (heuristic.toLowerCase().equals("manhattan")) {
	    n = 1;
	} else if (heuristic.toLowerCase().equals("euclidean")) {
	    n = 2;
	} else {
	    n = 0;
	}
	System.out.println(heuristic + n);

	
	//n = Integer.parseInt(args[1]);
	int counter = 0;

	char[][] char_maze = readMaze(fileName);
	height = char_maze.length;
	width = char_maze[0].length;	
	int[][] maze = parseMaze(char_maze);

	
	//iterate to find all ghosts, add to array
	List<Point> ghosts = new ArrayList<Point>();
	Point pacman = new Point(-1,-1);

	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		if ( (maze[i][j]&GHOST) != 0) {
		    ghosts.add(new Point(i, j));
		} else if ((maze[i][j]&PACMAN) != 0) {
		    pacman = new Point(i, j);
		}
	    }
	}
	if (pacman == new Point(-1, -1)) {
	    System.out.println("No Pacman Found");
	    System.exit(-1);
	} else if (ghosts.size() == 0) {
	    System.out.println("No Ghosts Found");
	    System.exit(-2);
	}
       

	int time = 0;
	int nodes_expanded = 0;
	int path_length = 0;
	int cost = 0;
	
	while(ghosts.size() > 0) {
	    //find closest (manhattans)
	    Point closest = new Point(-1,-1);
	    int closest_index = -1;
	    int min = height+width+1;
	    for (int i = 0; i < ghosts.size(); i++) {
		if (getManhattan(pacman, ghosts.get(i)) < min) {
		    closest = ghosts.get(i);
		    closest_index = i;
		}
	    }
	    // Perform ASTAR

	    PriorityQueue<Tile> open = new PriorityQueue<Tile>((Tile a, Tile b) ->{
		    if (a.finalCost < b.finalCost) {
			return -1;
		    } else if (a.finalCost > b.finalCost) {
			return 1;
		    } else {
			return 0;
		    }
		});
	    boolean[][] closed = new boolean[height][width];
	    
	    Tile firstTile = new Tile(pacman);
	    firstTile.heuristicCost = getF(firstTile.p, closest);
	    firstTile.finalCost = firstTile.heuristicCost;

	    open.add(firstTile);
	    while (true) {
		Tile current = open.poll();
		if (current == null) {
		    break;
		}

		
		if (current.p == closest) {
		    System.out.println("safasdf");
		    break;
		}
		closed[current.p.x][current.p.y] = true;
    
		//open.remove();
		//maze[current.p.x][current.p.y] ^= VISITED;
		//System.out.println(t.toString());
		System.out.println("remain:"+open.size());
		System.out.println("CURRENT"+ current.toString());
		System.out.println("C_COST " + current.finalCost);
		System.out.println(new Tile(closest));
		
		Tile t = new Tile(current.p);
		if (current.p.x > 0 && !closed[current.p.x-1][current.p.y]) {
		    t.p.x = current.p.x-1;
		    t.p.y = current.p.y;
		    t.heuristicCost = getF(t.p, closest);
		    if ((maze[t.p.x][t.p.y]&CAPSULE) != 0)
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 2;
		    else if ((maze[t.p.x][t.p.y]&WALL) != 0)
			t.finalCost = 100000;
		    else
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 4;
		    //System.out.println(t.toString());
		    open.add(t);
		    System.out.println("U_COST " + t.finalCost);

		}
		if (current.p.x < (height - 1) && !closed[current.p.x+1][current.p.y]) {
		    t.p.x = current.p.x+1;
		    t.p.y = current.p.y;
		    t.heuristicCost = getF(t.p, closest);
		    if ((maze[t.p.x][t.p.y]&CAPSULE) != 0)
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 2;
		    else if ((maze[t.p.x][t.p.y]&WALL) != 0)
			t.finalCost = 100000;
		    else
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 4;
		    //System.out.println(t.toString());
				    
		    open.add(t);
		    System.out.println("D_COST " + t.finalCost);
				    
		}
		if (current.p.y > 0 &&  !closed[current.p.x][current.p.y-1]) {
		    t.p.x = current.p.x;
		    t.p.y = current.p.y-1;
		    t.heuristicCost = getF(t.p, closest);
		    if ((maze[t.p.x][t.p.y]&CAPSULE) != 0)
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 2;
		    else if ((maze[t.p.x][t.p.y]&WALL) != 0)
			t.finalCost = 100000;
		    else
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 4;
		    open.add(t);
		    System.out.println("L_COST " + t.finalCost);
				    
		}
		if (current.p.y < (width - 1) &&  !closed[current.p.x][current.p.y+1]) {
		    t.p.x = current.p.x;
		    t.p.y = current.p.y+1;
		    t.heuristicCost = getF(t.p, closest);
		    if ((maze[t.p.x][t.p.y]&CAPSULE) != 0)
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 2;
		    else if ((maze[t.p.x][t.p.y]&WALL) != 0)
			t.finalCost = 100000;
		    else
			t.finalCost = current.finalCost - current.heuristicCost + t.heuristicCost + 4;
		    open.add(t);
		    System.out.println("R_COST " + t.finalCost);
				    
		}
		
	    }

	    pacman = closest;
	    ghosts.remove(closest_index);
	    

	}
	
	

	
    	  
    }
	
    public static int getManhattan(Point a, Point b) {
	return Math.abs(a.x-b.x) + Math.abs(a.y-b.y);
    }

    public static char[][] readMaze(String fileName) {
	ArrayList<String> mazeList = new ArrayList<String>();
	try {
	    File file = new File(fileName);
	    Scanner scanner = new Scanner(file).useDelimiter("\n");
	    while (scanner.hasNext()) {
		String line = scanner.next();
		//System.out.println(line);
		mazeList.add(line);
	    }
	    scanner.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	char[][] maze = new char[mazeList.size()][mazeList.get(0).length()];
	for (int i = 0; i < mazeList.size(); i++) {
	    maze[i] = mazeList.get(i).toCharArray();
	}
	return maze;
    }
    public static int[][] parseMaze(char[][] char_maze) {
	int[][] maze = new int[height][width];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		switch(char_maze[i][j]) {
		case ' ':
		    maze[i][j] ^= EMPTY;
		    break;
		case 'P':
		    maze[i][j] ^= EMPTY;
		    maze[i][j] ^= PACMAN;
		    break;
		case 'G':
		    maze[i][j] ^= EMPTY;
		    maze[i][j] ^= GHOST;
		    break;
		case 'O':
		    maze[i][j] ^= EMPTY;
		    maze[i][j] ^= CAPSULE;
		    break;
		    
		}
	    }
	}
	return maze;
	
    }

    public static int getF(Point s, Point t) {
	if (n == 0) {
	    return 0;
	} else if (n == 1) {
	    return getManhattan(s, t);
	} else {
	    return (int) Math.sqrt((s.x-t.x)*(s.x-t.x)+(s.y-t.y)*(s.y-t.y));
	}
  
    }
}

class Point {
    public int x, y;
    public  Point(int x, int y) {
	this.x = x;
	this.y = y;
    }
}

class Tile{  
    int heuristicCost = 0; //Heuristic cost
    int finalCost = 0; //G+H
    Point p;
    Tile parent; 
    
    Tile(Point p){
	this.p = p;
    }
    
        @Override
        public String toString(){
            return "["+this.p.x+", "+this.p.y+"]";
        }
    }
