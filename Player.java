
public class Player {

    private Board board;
    private int player;
    private int opponent;

    public static final int EMPTY = 0;

    //Directions
    public static final int[] DIRECTIONS = {-11, -10, -9, -1, 1, 9, 10, 11};


    public Player(Board board, int Player){

        this.board = board;
        this.player = Player;


        //For black the variable player equals 1 and for white the variable player equals 2
        switch (Player) {
            case 1:
                this.opponent = 2;
                break;
            case 2:
                this.opponent = 1;
                break;
        }
    }

    public int getPlayer(){
        return player;
    }

    public int getOpponent(){
        return opponent;
    }

    public String getColor(int player){

        if (player == 1){
            return "Black";
        }
        else {
            return "White";
        }
    }

    //Checking the move is within the board limits
    public boolean validMove(int square) {

        return (square >= 11) && (square <= 88) && (square % 10 >= 1) && (square % 10 <= 8);
    }

    public int flipsInDirection(int square, int dir) {

        int count = 0;
        int flips = 0;

        if (board.getBoard()[(square + dir)] == this.opponent) {

            while (board.getBoard()[square + dir] == this.opponent) {
                square = (square + dir);
                flips++;

            }
            if (board.getBoard()[(square + (dir * count) + dir)] == this.player){
                count = flips;
            }
        }
        return count;
    }

    //Checking the move is legal (in compliance with the rules of the game)
    public boolean legalMove(int square){

        int legalMoves = 0;

        for (int dir: DIRECTIONS) {

            if ( (this.board.getBoard()[square] == EMPTY) && (flipsInDirection(square, dir) > 0) ) {
                legalMoves++;
            }
        }
        return legalMoves > 0;
    }

    public boolean availableMoves(){

        int square = 11; //The first valid and legal square

        //Search the board for at least one legal move
        while ((square <= 88) && (!legalMove(square))) {
            square++;
        }

        //If not even one available legal move is found then return false
        return square <= 88;
    }


    //Human makes a move by clicking on a square from the board
    public void makeMove(int square) {

        if (legalMove(square)) {

            for (int dir : DIRECTIONS) {

                int count = flipsInDirection(square, dir);

                if (count > 0) {

                    switch (this.player) {

                        case 1:
                            for (int n = 0; n < count + 1; n++) {
                                this.board.setToBlack(square + dir * n);
                            }
                            break;

                        case 2:
                            for (int n = 0; n < count + 1; n++) {
                                this.board.setToWhite(square + dir * n);
                            }
                            break;
                    }
                }
            }
        }
    }

}
