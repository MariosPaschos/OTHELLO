import javax.crypto.Mac;

public class TestAI {

    private Board Board;
    private Machine black;
    private Machine white;
    private String AIstrategy1;
    private String AIstrategy2;
    public static int whiteWins = 0;
    public static int blackWins = 0;
    public static int Ties = 0;


    public TestAI(Board Board, Machine player1, Machine player2, String AIstrategy1, String AIstrategy2 ){

        this.Board = new Board();
        this.AIstrategy1 = AIstrategy1;
        this.AIstrategy2 = AIstrategy2;
        black = new Machine(this.Board, 1);
        white = new Machine(this.Board, 2);
    }


    public boolean game(boolean blackplayed, boolean whiteplayed) throws InterruptedException {

        if ((!black.availableMoves() && !white.availableMoves()) || Board.isTerminal()) {
            int result = winnerIs();

            if (result > 0) {
                TestAI.blackWins++;
                //System.out.println("Black wins");
            } else if (result < 0) {
                TestAI.whiteWins++;
                //System.out.println("White wins");
            } else  {
                TestAI.Ties++;
                //System.out.println("Tie");
            }
            return false;
        }

        if (!blackplayed) {
            if (black.availableMoves()) {
                black.aiMakeMove(Board, 5, black.getPlayer(),AIstrategy1);
            }
            return game(true, false);
        }

        else if (!whiteplayed){
            if (white.availableMoves()) {
                white.aiMakeMove(Board, 5, white.getPlayer(),AIstrategy2);
            }
            return game(false, true);
        }
        else return game(true, true);
    }




    public int winnerIs(){

        int result = 3;

        if ( (!black.availableMoves() && !white.availableMoves()) || Board.isTerminal()){

            result = Board.blacksOnBoard().size() - Board.whitesOnBoard().size();
        }

        //Default case if the game has not come to an end
        //System.out.println("Keep playing.");
        return result;
    }


    public int rollout(Board currentBoard, int player) {

        Board simBoard = new Board(currentBoard);
        Machine machine = new Machine(simBoard, player);
        Machine opponent = new Machine(simBoard, machine.getOpponentOf(player));

        if ((!machine.availableMoves() && !opponent.availableMoves()) || simBoard.isTerminal()) {

            int result;

            if (machine.getPlayer() == 1) {

                result = simBoard.blacksOnBoard().size() - simBoard.whitesOnBoard().size();
            }
            else result = simBoard.whitesOnBoard().size() - simBoard.blacksOnBoard().size();


            if (result > 0) {
                System.out.println("Player wins");
                return 1;
            } else if (result < 0) {
                System.out.println("Opponent wins");
                return -1;
            } else  {
                System.out.println("Tie");
                return 0;
            }
        }


        if (machine.availableMoves()) {
            machine.makeRandomMove();
            return rollout(simBoard, opponent.getPlayer());
        }
        else {
            if (opponent.availableMoves()) {
                opponent.makeRandomMove();
            }
            return rollout(simBoard, machine.getPlayer());
        }
    }

    public static void main(String[] args) throws InterruptedException {


        Board Board = new Board();
        String AIstrategy1 = "Monte Carlo Tree Search";
        String AIstrategy2 = "Minimax";
        Machine black = new Machine(Board, 1);
        Machine white = new Machine(Board, 2);

        TestAI test;

        long start = (System.nanoTime());
        System.out.println("Start: " + start);



        for (int i = 0; i< 5; i++) {

            test = new TestAI(Board, black, white, AIstrategy1, AIstrategy2);

            while (test.game(false, false)) {
                test.game(false, false);
            }
            System.out.println("Round: " + i);
        }

        System.out.println("Black wins: " + TestAI.blackWins);
        System.out.println("White wins: " + TestAI.whiteWins);
        System.out.println("Ties: " + TestAI.Ties);


        System.out.println("Time elapsed: " +  (System.nanoTime() - start));


    }


}
