package computerscienceia;

public class UserData {
    int wins;
    int losses;
    String name;
    int winRate;
    
    public UserData(int wins, int losses, String name){
        
        // Used to store data for players.
        this.wins = wins;
        this.losses = losses;
        this.name = name;
        
    }
    
    public int getWins(){
        return this.wins;
    }
    
    public int getLosses(){
        return this.losses;
    }
    
    public String getUser(){
        return this.name;
    }
    
    public int getWR(){
        if (wins+losses>0){
            return (100*wins)/(wins+losses);
        } else {
            return 0;
        }
    }
    
    public void incrementWins(){
        this.wins++;
    }
    
    public void incrementLosses(){
        this.losses++;
    }
    
}
