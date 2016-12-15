import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;
import org.jacop.satwrapper.*;
import org.jacop.jasat.utils.structures.IntVec;
import java.lang.Math;

public class SATPacman {
    private static int n, height, width;
    
    public static void main(String [] args) {
	String fileName = args[0];
	n = Integer.parseInt(args[1]);
	int counter = 0;
	// Precond: have maze file
	//char[][] maze;


	char[][] char_maze = new char[][] {
				{'%','%','%','%','%','%','%','%','%','%','%'},
				{'%','0','0','0','0','0','0','0','0','0','%'},
				{'%','%','%','%','0','%','%','%','%','%','%'},
				{'%','0','O','0','0','0','0','0','0','0','%'},
				{'%','0','0','0','0','0','O','0','0','0','%'},
				{'%','0','0','0','O','0','0','0','0','0','%'},
				{'%','%','%','%','%','%','%','%','%','%','%'}};


	//height = 4; width = 5;
	height = char_maze.length;
	width = char_maze[0].length;
	
	
	// Postcond:  have maze in array

	// Do Shit 

	Store store = new Store();
	SatWrapper satWrapper = new SatWrapper();
	store.impose(satWrapper);					/* Importante: sat problem */


	BooleanVar[][] pacman = new BooleanVar[height][width];
	BooleanVar[][][] ghosts = new BooleanVar[n][height][width];
	BooleanVar[][] maze = new BooleanVar[height][width];
	

	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		pacman[row][col] = new BooleanVar(store, "pacman is at "+row+","+col);
		counter++;
		
		for (int g = 0; g < n;  g++) {
		    ghosts[g][row][col] = new BooleanVar(store, "ghost"+g+" is at "+row+","+col);
		    counter++;
		}
		maze[row][col] = new BooleanVar(store, "wall or capsule at"+row+","+col);
		counter++;
	    }
	}


    	    
	
	BooleanVar[] allVariables = new BooleanVar[counter];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		allVariables[width*i+j] = pacman[i][j];
		satWrapper.register(pacman[i][j]);
		allVariables[width*height + width*i+j] = maze[i][j];
		satWrapper.register(maze[i][j]);
		for (int k = 0; k < n; k++) {
		    allVariables[width * height * 2 + height * width * k + width * i + j] = ghosts[k][i][j];
		    satWrapper.register(ghosts[k][i][j]);

		}
	    }
	}

	
	int pacmanLiteral[][] = new int[height][width];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		pacmanLiteral[i][j] = satWrapper.cpVarToBoolVar(pacman[i][j], 1, true);
	    }
	}

	int ghostLiteral[][][] = new int[n][height][width];
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < height; j++) {
		for (int k = 0; k < width; k++) {
		    ghostLiteral[i][j][k] = satWrapper.cpVarToBoolVar(ghosts[i][j][k], 1, true);
		}
	    }
	}
	int mazeLiteral[][] = new int[height][width];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		//System.out.println(" "+i+","+j+"---"+maze.length+","+maze[0].length);
		mazeLiteral[i][j] = satWrapper.cpVarToBoolVar(maze[i][j], 1, true);
	    }
	}
	


	// do stuff to pacman and ghost.
	addWalls(satWrapper, char_maze,  mazeLiteral, pacmanLiteral, ghostLiteral);
	addPacman(satWrapper, pacmanLiteral, ghostLiteral);
	addGhostsNStuff(satWrapper, ghostLiteral);
	
	//check for consistency
	System.out.println(store.consistency());

	Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();
	SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(allVariables, new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());



		
	Boolean result = search.labeling(store, select);
	prettyPrint(char_maze, pacman, ghosts);

       
	
    }
    /**
     * Helper function to create a CNF clause of an entire Matrix ORed 
     * together. i.e. a CNF clause dictating that there is at least one true value in the matrix
     *
     * @param satWrapper   a JaCoP library wrapper for logical satisfiability problems.
     * @param litMatrix    a matrix of JaCoP literals to be ORed
     */
    public static void addMatrixAtLeastOne(SatWrapper satWrapper, int[][] litMatrix) {
	IntVec clause = new IntVec(satWrapper.pool);
	for(int i = 0; i < litMatrix.length; i++) {
	    for (int j = 0; j < litMatrix[i].length; j++) {
		clause.add(litMatrix[i][j]);
	    }
	}
	satWrapper.addModelClause(clause.toArray());
	
    }

    /**
     * Helper function to create a CNF clause of each value in a matrix XORed 
     * with all other values.  i.e. a CNF clause dictating 
     * that there is at most one true value
     *
     * @param satWrapper   a JaCoP library wrapper for logical satisfiability problems.
     * @param litMatrix    a matrix of JaCoP literals to be XORed
     */
    public static void addMatrixOnlyOne(SatWrapper satWrapper, int[][] litMatrix) {
	for(int i = 0; i < litMatrix.length; i++) {
	    for (int j = 0; j < litMatrix[i].length; j++) {


		/* 
		 * Setting k = i and l = j ensures that no repeats occur.
		 */
		for (int k = i; k < litMatrix.length; k++) {
		    for (int l = j; l < litMatrix[k].length; l++) {

			if (i != k && j != l) {
			    IntVec clause = new IntVec(satWrapper.pool);
					
			    clause.add(-litMatrix[i][j]);
			    clause.add(-litMatrix[k][l]);
			    satWrapper.addModelClause(clause.toArray());	

			}
		    }
		}
		//System.out.println("X:"+i+" Y:"+j);				
	    }
	}
	
    }

    public static void addGhostsNStuff(SatWrapper satWrapper, int[][][] ghostLiteralMatrix) {
	System.out.println("BEFORE TEST");
	for (int h = 0; h < ghostLiteralMatrix.length; h++) {
	    System.out.println(h);
	    addMatrixAtLeastOne(satWrapper, ghostLiteralMatrix[h]);
	    //addMatrixOnlyOne(satWrapper, ghostLiteralMatrix[h]);
	}

	for (int ghost_num = 0; ghost_num < n; ghost_num++) {
	    for (int row_num = 0; row_num < height; row_num++) {
		for (int col_num = 0; col_num < width; col_num++) {


		    
		    for (int ghost_num2 = ghost_num+1; ghost_num2 < n; ghost_num2++) {
			for( int col_num2 = 0; col_num2 < width; col_num2++) {
			    System.out.println("G"+ghost_num+"-"+row_num+","+col_num+" --> ~G"+ghost_num2+""+row_num+""+col_num2);
			    IntVec clause = new IntVec(satWrapper.pool);
			    clause.add(-ghostLiteralMatrix[ghost_num][row_num][col_num]);
			    clause.add(-ghostLiteralMatrix[ghost_num2][row_num][col_num2]);
			    satWrapper.addModelClause(clause.toArray());	

			}
						    
		    }
		    
		}
	    }
	}

	
    }

    public static void addPacman(SatWrapper satWrapper, int[][] pacmanLiteralMatrix, int[][][] ghostLiteralMatrix) {
	addMatrixAtLeastOne(satWrapper, pacmanLiteralMatrix);
	addMatrixOnlyOne(satWrapper, pacmanLiteralMatrix);

	for (int p_row = 0; p_row < height; p_row++) {
	    for (int p_col = 0; p_col < width; p_col++) {

		for (int g = 0; g < n; g++) {
		    for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
			    System.out.println("P-"+p_row+","+p_col+" --> ~G"+g+"-"+Math.abs(p_row-i)%height+","+Math.abs(p_col-j)%width);
			    IntVec clause = new IntVec(satWrapper.pool);
			    clause.add(-pacmanLiteralMatrix[p_row][p_col]);
			    clause.add(-ghostLiteralMatrix[g][Math.abs(p_row-i)%height][Math.abs(p_col-j)%width]);
			    satWrapper.addModelClause(clause.toArray());
			}

		    }
		    
		}
		
	    }
	    
	}
	      
    }



    public static void addWalls(SatWrapper satWrapper, char[][] maze, int[][] mLitMatrix, int[][] pLitMatrix, int[][][] gLitMatrix) {
	int num1 = 0, num2 = 0;

	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		IntVec clause = new IntVec(satWrapper.pool);
		if (maze[row][col] == '%' ||  maze[row][col] == 'O') {
		    clause.add(mLitMatrix[row][col]);
		    num1++;
		} else {
		    clause.add(-mLitMatrix[row][col]);
		    num2++;
		}
		satWrapper.addModelClause(clause.toArray());

		clause = new IntVec(satWrapper.pool);
		clause.add(-mLitMatrix[row][col]);
		clause.add(-pLitMatrix[row][col]);
		satWrapper.addModelClause(clause.toArray());


		for (int g = 0; g < n; g++ ) {
		    clause = new IntVec(satWrapper.pool);
		    clause.add(-mLitMatrix[row][col]);
		    clause.add(-gLitMatrix[g][row][col]);
				

		    satWrapper.addModelClause(clause.toArray());

		}		    
	    }
	    System.out.println("asdf"+num1+"asdf"+num2);
		
	}
    }

    public static void prettyPrint(char[][] maze, BooleanVar[][] pMatrix, BooleanVar[][][] gMatrix) {
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
	        if(pMatrix[row][col].dom().max == 1)
		    maze[row][col] = 'P';
		for (int g = 0; g < n; g++) {
		    if (gMatrix[g][row][col].dom().max == 1) {
			maze[row][col] = 'G';
		    }
		}
		if (maze[row][col] == '0')
		    System.out.print(' ');
		else
		    System.out.print(maze[row][col]);
					     
	    }
	    System.out.println();
			
	    
	}
       
	
    }
}
