package computerscienceia;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Timer;
import javax.imageio.ImageIO;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class ChessPanel extends javax.swing.JPanel {
    
    // Board Setup variables
    boolean BS = false;
    String[] player;
    
    Main parent; // Parent of Object
    
    int currentTurn; // Who's turn it is
    
    int moveCount = 1; // Current move the players are on
    Rectangle parentBounds; // Bounds of the parentObject (used when resizing)
    
    boolean[] kingMoved = new boolean[]{false, false}; // Store whether the king has been moved
    
    // Timers
    
    Time[] timerValues = new Time[2];
    boolean timerActive = true;

    boolean disabled = false; // Allows moves to be made when false
    
    Pos previousClick = new Pos(-1, -1); // Where the player(s) previously clicked
    Pos[] kings = new Pos[]{new Pos(0, 0), new Pos(0, 0)}; // Keeps track of the position of both Kings.
    
    int winner = 0; // Holds the value of who won the game
    
    boolean started = false; // Goes true once at least a move has been made
        
    ChessGame currentGame; // Current Board Setup
    
    int indexW; // Variables to control the positioning of taken pieces
    int indexB;
    
    // GUI Elements
    
    java.awt.Font mainFont = new java.awt.Font("Comic Sans", 0, 20);
    
    // Array of taken pieces
    
    ChessPiece[] takenPiecesB = new ChessPiece[16];
    ChessPiece[] takenPiecesW = new ChessPiece[16];
    
    // Turn Display
    
    JTextField turnBlack;
    JTextField turnWhite;
    
    JButton[] pieceChange = new JButton[4]; // Buttons for pawn transformation
    
    // JLabels to display the squares of the chess board
    
    JLabel board[][] = new JLabel[8][8];
    
    // Scrolling Text Area to display previous turns
    
    JTextArea previousTurnsList = new JTextArea();
    JScrollPane previousTurns = new JScrollPane(previousTurnsList);
    LinkedList<String> turnsMade = new LinkedList<>(); // Used for saving later, contains all the turns made
    
    Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
    
    JLabel[] timers = new JLabel[2]; // Labels to display timer values

    // Pass in only a single object containing all of these parameters (Does all the reading of the file -> contains all the information required)
    
    public ChessPanel(ChessGame currentGame, Main parent) {
        
        this.parent = parent;

        this.currentGame = currentGame;
        
        String userDirectory = System.getProperty("user.dir"); // Gets directory of the file for referencing save files
        
        // Setting original values
        
        currentTurn = currentGame.turn;
        player = new String[]{currentGame.playerNames[0], currentGame.playerNames[1]};
        if (currentGame.timers[0] == -1){
            timerValues[0] = new Time(-1);
            timerValues[1] = new Time(-1);
            timerActive = false;
        } else {
            timerValues[0] = new Time(currentGame.timers[0]);
            timerValues[1] = new Time(currentGame.timers[1]);
        }
        
        if (!currentGame.saveFileName.equals("defaultArrangement")){
            
            // Add previous moves
            
            for (int i = 0; i < currentGame.previousMoves.size(); i++){
                previousTurnsList.append(currentGame.previousMoves.get(i)+"\n");
                turnsMade.add(currentGame.previousMoves.get(i));
                moveCount++;
            }
            
            // Add taken pieces
            for (int i = 0; i < currentGame.takenPiecesW.size(); i++){
                takenPiecesW[indexW] = new ChessPiece(Integer.parseInt(currentGame.takenPiecesW.get(i)), 8, indexW, this);
                this.add(takenPiecesW[indexW]);
                indexW++;
            }
            for (int i = 0; i < currentGame.takenPiecesB.size(); i++){
                
                takenPiecesB[indexB] = new ChessPiece(Integer.parseInt(currentGame.takenPiecesB.get(i)), 9, indexB, this);
                this.add(takenPiecesB[indexB]);
                indexB++;
            }
            
            this.revalidate();
            this.repaint();

        }
        
        createWindow();
        
        // Windowlistener to detect when the window is closed
        
        parent.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        parent.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent windowEvent){
                saveGame();
                System.exit(0);
            }
        });
        
        // Mouse listener gets where the mouse was clicked and makes pieces move if clicked correctly.
        
        addMouseListener(new MouseAdapter() { 
            @Override
            public void mousePressed(MouseEvent event) { 
                int mouseX = event.getX();
                int mouseY = event.getY();
                
                for (int i = 0; i < 2; i++){
                    
                    for (int r = 0; r < 8; r++){
                        for (int c = 0; c < 8; c++){
                            if (currentGame.board[c][r] != null && currentGame.board[c][r].type == 6 && currentGame.board[c][r].colour == i+8){
                                kings[i].col = c;
                                kings[i].row = r;
                            }
                        }
                    }
                }
                
                Pos position = getArrayCoords(mouseX, mouseY);
                if (position.col != -1 && position.row != -1 && !disabled){
                            
                    // First Click (Piece Selection)
                    
                    if (previousClick.col == -1 && previousClick.row == -1){
                        
                        if (currentGame.board[position.col][position.row] != null && currentGame.board[position.col][position.row].colour == (currentTurn+8)){
                            
                            previousClick = new Pos(position.col, position.row);

                            int moveTable[][] = currentGame.board[previousClick.col][previousClick.row].getMoveTable();
                            for (int row = 0; row < 8; row++){
                                for (int col = 0; col < 8; col++){
                                    try{
                                        
                                        // Displays coloured squares for where movement is possible
                                        
                                        if (moveTable[col][row] == 1){
                                            
                                            board[row][col].setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/SquareAvailable.png"))));
                                        } else if (moveTable[col][row] == 2){
                                            board[row][col].setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/SquareRemove.png"))));
                                        } else if (moveTable[col][row] == 5 || moveTable[col][row] == 6){
                                            board[row][col].setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/SquareSpecial.png"))));
                                        }
                                    } catch (Exception ex){
                                        ex.printStackTrace();;
                                    }
                                }
                            }
                        }
                        
                    } else {
                        
                        // Second Click (Move Piece)
                        
                        boolean swap = false;
                        
                        if ((currentGame.board[position.col][position.row] != null && currentGame.board[position.col][position.row].colour != currentGame.board[previousClick.col][previousClick.row].colour) || (currentGame.board[position.col][position.row] == null)){
                            swap = currentGame.board[previousClick.col][previousClick.row].movePiece(position.col, position.row);
                            started = true;
                        }
                        previousClick = new Pos(-1, -1); // Reset
                        resetSquares();
                        
                        // If true allow pawn exchange
                        
                        if (swap){
                            changePiece(position.col, position.row);
                        }
                    }
                }
                
                // Check Mate Detection
                
                if (whoCheck() != 0 && !disabled){
                    if(isCheckMate()){
                        disabled = true;
                        winner();
                    }
                }
            }
        });
        
        // Timers
        if (timerActive){
            
            // Activity to periodically run. (Reduces timer Value and ends the game if time is exceeded)
            
            TimerTask reduceTimer = new TimerTask(){
                @Override
                public void run(){
                    if (started){
                        if (timerValues[currentTurn].seconds > 0){
                            timerValues[currentTurn].seconds--;
                            timerValues[currentTurn].convertTime();
                            timerText(currentTurn);
                        } else if (timerValues[currentTurn].seconds <= 0){
                            if (currentTurn == 0){
                                winner = 9;
                            } else if (currentTurn == 1){
                                winner = 8;
                            }
                            started = false;
                            disabled = true;
                            winner();
                        }
                    }
                }
            };

            Timer overallTimer = new Timer();

            overallTimer.scheduleAtFixedRate(reduceTimer, 0, 1000);
        }
    }
    
    public void saveGame(){
        if (started){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");
            String fileName = "";
            
            // Creates the name of the file, this will be the same as the original if the game was loaded
            
            if (currentGame.saveFileName.equals("defaultArrangement")){
                fileName = player[0]+"_"+player[1]+"_"+dtf.format(LocalDateTime.now());
            } else {
                fileName = currentGame.saveFileName;
            }

            try {
                PrintWriter saveFile = new PrintWriter(new FileWriter(System.getProperty("user.dir")+"/Saves/"+fileName+".txt"));

                for (int row = 0; row < 8; row++){
                    String line = "";
                    for (int col = 0; col < 8; col++){
                        if (currentGame.board[col][row] != null){
                            line = line+currentGame.board[col][row].type+currentGame.board[col][row].colour;
                        } else {
                            line = line+"00";
                        }
                    }
                    saveFile.write(line+"\n");
                }
                saveFile.write(player[0]+"\n");
                saveFile.write(player[1]+"\n");
                saveFile.write(timerValues[0].seconds+"\n");
                saveFile.write(timerValues[0].seconds+"\n");
                saveFile.write(currentTurn+"\n");
                String lineWhite = "";
                for (int i = 0; i < 16; i++){
                    if (takenPiecesW[i] != null){
                        lineWhite = lineWhite+takenPiecesW[i].type;
                    }
                }
                saveFile.write(lineWhite+"\n");
                String lineBlack = "";
                for (int i = 0; i < 16; i++){
                    if (takenPiecesB[i] != null){
                        lineBlack = lineBlack+takenPiecesB[i].type;
                    }
                }
                saveFile.write(lineBlack+"\n");

                for (int i = 0; i < turnsMade.size(); i++){
                    saveFile.write(turnsMade.get(i)+"\n");
                }

                saveFile.close();
            } catch (Exception exc){
                exc.printStackTrace();
            }
        }
        
    }
    
    public void winner(){
        
        started = false; // To prevent the game from saving if the window is closed
        
        JTextField[] winnerField = new JTextField[3];
        
        String[] labelText = new String[3];

        labelText[0] = player[winner - 8];
        labelText[1] = "Won the game";
        
        try{
            
            // Store game finish information in the leaderboard text file
            
            PrintWriter infoWriter = new PrintWriter(new FileWriter(System.getProperty("user.dir")+"/LeaderBoard/LeaderBoard.txt", true));
            
            String winnerInformation = player[0]+" "+player[1];
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            if (winner == 8){
                winnerInformation = winnerInformation + " 1 0";
            } else if (winner == 9){
                winnerInformation = winnerInformation + " 0 1";
            }
            winnerInformation = winnerInformation+" "+dtf.format(LocalDateTime.now());
            
            infoWriter.append(winnerInformation+"\n");
            
            infoWriter.close();
            
        } catch (Exception m){
            System.out.println("Issue when recording winning information");
        }
        
        for (int i = 0; i < 3; i++){
            winnerField[i] = new JTextField();
            winnerField[i].setBounds(100, 150+(i*100), 700, 120);
            winnerField[i].setHorizontalAlignment(JLabel.CENTER);
            winnerField[i].setFont(mainFont);
            winnerField[i].setEditable(false);
            if (i == 0){
                winnerField[i].setBorder(BorderFactory.createMatteBorder(3, 3, 0, 3, Color.gray));
            } else {
                winnerField[i].setBorder(BorderFactory.createMatteBorder(0, 3, 3, 3, Color.gray));
            }
            
            
            if (winner == 8 ){
                winnerField[i].setForeground(new Color(0, 0, 0));
                winnerField[i].setBackground(new Color(255, 255, 255));
            } else if (winner == 9){
                winnerField[i].setForeground(new Color(255, 255, 255));
                winnerField[i].setBackground(new Color(0, 0, 0));
            }
            
            winnerField[i].setText(labelText[i]);
            
            this.add(winnerField[i]);
            this.setComponentZOrder(winnerField[i], 0);
        }
        
        try{ 
            
            // If the game was loaded, delete the save file if the game is concluded
            
            if (!currentGame.saveFileName.equals("defaultArrangement")){
                Files.deleteIfExists(Paths.get(System.getProperty("user.dir")+"/Saves/"+currentGame.saveFileName+".txt"));
            }
        } catch (Exception p){
            System.out.println("Error while deleting.");
        }
        
        Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
        JButton continueButton = new JButton();
        continueButton.setText("Continue");
        continueButton.setBounds(300, 360, 300, 80);
        continueButton.setFont(mainFont);
        continueButton.setBorder(buttonBorder);
        continueButton.setForeground(new Color(100, 100, 100));
        continueButton.addActionListener((ActionEvent e) -> {
            parent.createGUI("main");

        });
        
        
        this.add(continueButton);
        this.setComponentZOrder(continueButton, 0);
        
        this.revalidate();
        this.repaint();
    }
    
    public int whoCheck(){
        
        // Detects who is check and returns the integer for the color. 0 if no one is check
        
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (currentGame.board[r][c] != null && currentGame.board[r][c].type == 6){
                    if (currentGame.board[r][c].detectCheck(r, c)){
                        return currentGame.board[r][c].colour;
                    }
                }
            }
        }
        return 0;
    }
    
    public boolean isCheckMate(){
        
        // Check if the player is in a checkmate
        
        int side = whoCheck(); // Determine who is check and which side they are on
        
        boolean result = true; // Base value is true, so anything that could negate it invalidates it
        
        // Gets a list of all pieces used by that side
        
        LinkedList<ChessPiece> checkMate = new LinkedList<>();
        
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                if (currentGame.board[c][r] != null && currentGame.board[c][r].colour == side){
                    checkMate.add(currentGame.board[c][r]);
                }
            }
        }
        
        // Check each piece and see if there is a valid move that can be made
        
        for (int i = 0; i < checkMate.size(); i++){
            int[][] movesToCheck = checkMate.get(i).getMoveTable();
            
            for (int row = 0; row < 8; row++){
                for (int col = 0; col < 8; col++){
                    
                    // If at least one valid move can be made, it is not a checkmate
                    
                    if (movesToCheck[col][row] != 0){
                        result = false;
                    }
                }
            }
        }
        
        // If there is a checkmate, sets the winner.
        
        if (side == 8 && result == true){
            winner = 9;
            return true;
        } else if (side == 9 && result == true){
            winner = 8;
            return true;
        }
        
        return false; // Base return value if no checkmate is detected
    }
    
    public void removePiece(int col, int row){
        
        // Removes a piece from the board and places it to the side if it is taken
        
        int index = 0;
        if (currentGame.board[col][row].colour == 8){
            takenPiecesW[indexW] = currentGame.board[col][row];
            index = indexW;
            
            indexW++;
        } else if (currentGame.board[col][row].colour == 9){
            takenPiecesB[indexB] = currentGame.board[col][row];
            index = indexB;
            
            indexB++;
        }
        currentGame.board[col][row].takePiece(index);
        
        if (currentGame.board[col][row] != null){
            currentGame.board[col][row] = null;
        }
    }
    
    // Pawn Transformation
    
    public void changePiece(int col, int row){
        disabled = true; // Make it so no moves can be made until a new piece has been chosen
        
        // Parameters for buttons
        int[][] positions = new int[][]{{950, 250}, {1000, 250}, {950, 300}, {1000, 300}};
        String[][] imageSources = new String[][]{{"/resources/Castle_White.png", "/resources/Castle_Black.png"}, {"/resources/Knight_White.png", "/resources/Knight_Black.png"}, {"/resources/Bishop_White.png", "/resources/Bishop_Black.png"}, {"/resources/Queen_White.png", "/resources/Queen_Black.png"}};
        
        // Increase Window Size
        parentBounds = parent.getBounds();
        Rectangle newParentBounds = new Rectangle(parentBounds);
        newParentBounds.width = 1100;
        parent.setBounds(newParentBounds);
        this.setBounds(0, 0, newParentBounds.width, newParentBounds.height);
        
        // Create window prompts 
        JLabel[] prompts = new JLabel[3];
        String[] messages = new String[]{"Please Select a", "Pawn Replacement", "Piece"};
        for (int k = 0; k < 3; k++){
            prompts[k] = new JLabel();
            prompts[k].setBounds(900, 60+(20*k), 200, 20);
            prompts[k].setForeground(new Color(100, 100, 100));
            prompts[k].setHorizontalAlignment(JLabel.CENTER);
            prompts[k].setFont(mainFont);
            prompts[k].setText(messages[k]);
            this.add(prompts[k]);
        }
        
        // Create buttons to facillitate the the pawn transformation

        for (int j = 0; j < 4; j++){
            pieceChange[j] = new JButton();
            pieceChange[j].setBounds(positions[j][0], positions[j][1], 50, 50);
            
            try {
                pieceChange[j].setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(imageSources[j][currentGame.board[col][row].colour - 8]))));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            final int typeValue = j+2;
            
            pieceChange[j].addActionListener((ActionEvent e) -> {
                
                currentGame.board[col][row].setType(typeValue);
                finaliseChange();
            });
            pieceChange[j].setVisible(true);
            this.add(pieceChange[j]);
           
        }
        this.revalidate();
        this.repaint();

    }
    
    public void finaliseChange(){
        
        // Resets the window after a selection has been made
        
        for (int i = 0; i < 4; i++){
            if (pieceChange[i] != null){
                this.remove(pieceChange[i]);
            }
        }
        parent.setBounds(parentBounds);
        this.setBounds(0, 0, parentBounds.width, parentBounds.height);
        disabled = false;
    }
    
    public Pos getArrayCoords(int mouseX, int mouseY){
        
        // Gets the square position of the mouse (In which box it is on the chess board)
        
        Pos coords = new Pos(-1, -1);

        if (mouseX > 80 && mouseX < 480){
            coords.col = (mouseX - 80)/50;
        }
        if (mouseY > 60 && mouseY < 460){
            coords.row = (mouseY - 60)/50;
        }

        return coords;
    }
    
    public void resetSquares(){
        
        // Redraws the board squares
        
        BS = false;
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                board[row][col].setIcon(new ImageIcon(getClass().getResource(getImage())));

            }
            BS = !BS;
        }
    }
    
    public void createWindow(){
        
        this.setLayout(null);

        // Create Black and White Squares
        BS = false;
        
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                board[row][col] = new JLabel();
                board[row][col].setBounds((50*col)+80, (50*row)+60, 50, 50);
                try{ 
                    board[row][col].setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream(getImage()))));
                } catch (Exception l){
                    l.printStackTrace();
                }
                this.add(board[row][col]);
                
                this.setComponentZOrder(board[row][col], 0);

            }
            BS = !BS;
        }
        
        // Create Player Name Labels
        JLabel names[] = new JLabel[2];
        for (int i = 0; i < 2; i ++){
            names[i] = new JLabel();
            names[i].setText(player[1-i]);
            names[i].setBounds(80, (515*i), 400, 50);
            names[i].setFont(mainFont);
            names[i].setHorizontalAlignment(JLabel.CENTER);

            names[i].setForeground(new Color(100, 100, 100));
            
            this.add(names[i]);
        }
        
        // Create Board Labels (Numbers and Letters)
        
        JLabel numbers[] = new JLabel[8];
        for (int i = 0; i < 8; i++){
            numbers[i] = new JLabel();
            numbers[i].setText((i+1)+"");
            numbers[i].setBounds(40, 420-(i*50), 30, 30);
            numbers[i].setFont(mainFont);
            numbers[i].setHorizontalAlignment(JLabel.CENTER);
            numbers[i].setForeground(new Color(100, 100, 100));
            
            this.add(numbers[i]);
        }
        
        JLabel letters[] = new JLabel[8];
        char letter[] = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        
        for (int i = 0; i < 8; i++){
            letters[i] = new JLabel();
            letters[i].setText(letter[i]+"");
            letters[i].setBounds((i*50)+90, 470, 30, 30);
            letters[i].setFont(mainFont);
            letters[i].setHorizontalAlignment(JLabel.CENTER);
            letters[i].setForeground(new Color(100, 100, 100));
            
            this.add(letters[i]);
        }
        
        // Other Main Window Elements

        // Turn Display
        
        turnWhite = new JTextField();
        turnWhite.setBounds(500, 523, 180, 40);
        turnWhite.setEditable(false);
        turnWhite.setBackground(Color.white);
        this.add(turnWhite);
        
        turnBlack = new JTextField();
        turnBlack.setBounds(700, 523, 180, 40);
        turnBlack.setEditable(false);
        turnBlack.setBackground(Color.black);
        this.add(turnBlack);
        
        switchColour();
        
        // Previous Turns List

        previousTurns.setBounds(500, 60, 380, 150);
        previousTurnsList.setFont(mainFont);
        previousTurnsList.setAlignmentX(CENTER_ALIGNMENT);
        previousTurnsList.setEditable(false);
        
        this.add(previousTurns);
        
        // Timers
        if (timerActive){
            for (int i = 0; i < 2; i++){
                timers[i] = new JLabel();
                timers[i].setBounds(500+(i*200), 483, 200, 40);
                timers[i].setFont(mainFont);
                timers[i].setHorizontalAlignment(JLabel.CENTER);
                timers[i].setForeground(new Color(100, 100, 100));
                timerText(i);
                this.add(timers[i]);
            }
        }
        // Create Pieces
        
        currentGame.getPieces(this);
        
        JButton returnButton = new JButton();
        returnButton.setBounds(760, 20, 100, 30);
        returnButton.setFont(mainFont);
        returnButton.setBorder(buttonBorder);
        returnButton.setText("Back");
        returnButton.setForeground(new Color(100, 100, 100));
        returnButton.setHorizontalAlignment(JLabel.CENTER);
        returnButton.addActionListener((ActionEvent e) -> {
            saveGame();
            parent.createGUI("main");
        });
        System.out.println("4");
        this.add(returnButton);
       
        rePack();
    }
    
    public void timerText(int type){
        
        // Sets the text for the timer each time it changes
        
        String[] ms = new String[2];
        ms[0] = timerValues[type].m+"";
        ms[1] = timerValues[type].s+"";
        for (int i = 0; i < 2; i++){
            if (Integer.parseInt(ms[i]) < 10){
                ms[i] = "0"+ms[i];
            }
        }
        
        timers[type].setText(ms[0]+" : "+ms[1]);

    }
    
    // Gets Image sources
    
    public String getImage(){
        String whiteSquare = "/resources/WhiteSquare.png";
        String blackSquare = "/resources/BlackSquare.png";
        String returnValue = "";
        
        if (BS){
            returnValue = blackSquare;
        } else if (!BS){
            returnValue = whiteSquare;
        }
        BS = !BS;
        return returnValue;
    }
    
    // Add To Previous Turns Array
    
    public void addToPreviousTurns(String move){
        String message = moveCount+" | "+move;
        previousTurnsList.append(message+"\n");
        turnsMade.add(message);
        moveCount++;
    }
    
    // Turn Switcher
    
    public void switchTurn(){
        if (currentTurn == 0){
            currentTurn = 1;
        } else if (currentTurn == 1){
            currentTurn = 0;
        }
        switchColour();
    }
    
    // Switches the highlight to display the current turn
    
    public void switchColour(){
        Border selectionBorder = BorderFactory.createLineBorder(Color.yellow, 3);
        if (currentTurn == 0){
            turnWhite.setBorder(selectionBorder);
            turnBlack.setBorder(null);
        } else if (currentTurn == 1){
            turnWhite.setBorder(null);
            turnBlack.setBorder(selectionBorder);
        }
    }
    
    public void rePack(){
        parent.rePack();
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
