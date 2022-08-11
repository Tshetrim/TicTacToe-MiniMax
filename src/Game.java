import java.util.*;
import javax.swing.JButton;

import java.awt.Color;
import java.lang.reflect.Method;

public class Game{
    
    //variables related to event handling
    public String log = "";
    public int stageI = 0;
    public ArrayList<String> stageList = new ArrayList<>(Arrays.asList("Launch", "Pick Mode", "Mode Picked", "Mark", "Game", "Over"));
    public String difficulty = "";

    //variables related to board
    public String[][] board = new String[3][3];
    
    //variables related to game
    private boolean play = true;
    private String player1Mark;
    private String player2Mark;
    private String currentPlayer;

    int[] winningCords;

    private gGUI gui;
    

    public Game() throws Exception{
        gui = new gGUI();
        setRandomMarks();

        //run loop 
        while(play){
            //if the stage is below 4, continously calls the methods below 
            if(gui.TW == null || (gui.TW.isRunning() == false && stageI<4)){
                System.out.println("Called next stage");
                updateLogAndBoard();

                //passes the actionPerformedManager function to the gui's JButton actionPerformed method to try to keep states in Game.java 
                Method functionToPass = Game.class.getMethod("actionPerformedManager", (Class[]) getParameterTypesForActionPerformedManager());
                gui.updateGrid(this, board, functionToPass, getGameVariables());
                gui.updateText(log);

            } 
            //if the stage is the game stage and the first turn is the CPU's, invokes the CPU move once 
            else if(stageList.get(stageI).equals("Game")){
                if(!difficulty.equals("PVP")&&currentPlayer.equals("Player 2")&&boardIsEmpty(board)){
                    if(difficulty.equals("Easy CPU"))
                        easyCPUMove();
                    else
                        hardCPUMove();
                }
            } 
            //If game is over stops play loop and updates one last time
            else if(stageList.get(stageI).equals("Over")){
                play=false;
                updateLogAndBoard();
                Method functionToPass = Game.class.getMethod("actionPerformedManager", (Class[]) getParameterTypesForActionPerformedManager());
                gui.updateGrid(this, board, functionToPass, getGameVariables());
                gui.updateText(log);
            }
        }
    }
 

    //updates the state of the board depending on the current stage and other state variables 
    public void updateBoard(){
        String stage = stageList.get(stageI);
        //System.out.println("board stage: " + stage);

        //stage 1/2 -> Launch/Pick Mode
        if(stage.equals("Launch")){
            board[0][0] = "Welcome";
            board[0][1] = "To";
            board[0][2] = "Tic-Tac-Toe";
            board[2][2] = "Skip";
        } else if(stage.equals("Pick Mode")){
            System.out.println("updating board to pick mode");
            board[0][0] = "Player vs Player";
            board[0][1] = "Easy CPU";
            board[0][2] = "Hard CPU";
        }  else if(stage.equals("Mode Picked")){
            board[0][0] = "Mode";
            board[0][1] = "is";
            board[0][2] = difficulty;
        } else if(stage.equals("Mark")){
            for(int r = 0; r < board.length; r++){
                for(int c = 0; c < board.length; c++){
                    board[r][c] = "";
                }
            }
        } else if(stage.equals("Game")){
            for(int r = 0; r < board.length; r++){
                for(int c = 0; c < board.length; c++){
                    board[r][c] = "";
                }
            }
        } else if(stage.equals("Over")){
            int counter = 0;
            boolean tie = evaluate(board)==0;
            for(int r = 0; r < board.length; r++){
                for(int c = 0; c < board.length; c++){
                    if(tie)
                        board[r][c]="Tie";
                    else {
                        if(counter<5 && r==winningCords[counter] && c==winningCords[counter+1]){
                            board[r][c] = getCurrentPlayerMark();
                            gui.buttons[r][c].setBackground(Color.red);
                            counter+=2;
                        }
                        else
                            board[r][c] = "Game Over";
                    }
                }
            }
        }
    }

