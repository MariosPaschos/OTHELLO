import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;


public class Board extends JPanel {

    //Othello board and board states
    private int[] board;

    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int OUTER = -1;

    private ArrayList<Integer> blacks = new ArrayList<>();
    private ArrayList<Integer> whites = new ArrayList<>();

    private ArrayList<Integer> validSquares = new ArrayList<>();

    private final static int[] WEIGHTS = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 120, -20, 20, 5, 5, 20, -20, 120, 0,
            0, -20, -40, -5, -5, -5, -5, -40, -20, 0,
            0, 20, -5, 15, 3, 3, 15, -5, 20, 0,
            0, 5, -5, 3, 3, 3, 3, -5, 5, 0,
            0, 5, -5, 3, 3, 3, 3, -5, 5, 0,
            0, 20, -5, 15, 3, 3, 15, -5, 20, 0,
            0, -20, -40, -5, -5, -5, -5, -40, -20, 0,
            0, 120, -20, 20, 5, 5, 20, -20, 120, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0};



    //Board class constructor
    public Board() {

        this.board = new int[100];
        reset();

    }

    //Board class copy constructor
    public Board(Board initialBoard){

        super();
        this.board = Arrays.copyOf(initialBoard.board, 100);
        this.validSquares  = initialBoard.getValidSquares();
    }


    public void copyBoard(){}

    //Re-initialize the board
    public void reset() {

        for (int i = 0; i <= 9; i++) {
            this.board[i] = OUTER;
        }

        for (int i = 10; i <= 89; i++) {
            if ((i % 10 >= 1 && i % 10 <= 8)) {
                this.board[i] = EMPTY;
                validSquares.add(i);
            }
            else {
                this.board[i] = OUTER;
            }
        }

        for (int i = 90; i <= 99; i++) {
            this.board[i] = OUTER;
        }

        //Initialise board
        this.board[44] = WHITE;
        this.board[45] = BLACK;
        this.board[54] = BLACK;
        this.board[55] = WHITE;
    }


    public static boolean isValid(int square){

        return  ((square >= 10) && (square <= 89) && (square % 10 >= 1) && (square % 10 <= 8));
    }

    public void setToBlack(int square){

        this.board[square] = BLACK;
    }

    public void setToWhite(int square){

        this.board[square] = WHITE;
    }

    public int[] getBoard(){
        return board;
    }

    public static int getSquare(int column, int row){

        String x = Integer.toString(row);
        String y = Integer.toString(column);

        return Integer.parseInt(x+y);
    }

    public ArrayList<Integer> blacksOnBoard(){

        blacks.clear();

        for (int i = 11; i < 89; i++){
            if (this.board[i] == BLACK) {
                blacks.add(i);
            }
        }
        return blacks;
    }

    public ArrayList<Integer> whitesOnBoard(){

        whites.clear();

        for (int i = 11; i < 89; i++){
            if (this.board[i] == WHITE) {
                whites.add(i);
            }
        }
        return whites;
    }

    @Override
    public String toString() {
        return "Board: " + board;
    }

    public ArrayList<Integer> getValidSquares(){

        return validSquares;
    }

    public boolean isTerminal(){

        return blacksOnBoard().size() + whitesOnBoard().size() == 64;
    }





    //Returns the score for the current board state
    public static int evalStateOfBoard(int[] board, int player){

        int whiteScore = 0;
        int blackScore = 0;
        int score = 0;

        for (int square = 11; square < 89; square++) {

            switch (board[square]) {

                case BLACK:
                    blackScore += WEIGHTS[square];
                    break;

                case WHITE:
                    whiteScore += WEIGHTS[square];
                    break;
            }
        }

        if ((blackScore + whiteScore) != 0) {
            switch (player) {
                case 1:
                    score = 100 * (blackScore - whiteScore) / (blackScore + whiteScore);
                    break;
                case 2:
                    score = 100 * (whiteScore - blackScore) / (blackScore + whiteScore);
                    break;
            }
            return score;
        }
        else return 0;
    }



    public static int evalDiskParity(int[] board, int player){

        int score = 0;
        int blacks = 0;
        int whites = 0;

        for (int square = 11; square < 89; square++) {

            switch (board[square]) {

                case BLACK:
                    blacks++;
                    break;

                case WHITE:
                    whites++;
                    break;
            }
        }

        switch (player) {
            case 1:
                score = (blacks - whites)/(blacks + whites);
                break;
            case 2:
                score = (whites - blacks)/(blacks + whites) ;
                break;
        }
        return score;
    }



    public static int evalMobility(Board board, int player){

        Machine machine1 = new Machine(board, player);
        Machine machine2 = new Machine(board, machine1.getOpponent());

        int actualMobilityBlack;
        int actualMobilityWhite;

        int potentialMobilityBlack = 0;
        int potentialMobilityWhite = 0;

        int actualMobility;
        int potentialMobility;

        //Actual mobility - All the legal moves a player can perform
        if (machine1.getPlayer() == 1) {
            actualMobilityBlack = machine1.possibleMoves().size();
            actualMobilityWhite = machine2.possibleMoves().size();
        }
        else {
            actualMobilityBlack = machine2.possibleMoves().size();
            actualMobilityWhite = machine1.possibleMoves().size();
        }

        //Potential mobility - All the empty squares that lead to potentially legal moves for the player in the future
        for (int square = 11; square < 89; square++) {

            if (board.getBoard()[square] == 0){

                //Potential moves for the player
                for (int dir : Player.DIRECTIONS){
                    if (board.getBoard()[square + dir] == machine2.getPlayer()){
                        if (machine1.getPlayer() == 1){
                            potentialMobilityBlack++;
                        }
                        else potentialMobilityWhite++;
                    }
                }
                //Potential moves for the opponent
                for (int dir : Player.DIRECTIONS){
                    if (board.getBoard()[square + dir] == machine1.getPlayer()){
                        if (machine2.getPlayer() == 1){
                            potentialMobilityBlack++;
                        }
                        else potentialMobilityWhite++;
                    }
                }

            }
        }

        if ( ( (actualMobilityBlack + actualMobilityWhite) > 0 )  && ( (potentialMobilityBlack + potentialMobilityWhite) > 0) ) {

            if (player == 1) {
                actualMobility = 100 * (actualMobilityBlack - actualMobilityWhite) / (actualMobilityBlack + actualMobilityWhite);
                potentialMobility = 100 * (potentialMobilityBlack - potentialMobilityWhite) / (potentialMobilityBlack + potentialMobilityWhite);
            } else {
                actualMobility = 100 * (actualMobilityWhite - actualMobilityBlack) / (actualMobilityBlack + actualMobilityWhite);
                potentialMobility = 100 * (potentialMobilityWhite - potentialMobilityBlack) / (potentialMobilityBlack + potentialMobilityWhite);
            }

            //Just an evaluation metric for mobility
            //return 100 * (actualMobility / potentialMobility);

            return actualMobility;
            //return potentialMobility;

        }
        else return 0;
    }


    public static int evalMobility2(Board board, int player){

        Machine machine1 = new Machine(board, player);
        Machine machine2 = new Machine(board, machine1.getOpponent());

        int actualMobilityBlack;
        int actualMobilityWhite;

        int potentialMobilityBlack = 0;
        int potentialMobilityWhite = 0;

        int actualMobility;
        int potentialMobility;

        //Actual mobility - All the legal moves a player can perform
        if (machine1.getPlayer() == 1) {
            actualMobilityBlack = machine1.possibleMoves().size();
            actualMobilityWhite = machine2.possibleMoves().size();
        }
        else {
            actualMobilityBlack = machine2.possibleMoves().size();
            actualMobilityWhite = machine1.possibleMoves().size();
        }

        //Potential mobility - All the empty squares that lead to potentially legal moves for the player in the future
        for (int square = 11; square < 89; square++) {

            if (board.getBoard()[square] == 0){

                //Potential moves for the player
                for (int dir : Player.DIRECTIONS){
                    if (board.getBoard()[square + dir] == machine2.getPlayer()){
                        if (machine1.getPlayer() == 1){
                            potentialMobilityBlack++;
                        }
                        else potentialMobilityWhite++;
                    }
                }
                //Potential moves for the opponent
                for (int dir : Player.DIRECTIONS){
                    if (board.getBoard()[square + dir] == machine1.getPlayer()){
                        if (machine2.getPlayer() == 1){
                            potentialMobilityBlack++;
                        }
                        else potentialMobilityWhite++;
                    }
                }

            }
        }

        if ( ( (actualMobilityBlack + actualMobilityWhite) > 0 )  && ( (potentialMobilityBlack + potentialMobilityWhite) > 0) ) {

            if (player == 1) {
                actualMobility = 100 * (actualMobilityBlack - actualMobilityWhite) / (actualMobilityBlack + actualMobilityWhite);
                potentialMobility = 100 * (potentialMobilityBlack - potentialMobilityWhite) / (potentialMobilityBlack + potentialMobilityWhite);
            } else {
                actualMobility = 100 * (actualMobilityWhite - actualMobilityBlack) / (actualMobilityBlack + actualMobilityWhite);
                potentialMobility = 100 * (potentialMobilityWhite - potentialMobilityBlack) / (potentialMobilityBlack + potentialMobilityWhite);
            }

            //Just an evaluation metric for mobility
            //return 100 * (actualMobility / potentialMobility);

            //return actualMobility;
            return potentialMobility;

        }
        else return 0;
    }


}



