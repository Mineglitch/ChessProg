package computerscienceia;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

public class LeaderBoard extends javax.swing.JPanel{
    
    Main parent;
    
    // Variables and GUI elements for the display of pages
    
    int currentPage = 1;
    int maximumPages = 1;
    JLabel pageIndicator;
    JLabel noContentMessage;
    
    // Formatting
    
    Font mainFont = new Font("Comic Sans", 0, 20);
    Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
    
    LinkedList<String> prevGameContent = new LinkedList<>(); // Store information on all previous games
    LinkedList<UserData> userInfo = new LinkedList<>(); // Store information on specific users
    
    LinkedList<LeaderOptionButton> optionButtons = new LinkedList<>(); // Buttons to display information on users and provide additonal info
    
    JButton[] pageChangeButtons = new JButton[2]; // Buttons to change the current page
    
    public LeaderBoard(Main parent){
        
        this.parent = parent;
        createLeader();
    }
    
    public void createLeader(){
        
        // Creates GUI
        
        this.setLayout(null);
        
        JLabel screenLabel = new JLabel();
        screenLabel.setFont(new Font("Comic Sans", 1, 40));
        screenLabel.setBounds(0, 0, 900, 120);
        screenLabel.setHorizontalAlignment(JLabel.CENTER);
        screenLabel.setText("LeaderBoard");
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
        
        unravelFile(); // Gets information from the text file and adds information about each user to the linked list
        
        sort(); // Sorts the linkedlist by date 
        
        // Creates display buttons
        for (int i = 0; i < userInfo.size(); i++){
            optionButtons.add(new LeaderOptionButton(this, i, userInfo.get(i)));
        }
        
        // Code for allowing the cycling between multiple pages
        
        maximumPages = ((optionButtons.size()-1)/5)+1;
        
        String[] changeButtonsText = new String[]{"Previous", "Next"};
        for (int i = 0; i < 2; i++){
            pageChangeButtons[i] = new JButton();
            pageChangeButtons[i].setBorder(buttonBorder);
            pageChangeButtons[i].setFont(mainFont);
            pageChangeButtons[i].setHorizontalAlignment(JLabel.CENTER);
            pageChangeButtons[i].setVerticalAlignment(JLabel.CENTER);
            pageChangeButtons[i].setText(changeButtonsText[i]);
            pageChangeButtons[i].setBounds(50+(i*700), 500, 100, 50);
            pageChangeButtons[i].setForeground(new Color(100, 100, 100));
            pageChangeButtons[i].setVisible(false);
            
            final int type = i;
            final int maximum = maximumPages;
            
            pageChangeButtons[i].addActionListener((ActionEvent k) -> {
                if (type == 0 && currentPage > 1){
                    currentPage = currentPage-1;
                } else if (type == 1 && currentPage < maximum){
                    currentPage = currentPage+1;
                }
                updatePage();
            });
            
            this.add(pageChangeButtons[i]);
        }
        
        pageIndicator = new JLabel();
        pageIndicator.setFont(mainFont);
        pageIndicator.setForeground(new Color(100, 100, 100));
        pageIndicator.setHorizontalAlignment(JLabel.CENTER);
        pageIndicator.setVerticalAlignment(JLabel.CENTER);
        pageIndicator.setBounds(400, 530, 100, 50);
        
        this.add(pageIndicator);
        
        noContentMessage = new JLabel();
        noContentMessage.setFont(new Font("Comic Sans", 0, 30));
        noContentMessage.setForeground(new Color(160, 160, 160));
        noContentMessage.setHorizontalAlignment(JLabel.CENTER);
        noContentMessage.setVerticalAlignment(JLabel.CENTER);
        noContentMessage.setBounds(100, 200, 700, 50);
        noContentMessage.setText("No user Information available.");
        noContentMessage.setVisible(false);
        
        this.add(noContentMessage);
        
        updatePage();
        
    }
    
    public void updatePage(){
        
        // Sets the current working page of all objects associated with this object
        // Sets visibillity of page change buttons when on first or last page
        // Sets page indicator
        
        maximumPages = ((optionButtons.size()-1)/5)+1;
        if (optionButtons.size() == 0){
            noContentMessage.setVisible(true);
        } else {
            noContentMessage.setVisible(false);
        }
        
        if (currentPage > maximumPages){
            currentPage = maximumPages;
        }
        
        if (currentPage != 1){
            pageChangeButtons[0].setVisible(true);
        } else {
            pageChangeButtons[0].setVisible(false);
        }
        
        if (currentPage != maximumPages){
            pageChangeButtons[1].setVisible(true);
        } else {
            pageChangeButtons[1].setVisible(false);
        }
        
        pageIndicator.setText(currentPage+" / "+maximumPages);
        
        for (int i = 0; i < optionButtons.size(); i++){
            optionButtons.get(i).setPage(currentPage);
        }
    }
    
    public void unravelFile(){
        
        // Get content from leaderboard file
        
        try{
            BufferedReader prevGameFile = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/LeaderBoard/LeaderBoard.txt"));
            
            while (prevGameFile.ready()){
                prevGameContent.add(prevGameFile.readLine());
            }
            
        } catch (Exception t){
            System.out.println("Error when finding LeaderBoard file");
        }
        
        getUsers();
    }
    
    public void getUsers(){
        
        // Go through information from leaderboard file and 
        // set the specific information for each user that comes up
        
        for (int i = 0; i < prevGameContent.size(); i++){
            
            String[] lineBreakdown = prevGameContent.get(i).split(" ");
            boolean[] notComeUp = new boolean[]{true, true};
            
            for (int user = 0; user < userInfo.size(); user++){
                for (int l = 0; l < 2; l++){
                    if (userInfo.get(user).getUser().equalsIgnoreCase(lineBreakdown[l])){
                        notComeUp[l] = false;
                        
                        // Get the total wins and losses of a user
                        
                        if (lineBreakdown[l+2].equals("1")){
                            userInfo.get(user).incrementWins();
                        } else {
                            userInfo.get(user).incrementLosses();
                        }
                    }
                }
            }
            
            for (int add = 0; add < 2; add++){
                if (notComeUp[add] == true){
                    
                    // If they don't exist, add them
                    
                    userInfo.add(new UserData(0, 0, lineBreakdown[add]));
                    
                    if (lineBreakdown[add+2].equals("1")){
                        userInfo.getLast().incrementWins();
                    } else {
                        userInfo.getLast().incrementLosses();
                    }
                }
                
            }          
        }
    }
    
    public void sort(){
        
        // Sort userInfo array by date
        
        boolean notSorted = true;
        
        while (notSorted){
            int counter = 0;
            for (int i = 0; i < userInfo.size()-1; i++){
                
                if (userInfo.get(i).getWR() < userInfo.get(i+1).getWR()){
                    UserData temp = userInfo.get(i+1);
                    
                    userInfo.set(i+1, userInfo.get(i));
                    userInfo.set(i, temp);
                    
                } else {
                    counter++;
                }
                
            }
            if (counter >= userInfo.size()-1){
                notSorted = false;
            }
        }
    }
    
    public void moreInformation(String userName){
        
        // Gets and adds all games associated to a name to an object
        
        userMatchHistory playerHistory = new userMatchHistory(userName);
        
        for (int i = 0; i < prevGameContent.size(); i++){
            
            if (prevGameContent.get(i).toLowerCase().contains(userName.toLowerCase())){
                
                playerHistory.addPastGame(prevGameContent.get(i));
                
            }
            
        }
        
        // Load moreInfo with all the information
        
        parent.currentUser = playerHistory;
        parent.createGUI("moreInfo");
        
    }
    
}
