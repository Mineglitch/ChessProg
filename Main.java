package computerscienceia;

import javax.swing.*;
import java.io.*;
import java.awt.*;

public class Main extends javax.swing.JFrame{

    // Variables for GUI Setup:
    
    boolean BS = false;
    
    // Objects for GUI creation
    
    Rectangle currentBounds;
    ChessGame gameParameters;
    
    userMatchHistory currentUser;
    
    File[] folders = new File[2];

    public Main() {
        
        
        this.setBounds(200, 200, 900, 600);
        
        // Create Saves and Leaderboard directories as well as the defaultArrangement file.
        
        String[] folderNames = new String[]{"Saves", "LeaderBoard"};
        
        for (int i = 0; i < 2; i++){
            
            folders[i] = new File(folderNames[i]);
            
            if (!folders[i].exists()){
                
                System.out.println("Creating directory: " + folders[i].getName());
                boolean result = false;
                
                try{ 
                    
                    folders[i].mkdir();
                    result = true;
                    
                } catch (SecurityException c){
                    System.out.println("An error has occured whilst making a directory");
                }
                if (result){
                    System.out.println("Successfully created directory");
                }
                
                if (folders[i].getName().equals("Saves")){
                    
                    // Create defaultArrangement file
                    
                    try{
                        
                        PrintWriter arr = new PrintWriter(new FileWriter(folders[i].getAbsolutePath()+"/defaultArrangement.txt"));
                        
                        String[] arrContent = new String[]{"2939495969493929\n", "1919191919191919\n", "0000000000000000\n", "0000000000000000\n", "0000000000000000\n", "0000000000000000\n", "1818181818181818\n", "2838485868483828\n"};
                        
                        for (int l = 0; l < 8; l++){
                            arr.write(arrContent[l]);
                        }
                        
                        arr.close();
                        
                    } catch (Exception e){
                        System.out.println("Error with creating defaultArrangement");
                    }
                    
                }
                if (folders[i].getName().equals("LeaderBoard")){
                    
                    // Create leaderboard file
                    
                    try{
                        
                        PrintWriter lead = new PrintWriter(new FileWriter(folders[i].getAbsolutePath()+"/LeaderBoard.txt"));
                        
                        lead.close();
                        
                    } catch (Exception e){
                        System.out.println("Error with creating leaderBoard");
                    }
                    
                }
                
            }
            
        }
        
        // Create the Main Menu GUI
        
        createGUI("main");
   
    }
    
    // GUI Creation
    
    JPanel displayWindow;
    
    public void createGUI(String type){
        
        this.setDefaultCloseOperation(3); // Close the JVM when window is closed.
        
        // Get current Window constraints
        
        currentBounds = this.getBounds();
        
        // Reset Content Pane
        
        getContentPane().setLayout(null);
        getContentPane().removeAll();
        
        // Create New Window
        
        if (type.equals("board")){
            
            // Create Chess Board
            
            if (gameParameters != null){
                displayWindow = new ChessPanel(gameParameters, this);
            }
            
        } else if (type.equals("main")){
            
            // Create Main Menu
            
            displayWindow = new MainMenu(this);
            
        } else if (type.equals("Start")){
            
            // Create New Game Menu
            
            displayWindow = new NewGame(this);
            
        } else if (type.equals("load")){
            
            // Create Load Game Menu
            
            displayWindow = new LoadGame(this);
            
        } else if (type.equals("leader")){
            
            // Create Leaderboard Menu
        
            displayWindow = new LeaderBoard(this);
            
        } else if (type.equals("moreInfo")){
            
            // Create More Info Menu
            
            if (currentUser != null){
                displayWindow = new MoreInformation(this, currentUser);
            }
        
        }else {
            
            System.out.println("Failed To Create Window: Invalid Call");
        }
        
        // Initialise Window

        displayWindow.setBounds(0, 0, 900, 600);
        getContentPane().add(displayWindow);
        
        rePack();
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void rePack(){
        pack();
        this.setBounds(currentBounds);
    }
    
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
