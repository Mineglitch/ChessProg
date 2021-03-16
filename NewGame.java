package computerscienceia;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class NewGame extends javax.swing.JPanel{
    Main parent;
    
    Font mainFont = new Font("Comic Sans", 0, 20);
    Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
    JTextField timeInput;
    
    boolean checked = false;
    
    public NewGame(Main Parent){
        this.parent = Parent;
        createNewGame();
    }
    
    public void createNewGame(){
        
        // GUI Setup
        
        this.setLayout(null);
        
        JLabel screenLabel = new JLabel();
        screenLabel.setFont(new Font("Comic Sans", 1, 40));
        screenLabel.setBounds(0, 0, 900, 120);
        screenLabel.setHorizontalAlignment(JLabel.CENTER);
        screenLabel.setText("Game Setup");
        screenLabel.setForeground(new Color(100, 100, 100));
        
        this.add(screenLabel);
        
        JButton returnButton = new JButton();
        returnButton.setBounds(760, 20, 100, 30);
        returnButton.setFont(mainFont);
        returnButton.setBorder(buttonBorder);
        returnButton.setText("Back");
        returnButton.setForeground(new Color(100, 100, 100));
        returnButton.setHorizontalAlignment(JLabel.CENTER);
        returnButton.addActionListener((ActionEvent e) -> {
            parent.createGUI("main");
        });
        
        this.add(returnButton);
        
        // UserInput fields
        
        JTextField[] playerInput = new JTextField[2];
        JLabel[] playerLabels = new JLabel[2];
        
        String[] labelTexts = new String[]{"White Player", "Black Player"};
        
        for (int i = 0; i < 2; i++){
            playerInput[i] = new JTextField();
            playerLabels[i] = new JLabel();
            
            playerInput[i].setBorder(buttonBorder);
            playerLabels[i].setText(labelTexts[i]);
            
            playerInput[i].setForeground(new Color(100, 100, 100));
            
            if (i == 0){
                playerLabels[i].setForeground(new Color(255, 255, 255));
            } else if (i == 1){
                playerLabels[i].setForeground(new Color(0, 0, 0));
            }
            
            playerInput[i].setBounds(400, 140 + (100*i), 300, 50);
            playerLabels[i].setBounds(200, 140 + (100*i), 200, 50);
            
            playerInput[i].setFont(mainFont);
            playerLabels[i].setFont(new Font("Comic Sans", 1, 20));
            
            playerInput[i].setHorizontalAlignment(JLabel.CENTER);
            playerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            
            this.add(playerInput[i]);
            this.add(playerLabels[i]);
        }
        
        timeInput = new JTextField();
        timeInput.setForeground(new Color(100, 100, 100));
        timeInput.setFont(mainFont);
        timeInput.setBorder(buttonBorder);
        timeInput.setHorizontalAlignment(JLabel.CENTER);
        timeInput.setBounds(400, 340, 150, 50);
        timeInput.setEnabled(false);
        this.add(timeInput);
        
        JLabel timerE = new JLabel();
        timerE.setText("Timer");
        timerE.setFont(new Font("Comic Sans", 1, 20));
        timerE.setForeground(new Color(100, 100, 100));
        timerE.setBounds(200, 340, 200, 50);
        timerE.setHorizontalAlignment(JLabel.CENTER);
        this.add(timerE);
        
        JLabel finish = new JLabel();
        finish.setText("minutes");
        finish.setFont(mainFont);
        finish.setForeground(new Color(100, 100, 100));
        finish.setHorizontalAlignment(JLabel.CENTER);
        finish.setBounds(550, 340, 100, 50);
        this.add(finish);
        
        JButton timeCheckButton = new JButton();
        timeCheckButton.setBounds(350, 352, 28, 25);
        timeCheckButton.setContentAreaFilled(false);
        timeCheckButton.setFocusPainted(false);
        timeCheckButton.setBorderPainted(false);
        try {
            timeCheckButton.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/UnChecked.png"))));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        timeCheckButton.addActionListener((ActionEvent e) -> {
            if (checked == false){
                checked = true;
                try {
                    timeCheckButton.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/Checked.png"))));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if (checked == true){
                checked = false;
                try {
                    timeCheckButton.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/UnChecked.png"))));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                timeInput.setText("");
            }
            timeInput.setEnabled(checked);
            parent.repaint();

        });
        this.add(timeCheckButton);
        
        JButton confirmation = new JButton();
        confirmation.setBounds(300, 450, 300, 80);
        confirmation.setForeground(new Color(100, 100, 100));
        confirmation.setFont(mainFont);
        confirmation.setHorizontalAlignment(JLabel.CENTER);
        confirmation.setBorder(buttonBorder);
        confirmation.addActionListener((ActionEvent e) -> {
            final String playerW = playerInput[0].getText();
            final String playerB = playerInput[1].getText();
            
            // Set game parameters and start a new game
            
            if ((!playerW.equals("") && !playerB.equals("")) && !playerW.equals(playerB)){
                try{
                    int rTime = 0;
                    if (checked == false){
                        rTime = -1;
                    } else {
                        rTime = (int)(Double.parseDouble(timeInput.getText())*60);
                    }
                    if (rTime >= 120 || !checked){
                        parent.gameParameters = new ChessGame(playerW, playerB, rTime);
                        parent.createGUI("board");
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(parent, "Time input too small");
                    }
                    
                } catch (Exception m){
                    
                    javax.swing.JOptionPane.showMessageDialog(parent, "Invalid timer Input");
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(parent, "Player names may be empty or the same");
            }
        
        });
        confirmation.setText("Begin Game");
        this.add(confirmation);
        
    }
}
