package gui;

//SudokuSolver löst ein Sudoku mit vorgegebenen Größen (Höhe, Breite, Teiler der beiden Achsen)

public class SudokuSolver extends Thread {
    private static int[][] field;
    private static int divader = 3, 
	    fieldRows = 9, fieldCols = 9;
    private static String[][] candidateLists = new String[9][9];
    
    private SudokuSolver(){}
    
    public static int[][] solveSudoku(int[][] f) {return solveSudoku(f, 3);}
    
    public static int[][] solveSudoku(int[][] f, int d) {
	field = f;
	fieldRows = field.length;
	fieldCols = field[0].length;
	if(fieldRows != fieldCols || field == null) return null;
	divader = d;
	
	//Prüfung auf Gültigkeit des Feldes
	for(int i = 0; i<fieldRows; i++)
	    if(fieldRows%divader != 0 || fieldCols%divader != 0 || fieldCols != field[i].length)
		return null;
	

	if(!checkCandidates()) return null;
	
	//Prüfung auf Richtigkeit des Feldes
	for(int i = 0; i<fieldRows; i++ ) {
	    for(int j = 0; j<fieldCols; j++) {
		if(field[i][j] == 0) continue;
		//Y
		for(int l = 0; l<fieldRows; l++) {
		    if(i==l) continue;
		    if(field[i][j] == field[l][j])
			return null;
		}
		
		//X
		for(int l = 0; l<fieldCols; l++) {
		    if(j==l) continue;
		    if(field[i][j] == field[i][l])
			return null;
		}
		
		//Felder
		int rowS, colS;
		rowS = i - i%(fieldRows/divader);
		colS = j - j%(fieldCols/divader);
		for(int l = 0; l<(fieldRows/divader); l++) {
		    for(int k = 0; k<(fieldCols/divader); k++) {
			if(i%(fieldRows/divader)==l && j%(fieldCols/divader)==k) continue;
			if(field[i][j] == field[l+rowS][k+colS]) {
			    return null;
			}
		    }
		}
	    }
	}
	
	
	if(solve())
	    return field;
	return null;
	
    }
    
    private static boolean solve() {
	int row, col;
	if(!(nextEmptyField() == null)) {
	    int[] nef = nextEmptyField();
	    row = nef[0];
	    col = nef[1];
	} else
	    return true;
	
	for(int i = 1; i<=fieldRows; i++) {
	    if(isSave(row, col, i)) {
		field[row][col] = i;
		
		if(solve()) return true;
		
		field[row][col] = 0;
	    }
	}
	return false;
    }
    
    private static int[] nextEmptyField() {
	for(int i = 0; i<fieldRows; i++)
	    for(int j = 0; j<fieldCols; j++)
		if(field[i][j] == 0)
		    return new int[] {i, j};
	return null;
    }
    
    private static boolean isSave(int row, int col, int num) {
	if(checkRow(row, num) && checkCol(col, num) && checkField(row - row%(fieldRows/divader), col - col%(fieldCols/divader), num))
	    return true;
	return false;
    }
    private static boolean checkRow(int row, int num) {
	for(int i = 0; i<fieldRows; i++)
	    if(field[row][i] == num)
		return false;
	return true;
    }
    private static boolean checkCol(int col, int num) {
	for(int i = 0; i<fieldCols; i++)
	    if(field[i][col] == num)
		return false;
	return true;
    }
    private static boolean checkField(int rowS, int colS, int num) {
	for(int i = 0; i<fieldRows/divader; i++)
	    for(int j = 0; j<fieldCols/divader; j++)
		if(field[rowS+i][colS+j] == num)
		    return false;
	return true;
    }
    
    private static boolean checkCandidates() {
	createCandidateList();
	fillCandidateLists();
	return checkCandidateList();
    }
    private static void createCandidateList() {
	for(int i = 0; i<fieldRows; i++)
	    for(int j = 0; j<fieldCols; j++) {
		candidateLists[i][j] = "";
		for(int l = 1; l<=fieldRows; l++) 
		    if(isSave(i, j, l)) candidateLists[i][j] += l;
	    }
    }
    private static boolean checkCandidateList() {
	for(int i = 0; i<fieldRows; i++)
	    for(int j = 0; j<fieldCols; j++)
		if(candidateLists[i][j].equals("") && field[i][j] == 0) return false;
	return true;
    }

    private static void fillCandidateLists() {
	for(int i = 0; i<fieldRows; i++)
	    for(int j = 0; j<fieldCols; j++)
		if(candidateLists[i][j].length() == 1 && field[i][j] == 0)
		    field[i][j] = Integer.parseInt(candidateLists[i][j]);
    }
}
