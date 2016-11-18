import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;
import org.jacop.satwrapper.*;
import org.jacop.jasat.utils.structures.IntVec;


public class SATPacman {
    
    public static void main(String [] args) {
	String fileName = args[0];
	int n = Integer.parseInt(args[1]);
	int height = 2, width = 3;
	int counter = 0;
	// Precond: have maze file
	char[][] maze;
	// Postcond:  have maze in array

	// Do Shit 

	Store store = new Store();
	SatWrapper satWrapper = new SatWrapper(); 

	//SatTranslation sat = new SatTranslation(store);
	//sat.impose();
	// At least one pacman

	BooleanVar[][] pacman = new BooleanVar[height][width];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		pacman[i][j] = new BooleanVar(store, "pacman is at "+i+","+j);
		counter++;
	    }
	}
	//sat.generate_or(new BooleanVar[] {pacman[0][0]}, pacman[1][1]);
	    
	//BooleanVar[][] pacman = new BooleanVar[height][width](store, "pacman");
	
	// every time, incrase counter
	BooleanVar[] allVariables = new BooleanVar[counter];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		allVariables[height*i+j] = pacman[i][j];
		satWrapper.register(pacman[i][j]);
	    }
	}
	int pacmanLiteral[][] = new int[height][width];
	for (int i = 0; i < height; i++) {
	    for (int j = 0; j < width; j++) {
		pacmanLiteral[i][j] = satWrapper.cpVarToBoolVar(pacman[i][j], 1, true);
	    }
	}

	
       
	
    }

    public static void addMatrix(SatWrapper satWrapper, int[][] litMatrix) {
	IntVec clause = new IntVec(satWrapper.pool);
	for(int i = 0; i < litMatrix.length; i++) {
	    for (int j = 0; j < litMatrix[i].length; j++) {
		clause.add(litMatrix[i][j]);
	    }
	}
	satWrapper.addModelClause(clause.toArray());
    }
}
