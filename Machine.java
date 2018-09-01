import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Machine extends Player {

    private Board board;
    private int player;
    private int opponent;
    //private long timeout = System.currentTimeMillis() + 500;

    private ArrayList<Integer> squaresToPlay = new ArrayList<>();


    public Machine(Board board, int player) {

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


    public int getPlayer(){
        return player;
    }

    public int getOpponentOf(int player){

        int opponent = 0;

        switch (player){
            case 1:
                opponent = 2;
                break;
            case 2:
                opponent = 1;
                break;
        }
        return opponent;
    }

    //Searches around the blacks on board for possible moves for white to play
    //Stores the legal squares to the list squaresToPlay
    public ArrayList<Integer> possibleMoves() {

        squaresToPlay.clear();

        for (int i : this.board.getValidSquares()){
            if (legalMove(i)){
                if (!squaresToPlay.contains(i)){
                    squaresToPlay.add(i);
                }
            }
        }
        return squaresToPlay;
    }



    //Returns the node with the best value
    public static Node getMax(ArrayList<Node> children){

        Node maxNode = new Node();

        maxNode.setParent(null);
        maxNode.setSquare(-1);
        maxNode.setEval((int)(Float.NEGATIVE_INFINITY));

        for (Node node : children){

            if (node.getEval() > maxNode.getEval()){
                maxNode.setEval(node.getEval());
                maxNode.setSquare(node.getSquare());
                maxNode.setParent(node.getParent());
            }
        }

        return maxNode;
    }

    //Returns a list which containing the nodes with the best values. Some may share the same value
    public static ArrayList<Node> getBestMoves(ArrayList<Node> children){

        Node maxNode = new Node();

        maxNode.setParent(null);
        maxNode.setSquare(-1);
        maxNode.setEval((int)(Float.NEGATIVE_INFINITY));

        //The list will contain all the moves with the same best value. It may have only one element
        ArrayList<Node> bestMoves = new ArrayList<>();

        for (Node node : children){

            //Finding the max for the parent node
            if (node.getEval() > maxNode.getEval()){
                maxNode.setEval(node.getEval());
                maxNode.setSquare(node.getSquare());
                maxNode.setParent(node.getParent());
            }
        }

        //After finding the maximum for the parent node, we add all the nodes with value equal to maxNode's to the bestMoves list
        for (Node node: children){
            if (node.getEval() == maxNode.getEval()){
                bestMoves.add(node);
            }
        }

        return bestMoves;
    }


    //AI algorithms
    public int Minimax(Board currentBoard, int depth, int player, Node node){

        int bestEval;

        Machine machine = new Machine(currentBoard, player);

        //Evaluate leaf node
        if (machine.possibleMoves().size() == 0 || (depth == 0)) {

            return Board.evalStateOfBoard(currentBoard.getBoard(), machine.getPlayer());
            //return Board.evalDiskParity(currentBoard.getBoard(), machine.getPlayer());
            //return Board.evalMobility(currentBoard, machine.getPlayer());
            //return Board.evalMobility2(currentBoard, machine.getPlayer());
        }

        //Maximizing player
        if (player == this.getPlayer()){

            bestEval = (int)(Float.NEGATIVE_INFINITY);
            node.setEval(bestEval);

            for (int i : machine.possibleMoves()) {

                Board simBoard = new Board(currentBoard);
                Machine tempMachine = new Machine(simBoard, player);
                tempMachine.makeMove(i);

                Node child = new Node();
                node.addChild(child);
                child.setSquare(i);
                child.setEval(Minimax(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child));

                if (child.getEval() > node.getEval()){
                    bestEval = child.getEval();
                    node.setEval(bestEval);
                }
            }
            return node.getEval();
        }

        //Minimizing player Black
        else {

            bestEval = (int)(Float.POSITIVE_INFINITY);
            node.setEval(bestEval);

            for (int j : machine.possibleMoves()) {

                Board simBoard = new Board(currentBoard);
                Machine tempMachine = new Machine(simBoard, getOpponentOf(player));
                tempMachine.makeMove(j);

                Node child = new Node();
                node.addChild(child);
                child.setSquare(j);
                child.setEval(Minimax(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child));

                if (child.getEval() < node.getEval()){
                    bestEval = child.getEval();
                    node.setEval(bestEval);
                }
            }

            return node.getEval();
        }
    }

    public int callMinimax(Board currentBoard, int depth, int player){

        Node root = new Node();

        Minimax(currentBoard, depth, player, root);

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }


    public int AlphaBeta(Board currentBoard, int depth, int player, Node node, int a, int b){

        int bestEval;

        Machine machine = new Machine(currentBoard, player);

        //Evaluate leaf node
        if (machine.possibleMoves().size() == 0 || (depth == 0)) {

            return Board.evalStateOfBoard(currentBoard.getBoard(), machine.getPlayer());
            //return Board.evalDiskParity(currentBoard.getBoard(), machine.getPlayer());
            //return Board.evalMobility(currentBoard, machine.getPlayer());
            //return Board.evalMobility2(currentBoard, machine.getPlayer());
        }

        //Maximizing player
        if (player == this.getPlayer()){

            bestEval = (int)(Float.NEGATIVE_INFINITY);
            node.setEval(bestEval);

            for (int i : machine.possibleMoves()) {

                Board simBoard = new Board(currentBoard);
                Machine tempMachine = new Machine(simBoard, player);
                tempMachine.makeMove(i);

                Node child = new Node();
                node.addChild(child);
                child.setSquare(i);

                bestEval = max(bestEval, AlphaBeta(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child, a, b));
                child.setEval(bestEval);

                a = max(a, bestEval);

                if (b <= a){ // b cut-off
                    break;
                }

                node.setEval(bestEval);
            }
            return node.getEval();
        }

        //Minimizing player
        else {
            bestEval = (int)(Float.POSITIVE_INFINITY);
            node.setEval(bestEval);

            for (int j : machine.possibleMoves()) {

                Board simBoard = new Board(currentBoard);
                Machine tempMachine = new Machine(simBoard, getOpponentOf(player));
                tempMachine.makeMove(j);

                Node child = new Node();
                node.addChild(child);
                child.setSquare(j);

                bestEval = min(bestEval, AlphaBeta(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child, a, b));
                child.setEval(bestEval);

                b = min(b, bestEval);

                if (b <= a){  // a cut-off
                    break;
                }

                node.setEval(bestEval);
            }
            return node.getEval();
        }
    }

    public int callAlphaBeta(Board currentBoard, int depth, int player){

        Node root = new Node();

        AlphaBeta(currentBoard, depth, player, root, (int)(Float.NEGATIVE_INFINITY), (int)(Float.POSITIVE_INFINITY));

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }


    public int Negamax(Board currentBoard, int depth, int player, Node node){

        int bestEval;

        Machine machine = new Machine(currentBoard, player);

        //Evaluate leaf node
        if (machine.possibleMoves().size() == 0 || (depth == 0)) {

             if (player == this.getPlayer()) {
                return Board.evalStateOfBoard(currentBoard.getBoard(), player);
            }
            else{
                return -Board.evalStateOfBoard(currentBoard.getBoard(), player);
            }
        }

        //Maximizing player
        else {

            bestEval = (int)(Float.NEGATIVE_INFINITY);
            node.setEval(bestEval);

            for (int i : machine.possibleMoves()) {

                Board simBoard = new Board(currentBoard);
                Machine tempMachine = new Machine(simBoard, player);
                tempMachine.makeMove(i);

                Node child = new Node();
                node.addChild(child);
                child.setSquare(i);

                if (player == this.getPlayer()) {
                    child.setEval(Negamax(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child));
                }
                else{
                    child.setEval(-Negamax(simBoard, depth - 1, getOpponentOf(tempMachine.getPlayer()), child));
                }


                if (child.getEval() > node.getEval()){
                    bestEval = child.getEval();
                    node.setEval(bestEval);
                }
            }
            return node.getEval();
        }
    }

    public int callNegamax(Board currentBoard, int depth, int player){

        Node root = new Node();
        Negamax(currentBoard, depth, player, root);

        //Node bestNode = getMax(root.getChildren());

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }




    //MONTE CARLO TREE SEARCH ALGORITHM

    //MCTS Paranoid - Cazenave (2008)
    public Node MCTS(Tree tree, long timeout){

        try {
            while (System.currentTimeMillis() < timeout) {

                //Select the root child with the maximum UCB1 value. In the beginning all root children will have UCB1 value of positive infinity
                Node promisingNode = Machine.selectPromisingNode(tree.getRoot(), tree);

                //If node is not a leaf node then select the child with the maximum UCB1 value
                if (!promisingNode.isLeaf()) {
                    promisingNode = Machine.selectPromisingNode(promisingNode, tree);
                }

                Node nodeToExplore = promisingNode;

                /*
                if the leaf node has been visited before then for each available moves from the current create
                a new child and add it to the current node. Then set the node to explore the first of these
                children and do a random playout on it
                */
                if (nodeToExplore.isVisited()) {
                    Machine.expandNode(nodeToExplore);

                    if (nodeToExplore.getChildren().size() > 0) {
                        nodeToExplore = selectPromisingNode(nodeToExplore, tree);
                    }
                    else {
                        nodeToExplore = promisingNode;
                    }
                }
                //We do a rollout(a random playout until the end) on the node to see who wins
                randomPlayout(nodeToExplore, tree);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return getBestChildOf(tree.getRoot(), tree);
    }

    public int callMCTS(Board currentBoard){

        Node root = new Node(currentBoard);
        root.setName("Root");
        root.setPlayer(this.getPlayer());
        Machine.expandRootNode(root); //We expand the root node in order to avoid being flagged as a leaf node

        Tree tree = new Tree();
        tree.setRoot(root);

        long timeout = System.currentTimeMillis() + 100;

        Node bestNode = new Node();

        try {
            bestNode = MCTS(tree, timeout);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(root.getChildren());

        return bestNode.getSquare();
    }


    public static Node getBestChildOf(Node parent, Tree tree){

        double maxUCB1val = Double.NEGATIVE_INFINITY;

        ArrayList<Node> bestNodes = new ArrayList<>();

        //To avoid NaN problems when calculating the UCB1 value on nodes that have not been visited before
        for(Node n: parent.getChildren()){
            if (n.isVisited()){
                n.updateUCB1val(tree);
            }
        }

        for(Node n: parent.getChildren()){
            if (n.getUCB1val() > maxUCB1val){
                maxUCB1val = n.getUCB1val();
            }
        }

        for (Node n: parent.getChildren()){
            if (n.getUCB1val() == maxUCB1val){
                bestNodes.add(n);
            }
        }
    

        if (bestNodes.size() > 0) {
            //Randomise the selection of the nodes with equal UCB1 value
            int choices = bestNodes.size();
            Random random = new Random();
            int randomNum = random.nextInt(choices);

            return bestNodes.get(randomNum);
        }
        else {
            return null;
        }
    }

    public static Node selectPromisingNode(Node currentNode, Tree tree){

        Node node = currentNode;

        if (node.getChildren().size() > 0) {
            while (node.getChildren().size() > 0){
                node = getBestChildOf(node, tree);
            }
        }
        
        return node;
    }

    public static void expandRootNode(Node root){

        Machine rootPlayer = new Machine(root.getBoard(), root.getPlayer());

        for (int i: rootPlayer.possibleMoves()){

            Board childBoard = new Board(root.getBoard());
            Machine childPlayer = new Machine(childBoard, root.getPlayer());

            childPlayer.makeMove(i);  //Make the move to the square so that the board state gets updated
            Node child = new Node(childBoard);
            child.setName("(" + root.getName() + "\\" + "(" + root.getPlayer() + ")" + ":" + Integer.toString(i) + ")");
            child.setSquare(i);
            child.setPlayer(root.getPlayer());
            root.addChild(child);
        }
    }

    public static void expandNode(Node node){

        Machine nodePlayer = new Machine(node.getBoard(), node.getPlayer());
        nodePlayer.makeMove(node.getSquare()); //Make the move to update the board so that the opponent can then find all the possible moves

        Machine machine = new Machine(node.getBoard(), node.getOpponent());

        //If there are available moves for the opponent
        if (machine.possibleMoves().size() > 0) {

            for (int i : machine.possibleMoves()) {

                Board childBoard = new Board(node.getBoard());
                Machine childPlayer = new Machine(childBoard, node.getOpponent());

                childPlayer.makeMove(i);  //Make the move to the square so that the board state gets updated

                Node child = new Node(childBoard);
                child.setPlayer(node.getOpponent());
                child.setSquare(i);
                child.setName("(" + node.getName() + "\\" + "(" + child.getPlayer() + ")" + ":" + Integer.toString(i) + ")");
                node.addChild(child);
            }
        }
    }

    public int rollout(Board currentBoard, int player, Tree tree) {

        Board simBoard = new Board(currentBoard);
        Machine machine = new Machine(simBoard, player);
        Machine opponent = new Machine(simBoard, machine.getOpponent());

        if ((!machine.availableMoves() && !opponent.availableMoves()) || currentBoard.isTerminal()) {

            int result = currentBoard.blacksOnBoard().size() - currentBoard.whitesOnBoard().size();

            //Returns the number of the player who won. 1 if black won, 2 if white won and 0 if is a draw
            if (result > 0) {
                System.out.println("Black wins");
                return 1;
            }
            else if (result < 0) {
                System.out.println("White wins");
                return 2;
            }
            else  {
                System.out.println("Draw");
                return 0;
            }
        }


        if (machine.availableMoves()) {
            machine.makeRandomMove();
            //machine.makeStrategicMove();
            return rollout(simBoard, machine.getOpponent(),tree);
        }
        else {
            if (opponent.availableMoves()) {
                opponent.makeRandomMove();
                //opponent.makeStrategicMove();
                return rollout(simBoard, machine.getPlayer(), tree);
            }
            return rollout(simBoard, machine.getPlayer(), tree);
        }
    }

    public void randomPlayout(Node currentNode, Tree tree){

        int result = rollout(currentNode.getBoard(), currentNode.getOpponent(), tree);

        //Backpropagate the results and update the stats for the current and his parent nodes
        currentNode.backPropagation(result);

        //Update the UCB1 value of the current node
        double UCB1val = currentNode.calculateUCB1val(tree);
        currentNode.setUCB1val(UCB1val);
    
    }









    //Multithreaded versions of the algorithms
    public class MinimaxThread implements Runnable{

        private String threadName;
        private Board board;
        private int depth;
        private int player;
        private Node node;

        //Thread constructor
        public MinimaxThread(String threadName, Board currentBoard, int depth, int player, Node node, int square){

            this.threadName = threadName;
            this.board = currentBoard;
            this.depth = depth;
            this.player = player;
            this.node = node;
            node.setSquare(square);  //Every thread is assigned to each unique possible move so it's used as its ID
        }


        public void run(){
            try {
                System.out.println("Thread " + threadName + " for move " + node.getSquare() + " is running");
                int rootChildEval = Minimax(this.board, this.depth, this.player, this.node);
                node.setEval(rootChildEval);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Thread " + threadName + " finished");
            }
        }
    }

    public int callMinimaxThread(Board currentBoard, int depth, int player) throws InterruptedException {

        Node root = new Node();
        Machine machine = new Machine(currentBoard, player);
        ArrayList<Integer> moves = machine.possibleMoves();

        ArrayList<Thread> threads = new ArrayList<>(); //Threads to a list
       

        for (int i = 0; i< moves.size(); i++){

            Board simBoard = new Board(currentBoard);
            Machine tempMachine = new Machine(simBoard, player);
            tempMachine.makeMove(moves.get(i));

            Node rootChild = new Node();
            root.addChild(rootChild);

            String threadName = Integer.toString(i+1);
            MinimaxThread mth = new MinimaxThread(threadName, simBoard, depth, player, rootChild, moves.get(i));

            Thread t = new Thread(mth);
            threads.add(t);  
            t.start();
                     
        }

        //Each thread waits for the other threads to finish
        for (Thread t : threads){
            t.join();
        }
       
      
        

        //Node bestNode = getMax(root.getChildren()); //This always produces the same one move in the beginning

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }



    public class AlphaBetaThread implements Runnable{

        private String threadName;
        private Board board;
        private int depth;
        private int player;
        private Node node;
        private int a;
        private int b;

        //Thread constructor
        public AlphaBetaThread(String threadName, Board currentBoard, int depth, int player, Node node, int square, int a, int b){

            this.threadName = threadName;
            this.board = currentBoard;
            this.depth = depth;
            this.player = player;
            this.node = node;
            node.setSquare(square);
            this.a = a;
            this.b = b;
        }


        public void run(){
            try {
                System.out.println("Thread " + threadName + " for move " + node.getSquare() + " is running");
                int rootChildEval = AlphaBeta(this.board, this.depth, this.player, this.node, a, b);
                node.setEval(rootChildEval);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Thread " + threadName + " finished");
            }
        }
    }

    public int callAlphaBetaThread(Board currentBoard, int depth, int player) throws InterruptedException {

        Node root = new Node();
        Machine machine = new Machine(currentBoard, player);
        ArrayList<Integer> moves = machine.possibleMoves();

        ArrayList<Thread> threads = new ArrayList<>(); //Add all threads to a list

        for (int i = 0; i< moves.size(); i++){

            Board simBoard = new Board(currentBoard);
            Machine tempMachine = new Machine(simBoard, player);
            tempMachine.makeMove(moves.get(i));

            Node rootChild = new Node();
            root.addChild(rootChild);

            String threadName = Integer.toString(i+1);
            AlphaBetaThread abth = new AlphaBetaThread(threadName, simBoard, depth, player, rootChild, moves.get(i), (int)(Float.NEGATIVE_INFINITY), (int)(Float.POSITIVE_INFINITY));

            Thread t = new Thread(abth);
            t.start();
            threads.add(t);
            
        }
        //Each thread waits for the other threads to finish
        for (Thread t : threads){
            t.join();
        }

        //Node bestNode = getMax(root.getChildren()); //This always produces the same one move in the beginning

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }



    public class NegamaxThread implements Runnable{

        private String threadName;
        private Board board;
        private int depth;
        private int player;
        private Node node;

        //Thread constructor
        public NegamaxThread(String threadName, Board currentBoard, int depth, int player, Node node, int square){

            this.threadName = threadName;
            this.board = currentBoard;
            this.depth = depth;
            this.player = player;
            this.node = node;
            node.setSquare(square);  //Every thread is assigned to each unique possible move so it's used as its ID
        }


        public void run(){
            try {
                System.out.println("Thread " + threadName + " for move " + node.getSquare() + " is running");
                int rootChildEval = Minimax(this.board, this.depth, this.player, this.node);
                node.setEval(rootChildEval);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Thread " + threadName + " finished");
            }
        }
    }

    public int callNegamaxThread(Board currentBoard, int depth, int player) throws InterruptedException {

        Node root = new Node();
        Machine machine = new Machine(currentBoard, player);
        ArrayList<Integer> moves = machine.possibleMoves();

        ArrayList<Thread> threads = new ArrayList<>(); //Add all threads to a list

        for (int i = 0; i< moves.size(); i++){

            Board simBoard = new Board(currentBoard);
            Machine tempMachine = new Machine(simBoard, player);
            tempMachine.makeMove(moves.get(i));

            Node rootChild = new Node();
            root.addChild(rootChild);

            String threadName = Integer.toString(i+1);
            NegamaxThread ngth = new NegamaxThread(threadName, simBoard, depth, player, rootChild, moves.get(i));

            Thread t = new Thread(ngth);
            t.start();
            threads.add(t);
        }
        //Each thread waits for the other threads to finish
        for (Thread t : threads){
            t.join();
        }

        //Node bestNode = getMax(root.getChildren()); //This always produces the same one move in the beginning

        //To randomise the selection from the moves with same best value
        int choices = getBestMoves(root.getChildren()).size();
        Random random = new Random();
        int randomNum =  random.nextInt(choices);

        Node bestNode = getBestMoves(root.getChildren()).get(randomNum);

        return bestNode.getSquare();
    }



    public class MCTSThread implements Runnable{

        private String threadName;
        private Board board;
        private Node root;
        private Tree tree;
        private long timeout;

        //Thread constructor
        public MCTSThread(String threadName, Board currentBoard, int player, int square, long timeout){

            this.threadName = threadName;
            this.board = currentBoard;
            this.timeout = timeout;

            root = new Node(currentBoard);
            root.setSquare(square);
            root.setName("Root");
            root.setPlayer(player);
            Machine.expandNode(root);

            this.tree = new Tree();
            tree.setRoot(root);
        }


        public void run(){
            try {
                System.out.println("Thread " + threadName + " for move " + root.getSquare() + " is running");
                MCTS(tree, timeout);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("Thread " + threadName + " finished");
            }
        }
    }

    public int callMCTSThread(Board currentBoard, int player) throws InterruptedException {

        Node Root = new Node(currentBoard);
        Root.setPlayer(player);
        Root.setName("ROOT");
        Machine.expandRootNode(Root);
        long timeout = System.currentTimeMillis() + 10;

        ArrayList<Thread> threads = new ArrayList<>(); //Add all threads to a list

        int i = 0; //Thread counter

        for (Node n: Root.getChildren()){

            String threadName = Integer.toString(i++);

            MCTSThread mcts = new MCTSThread(threadName, n.getBoard(), n.getPlayer(), n.getSquare(), timeout);

            Thread t = new Thread(mcts);
            t.start();
            threads.add(t);
        }
        //Each thread waits for the other threads to finish
        for (Thread t : threads){
            t.join();
        }

        Node bestNode = Machine.getMaxChildOf(Root);

        if (bestNode != null) {
            return bestNode.getSquare();
        }
        else return -1;
    }


    public static Node getMaxChildOf(Node parent){

        double maxUCB1val = Double.NEGATIVE_INFINITY;

        ArrayList<Node> bestNodes = new ArrayList<>();

        for (Node n: parent.getChildren()){
            if (n.getUCB1val() > maxUCB1val){
                maxUCB1val = n.getUCB1val();
            }
        }

        for (Node n: parent.getChildren()){
            if (n.getUCB1val() == maxUCB1val){
                bestNodes.add(n);
            }
        }

        if (bestNodes.size() > 0) {
            //Randomise the selection of the nodes with equal UCB1 value
            int choices = bestNodes.size();
            Random random = new Random();
            int randomNum = random.nextInt(choices);

            return bestNodes.get(randomNum);
        }
        else{
            return null;
        }

    }






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

    public void makeRandomMove(){

        int allMoves = this.possibleMoves().size();
        Random random = new Random();
        int randomMove =  random.nextInt(allMoves);
        int square = this.possibleMoves().get(randomMove);

        if (this.possibleMoves().size() != 0){
            this.makeMove(square);
            //System.out.println("White chose random square: " + square);
        }
    }


    public void makeStrategicMove(){

        Board b = this.board;
        int maxVal = (int)(Float.NEGATIVE_INFINITY);
        int bestMove = -1;

        for(int i: this.possibleMoves()){

            Board tempBoard = new Board(b);
            Machine tempMachine = new Machine(tempBoard, this.getPlayer());
            tempMachine.makeMove(i);


            int val = Board.evalStateOfBoard(tempBoard.getBoard(), tempMachine.getPlayer());
            //int val = Board.evalDiskParity(tempBoard.getBoard(), tempMachine.getPlayer());
            //int val = Board.evalMobility(tempBoard.getBoard(), tempMachine.getPlayer());
            //int val = Board.evalMobility2(tempBoard.getBoard(), tempMachine.getPlayer());
            if (val > maxVal) {
                maxVal = val;
                bestMove = i;
            }
        }

        if (bestMove != -1) {
            this.makeMove(bestMove);
        }
    }


    //The computer makes a move
    public void aiMakeMove(Board board, int depth, int player, String AIstrategy) throws InterruptedException {

        int bestMove = -1;

        switch (AIstrategy){

            case "Minimax":
                bestMove = callMinimax(board, depth, player);
                break;
            case "Alpha-Beta Pruning":
                bestMove = callAlphaBeta(board, depth, player);
                break;
            case "Negamax":
                bestMove = callNegamax(board, depth,  player);
                break;
            case "Monte Carlo Tree Search":
                bestMove = callMCTS(board);
                break;
            case "Multithreaded Minimax":
                bestMove = callMinimaxThread(board, depth, player);
                break;
            case "Multithreaded Alpha Beta Pruning":
                bestMove = callAlphaBetaThread(board, depth, player);
                break;
            case "Multithreaded Negamax":
                bestMove = callNegamaxThread(board, depth, player);
                break;
            case "Multithreaded Monte Carlo Tree Search":
                bestMove = callMCTSThread(board, player);
                break;
        }
    

        //If the machine does not have any legal move to play it will return best move -1
        if (bestMove != -1) {
            makeMove(bestMove);
        }
    }   
}




