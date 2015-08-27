
/**
 * Processes class contains all the attributes to be stored in processes and the methods that
 * help set and access all of these attributes.
 * 
 * @author Robert King 
 * @version Assignment #2
 */
public class Process
{
    // instance variables 
    private int size;           //The two different sizes are for the RR techniques to keep track
    private int original_size;  //of the original size and the size of the process thats left
    private int wait;
    private int turnaround;
    private int entranceTime;

    /**
     * Constructor to intialize a process starting every value at 0 except size can be set
     */
    public Process(double n)
    {
        size = (int)n;
        original_size = (int)n;
        wait = 0;
        turnaround = 0;
        entranceTime = 0;
    }

    /**
     * Method to return the size of the process
     * @param void
     * @return size
     */
    public int getSize(){
        return size;
    }
    
    /**
     * Method to get the original size the process starts out at
     * @param void
     * @return original_size
     */
    public int getOSize(){
        return original_size;
    }
    
    /**
     * Method to return the wait time for the process
     * @param void
     * @return wait
     */
    public int getWait(){
        return (wait - entranceTime);
    }
    
    /**
     * Method to return the turnaround time for a process
     * @param void
     * @return turnaround = original time + wait
     */
    public int getTurnaround(){
        return (wait - entranceTime) + original_size;
    }
    
    public int getEntranceTime(){
        return entranceTime;
    }
    
    /**
     * Method to set the size ofthe process
     * @param int n represents the new size of the process
     * @return void
     */
    public void setSize(int n){
        size = n;
    }
    
    /**
     * Method to set the wait tiem for the process
     * @param int n represents the total wait for the process
     * @return void
     */
    public void setWait(int n){
        wait = n;
    }
    
    /**
     * Method to set the turnaround time for the process
     * @param int n represents the turnaround time for the process
     * @return void
     */
    public void setTurnaround(int n){
        turnaround = n;
    }
    
    /**
     * Method to set the time when the process enters the ready queue
     * @param int n
     * @return void
     */
    public void setEntranceTime(int n){
        entranceTime = n;
    }
    
    /**
     * Method to format a string printing details about the process
     * @param void
     * @return String 
     */
    public String toString(){
        return getSize() + "\t    " + getWait() + "\t\t   " + wait + "\t\t   " +getTurnaround() + "\n";
    }
    
    public String toString2(){
        return getSize()+"\t"+getWait();
    }
    
    /**
     * Method to format a string printing details about the process
     * @param void
     * @return String 
     */
    public String toStringRR(){
        return getOSize() + "\t    " + getWait() + "\t\t   " + wait + "\t\t   " +getTurnaround() + "\n";
    }
    
    public String toStringRR2(){
        return getOSize()+"\t"+getWait();
    }
    
    public void reset(){
        size = original_size;
        wait = 0;
        turnaround = 0;
        entranceTime = 0;
    }
}
