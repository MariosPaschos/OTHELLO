import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;


public class Game extends JPanel {

    //Othello board and board states
    public Board board;

    private Human human;
    private Machine machine;

    private int turn;
    private int AIdepth;
    private String AIstrategy;

    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private final static int SpaceSize = 80;
    private final static int CellPadding = 5;


    //Game class constructor
    public Game(int humanPlayer, int depth, String AIstrategy)  throws InterruptedException{

        super();

        board = new Board();
        board.reset();

        //The max depth as chosen by the player
        this.AIdepth = depth;

        //The AI strategy as chosen by the human player
        this.AIstrategy = AIstrategy;

        switch (humanPlayer) {

            case 1:
                this.human = new Human(board, 1); //Human plays with black
                this.machine = new Machine(board, 2); //Machine plays with white

                setCurrentPlayer(this.human);
                break;

            case 2:
                this.machine = new Machine(board, 1); //Machine plays with black
                this.human = new Human(board, 2); //Human plays with white

                setCurrentPlayer(this.machine);

                this.machine.aiMakeMove(board, AIdepth, 1, AIstrategy);
                repaint();
                setCurrentPlayer(this.human);
                break;
        }
        addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {

                int squareClicked = Board.getSquare(e.getX() / SpaceSize, e.getY() / SpaceSize);

                if (isGameOver()) {
                    return;
                }
                else if (!isCurrentPlayer(human)){
                    System.out.println("AI plays! Wait for your turn");
                    JOptionPane.showMessageDialog(null, "AI plays! Wait for your turn");
                    return;
                }
                else if (!(human.availableMoves())) {
                    System.out.println("No moves for human player! AI plays!");
                    JOptionPane.showMessageDialog(null, "No moves for human player! AI plays!");

                    setCurrentPlayer(machine);

                    //If human player has no moves to play, the AI plays its turn
                    if (machine.availableMoves()) {
                        setCurrentPlayer(machine);
                        try {
                            machine.aiMakeMove(board, AIdepth, machine.getPlayer(), AIstrategy);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        repaint();

                        setCurrentPlayer(human);
                        return;
                    }
                    //If none of the players has any moves to play the game is over
                    else {
                        isGameOver();
                        return;
                    }
                }
                else if (!human.validMove(squareClicked)) {
                    System.out.println("Please select a valid square inside the Othello board!");
                    JOptionPane.showMessageDialog(null, "Please select a valid square inside the Othello board!");
                    return;
                }
                else if (!human.legalMove(squareClicked)) {
                    System.out.println("Move not legal!");
                    JOptionPane.showMessageDialog(null, "Move not legal!");
                    return;
                }
                else {
                    //Human player makes a move
                    human.makeMove(squareClicked);
                    repaint();
                    isGameOver();
                    setCurrentPlayer(machine);

                    if (machine.availableMoves()) {
                        //Machine player plays
                        try {
                            machine.aiMakeMove(board, AIdepth, machine.getPlayer(), AIstrategy);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        repaint();
                        isGameOver();
                        setCurrentPlayer(human);


                        while (!human.availableMoves()){
                            if (machine.availableMoves()) {
                                setCurrentPlayer(machine);
                                try {
                                    machine.aiMakeMove(board, AIdepth, machine.getPlayer(), AIstrategy);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                repaint();
                                isGameOver();
                                setCurrentPlayer(human);
                            }
                            else return;
                        }
                        return;
                    }
                    else return;
                }
            }
        });
    }


    public void setCurrentPlayer(Player player){

        turn = player.getPlayer();
    }

    public boolean isCurrentPlayer(Player player){

        return player.getPlayer() == turn;
    }

    public int gameStatus(){

        int result;

        if( (!human.availableMoves() && !machine.availableMoves()) || board.isTerminal()){

            result = board.blacksOnBoard().size() - board.whitesOnBoard().size();

            if (result == 0) {
                System.out.println("Tie");
                JOptionPane.showMessageDialog(null, "Tie");
                return 0;
            }
            else if (result > 0) {
                System.out.println("Black won");
                JOptionPane.showMessageDialog(null, "Black won");
                return 1;
            }
            else {
                System.out.println("White won");
                JOptionPane.showMessageDialog(null, "White won");
                return 2;
            }
        }
        else {
            //Default case if the game has not come to an end return 3
            return 3;
        }
    }

    public boolean isGameOver(){

        switch (gameStatus()){

            //No victor. Game is tie.
            case 0:
                System.out.println("The game has ended. Tie! ");
                return true;

            //Black won.
            case 1:
                System.out.println("The game has ended. Black won! ");
                return true;

            //White won.
            case 2:
                System.out.println("The game has ended. White won! ");
                return true;

            default:
                System.out.println("Game in progress! Keep playing!");
                return false;
        }
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        //Board background color
        g.setColor(Color.GREEN.darker());
        g.fillRect(SpaceSize, SpaceSize, SpaceSize * 8, SpaceSize * 8);

        //Borders
        g.setColor(Color.BLACK);
        g.fillRect(0,0,SpaceSize * 10, SpaceSize);
        g.fillRect(0,SpaceSize * 9,SpaceSize * 10, SpaceSize);

        g.fillRect(0,0, SpaceSize, SpaceSize * 10);
        g.fillRect(SpaceSize * 9,0, SpaceSize, SpaceSize * 10);

            /*
            drawLine( int x1, int y1, int x2, int y2)
            Draws a line, using the current color, between the points (x1, y1) and (x2, y2)
            in the graphics context's coordinate system
            */

        g.setColor(Color.BLACK);

        //Draw Vertical Lines
        g.drawLine(0, 0, 0, SpaceSize * 10);
        g.drawLine(SpaceSize, 0, SpaceSize, SpaceSize * 10);
        g.drawLine(SpaceSize * 2, 0, SpaceSize * 2, SpaceSize * 10);
        g.drawLine(SpaceSize * 3, 0, SpaceSize * 3, SpaceSize * 10);
        g.drawLine(SpaceSize * 4, 0, SpaceSize * 4, SpaceSize * 10);
        g.drawLine(SpaceSize * 5, 0, SpaceSize * 5, SpaceSize * 10);
        g.drawLine(SpaceSize * 6, 0, SpaceSize * 6, SpaceSize * 10);
        g.drawLine(SpaceSize * 7, 0, SpaceSize * 7, SpaceSize * 10);
        g.drawLine(SpaceSize * 8, 0, SpaceSize * 8, SpaceSize * 10);
        g.drawLine(SpaceSize * 9, 0, SpaceSize * 9, SpaceSize * 10);
        g.drawLine(SpaceSize * 10, 0, SpaceSize * 10, SpaceSize * 10);

        //Draw horizontal Lines
        g.drawLine(0, 0, SpaceSize * 10, 0);
        g.drawLine(0, SpaceSize, SpaceSize * 10, SpaceSize);
        g.drawLine(0, SpaceSize * 2, SpaceSize * 10, SpaceSize * 2);
        g.drawLine(0, SpaceSize * 3, SpaceSize * 10, SpaceSize * 3);
        g.drawLine(0, SpaceSize * 4, SpaceSize * 10, SpaceSize * 4);
        g.drawLine(0, SpaceSize * 5, SpaceSize * 10, SpaceSize * 5);
        g.drawLine(0, SpaceSize * 6, SpaceSize * 10, SpaceSize * 6);
        g.drawLine(0, SpaceSize * 7, SpaceSize * 10, SpaceSize * 7);
        g.drawLine(0, SpaceSize * 8, SpaceSize * 10, SpaceSize * 8);
        g.drawLine(0, SpaceSize * 9, SpaceSize * 10, SpaceSize * 9);
        g.drawLine(0, SpaceSize * 10, SpaceSize * 10, SpaceSize * 10);



        //Draw blacks and whites
        for(int row=0; row<10; row++) {
            for (int col=0; col<10; col++) {

                switch (board.getBoard()[Board.getSquare(row,col)]){

                    case BLACK:
                        // Draw a black on the board.
                        g.setColor(Color.BLACK);
                        g.drawOval( SpaceSize * row + CellPadding,
                                SpaceSize * col + CellPadding,
                                SpaceSize - CellPadding * 2,
                                SpaceSize - CellPadding * 2);
                        g.fillOval(SpaceSize * row + CellPadding,
                                SpaceSize * col + CellPadding,
                                SpaceSize - CellPadding * 2,
                                SpaceSize - CellPadding * 2);
                        break;

                    case WHITE:
                        // Draw a White on the board.
                        g.setColor(Color.WHITE);
                        g.drawOval( SpaceSize * row + CellPadding,
                                SpaceSize * col + CellPadding,
                                SpaceSize - CellPadding * 2,
                                SpaceSize - CellPadding * 2);
                        g.fillOval(SpaceSize * row + CellPadding,
                                SpaceSize * col + CellPadding,
                                SpaceSize - CellPadding * 2,
                                SpaceSize - CellPadding * 2);
                        break;
                }
            }
        }
    }

}



