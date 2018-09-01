public class Human extends Player{

    private Board board;
    private int player;
    private int opponent;

    public Human(Board board, int player) {
        super(board, player);

        this.board = board;
        this.player = player;

        //For black the variable player equals 1 and for white the variable player equals 2
        switch (player) {
            case 1:
                this.opponent = 2;
                break;

            case 2:
                this.opponent = 1;
                break;
        }
    }

}



