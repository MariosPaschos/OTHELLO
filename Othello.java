import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Othello extends JFrame{

    private Game game;
    private String AIstrategy;
    private int depth;
    private String color;

    public Othello() throws InterruptedException {
        super("Othello");
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900,900);


        // Toolkit is the super class for the Abstract Window Toolkit
        // It allows us to ask questions of the OS
        Toolkit tk = Toolkit.getDefaultToolkit();

        // A Dimension can hold the width and height of a component
        // getScreenSize returns the size of the screen
        Dimension dim = tk.getScreenSize();


        // dim.width returns the width of the screen
        // this.getWidth returns the width of the frame you are making
        int xPos = (dim.width / 2) - (this.getWidth() / 2);
        int yPos = (dim.height / 2) - (this.getHeight() / 2);

        // You could also define the x, y position of the frame
        this.setLocation(xPos, yPos);

        //Popup menu where the human player selects which color he wants to play with, black or white
        String[] colors = {"Black", "White"};
        this.color = (String)JOptionPane.showInputDialog(getContentPane(),
                "Select a color to play with: ",
                "Player selection",
                JOptionPane.PLAIN_MESSAGE,null,
                colors,
                "Black");


        //Popup menu where the human player selects which AI strategy the computer will play with
        String[] AIstrategies = {"Minimax","Alpha-Beta Pruning","Negamax", "Monte Carlo Tree Search",
                                "Multithreaded Minimax","Multithreaded Alpha Beta Pruning", "Multithreaded Negamax",
                                "Multithreaded Monte Carlo Tree Search"};
        this.AIstrategy = (String)JOptionPane.showInputDialog(getContentPane(),
                "Select an AI strategy for the computer player: ",
                "Strategy selection",
                JOptionPane.PLAIN_MESSAGE,null,
                AIstrategies,
                "Minimax");


        //Popup menu where the human player sets depth for the AI to run its algorithm
        String[] depths = {"1", "2", "3", "4","5", "6", "7", "8", "9", "10"};

        String depth = (String) JOptionPane.showInputDialog(getContentPane(),
                "Select the depth for the AI search algorithm: ",
                "Depth selection",
                JOptionPane.PLAIN_MESSAGE,null,
                depths,
                "1");

        this.depth = Integer.parseInt(depth);



        // Initialize the game instance variable with the corresponding color for the human player
        switch (this.color) {
            case "Black":
                try {
                    this.game = new Game(1, this.depth, this.AIstrategy);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case "White":
                try {
                    this.game = new Game(2, this.depth, this.AIstrategy);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }


        // initialize the JButton instance variable.
        JButton resetButton = new JButton("New Game");

        // Add a listener for the reset button.
        resetButton.addMouseListener(
                new MouseAdapter() {
                    public void mousePressed(MouseEvent e){
                        game.board.reset();
                        repaint();
                    }
                }
        );

        getContentPane().add(game, BorderLayout.CENTER);
        getContentPane().add(resetButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        new Othello();


    }
}
