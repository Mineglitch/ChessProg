package computerscienceia;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class MoreInformation extends javax.swing.JPanel{
    
    userMatchHistory history;
    Main parent;
    
    // Formatting
    Font mainFont = new Font("Comic Sans", 0, 20);
    Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
    
    // GUI Elements
    JTextArea previousGamesList = new JTextArea();
    JScrollPane previousGames = new JScrollPane(previousGamesList);
    
    public MoreInformation(Main parent, userMatchHistory History){
        
        this.history = History;
        this.parent = parent;
        
        this.setLayout(null);
        
        // Create GUI
        JLabel screenLabel = new JLabel();
        screenLabel.setFont(new Font("Comic Sans", 1, 40));
        screenLabel.setBounds(0, 0, 900, 120);
        screenLabel.setHorizontalAlignment(JLabel.CENTER);
        screenLabel.setText(history.userName);
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
            parent.createGUI("leader");
        });
        
        this.add(returnButton);
        
        previousGames.setBounds(50, 150, 800, 350);
        previousGamesList.setFont(mainFont);
        previousGamesList.setForeground(new Color(100, 100, 100));
        previousGamesList.setAlignmentX(CENTER_ALIGNMENT);
        previousGamesList.setEditable(false);
        
        this.add(previousGames);
        
        addPastGames();
        
    }
    
    public void addPastGames(){
        
        history.sortPastGames(); // Sort games
        
        // Add to the previousGamesList from 
        for (int i = 0; i < history.pastGames.size(); i++){
            previousGamesList.append(history.pastGames.get(i)+"\n");
        }
        
    }
    
}