    //updates the String log for the gui and the board depending on the current stage and other state variables
    //also has a hand in incrementing the stage (stages 0-4) if that stage's events are completed
    public int updateLogAndBoard(){
        String stage = stageList.get(stageI);
        System.out.println("log stage: " + stage);
        //System.out.println("Stage: "+stage + " Manage Log Triggered");
        
        //stage 1 -> Launch
        if(stage.equals("Launch")){
            log = (">> Hello. Welcome to the game.\n>> To the grid to your right, there is a grid of boxes.\n>> Beat your opponent by getting a row, diagonal, or column of your own marks\n>> First, pick your game mode:");
            updateBoard();
            stageI++;
        } 

        //stage 2 -> Pick Mode
        if(stage.equals("Pick Mode") && difficulty.length()==0){
            //log+=".";
            updateBoard();
        } 

        //stage 3-> Mode Picked
        if(stage.equals("Mode Picked") && difficulty.length()>0){
            if(difficulty.equals("PVP"))
                log+= "\n>> Game starting as Player vs Player ";
            else 
                log+= "\n>> Game starting on difficulty " + difficulty;
                updateBoard();
                stageI++;
        } 

        //stage 4 -> Mark 
        if(stage.equals("Mark")){
            log+="\n>> Flipping coin..............";
            if(difficulty.equals("PVP")){
                log+= "\n>> Player 1 is : " + player1Mark +"\n>> Player 2 is : " + player2Mark;
                log+= "\n>> Game commencing...... \n>> You may make your move " + currentPlayer;
            }
            else{
                log+= "\n>> You are : " + player1Mark +"\n>> CPU is : " + player2Mark;
                if(currentPlayer.equals("Player 1"))
                    log+="\n>> Game commencing...... \n>> You are first and may make your move ";
                else
                    log+="\n>> Game commencing...... \n>> CPU is first, CPU making move ... ";
            }
            updateBoard();
            stageI++;
        }

        //stage 6 -> Game Over
        if(stage.equals("Over")){
            if(evaluate(board)==0){
                log+=("\n>> Game over!\n>> Game was a tie. Relaunch to play.");
            }else {
                if(difficulty.equals("PVP"))
                    log+=("\n>> Game over!\n>> Winner is "+currentPlayer);
                else {
                    if(currentPlayer.equals("Player 1"))
                        log+=("\n>> Game over!\n>> Congratulations. You won the game!");
                    else
                        log+=("\n>> Game over!\n>> CPU has won the game. Relaunch to play.");
                }
            }
            updateBoard();
        }


        return stageI;
    }

    //randomly set plays marks and set goes first 
    public void setRandomMarks(){
        if((int)(Math.random()*2)!=-1){
            player1Mark = "❌";
            player2Mark = "⭕";
            currentPlayer = "Player 1";
        } else {
            player1Mark = "⭕";
            player2Mark = "❌";
            currentPlayer = "Player 2";
        }
    }

    //flips the mark of the current player
    public void flipCurrent(){
        if(currentPlayer.equals("Player 1"))
            currentPlayer = "Player 2";
        else 
            currentPlayer = "Player 1";
    }

    //returns the mark of the current player
    public String getCurrentPlayerMark(){
        if(currentPlayer.equals("Player 1"))
            return player1Mark;
        else 
            return player2Mark;
    }

    //returns the mark of the player who's turn is not now. 
    public String getOtherPlayerMark(){
        if(currentPlayer.equals("Player 1"))
            return player2Mark;
        else 
            return player1Mark;
    }

    //All if statements for what pressing a button will do depending on the state of the game stage 
    public void actionPerformedManager(JButton button, TypeWriter TW){
        String stage = stageList.get(stageI);
        String buttonText = button.getText();
        //System.out.println("Stage at button press: "+ stage);
        //System.out.println("Button Pressed: " + buttonText);

        //stage 1/2 -> Lauch/Pick Mode - skip text and auto fill
        if(stage.equals("Pick Mode")){
            System.out.println("Log mode: " + stage);
            if(buttonText.equals("Skip")){
                System.out.println("Trying to skip");
                 //If intro text is still running and event was detected, text autocompletes 
                 if(TW.isRunning()){
                    TW.stop();
                    TW.getTextArea().setText(log);
                } 
            }
        }

        //stage 2 -> Pick Mode
        if(stage.equals("Pick Mode")){
            if(buttonText.equals("Player vs Player"))
                difficulty = "PVP";
            else if(buttonText.equals("Easy CPU"))
                difficulty = "Easy CPU";
            else if(buttonText.equals("Hard CPU"))
                difficulty = "Hard CPU";
            

            if(difficulty.length()!=0){
                System.out.println("Mode picked" +  difficulty);
                stageI++;
            }
        }

        //stage 5 -> Game Mode
        //Player VS Player
        if(stage.equals("Game")){
            //Player vs Player
            if(difficulty.equals("PVP")){
                if(buttonText.length()==0){
                    playerMove(button);
                }
            }

            //Player vs CPU Easy 
            else if (difficulty.equals("Easy CPU")){
                if(currentPlayer.equals("Player 1")){
                    if(buttonText.length()==0){
                        playerMove(button);
                    } 
                } 
                //Important Note:  the reason why this works right now is because clicking a button causes multiple instances of it to be invoked and the later instances invoke the CPU if 
                //because by then, the player if will be completed
                //In reality, need to create some sort of CPU Timer and action listerner that sends an event to an event manager
                if(currentPlayer.equals("Player 2")){
                    easyCPUMove();
                }
            }
    
            //Player vs CPU Hard 
            else if(difficulty.equals("Hard CPU")){
                if(currentPlayer.equals("Player 1")){
                    if(buttonText.length()==0){
                        playerMove(button);
                    } 
                } 
                //same reasoning as important note above
                if(currentPlayer.equals("Player 2")){
                    hardCPUMove();
                }
            }
        }

    }


