package computerscienceia;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class LoadOptionButton extends javax.swing.JButton{
    
    LoadGame parent;
    int page;
    String fileName;
    int height;
    int index;
    
    public LoadOptionButton(LoadGame parent, int index, String fileName){
        this.parent = parent;
        this.page = (index/5) + 1;
        this.fileName = fileName;
        this.height = index - ((this.page - 1)*5);
        this.index = index;
        
        try {
            createButton();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void updateValues(int newIndex){
        
        // Update the pages and index values
        
        this.index = newIndex;
        this.page = (this.index/5) + 1;
        this.height = index - ((this.page-1)*5);
        
        this.setBounds(50, 130+(height*70), 800, 50);
        
        parent.revalidate();
        parent.repaint();
    }
    
    public void setPage(int value){
        
        // Set the page 
        
        if (value == page){
            this.setVisible(true);
        } else {
            this.setVisible(false);
        }
    }
    
    public void createButton() throws IOException{
        
        this.setLayout(null);
        
        int nameLength = 10;
        String[] nameBreakdown = fileName.split("_");
        
        // Display the information about the game
        
        String[] elements = new String[]{nameBreakdown[0], "Vs", nameBreakdown[1], "|", nameBreakdown[2]+"/"+nameBreakdown[3]+"/"+nameBreakdown[4], nameBreakdown[5]+":"+nameBreakdown[6]};

        int[][] positions = new int[][]{{20, 150},{170, 30},{200, 150}, {350, 20},{370, 150},{520, 100}};
        
        JLabel[] elementLabels = new JLabel[6];
        for (int i = 0; i < 6; i++){
            elementLabels[i] = new JLabel();
            elementLabels[i].setBounds(positions[i][0], 0, positions[i][1], 50);
            elementLabels[i].setFont(parent.mainFont);
            elementLabels[i].setHorizontalAlignment(JLabel.CENTER);
            elementLabels[i].setVerticalAlignment(JLabel.CENTER);
            elementLabels[i].setForeground(new Color(100, 100, 100));
            elementLabels[i].setText(elements[i]);
            this.add(elementLabels[i]);
        }
        
        JButton removeButton = new JButton();
        removeButton.setBounds(750, 0, 40, 50);
        removeButton.setIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/Trash.png"))));
        removeButton.setRolloverIcon(new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/resources/OpenTrash.png"))));
        removeButton.setContentAreaFilled(false);
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.addActionListener((ActionEvent e) -> {
            int confirmationDialog = JOptionPane.showConfirmDialog(parent, "Are you sure you wish to permanently remove this save file?", "Warning", 0, JOptionPane.WARNING_MESSAGE);
            if (confirmationDialog == JOptionPane.YES_OPTION){
                parent.removeFile(this.index);
            }
        });
        
        this.add(removeButton);
        
        this.setBounds(50, 130+(height*70), 800, 50);
        this.setBorder(parent.buttonBorder);
        
        this.setVisible(false);
        
        this.addActionListener((ActionEvent e) -> {
            parent.loadFile(fileName);
        });
        
        parent.add(this);
    }
    
}
