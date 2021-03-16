package computerscienceia;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class LeaderOptionButton extends javax.swing.JButton{
    
    // Variables for leaderboard buttons
    
    LeaderBoard parent;
    int page;
    UserData userInfo;
    int height;
    int index;
    
    public LeaderOptionButton(LeaderBoard parent, int index, UserData userInfo){
        
        this.parent = parent;
        this.page = (index/5) + 1;
        this.userInfo = userInfo;
        this.height = index - ((this.page - 1)*5);
        this.index = index;
        
        createButton();
        
    }
    
    public void updateValues(int newIndex){
        
        // Updates the index and therefore the page and height values (and position of the buttons)
        
        this.index = newIndex;
        this.page = (this.index/5) + 1;
        this.height = index - ((this.page-1)*5);
        
        this.setBounds(50, 130+(height*70), 800, 50);
        
        parent.revalidate();
        parent.repaint();
    }
    
    public void setPage(int value){
        
        // Sets the active page to the specified value
        
        if (value == page){
            this.setVisible(true);
        } else {
            this.setVisible(false);
        }
    }
    
    public void createButton(){
        
        this.setLayout(null);
        
        double winRate = 100*(userInfo.getWins())/((userInfo.getWins()+userInfo.getLosses()));
        
        String[] elements = new String[]{userInfo.getUser()+"", userInfo.getWins()+"", "Wins", "/", userInfo.getLosses()+"", "Losses", userInfo.getWR()+"%", "W/R"};

        int[][] positions = new int[][]{{20, 150},{250, 40},{280, 60}, {340, 20},{350, 40},{370, 100}, {660, 60}, {720, 50}};
        
        JLabel[] elementLabels = new JLabel[8];
        for (int i = 0; i < 8; i++){
            elementLabels[i] = new JLabel();
            elementLabels[i].setBounds(positions[i][0], 0, positions[i][1], 50);
            elementLabels[i].setFont(parent.mainFont);
            elementLabels[i].setHorizontalAlignment(JLabel.CENTER);
            elementLabels[i].setVerticalAlignment(JLabel.CENTER);
            elementLabels[i].setForeground(new Color(100, 100, 100));
            elementLabels[i].setText(elements[i]);
            this.add(elementLabels[i]);
        }
        
        this.setBounds(50, 130+(height*70), 800, 50);
        this.setBorder(parent.buttonBorder);
        
        this.setVisible(false);
        
        this.addActionListener((ActionEvent e) -> {
            parent.moreInformation(this.userInfo.name);
        });
        
        parent.add(this);
    }
    
}