    //handles when it is the player's turn to move
    public void playerMove(JButton button){
        int r = calculateRC(Integer.valueOf(button.getName()))[0];
        int c = calculateRC(Integer.valueOf(button.getName()))[1];
        //System.out.print(button.getName());

        board[r][c] = getCurrentPlayerMark();
        button.setText(getCurrentPlayerMark());

        endTurn();
    }


    //handles when it is the Easy CPU's turn to move including calculation of its move
    public void easyCPUMove(){
        int ifNextSelf[] = nextMoveWinPossible(getCurrentPlayerMark());
        int ifNextOther[] = nextMoveWinPossible(getOtherPlayerMark());
        if(ifNextSelf!=null){
            System.out.println("Win detected at "+ ifNextSelf[0] + "," + ifNextSelf[1]);
            placeInCell(ifNextSelf[0], ifNextSelf[1]);
        }
        else if(ifNextOther!=null){
            System.out.println("Win detected at "+ ifNextOther[0] + "," + ifNextOther[1]);
            placeInCell(ifNextOther[0], ifNextOther[1]);
        } else {
            int randomCell = getRandomEmptyCellOnBoard();
            //placing in cell
            System.err.println("CPU trying to place mark at id: "+randomCell + " randomValue was: "+randomCell);
            placeInCell(randomCell);
        }
        endTurn();
        System.out.println("CPU ends turn");
    }


    //handles when it is the Hard CPU's turn to move including calculation of its move
    public void hardCPUMove(){
        int[] bestMove = findBestMovePossibleOnBoard(board);
        placeInCell(bestMove[0], bestMove[1]);
        endTurn();
        System.out.println("CPU ends turn");
    }

