import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;
import org.jacop.satwrapper.*;
import org.jacop.jasat.utils.structures.IntVec;
import java.lang.Math;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import java.io.FileNotFoundException;
import java.io.File;

public class SATPacman {
    private static int n, height, width;
    private static final int WALL = 0, EMPTY = 1, CAPSULE = 2, GHOST = 4, PACMAN = 8;
    
    public static void main(String [] args) {
	if (args.length < 2) {
	    //throw some sort of exception
	}
	String fileName = args[0];
	String heuristic = args[1];
	//n = Integer.parseInt(args[1]);
	int counter = 0;

	char_maze = readMaze(fileName);
	height = char_maze.length;
	width = char_maze[0].length;
	
	int[][] maze= parseMaze(char_maze);
	


    	  
    }


    public static char[][] readMaze(String fileName) {
	ArrayList<String> mazeList = new ArrayList<String>();
	try {
	    File file = new File(fileName);
	    Scanner scanner = new Scanner(file);
	    while (scanner.hasNext()) {
		String line = scanner.next();
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
}
