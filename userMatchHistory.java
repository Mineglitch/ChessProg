package computerscienceia;

import java.util.LinkedList;

public class userMatchHistory {
    
    String userName;
    int page;
    
    LinkedList<String> pastGames = new LinkedList<>();
    
    // Store game data for players
    
    public userMatchHistory(String userName){
        
        this.userName = userName;
        
    }
    
    public void addPastGame(String info){
        
        String[] splitString = info.split(" ");
        
        String[] players = new String[]{splitString[0], splitString[1]};
        
        int characterLimit = 10;
        
        // Format the data for display and add it to the data array
        
        String specialUserName = userName;
        if (specialUserName.length()>characterLimit){
            specialUserName = specialUserName.substring(0, characterLimit);
        }
        
        String spaces = "";
        
        for (int i = 0; i < 2; i++){
            if (players[i].length() > characterLimit){
                players[i] = players[i].substring(0, characterLimit);
            } else {
                for (int sp = 0; sp < (characterLimit - players[i].length()); sp++){
                    spaces = spaces + "  ";
                }
            }
        }
        
        String winner = "";
        String date = splitString[4];
        
        if (splitString[2].equals("0")){
            winner = players[1];
        } else {
            winner = players[0];
        }
        
        String information = "";
        
        if (players[0].equalsIgnoreCase(specialUserName)){
            information = players[0]+" Vs "+players[1]+" "+spaces+" "+date+"                   "+winner+" Won!";
        } else if (players[1].equalsIgnoreCase(specialUserName)){
            information = players[1]+" Vs "+players[0]+" "+spaces+" "+date+"                   "+winner+" Won!";
        }
        
        pastGames.add(information);
        
    }
    
    public void sortPastGames(){
        
        // Sort the information stored in pastGames by date
        
        boolean notSorted = true;
        
        while (notSorted){
            int counter = 0;
            for (int i = 0; i < pastGames.size()-1; i++){
                
                if (getLineDate(pastGames.get(i)) < getLineDate(pastGames.get(i+1))){
                    
                    String temp = pastGames.get(i+1);
                    
                    pastGames.set(i+1, pastGames.get(i));
                    pastGames.set(i, temp);
                    
                } else {
                    counter++;
                }
                
            }
            if (counter >= pastGames.size()-1){
                notSorted = false;
            }
        }
        
    }
    
    public Long getLineDate(String pastGame){
        
        // Turn a formatted date into a long that can be sorted
        
        String[] s = pastGame.replaceAll("\\s{2,}", " ").split(" ");
        
        String[] date = s[3].split("/");
        
        return Long.parseLong(date[2]+date[1]+date[0]);
        
    }
    
}