    public int[] nextMoveWinPossible(String playerMark){
        
        int emptyH[] = null, emptyV[] = null, emptyDR[] = null, emptyDL[] = null;
        int counterH = 0, counterV = 0, counterDR = 0, counterDL = 0;

        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                //horizontal check
                if(board[r][c].equals(playerMark))
                    counterH++;
                else if(board[r][c].equals(""))
                    emptyH = new int[]{r,c};
                if(counterH==2 && emptyH!=null)
                    return emptyH;
                
                //vertical check
                if(board[c][r].equals(playerMark))
                    counterV++;
                else if(board[c][r].equals(""))
                    emptyV = new int[]{c,r};
                if(counterV==2 && emptyV!=null)
                    return emptyV;

                //diagonal towards bottom right check 
                if(r==c){
                    if(board[r][c].equals(playerMark))
                        counterDR++;
                    else if(board[r][c].equals("") )
                        emptyDR = new int[]{r,c};
                    if(counterDR==2 && emptyDR!=null)
                        return emptyDR;
                }
                
                //diagonal towards bottom left check
                if(r+c==2){
                    if(board[r][c].equals(playerMark) )
                        counterDL++;
                    else if(board[r][c].equals("") )
                        emptyDL = new int[]{r,c};
                    if(counterDL==2 && emptyDL!=null)
                        return emptyDL;
                }
            }
            counterH = 0;
            counterV = 0;
            emptyH = null;
            emptyV = null;
        }
        return null;

    }


    //finding the best move possible on the board using minimax algorithm 
    //returns an int[]{r,c} where int[0] = r and int[1] = c
    private int[] findBestMovePossibleOnBoard(String[][] board) {
        System.out.println("CPU trying to determine best move");

        //setting a random empty cell as next move
        int[] bestMove = null;

        //making currentPlayerMark static 
        String currentPlayerMark = getCurrentPlayerMark();
        String opposingPlayerMark = getOtherPlayerMark();

        int bestValue = -1000;
        for(int r = 0; r<board.length; r++){
            for(int c = 0; c<board.length; c++){
                if(board[r][c].equals("")){
                    //do the move 
                    board[r][c] = currentPlayerMark;

                    //compute evaluation for the move
                    //System.out.println("Calling minimax");
                    int moveValue = miniMax(board, 0, opposingPlayerMark);

                    //undo move
                    board[r][c] = "";

                    //calculate if new move is better than current best move
                    if(r==1&&c==1&&moveValue>=bestValue){
                        bestMove = new int[]{r,c};
                        bestValue = moveValue;
                    } else if(moveValue>bestValue){
                        bestMove = new int[]{r,c};
                        bestValue = moveValue;
                    }
                }
            }
        }

        System.out.println("Best move for CPU found to be at r:"+bestMove[0]+" c: "+bestMove[1]);
        return bestMove;
    }


    public int miniMax(String[][] boardState, int depth, String currentPlayerMark){

        int score = evaluate(boardState);

        if (score == 1)
            return score;
        if (score == -1)
            return score;
    
        // If there are no more moves and
        // no winner then it is a tie
        if (boardIsFull(boardState))
            return 0;
        
        //maximizing player turn
        if(currentPlayerMark.equals(getCurrentPlayerMark())){
            int max = Integer.MIN_VALUE;

            for(int r = 0; r<boardState.length; r++){
                for(int c = 0; c<boardState.length; c++){
                    if(boardState[r][c].equals("")){
                        //do the move 
                        boardState[r][c] = currentPlayerMark;
    
                        //recall minimax
                        int eval = miniMax(boardState, depth-1, getOtherPlayerMark());
                        max = Math.max(max, eval);
    
                        //undo move
                        boardState[r][c] = "";
                    }
                }
            }
            return max;
        }

        //minimizing player turn
        else {
            int min = Integer.MAX_VALUE;
            for(int r = 0; r<boardState.length; r++){
                for(int c = 0; c<boardState.length; c++){
                    if(boardState[r][c].equals("")){
                        //do the move 
                        boardState[r][c] = currentPlayerMark;

                        //recall minimax
                        int eval = miniMax(boardState, depth-1, getCurrentPlayerMark());
                        min = Math.min(min, eval);

                        //undo move
                        boardState[r][c] = "";
                    }
                }
            }
            return min;
        }
    }

    //returns 1 if player 1 wins or -1 if player 2 wins and 0 neither has currently won
    public int evaluate(String[][] boardPosition){
        // Checking for rows 
        for (int row = 0; row < 3; row++)
        {
            if (boardPosition[row][0].equals(boardPosition[row][1]) &&
                boardPosition[row][1].equals(boardPosition[row][2]))
            {
                if (boardPosition[row][0].equals(getCurrentPlayerMark()))
                    return 1;
                else if (boardPosition[row][0].equals(getOtherPlayerMark()))
                    return -1;
            }
        }

        // Checking for columns
        for (int col = 0; col < 3; col++)
        {
            if (boardPosition[0][col].equals(boardPosition[1][col]) &&
                boardPosition[1][col].equals(boardPosition[2][col]))
            {
                if (boardPosition[0][col].equals(getCurrentPlayerMark()))
                    return 1;
                else if (boardPosition[0][col].equals(getOtherPlayerMark()))
                    return -1;
            }
        }

        // Checking for diagonals 
        if (boardPosition[0][0].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][2]))
        {
            if (boardPosition[0][0].equals(getCurrentPlayerMark()))
                return 1;
            else if (boardPosition[0][0].equals(getOtherPlayerMark()))
                return -1;
        }
    
        if (boardPosition[0][2].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][0]))
        {
            if (boardPosition[0][2].equals(getCurrentPlayerMark()))
                return 1;
            else if (boardPosition[0][2].equals(getOtherPlayerMark()))
                return -1;
        }

        //returns 0 if neither side has currently won (does not guarantee there is a tie if board is not full)
        return 0;
    }


    //places a mark on the board using cell ID
    public void placeInCell(int cellID){
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                if(cellID==Integer.valueOf(gui.buttons[r][c].getName())){
                    board[r][c] = getCurrentPlayerMark();
                    gui.buttons[r][c].setText(getCurrentPlayerMark());
                }
            }
        }
    }

    //places a mark on the board using board coordinates 
    public void placeInCell(int r, int c){
        board[r][c] = getCurrentPlayerMark();
        gui.buttons[r][c].setText(getCurrentPlayerMark());
    }

    //returns the id of a random cell on the board that is currently empty
    public int getRandomEmptyCellOnBoard(){
        //calculating a random value
        int identifierValue = 0;
        int emptyCellValue = 0;
        HashMap<Integer, Integer> hash = new HashMap<>();
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                if(board[r][c].length()==0){
                    hash.put(emptyCellValue,identifierValue);
                    emptyCellValue++;
                }
                identifierValue++;
            }
        }
        System.out.println("Hashsize: "+ hash.size());
        int randomCell = (int)Math.random()*(hash.size());

        for(Integer i: hash.keySet()){
            System.out.println("i : " + i + " id: "+ hash.get(i));
        }

        return hash.get(randomCell);
    }


    //returns true if board is empty, false if not
    public boolean boardIsEmpty(String[][] board){
        for(int r=0; r<board.length; r++){
            for(int c=0; c<board.length; c++){
                if(!board[r][c].equals(""))
                    return false;
            }
        }
        return true;
    }

    //returns true if board is full, false if not
    public boolean boardIsFull(String[][] board){
        for(int r=0; r<board.length; r++){
            for(int c=0; c<board.length; c++){
                if(board[r][c].equals(""))
                    return false;
            }
        }
        return true;
    }

    //returns int id based on row and column given. (0,0) = 0, (0,1) = 1, (0,2) = 2, (1,0) = 3 ...
    public int calculateId(int rF, int cF){
        int iterator = 0;
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                if(rF==r && cF==c)
                    return iterator;
                iterator++;
            }
        } 
        return iterator;
    }

    //returns r and c  corresponding to an id. 0 = (0,0) , 1= (0,1), 2 = (0,2), 3 = (1,0)...
    public int[] calculateRC(int id){
        int iterator = 0;
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                if(iterator == id)
                    return new int[]{r,c};
                iterator++;
            }
        } 
        return null;
    }

    //handles when a turn ends by checking if anyone won or if a tie occured.
    //if not, flips marks and next turn begins
    public void endTurn(){
        if(evaluate(board)==1||evaluate(board)==-1||boardIsFull(board)){
            //System.out.println(Arrays.deepToString(board));
            if(!boardIsFull(board))
                winningCords = getWinningCoords(board);

            System.out.println("Game Over");
            stageI = 5;
        } else
            flipCurrent();
    }

    //retuns an int arr with winning coords 
    public int[] getWinningCoords(String boardPosition[][]){
        // Checking for rows 
        for (int row = 0; row < 3; row++)
        {
            if (boardPosition[row][0].equals(boardPosition[row][1]) &&
                boardPosition[row][1].equals(boardPosition[row][2]))
            {
                if (boardPosition[row][0].equals(getCurrentPlayerMark()) || boardPosition[row][0].equals(getOtherPlayerMark()))
                    return new int[]{row,0,row,1,row,2};
            }
        }

        // Checking for columns
        for (int col = 0; col < 3; col++)
        {
            if (boardPosition[0][col].equals(boardPosition[1][col]) &&
                boardPosition[1][col].equals(boardPosition[2][col]))
            {
                if (boardPosition[0][col].equals(getCurrentPlayerMark()) || boardPosition[0][col].equals(getOtherPlayerMark()))
                    return new int[]{0,col,1,col,2,col};
            }
        }

        // Checking for diagonals 
        if (boardPosition[0][0].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][2]))
        {
            if (boardPosition[0][0].equals(getCurrentPlayerMark()) || boardPosition[0][0].equals(getOtherPlayerMark()))
                return new int[]{0,0,1,1,2,2};
        }
    
        if (boardPosition[0][2].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][0]))
        {
            if (boardPosition[0][2].equals(getCurrentPlayerMark()) || boardPosition[0][2].equals(getOtherPlayerMark()))
                return new int[]{0,2,1,1,2,0};
        }

        return null;
    }

    //Set up and return object array for getMethod - to be casted into a Class array 
    //hard coded the classes (JButton, TypeWriter)
    public Object[] getParameterTypesForActionPerformedManager(){
        Object[] parameterTypes = new Class[2];
        parameterTypes[0] = JButton.class;
        parameterTypes[1] = TypeWriter.class;

        return parameterTypes;
    }

    //bundles variables into a Hashmap
    public HashMap<String, Object> getGameVariables(){
        HashMap<String, Object> bundledGameVariables = new HashMap<String, Object>(){};
        bundledGameVariables.put("log", this.log);
        bundledGameVariables.put("stageI", this.stageI);
        bundledGameVariables.put("stageList", this.stageList);
        bundledGameVariables.put("difficulty", this.difficulty);
        return bundledGameVariables;
    }
    

    public static void main(String[] args) throws Exception{
        new Game();
    }
}
