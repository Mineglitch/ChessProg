package computerscienceia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class ChessGame {
    
    // Loading Parameters
    
    boolean isDefault = true; // Default when it has loaded defaultArrangement.txt
    
    LinkedList<String> textFileContent = new LinkedList<>(); // Used to store the content of the set File Name content 
    
    String saveFileName = "defaultArrangement"; // Default Save File (Base board layout)
    
    ChessPiece[][] board = new ChessPiece[8][8]; // 8x8 Array to contain chess pieces for each chess location
    String[] playerNames = new String[2]; // Array used to store the player usernames from the load file
    
    int[] timers = new int[2]; // Array used to store the timers for each player from the load file
    
    LinkedList<String> previousMoves = new LinkedList<>(); // Linkedlist to store the previousmoves that have been made
    LinkedList<String> takenPiecesW = new LinkedList<>(); // Linkedlist to store the white pieces that have been taken
    LinkedList<String> takenPiecesB = new LinkedList<>(); // Linkedlist to store the black piecds that have been taken
    
    int turn = 0; // Array used to store the turn from the load file
    
    public ChessGame(String playerW, String playerB, int time){
        
        // Constructor for a new game.
        
        playerNames[0] = playerW; playerNames[1] = playerB; // Sets the player names for the game to those entered through the new game menu
        
        timers[0] = time; timers[1] = time; // Above for the timers
        
        unravelContent();
        
    }
    
    public ChessGame(String saveFileName){
        
        // Constructor for a loaded game
        
        isDefault = false; // To denote that the game is not based on the default arrangement
        this.saveFileName = saveFileName; // Sets the new file name to that passed in as an arguement
        unravelContent();
        
        // FileName format = PlayerW_PlayerB_Date_Time
        // First 8 lines = Board
        // Next 2 = Names
        // Next 2 = Timers
        // Next = Turn
        // Taken Pieces W
        // Taken Pieces B
        // Remaining = Previous Moves
    
    }
    
    public void unravelContent(){
        
        // Gets all the information from the save file
        
        try{
            BufferedReader getBoard = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/Saves/"+saveFileName+".txt"));
            
            // Get all the content from the save file into one big array
            
            while (getBoard.ready()){
                textFileContent.add(getBoard.readLine());
            }
            
            // If its not the default arrangement, get the information required for the chess game to continue
            
            if (textFileContent.size() > 8){
                playerNames[0] = textFileContent.get(8);
                playerNames[1] = textFileContent.get(9);
                timers[0] = Integer.parseInt(textFileContent.get(10));
                timers[1] = Integer.parseInt(textFileContent.get(11));
                turn = Integer.parseInt(textFileContent.get(12));
                for (int i = 0; i < textFileContent.get(13).length(); i++){
                    takenPiecesW.add(textFileContent.get(13).substring(i, i+1));
                }
                for (int i = 0; i < textFileContent.get(14).length(); i++){
                    takenPiecesB.add(textFileContent.get(14).substring(i, i+1));
                }
                for (int i = 15; i < textFileContent.size(); i++){
                    previousMoves.add(textFileContent.get(i));
                }
            }
            
            getBoard.close();
 
        } catch (IOException | NumberFormatException e){
            e.printStackTrace();
        }

    }
    
    public void getPieces(ChessPanel parent){
        
        // Add the pieces in their specific places on the board based on the arrangement
        
        // Pieces are saved in the format   int int  where the first int denotes the colour and the second its type

        for (int row = 0; row < 8; row++){

            String line = textFileContent.get(row);

            for (int col = 0; col < 8; col++){

                if (Integer.parseInt(line.substring(col*2, (col*2)+1)) != 0){
                    board[col][row] = new ChessPiece(Integer.parseInt(line.substring(col*2, (col*2)+1)), 
                        Integer.parseInt(line.substring(col*2+1, (col*2)+2)),
                        new Pos(col, row), 0, parent);
                }

            }
        }

    }
    
}
