import java.util.Arrays;

public class jest2 {

        private static String currentPlayer = "O";
        //finding the best move possible on the board using minimax algorithm 

        private static int[] findBestMovePossibleOnBoard(String board[][]) {
            System.out.print("CPU trying to determine best move");
    
            //setting a random empty cell as next move
            int[] bestMove = null;
    
            // //if middle of board is empty, start with that as best move 
            // if(board[1][1].equals(""))
            //         bestMove = new int[]{1,1};
    
            int bestValue = -1000;

            for(int r = 0; r<board.length; r++){
                for(int c = 0; c<board.length; c++){
                    if(board[r][c].equals("")){
                        //do the move 
                        board[r][c] = currentPlayer;
    
                        //compute evaluation for the move
                        //System.out.println("Calling minimax");
                        int moveValue = miniMax(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, getOtherPlayerMark(currentPlayer));
				        System.out.println("-----------------------------------------" + moveValue);
                        
    
                        //undo move
                        board[r][c] = "";
    
                        //calculate if new move is better than current best move, if middle is open and the eval is equal to previous bestValue, take that as precedence
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
    
    
        public static int miniMax(String[][] boardState, int depth, int alpha, int beta, String currentPlayerMark){
            int score = evaluate(boardState);
 
            // If Maximizer has won the game
            // return his/her evaluated score
            if (score == 1)
                return score;
        
            // If Minimizer has won the game
            // return his/her evaluated score
            if (score == -1)
                return score;
        
            // If there are no more moves and
            // no winner then it is a tie
            if (boardIsFull(boardState))
                return 0;
            
            //maximizing player turn
            if(currentPlayerMark.equals(currentPlayer)){
                int max = Integer.MIN_VALUE;
    
                for(int r = 0; r<boardState.length; r++){
                    for(int c = 0; c<boardState.length; c++){
                        if(boardState[r][c].equals("")){
                            //do the move 
                            boardState[r][c] = currentPlayerMark;
        
                            //recall minimax
                            int eval = miniMax(boardState, depth-1, alpha, beta, getOtherPlayerMark(currentPlayerMark));
                            max = Math.max(max, eval);
                            
                            // alpha = Math.max(alpha, eval);
                            // if(beta <= alpha)
                            //     break;

        
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
                            int eval = miniMax(boardState, depth-1, alpha, beta, getOtherPlayerMark(currentPlayerMark));
                            min = Math.min(min, eval);
                            
                            // beta = Math.min(beta, eval);
                            // if(beta <= alpha)
                            //     break;
                            
                            //undo move
                            boardState[r][c] = "";
                        }
                    }
                }
                return min;
            }
        }
        
        //returns 1 or -1 depending on which current player will win and 0 if the board is a tie
        public static int evaluate(String[][] boardPosition){
            //System.out.println(currentPlayer + " other: "+ getOtherPlayerMark(currentPlayer));
            System.out.println(Arrays.deepToString(boardPosition));
            // Checking for rows 
            for (int row = 0; row < 3; row++)
            {
                if (boardPosition[row][0].equals(boardPosition[row][1]) &&
                    boardPosition[row][1].equals(boardPosition[row][2]))
                {
                    if (boardPosition[row][0].equals(getCurrentPlayerMark(currentPlayer)))
                        return 1;
                    else if (boardPosition[row][0].equals(getOtherPlayerMark(currentPlayer)))
                        return -1;
                }
            }
    
            // Checking for columns
            for (int col = 0; col < 3; col++)
            {
                if (boardPosition[0][col].equals(boardPosition[1][col]) &&
                    boardPosition[1][col].equals(boardPosition[2][col]))
                {
                    if (boardPosition[0][col].equals(getCurrentPlayerMark(currentPlayer))){
                        //System.out.println("Victory found here at column player "+ getCurrentPlayerMark(currentPlayer));
                        return 1;
                    }
                    else if (boardPosition[0][col].equals(getOtherPlayerMark(currentPlayer))){
                        //System.out.println("Victory found at column other player "+ getOtherPlayerMark(currentPlayer));
                        return -1;
                    }
                }
            }
    
            // Checking for diagonals 
            if (boardPosition[0][0].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][2]))
            {
                if (boardPosition[0][0].equals(getCurrentPlayerMark(currentPlayer)))
                    return 1;
                else if (boardPosition[0][0].equals(getOtherPlayerMark(currentPlayer)))
                    return -1;
            }
        
            if (boardPosition[0][2].equals(boardPosition[1][1]) && boardPosition[1][1].equals(boardPosition[2][0]))
            {
                if (boardPosition[0][2].equals(getCurrentPlayerMark(currentPlayer)))
                    return 1;
                else if (boardPosition[0][2].equals(getOtherPlayerMark(currentPlayer)))
                    return -1;
            }
    
            //returns 0 if neither side has currently won (does not guarantee there is a tie if board is not full)
            return 0;
        }

        public static boolean boardIsFull(String[][] board){
            for(int r=0; r<board.length; r++){
                for(int c=0; c<board.length; c++){
                    if(board[r][c].equals(""))
                        return false;
                }
            }
            return true;
        }
    
    

        public static void main(String[] args) {
            //System.out.println(getCurrentPlayerMark("X"));
            //System.out.println(getOtherPlayerMark("X"));
            long startTime = System.currentTimeMillis();

            String board[][] = {{ "", "", "X" },
					            { "", "O", "X" },
					            { "", "", "" }};

            int[]bestMove = findBestMovePossibleOnBoard(board);
            
            long endTime = System.currentTimeMillis();

            System.out.println("That took " + (endTime - startTime) + " milliseconds");
            System.out.printf("The Optimal Move is :\n");
            System.out.printf("ROW: %d COL: %d\n\n",
                    bestMove[0], bestMove[1] );

        }


         //returns the mark of the current player
        public static String getCurrentPlayerMark(String currPlayer){
            if(currPlayer.equals("X"))
                return "X";
            else 
                return "O";
        }

        //returns the mark of the player who's turn is not now. 
        public static String getOtherPlayerMark(String currPlayer){
            if(currPlayer.equals("X"))
                return "O";
            else 
                return "X";
        }
}
