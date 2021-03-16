 package computerscienceia;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class MainMenu extends javax.swing.JPanel {

    Main parent;
    
    // GUI Elements
    
    JButton[] menuButtons = new JButton[3];
    
    public MainMenu(Main parent) {
        this.parent = parent;
        createMainMenu();
    }
    
    public void createMainMenu(){
        
        this.setLayout(null);
        
        // Formatting
        java.awt.Font mainFont = new java.awt.Font("Comic Sans", 0, 20);
        Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
        
        // Create GUI
        JLabel title = new JLabel();
        title.setBounds(0, 0, 900, 200);
        title.setFont(new java.awt.Font("Comic Sans", 1, 100));
        title.setForeground(new Color(100, 100, 100));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setText("CHESS");
        
        this.add(title);
        
        String[] buttonTypes = new String[]{"New Game", "Load Game", "Leaderboard"};
        
        for (int i = 0; i < 3; i++){
            final int typeValue = i;
            menuButtons[i] = new JButton();
            menuButtons[i].setBounds(300, 200 + (100*i), 300, 80);
            menuButtons[i].setFont(mainFont);
            menuButtons[i].setBorder(buttonBorder);
            menuButtons[i].setForeground(new Color(100, 100, 100));
            menuButtons[i].setText(buttonTypes[i]);
            
            menuButtons[i].addActionListener((ActionEvent e) -> {
                if (typeValue == 0){
                    
                    parent.createGUI("Start");
                    
                } else if (typeValue == 1){
                    
                    parent.createGUI("load");
                    
                } else if (typeValue == 2){
                    
                    parent.createGUI("leader");
                }
            });
            
            this.add(menuButtons[i]);
        }
    }

    @SuppressWarnings("unchecked")
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
