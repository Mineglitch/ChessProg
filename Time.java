package computerscienceia;

public class Time {
    int m;
    int s;
    int seconds;
    
    // Convert a time in seconds to minutes and seconds
    public Time(int seconds){
        
        this.seconds = seconds;
        convertTime();
    }
    
    public void convertTime(){
        this.m = seconds / 60;
        this.s = seconds - (60*this.m);
    }
}
