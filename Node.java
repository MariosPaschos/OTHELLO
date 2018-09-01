
import javax.crypto.Mac;
import java.util.ArrayList;

import static java.lang.Double.NaN;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;


public class Node {

    private Node parent;
    private int square;
    private int eval;
    private String name;

    private Board board;
    private double visits;
    private double wins;
    private double UCB1val;
    private int player;



    //Default constructor
    public Node(){
        this.name = null;
    }

    //Node constructor for use in the Monte Carlo Tree Search
    public Node(Board board){

        this.board = board;
        this.wins = 0;
        this.visits = 0;
        this.UCB1val = Double.POSITIVE_INFINITY;
    }

    //Copy constructor
    public Node(Node node){

        this.board = node.getBoard();
        this.parent = node.getParent();
        this.name = node.getName();
        this.square = node.getSquare();
        this.visits = node.getVisits();
        this.wins = node.getWins();
        this.UCB1val = node.getUCB1val();
    }

    private ArrayList<Node> children = new ArrayList<>();

    public void setParent(Node parent){
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public void setSquare(int square) {
        this.square = square;
    }

    public int getSquare() {
        return square;
    }

    public void setEval(int eval) {
        this.eval = eval;
    }

    public int getEval() {
        return eval;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void addChild(Node child){

        if (!children.contains(child)) {
            child.setParent(this);
            this.getChildren().add(child);
        }
    }

    @Override
    public String toString() {
        //return "(" + name +"\nSquare: " + square + "\nEval: " + eval + "\nUCB1val: " + UCB1val + "\nParent: " + parent.getName() + ")" + "\n";

        return "(" + name +"\nSquare: " + square + "\nUCB1val: " + UCB1val + ")";
    }



    //Monte Carlo node methods
    public boolean isLeaf(){
        return this.getChildren().size() == 0;
    }

    public boolean isVisited(){
        return this.getVisits() != 0;
    }

    public double getVisits(){
        return visits;
    }

    public double getWins(){
        return wins;
    }

    public void backPropagation(int winner){

        Node node = this;

        while (node != null){

            node.visits += 1.0;

            if (node.getPlayer() == winner) {
                node.wins += 1.0;
            }
            else {
                if (winner == 0){
                    node.wins += 0.5;
                }
            }
            node = node.getParent();
        }
    }

    public void setBoard(Board board){
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setPlayer(int player){
        this.player = player;
    }

    public int getPlayer(){
        return player;
    }

    public int getOpponent(){
        return this.getPlayer() == 1 ? 2 : 1;
    }

    public void setUCB1val(double UCB1){

        this.UCB1val = UCB1;
    }

    public double getUCB1val(){

        return UCB1val;
    }

    public double updateUCB1val(Tree tree){

        double UCB1 = this.calculateUCB1val(tree);
        if (UCB1 == NaN) {
            return Double.POSITIVE_INFINITY;
        }
        else {
            this.setUCB1val(UCB1);
            return this.UCB1val;
        }
    }

    public double calculateUCB1val(Tree tree){

        double w = this.getWins();
        double n = this.getVisits();
        double C = 1 / sqrt(2); //The value Cp = 1/ √ 2 was shown by Kocsis and Szepesvari [120] to satisfy the Hoeffding ineqality with rewards in the range [0, 1].
        double N = tree.getRoot().getVisits();

        if (this.getPlayer() == tree.getRoot().getPlayer()){
            return (w/n) + C * sqrt(log(N/n));
        }
        else{
            return (1-(w/n)) + C * sqrt(log(N/n));
        }
    }

}