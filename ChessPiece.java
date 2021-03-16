package computerscienceia;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ChessPiece extends javax.swing.JLabel {
    
    // Chess Piece properties
    
    int type; // What kind of piece it is
    int colour; // The colour of the piece
    int taken; // Whether the piece has been taken or not
    Pos position; // The position (Squares) of the piece
    ChessPanel Parent;
    
    public ChessPiece(int type, int colour, Pos position, int taken, ChessPanel Parent) {
        this.type = type;
        this.colour = colour;
        this.taken = taken;
        this.position = position;
        this.Parent = Parent;
        
        // Initialise piece
        setType(type);
        setLocation();
    }
    
    public ChessPiece(int type, int colour, int index, ChessPanel Parent){
        this.type = type;
        this.colour = colour;
        this.Parent = Parent;
        this.setType(type);
        this.takePiece(index);
    }
    
    public void setType(int value){
        type = value;
        try {
            this.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(imageResource()))));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
    // Get Image Resource
    
    public String imageResource(){
        String imageDirectory = "/resources/";
        
        if (type == 1){
            imageDirectory = imageDirectory+"Pawn";
        } else if (type == 2){
            imageDirectory = imageDirectory+"Castle";
        } else if (type == 3){
            imageDirectory = imageDirectory+"Knight";
        } else if (type == 4){
            imageDirectory = imageDirectory+"Bishop";
        } else if (type == 5){
            imageDirectory = imageDirectory+"Queen";
        } else if (type == 6){
            imageDirectory = imageDirectory+"King";
        }
        
        if (colour == 8){
            imageDirectory = imageDirectory+"_White.png";
        } else if(colour == 9){
            imageDirectory = imageDirectory+"_Black.png";
        }

        return imageDirectory;
    }
    
    // Set original position of piece
    
    public void setLocation(){
        this.setBounds((50*position.col)+80, (50*position.row)+60, 50, 50);

        Parent.add(this);
        Parent.setComponentZOrder(this, 0);

    }
    
    // Move the piece
    
    public boolean movePiece(int col, int row){
        
        int[][] moveTable = getMoveTable();
        
        if (moveTable[col][row] == 5 || moveTable[col][row] == 6){
            allowMove(col, row, false);
            if (moveTable[col][row] == 5){
                Parent.currentGame.board[0][row].allowMove(3, row, true);
            } else if (moveTable[col][row] == 6){
                Parent.currentGame.board[7][row].allowMove(5, row, true);
            }
            
        } else if (moveTable[col][row] == 1){
            allowMove(col, row, false);
            if (type == 1 && (row == 0 || row == 7)){
                return true;
            }
        } else if (moveTable[col][row] == 2){
            Parent.removePiece(col, row);
            allowMove(col, row, false);
            if (type == 1 && (row == 0 || row == 7)){
                return true;
            }
        }
        
        return false;
    
    }
    
    // Used to keep track of the king moving
    public void kingMoved(){
        if (colour == 8){
            Parent.kingMoved[0] = true;
        } else if (colour == 9){
            Parent.kingMoved[1] = true;
        }
    }
    
    // Moves the piece off the board and onto the side
    public void takePiece(int index){
        
        int modifier= index / 8;
        index = index - (8*modifier);
        
        if (colour == 8){
            this.setBounds(500+(40*index), 230+(40*modifier), 50, 50);
        } else if (colour == 9){
            this.setBounds(500+(40*index), 330+(40*modifier), 50, 50);
        }
        taken = 1;
    }
    
    public void allowMove(int col, int row, boolean special){
        
        // If the move is permitted, make it execute that move
        
        if (type == 6){
            kingMoved();
        }
        
        // Labelling for move history
        
        String[] locationLetters = new String[]{"A","B","C","D","E","F","G","H"};
        String[] locationNumbers = new String[]{"8","7","6","5","4","3","2","1"};
        String moveDetail = locationLetters[position.col]+""+locationNumbers[position.row]+" To "+locationLetters[col]+""+locationNumbers[row];
        
        // Change Position in record
        
        Parent.currentGame.board[col][row] = this;
        Parent.currentGame.board[position.col][position.row] = null;

        // Change position parameters
        position.col = col;
        position.row = row;
        
        // Change Actual position
        this.setBounds((50*position.col)+80, (50*position.row)+60, 50, 50);
        
        // Validate and Finish new piece position
        if (!special){
            Parent.addToPreviousTurns(moveDetail);
            Parent.switchTurn();
        }

    }
    
    public boolean detectCheck(int col, int row){
        boolean result = false;
        
        // Used to detect whether the king is in check or will be in check
        
        int[][] checkTable = new int[8][8];
        int[][] otherPieceTable = new int[8][8];
 
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if(Parent.currentGame.board[c][r]!=null){
                    otherPieceTable[c][r] = Parent.currentGame.board[c][r].colour;
                }
            }
        }
        
        // 2 = Pawn / Bishop / Queen / King
        // 3 = King / Bishop / Queen (Immediate Surroundings)
        // 4 = King / Castle / Queen (Immediate Surroundings)
        // 5 = Bishop / Queen
        // 6 = Castle / Queen
        // 7 = Knight
        
        // Checks all directions and notes opposing pieces with a type
        // If that piece has said type then the king will be in check
        
        // 7 - Knight
        // <editor-fold defaultstate="collapsed" desc="7 Piece Readings">
        for (int cycle = 0; cycle < 4; cycle++){
            // Check RIGHT
            if (cycle == 0){
                int cVal = col+2;
                if (cVal <= 7 && row + 1 <= 7){
                    if (otherPieceTable[cVal][row+1] == getOpponentColour()){
                        checkTable[cVal][row+1] = 7;
                    }
                }
                if (cVal <= 7 && row - 1 >= 0){
                    if (otherPieceTable[cVal][row-1] == getOpponentColour()){
                        checkTable[cVal][row-1] = 7;
                    }
                }
            } else if (cycle == 1){
                // Check Left
                int cVal = col-2;
                if (cVal >= 0 && row + 1 <= 7){
                    if (otherPieceTable[cVal][row+1] == getOpponentColour()){
                        checkTable[cVal][row+1] = 7;
                    }
                }
                if (cVal >= 0 && row - 1 >= 0){
                    if (otherPieceTable[cVal][row-1] == getOpponentColour()){
                        checkTable[cVal][row-1] = 7;
                    }
                }
            } else if (cycle == 2){
                // Check UP
                int rVal = row-2;
                if (rVal >= 0 && col + 1 <= 7){
                    if (otherPieceTable[col+1][rVal] == getOpponentColour()){
                        checkTable[col+1][rVal] = 7;
                    }
                }
                if (rVal >= 0 && col - 1 >= 0){
                    if (otherPieceTable[col-1][rVal] == getOpponentColour()){
                        checkTable[col-1][rVal] = 7;
                    }
                }
            } else if (cycle == 3){
                // Check Down
                int rVal = row+2;
                if (rVal <= 7 && col + 1 <= 7){
                    if (otherPieceTable[col+1][rVal] == getOpponentColour()){
                        checkTable[col+1][rVal] = 7;
                    }
                }
                if (rVal <= 7 && col - 1 >= 0){
                    if (otherPieceTable[col-1][rVal] == getOpponentColour()){
                        checkTable[col-1][rVal] = 7;
                    }
                }
            }
        }
        // </editor-fold>
        
        // 5/6 - Bishop / Queen / Castle
        // <editor-fold defaultstate="collapsed" desc="5/6 Piece Readings">
        for (int cycle = 0; cycle < 4; cycle++){
                
                boolean cont = true;
                
                for (int check = 1; check <= 7; check++){
                    
                    if (cycle == 0){
                        // Up Right
                        
                        if (col - check >= 0 && row + check <= 7 && cont){
                            if (otherPieceTable[col-check][row+check] == getOpponentColour()){
                                checkTable[col-check][row+check] = 5;
                                cont = false;
                            } else if(otherPieceTable[col-check][row+check] == colour){
                                cont = false;
                            }
                        }

                    } else if (cycle == 1){
                        // Up Left
                        
                        if (col - check >= 0 && row - check >= 0 && cont){
                            if (otherPieceTable[col-check][row-check] == getOpponentColour()){
                                checkTable[col-check][row-check] = 5;
                                cont = false;
                            } else if(otherPieceTable[col-check][row-check] == colour){
                                cont = false;
                            }
                        }

                    } else if (cycle == 2){

                        // Down Left
                        
                        if (col + check <= 7 && row - check >= 0 && cont){
                            if (otherPieceTable[col+check][row-check] == getOpponentColour()){
                                checkTable[col+check][row-check] = 5;
                                cont = false;
                            } else if(otherPieceTable[col+check][row-check] == colour){
                                cont = false;
                            }
                        }

                    } else if (cycle == 3){

                        // Down Right
                        
                        if (col + check <= 7 && row + check <= 7 && cont){
                            if (otherPieceTable[col+check][row+check] == getOpponentColour()){
                                checkTable[col+check][row+check] = 5;
                                cont = false;
                            } else if(otherPieceTable[col+check][row+check] == colour){
                                cont = false;
                            }
                        }

                    }
                }
            }
            for (int cycle = 0; cycle < 4; cycle++){
                // Check Right
                boolean cont = true;
                if (cycle == 0){
                    for (int checkR = col + 1; checkR <= 7; checkR++){
                        if (cont){
                            if (otherPieceTable[checkR][row] == getOpponentColour()){
                                checkTable[checkR][row] = 6;
                                cont = false;
                            } else if (otherPieceTable[checkR][row] == colour){
                                cont = false;
                            }
                        }
                    }
                    // Check Left
                } else if (cycle == 1){
                    for (int checkL = col - 1; checkL >= 0; checkL--){
                        if (cont){
                            if (otherPieceTable[checkL][row] == getOpponentColour()){
                                checkTable[checkL][row] = 6;
                                cont = false;
                            } else if (otherPieceTable[checkL][row] == colour){
                                cont = false;
                            }
                        }
                    }
                    // Check UP
                } else if (cycle == 2){
                    for (int checkU = row - 1; checkU >= 0; checkU--){
                        if (cont){
                            if (otherPieceTable[col][checkU] == getOpponentColour()){
                                checkTable[col][checkU] = 6;
                                cont = false;
                            } else if (otherPieceTable[col][checkU] == colour){
                                cont = false;
                            }
                        }
                    }
                    // Check Down
                } else if (cycle == 3){
                    for (int checkD = row + 1; checkD <= 7; checkD++){
                        if (cont){
                            if (otherPieceTable[col][checkD] == getOpponentColour()){
                                checkTable[col][checkD] = 6;
                                cont = false;
                            } else if (otherPieceTable[col][checkD] == colour){
                                cont = false;
                            }
                        }
                    }
                }
                
            }
        
        // </editor-fold>
        
        // 3/4 - King / Bishop / Queen / Castle (Immediate Surroundings)
        // <editor-fold defaultstate="collapsed" desc="3/4 Piece Readings">
        for (int cycle = 0; cycle < 4; cycle++){
            // Check Right
            if (cycle == 0){

                int checkR = col + 1;
                if (checkR <= 7){
                    if (otherPieceTable[checkR][row] == getOpponentColour()){
                        checkTable[checkR][row] = 4;
                    }
                    // UR
                    if (row -1 >= 0 && otherPieceTable[checkR][row - 1] == getOpponentColour()){
                        checkTable[checkR][row - 1] = 3;
                    }
                }

                // Check Left
            } else if (cycle == 1){

                int checkL = col - 1;
                if (checkL >= 0){
                    if (otherPieceTable[checkL][row] == getOpponentColour()){
                        checkTable[checkL][row] = 4;
                    }
                    // DL
                    if (row + 1 <= 7 && otherPieceTable[checkL][row + 1] == getOpponentColour()){
                        checkTable[checkL][row + 1] = 3;
                    }
                }

                // Check UP
            } else if (cycle == 2){
                int checkU = row - 1;
                if (checkU >= 0){
                    if (otherPieceTable[col][checkU] == getOpponentColour()){
                        checkTable[col][checkU] = 4;
                    }
                    // UL
                    if (col - 1 >= 0 && otherPieceTable[col-1][checkU] == getOpponentColour()){
                        checkTable[col-1][checkU] = 3;
                    }
                }

                // Check Down
            } else if (cycle == 3){
                int checkD = row + 1;
                if (checkD <= 7){
                    if (otherPieceTable[col][checkD] == getOpponentColour()){
                        checkTable[col][checkD] = 4;
                    }
                    // DR
                    if (col + 1 <= 7 && otherPieceTable[col + 1][checkD] == getOpponentColour()){
                        checkTable[col + 1][checkD] = 3;
                        
                    }
                }

            }

        }
        // </editor-fold>
        
        // 2 - Pawn / Bishop / Queen / King (Immediate Sorroundings - Diagonal)
        // <editor-fold defaultstate="collapsed" desc="2 Piece Readings">
        int colourModifier = 0;

        if (colour == 8){
            colourModifier = -1;
        } else if(colour == 9){
            colourModifier = 1;
        }
        
        if (col - 1 >= 0 && otherPieceTable[col - 1][row + colourModifier]== getOpponentColour()){
            checkTable[col - 1][row + colourModifier] = 2;
        }
        if (col + 1 <= 7 && otherPieceTable[col + 1][row + colourModifier]== getOpponentColour()){
            checkTable[col + 1][row + colourModifier] = 2;
        }
        // </editor-fold>
        
        // -----------
        
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                int t = 0;
                if (Parent.currentGame.board[j][i] != null){
                    t = Parent.currentGame.board[j][i].type;
                }
                // Types
                // 1 = Pawn
                // 2 = Castle
                // 3 = Knight
                // 4 = Bishop
                // 5 = Queen
                // 6 = King
                
                // Checks pieces with types
                
                if ((checkTable[j][i] == 2 && (t == 1 || t == 4 || t == 5 || t == 6)) ||
                        (checkTable[j][i] == 3 && (t == 4 || t == 5 || t == 6)) ||
                        (checkTable[j][i] == 4 && (t == 2 || t == 5 || t == 6)) ||
                        (checkTable[j][i] == 5 && (t == 4 || t == 5 )) ||
                        (checkTable[j][i] == 6 && (t == 2 || t == 5 )) ||
                        (checkTable[j][i] == 7 && (t == 3))){

                    return true;

                }
            }
        }
        
        return false;
    }
    
    public int[][] getMoveTable(){
        
        // Generates a table of all possible moves a given piece can make
        // 1 Indicates simply moving there
        // 2 Indicates taking another piece
        // 5 and 6 are special moves (For a king and castle swap)
        
        int[][] moveTable = new int[8][8];
        int[][] otherPieceTable = new int[8][8];
        
        int c = position.col;
        int r = position.row;

        for (int col = 0; col < 8; col++){
            for (int row = 0; row < 8; row++){
                if(Parent.currentGame.board[col][row]!=null){
                    otherPieceTable[col][row] = Parent.currentGame.board[col][row].colour;
                }
            }
        }
        // <editor-fold defaultstate="collapsed" desc="Pawn Movement">
        // PAWN MOVEMENT
        if (type == 1){
            
            int colourModifier = 0;

            if (colour == 8){
                colourModifier = -1;
            } else if(colour == 9){
                colourModifier = 1;
            }
            if (otherPieceTable[c][r + colourModifier]== 0){
                
                moveTable[c][r + colourModifier] = 1;
                
                if (r == 6 && colourModifier == -1 && otherPieceTable[c][r - 2]== 0){
                    moveTable[c][r - 2] = 1;
                } else if (r == 1 && colourModifier == 1 && otherPieceTable[c][r + 2] == 0){
                    moveTable[c][r + 2] = 1;
                }
            }
            // Pieces that can be taken
            
            if (c - 1 >= 0 && otherPieceTable[c - 1][r + colourModifier]== getOpponentColour()){
                moveTable[c - 1][r + colourModifier] = 2;
            }
            if (c + 1 <= 7 && otherPieceTable[c + 1][r + colourModifier]== getOpponentColour()){
                moveTable[c + 1][r + colourModifier] = 2;
            }
        // </editor-fold>
            // CASTLE MOVEMENT
        // <editor-fold defaultstate="collapsed" desc="Castle Movement">
        } else if (type == 2){
            
            for (int cycle = 0; cycle < 4; cycle++){
                // Check Right
                boolean cont = true;
                if (cycle == 0){
                    for (int checkR = c + 1; checkR <= 7; checkR++){
                        if (cont){
                            if (otherPieceTable[checkR][r] == getOpponentColour()){
                                moveTable[checkR][r] = 2;
                                cont = false;
                            } else if (otherPieceTable[checkR][r] == colour){
                                cont = false;
                            } else {
                                moveTable[checkR][r] = 1;
                            }
                        }
                    }
                    // Check Left
                } else if (cycle == 1){
                    for (int checkL = c - 1; checkL >= 0; checkL--){
                        if (cont){
                            if (otherPieceTable[checkL][r] == getOpponentColour()){
                                moveTable[checkL][r] = 2;
                                cont = false;
                            } else if (otherPieceTable[checkL][r] == colour){
                                cont = false;
                            } else {
                                moveTable[checkL][r] = 1;
                            }
                        }
                    }
                    // Check UP
                } else if (cycle == 2){
                    for (int checkU = r - 1; checkU >= 0; checkU--){
                        if (cont){
                            if (otherPieceTable[c][checkU] == getOpponentColour()){
                                moveTable[c][checkU] = 2;
                                cont = false;
                            } else if (otherPieceTable[c][checkU] == colour){
                                cont = false;
                            } else {
                                moveTable[c][checkU] = 1;
                            }
                        }
                    }
                    // Check Down
                } else if (cycle == 3){
                    for (int checkD = r + 1; checkD <= 7; checkD++){
                        if (cont){
                            if (otherPieceTable[c][checkD] == getOpponentColour()){
                                moveTable[c][checkD] = 2;
                                cont = false;
                            } else if (otherPieceTable[c][checkD] == colour){
                                cont = false;
                            } else {
                                moveTable[c][checkD] = 1;
                            }
                        }
                    }
                }
                
            }
            // </editor-fold>
            // KNIGHT MOVEMENT
        // <editor-fold defaultstate="collapsed" desc="Knight Movement">
        } else if (type == 3){
            
            for (int cycle = 0; cycle < 4; cycle++){
                // Check RIGHT
                if (cycle == 0){
                    int cVal = c+2;
                    if (cVal <= 7 && r + 1 <= 7){
                        if (otherPieceTable[cVal][r+1] == getOpponentColour()){
                            moveTable[cVal][r+1] = 2;
                        } else if(otherPieceTable[cVal][r+1] == 0){
                            moveTable[cVal][r+1] = 1;
                        }
                    }
                    if (cVal <= 7 && r - 1 >= 0){
                        if (otherPieceTable[cVal][r-1] == getOpponentColour()){
                            moveTable[cVal][r-1] = 2;
                        } else if(otherPieceTable[cVal][r-1] == 0){
                            moveTable[cVal][r-1] = 1;
                        }
                    }
                } else if (cycle == 1){
                    // Check Left
                    int cVal = c-2;
                    if (cVal >= 0 && r + 1 <= 7){
                        if (otherPieceTable[cVal][r+1] == getOpponentColour()){
                            moveTable[cVal][r+1] = 2;
                        } else if(otherPieceTable[cVal][r+1] == 0){
                            moveTable[cVal][r+1] = 1;
                        }
                    }
                    if (cVal >= 0 && r - 1 >= 0){
                        if (otherPieceTable[cVal][r-1] == getOpponentColour()){
                            moveTable[cVal][r-1] = 2;
                        } else if(otherPieceTable[cVal][r-1] == 0){
                            moveTable[cVal][r-1] = 1;
                        }
                    }
                } else if (cycle == 2){
                    // Check UP
                    int rVal = r-2;
                    if (rVal >= 0 && c + 1 <= 7){
                        if (otherPieceTable[c+1][rVal] == getOpponentColour()){
                            moveTable[c+1][rVal] = 2;
                        } else if(otherPieceTable[c+1][rVal] == 0){
                            moveTable[c+1][rVal] = 1;
                        }
                    }
                    if (rVal >= 0 && c - 1 >= 0){
                        if (otherPieceTable[c-1][rVal] == getOpponentColour()){
                            moveTable[c-1][rVal] = 2;
                        } else if(otherPieceTable[c-1][rVal] == 0){
                            moveTable[c-1][rVal] = 1;
                        }
                    }
                } else if (cycle == 3){
                    // Check Down
                    int rVal = r+2;
                    if (rVal <= 7 && c + 1 <= 7){
                        if (otherPieceTable[c+1][rVal] == getOpponentColour()){
                            moveTable[c+1][rVal] = 2;
                        } else if(otherPieceTable[c+1][rVal] == 0){
                            moveTable[c+1][rVal] = 1;
                        }
                    }
                    if (rVal <= 7 && c - 1 >= 0){
                        if (otherPieceTable[c-1][rVal] == getOpponentColour()){
                            moveTable[c-1][rVal] = 2;
                        } else if(otherPieceTable[c-1][rVal] == 0){
                            moveTable[c-1][rVal] = 1;
                        }
                    }
                }
            }
            // </editor-fold>
            // BISHOP MOVEMENT
        // <editor-fold defaultstate="collapsed" desc="Bishop Movement">
        } else if (type == 4){
            
            for (int cycle = 0; cycle < 4; cycle++){
                
                boolean cont = true;
                
                for (int check = 1; check <= 7; check++){
                    
                    if (cycle == 0){
                        // Up Right
                        
                        if (c - check >= 0 && r + check <= 7 && cont){
                            if (otherPieceTable[c-check][r+check] == getOpponentColour()){
                                moveTable[c-check][r+check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c-check][r+check] == colour){
                                cont = false;
                            } else {
                                moveTable[c-check][r+check] = 1;
                            }
                        }

                    } else if (cycle == 1){
                        // Up Left
                        
                        if (c - check >= 0 && r - check >= 0 && cont){
                            if (otherPieceTable[c-check][r-check] == getOpponentColour()){
                                moveTable[c-check][r-check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c-check][r-check] == colour){
                                cont = false;
                            } else {
                                moveTable[c-check][r-check] = 1;
                            }
                        }

                    } else if (cycle == 2){

                        // Down Left
                        
                        if (c + check <= 7 && r - check >= 0 && cont){
                            if (otherPieceTable[c+check][r-check] == getOpponentColour()){
                                moveTable[c+check][r-check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c+check][r-check] == colour){
                                cont = false;
                            } else {
                                moveTable[c+check][r-check] = 1;
                            }
                        }

                    } else if (cycle == 3){

                        // Down Right
                        
                        if (c + check <= 7 && r + check <= 7 && cont){
                            if (otherPieceTable[c+check][r+check] == getOpponentColour()){
                                moveTable[c+check][r+check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c+check][r+check] == colour){
                                cont = false;
                            } else {
                                moveTable[c+check][r+check] = 1;
                            }
                        }

                    }
                }
            }
            // </editor-fold>
            // Queen Movement
        // <editor-fold defaultstate="collapsed" desc="Queen Movement">
        } else if (type == 5){
            for (int cycle = 0; cycle < 4; cycle++){
                
                boolean cont = true;
                
                for (int check = 1; check <= 7; check++){
                    
                    if (cycle == 0){
                        // Up Right
                        
                        if (c - check >= 0 && r + check <= 7 && cont){
                            if (otherPieceTable[c-check][r+check] == getOpponentColour()){
                                moveTable[c-check][r+check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c-check][r+check] == colour){
                                cont = false;
                            } else {
                                moveTable[c-check][r+check] = 1;
                            }
                        }

                    } else if (cycle == 1){
                        // Up Left
                        
                        if (c - check >= 0 && r - check >= 0 && cont){
                            if (otherPieceTable[c-check][r-check] == getOpponentColour()){
                                moveTable[c-check][r-check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c-check][r-check] == colour){
                                cont = false;
                            } else {
                                moveTable[c-check][r-check] = 1;
                            }
                        }

                    } else if (cycle == 2){

                        // Down Left
                        
                        if (c + check <= 7 && r - check >= 0 && cont){
                            if (otherPieceTable[c+check][r-check] == getOpponentColour()){
                                moveTable[c+check][r-check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c+check][r-check] == colour){
                                cont = false;
                            } else {
                                moveTable[c+check][r-check] = 1;
                            }
                        }

                    } else if (cycle == 3){

                        // Down Right
                        
                        if (c + check <= 7 && r + check <= 7 && cont){
                            if (otherPieceTable[c+check][r+check] == getOpponentColour()){
                                moveTable[c+check][r+check] = 2;
                                cont = false;
                            } else if(otherPieceTable[c+check][r+check] == colour){
                                cont = false;
                            } else {
                                moveTable[c+check][r+check] = 1;
                            }
                        }

                    }
                }
            }
            for (int cycle = 0; cycle < 4; cycle++){
                // Check Right
                boolean cont = true;
                if (cycle == 0){
                    for (int checkR = c + 1; checkR <= 7; checkR++){
                        if (cont){
                            if (otherPieceTable[checkR][r] == getOpponentColour()){
                                moveTable[checkR][r] = 2;
                                cont = false;
                            } else if (otherPieceTable[checkR][r] == colour){
                                cont = false;
                            } else {
                                moveTable[checkR][r] = 1;
                            }
                        }
                    }
                    // Check Left
                } else if (cycle == 1){
                    for (int checkL = c - 1; checkL >= 0; checkL--){
                        if (cont){
                            if (otherPieceTable[checkL][r] == getOpponentColour()){
                                moveTable[checkL][r] = 2;
                                cont = false;
                            } else if (otherPieceTable[checkL][r] == colour){
                                cont = false;
                            } else {
                                moveTable[checkL][r] = 1;
                            }
                        }
                    }
                    // Check UP
                } else if (cycle == 2){
                    for (int checkU = r - 1; checkU >= 0; checkU--){
                        if (cont){
                            if (otherPieceTable[c][checkU] == getOpponentColour()){
                                moveTable[c][checkU] = 2;
                                cont = false;
                            } else if (otherPieceTable[c][checkU] == colour){
                                cont = false;
                            } else {
                                moveTable[c][checkU] = 1;
                            }
                        }
                    }
                    // Check Down
                } else if (cycle == 3){
                    for (int checkD = r + 1; checkD <= 7; checkD++){
                        if (cont){
                            if (otherPieceTable[c][checkD] == getOpponentColour()){
                                moveTable[c][checkD] = 2;
                                cont = false;
                            } else if (otherPieceTable[c][checkD] == colour){
                                cont = false;
                            } else {
                                moveTable[c][checkD] = 1;
                            }
                        }
                    }
                }
                
            }
            // </editor-fold>
            // King Movement
        // <editor-fold defaultstate="collapsed" desc="King Movement">
        } else if (type == 6){
            for (int cycle = 0; cycle < 4; cycle++){
                // Check Right
                if (cycle == 0){

                    int checkR = c + 1;
                    if (checkR <= 7){
                        // R
                        if (otherPieceTable[checkR][r] == getOpponentColour()){
                            moveTable[checkR][r] = 2;
                        } else if (otherPieceTable[checkR][r] == 0){
                            moveTable[checkR][r] = 1;
                        } 
                        // UR
                        if (r -1 >= 0){
                            if (otherPieceTable[checkR][r - 1] == getOpponentColour()){
                                moveTable[checkR][r - 1] = 2;
                            } else if (otherPieceTable[checkR][r - 1] == 0){
                                moveTable[checkR][r - 1] = 1;
                            } 
                        }
                    }
                    
                    // Check Left
                } else if (cycle == 1){
                    
                    int checkL = c - 1;
                    if (checkL >= 0){
                        // L
                        if (otherPieceTable[checkL][r] == getOpponentColour()){
                            moveTable[checkL][r] = 2;
                        } else if (otherPieceTable[checkL][r] == 0){
                            moveTable[checkL][r] = 1;
                        } 
                        // DL
                        if (r + 1 <= 7){
                            if (otherPieceTable[checkL][r + 1] == getOpponentColour()){
                                moveTable[checkL][r + 1] = 2;
                            } else if (otherPieceTable[checkL][r + 1] == 0){
                                moveTable[checkL][r + 1] = 1;
                            } 
                        }
                    }
                    
                    // Check UP
                } else if (cycle == 2){
                    int checkU = r - 1;
                    if (checkU >= 0){
                        // U
                        if (otherPieceTable[c][checkU] == getOpponentColour()){
                            moveTable[c][checkU] = 2;
                        } else if (otherPieceTable[c][checkU] == 0){
                            moveTable[c][checkU] = 1;
                        } 
                        // UL
                        if (c - 1 >= 0){
                            if (otherPieceTable[c-1][checkU] == getOpponentColour()){
                                moveTable[c-1][checkU] = 2;
                            } else if (otherPieceTable[c-1][checkU] == 0){
                                moveTable[c-1][checkU] = 1;
                            }
                        }
                    }
                    
                    // Check Down
                } else if (cycle == 3){
                    int checkD = r + 1;
                    if (checkD <= 7){
                        // D
                        if (otherPieceTable[c][checkD] == getOpponentColour()){
                            moveTable[c][checkD] = 2;
                        } else if (otherPieceTable[c][checkD] == 0){
                            moveTable[c][checkD] = 1;
                        }
                        // DR
                        if (c + 1 <= 7){
                            if (otherPieceTable[c + 1][checkD] == getOpponentColour()){
                                moveTable[c + 1][checkD] = 2;
                            } else if (otherPieceTable[c + 1][checkD] == 0){
                                moveTable[c + 1][checkD] = 1;
                            }
                        }
                    }
                    
                }
                if (Parent.kingMoved[colour-8] == false){
                    // Castle left
                    if (Parent.currentGame.board[0][r] != null && Parent.currentGame.board[0][r].type == 2 && Parent.currentGame.board[0][r].colour == this.colour){
                        boolean possible = true;
                        for (int col = 1; col < 4; col++){
                            if (otherPieceTable[col][r] != 0){
                                possible = false;
                            }
                        }
                        if (possible == true){
                            moveTable[2][r] = 5;
                        }
                    }
                    // Castle right
                    if (Parent.currentGame.board[7][r] != null && Parent.currentGame.board[7][r].type == 2 && Parent.currentGame.board[7][r].colour == this.colour){
                        boolean possible = true;
                        for (int col = 5; col <= 6; col++){
                            if (otherPieceTable[col][r] != 0){
                                possible = false;
                            }
                        }
                        if (possible == true){
                            moveTable[6][r] = 6;
                        }
                    }
                }
                
            }
        }
        // </editor-fold>

        // Removes all moves that would lead to the king being in check
        
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                boolean val = false;
                if (moveTable[j][i] != 0){
                    
                    ChessPiece temp = null;
                    
                    if (Parent.currentGame.board[j][i] != null){
                        temp = Parent.currentGame.board[j][i];
                    }
                    
                    Parent.currentGame.board[j][i] = this;
                    Parent.currentGame.board[position.col][position.row] = null;

                    if (detectCheck(Parent.kings[colour-8].col, Parent.kings[colour-8].row) && type != 6){
                        moveTable[j][i] = 0;
                    } else if (type == 6 && detectCheck(j, i)){
                        moveTable[j][i] = 0;
                    }
                    
                    Parent.currentGame.board[position.col][position.row] = this;
                    Parent.currentGame.board[j][i] = temp;
                    
                }
            }
        }
        
        // </editor-fold>
        
        return moveTable;
    }
    
    public boolean checkValid(int value, int increment){
        if (increment == 1 && value <= 7){
            return true;
        } else if (increment == -1 && value >= 0){
            return true;
        } else {
            return false;
        }
    }
    
    public int getOpponentColour(){
        
        // Gets the opposing colour to that of this piece
        
        int returnValue = 0;
        if (colour == 8){
            returnValue = 9;
        } else if (colour == 9){
            returnValue = 8;
        }
        return returnValue;
    }
    
}
