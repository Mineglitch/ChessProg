package computerscienceia;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.Border;

public class LoadGame extends javax.swing.JPanel{
    
    Main parent;
    
    // Formatting
    Font mainFont = new Font("Comic Sans", 0, 20);
    Border buttonBorder = BorderFactory.createLineBorder(Color.gray, 3);
    
    // Page variables and Objects
    JButton[] pageChangeButtons = new JButton[2];
    
    int currentPage = 1;
    int maximumPages = 1;
    JLabel pageIndicator;
    JLabel noContentMessage;
    
    LinkedList<String> fileNames = new LinkedList<>(); // Contains a list of all the fileNames in the Saves folder
    
    LinkedList<LoadOptionButton> loadButtons = new LinkedList<>(); // Contains all the buttons for loading buttons
    
    public LoadGame (Main Parent){
        this.parent = Parent;
        createLoadGame();
    }
    
    public void createLoadGame(){
        
        this.setLayout(null);
        
        JLabel screenLabel = new JLabel();
        screenLabel.setFont(new Font("Comic Sans", 1, 40));
        screenLabel.setBounds(0, 0, 900, 120);
        screenLabel.setHorizontalAlignment(JLabel.CENTER);
        screenLabel.setText("Load Game");
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
        
        // Get all of the files out of the Saves folder
        File saves = new File(System.getProperty("user.dir")+"/Saves");
        File[] saveFiles = saves.listFiles();
        
        // Add specific file names to the list (Ignoring the default arrangement)
        for (int i = 0; i < saveFiles.length; i++){
            if (saveFiles[i].isFile() && saveFiles[i].getName().substring(saveFiles[i].getName().length()-4, saveFiles[i].getName().length()).equals(".txt")){
                String file = saveFiles[i].getName();
                if (!file.substring(0, file.length()-4).equals("defaultArrangement")){
                    fileNames.add(file.substring(0, file.length()-4));
                }
            }
        }
        
        sort(); // Sort the file Names by date played
        
        // Create LOAD buttons based on file names
        for (int files = 0; files < fileNames.size(); files++){
            loadButtons.add(new LoadOptionButton(this, files, fileNames.get(files)));
        }
       
        // Page Changing
        
        maximumPages = ((loadButtons.size()-1)/5)+1;
        
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
        noContentMessage.setText("There are currently no saved games available.");
        noContentMessage.setVisible(false);
        
        this.add(noContentMessage);
        
        updatePage();
        
    }
    
    public void updatePage (){
        
        // Update the pages 
        
        maximumPages = ((loadButtons.size()-1)/5)+1;
        if (loadButtons.size() == 0){
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
        
        for (int i = 0; i < loadButtons.size(); i++){
            loadButtons.get(i).setPage(currentPage);
        }
    }
    
    public void removeFile(int index){
        
        // Delete a Save file
        
        try{ 
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir")+"/Saves/"+loadButtons.get(index).fileName+".txt"));
        } catch (Exception p){
            System.out.println("Error while deleting.");
        }
        
        loadButtons.get(index).setVisible(false);
        this.remove(loadButtons.get(index));
        
        loadButtons.remove(index);
            
        fileNames.remove(index);
        
        for (int i = 0; i < loadButtons.size(); i++){
            loadButtons.get(i).updateValues(i);
        }
        
        updatePage();
    }
    
    public void loadFile(String fileName){
        
        // Load the Game
        
        parent.gameParameters = new ChessGame(fileName);
        parent.createGUI("board");
        
    }
    
    public void sort(){
        
        boolean notSorted = true;
        
        while (notSorted){
            int counter = 0;
            for (int i = 0; i < fileNames.size()-1; i++){
                
                String[] content1 = fileNames.get(i).split("_");
                Long specialNumber1 = Long.parseLong(content1[4]+content1[3]+content1[2]+content1[5]+content1[6]);
                
                String[] content2 = fileNames.get(i+1).split("_");
                Long specialNumber2 = Long.parseLong(content2[4]+content2[3]+content2[2]+content2[5]+content2[6]);
                
                if (specialNumber1 < specialNumber2){
                    
                    String temp = fileNames.get(i+1);
                    
                    fileNames.set(i+1, fileNames.get(i));
                    fileNames.set(i, temp);
                    
                } else {
                    counter++;
                }
                
            }
            if (counter >= fileNames.size()-1){
                notSorted = false;
            }
            
        }
        
    }
    
    
}
