
/**
 * This is a class containing methods to simulate the process scheduling process.
 * 
 * @author Robert King 
 * @version Assignment#2
 */

import java.util.*;
import java.lang.Math;
import java.io.*;
public class Scheduler
{
    private Queue<Object> queue;
    private Queue<Object> ready;
    private int readyLength;        //length of queue as a function of time
    private boolean steady;
    private Object[] nums;
    private Object[] sjfNums;       //array used specifically for shortest job first algorithm
    private int numLoc;
    private Object[] numArray;
    private int numProcesses;       

    /**
     * Constructor for objects of class Scheduler
     */
    public Scheduler(int n)
    {
        //initializing values of instance variables 
        numProcesses = n;
        queue = new LinkedList<Object>();
        ready = new LinkedList<Object>();
        readyLength = 0;
        steady = false;
        nums = new Object[numProcesses];
        numLoc = 0;
        numArray = new Object[numProcesses];
        sjfNums = new Object[numProcesses];
        
        //fills nums and sjf arrays with Process objects
        createProc();
        
        Random generator = new Random();
        int temp = generator.nextInt(3) + 2;
        for(int i = 0; i < temp; i++){
            shuffle();
        }
    }
    
    /**
     * Method to store processes in the nums array.
     * @param void
     * @return void
     */
    public void createProc(){
        double shortProc = Math.floor(numProcesses * .65);
        double longProc = numProcesses - shortProc;
        double incS = 19/shortProc;
        double incL = 45/longProc;
        double value = 1.0;
        for(int i = 0; i < shortProc; i++){
            nums[i] = new Process(Math.floor(value));
            sjfNums[i] = new Process(Math.floor(value));
            value += incS;
        }
        value += incL;
        for(int i = (int)(shortProc); i < numProcesses; i++){
            nums[i] = new Process(Math.floor(value));
            sjfNums[i] = new Process(Math.floor(value));
            value += incL;
        }
    }
    
    /**
     * Method to shuffle the nums array as you would a deck of cards, by cutting the deck in half 
     * and then shuffle them in alternating order
     * @param void
     * @return void
     */
    public void shuffle(){
        int x = 0;
        int y = (int)(numProcesses/2);
        Object[] temp = new Object[nums.length];
        int loc = 0;
        while(loc <= nums.length-1){
            temp[loc] = nums[y];
            y++;
            loc++;
            if(loc <= nums.length-1){
                temp[loc] = nums[x];
                x++;
                loc++;
            }
        }
        nums = temp;
    }
    
    /**
     * Method to create the ready queue with a "steady state" of 12 processes from nums array
     * for use with FCFS, RR, & MRR
     * @param void
     * @return void
     */
    public void createReadyQueue(){
        for(int i = 0; i < 12; i++){
            Process proc = (Process)nums[i];
            readyLength += proc.getSize();
            ready.add(proc); 
        }
        numLoc = 12;
    }
    
    /**
     * Method to create the ready queue with a "steady state" of 12 processes from sjfNums array
     * for use with SJF only
     * @param void
     * @return void
     */
    public void createReadyQueueSJF(){
        for(int i = 0; i < 12; i++){
            Process proc =  (Process)sjfNums[i];
            readyLength += proc.getSize();
            ready.add(proc); 
        }
        numLoc = 12;
    }
    
    /**
     * Method to add n Processes to the ready queue, if n is too large then the rest of the 
     * available processes are added
     * @param int n
     * @param int y
     * @return void
     */
    public void updateReadyQueue(int n, int y){
        int x = numLoc + n;
        if(x >= numProcesses){
            x = numProcesses-numLoc;
            x += numLoc;
        }
        for(int i = numLoc; i < x; i++){
            Process temp = (Process)nums[i];
            temp.setEntranceTime(y);
            readyLength += temp.getSize();
            ready.add(temp);
            numLoc++;
        }
    }
    
    /**
     * Method to add n Processes to the ready queue, if n is too large then the rest of the 
     * available processes are added. For use with SJF only.
     * @param int n
     * @param int y
     * @return void
     */
    public void updateReadyQueueSJF(int n, int y){
        int x = numLoc + n;
        if(x >= numProcesses){
            x = numProcesses-numLoc;
            x += numLoc;
        }
        for(int i = numLoc; i < x; i++){
            Process temp = (Process)sjfNums[i];
            temp.setEntranceTime(y);
            readyLength += temp.getSize();
            ready.add(temp);
            numLoc++;
        }
    }
    
    /**
     * Method to determine if the ready queue should update or not
     * @param void
     * @return boolean up
     */
    public boolean update(){
        boolean up = false;
        Random generator = new Random();
        int temp = (generator.nextInt(10))%2;
        if(temp == 0){
            up = true;   
        }
        return up;
    }
    
    public void resetVals(){
        for(int i = 0; i < nums.length; i++){
            Process temp = (Process)nums[i];
            temp.reset();
        }
        readyLength = 0;
    }
    
    /**
     * Method to simulate the First Come First Served Process scheduling algorithm
     * @param void
     * @return void 
     */
    public void fcfs(){
        resetVals();
        createReadyQueue();
        int totalTime = 0;
        int totalWait = 0;
        int qwait = 0;
        int wait = 0;
        int size = 0;
        String fileStr = "Size\tWait in Queue\tTotal Wait\tTurnAround";
        try{
            //Setting up the FileWriter and BufferedWriter
            //saving output to file named "fcfs.txt"
            FileWriter fstream = new FileWriter("fcfs.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            
            FileWriter fstream2 = new FileWriter("fcfs-time_vs_readyLength.txt");
            BufferedWriter out2 = new BufferedWriter(fstream2);
            
            FileWriter fstream3 = new FileWriter("fcfs-size_vs_wait.txt");
            BufferedWriter out3 = new BufferedWriter(fstream3);
            out.write(fileStr);
            out.newLine();
            //While loop is where the scheduling algorithm does all its work
            while(ready.peek() != null){
                out2.write(totalTime+"\t"+readyLength);
                out2.newLine();
                
                Process proc = (Process)ready.remove();
                size = proc.getSize();
                proc.setWait(totalTime);
                qwait += proc.getWait();
                totalWait += totalTime;
                totalTime += size;
                readyLength -= size;
            
                out.write(proc.toString());
                out.newLine();
                
                out3.write(proc.toString2());
                out3.newLine();
            
                Random generator = new Random();
                int x = generator.nextInt(12)+4;
                if((update() && ready.size()<5) || (ready.peek() == null && numLoc < numProcesses)){
                    updateReadyQueue(x,totalTime);
                }
            }
            
            out.write("Number of Processes: " + numProcesses);
            out.newLine();
            out.write("Total time: " + (((double)totalTime/(double)1000) + " seconds"));
            out.newLine();
            out.write("Average Wait in Ready Queue: " + (((double)qwait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.write("Average Total Wait: " + (((double)totalWait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.close();
            out2.close();
            out3.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        numLoc = 0;
    }
    
    /**
     * Method to simulate the Shortest Job First Process scheduling algorithm
     * @param void
     * @return void 
     */
    public void sjf(){
        createReadyQueueSJF();
        int totalTime = 0;
        int totalWait = 0;
        int qwait = 0;
        int wait = 0;
        int size = 0;
        String fileStr = "Size\tWait in Queue\tTotal Wait\tTurnAround";
        try{
            //Setting up the FileWriter and BufferedWriter
            //saving output to file named "sjf.txt"
            FileWriter fstream = new FileWriter("sjf.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            
            FileWriter fstream2 = new FileWriter("sjf-time_vs_readyLength.txt");
            BufferedWriter out2 = new BufferedWriter(fstream2);
            
            FileWriter fstream3 = new FileWriter("sjf-size_vs_wait.txt");
            BufferedWriter out3 = new BufferedWriter(fstream3);
            
            out.write(fileStr);
            out.newLine();
            //While loop is where the scheduling algorithm does all its work
            while(ready.peek() != null){
                out2.write(totalTime+"\t"+readyLength);
                out2.newLine();
                
                Process proc = (Process)ready.remove();
                size = proc.getSize();
                proc.setWait(totalTime);
                qwait += proc.getWait();
                totalWait += totalTime;
                totalTime += size;
                readyLength -= size;
            
                out.write(proc.toString());
                out.newLine();
                
                out3.write(proc.toString2());
                out3.newLine();
            
                Random generator = new Random();
                int x = generator.nextInt(12)+4;
                if((update() && ready.size()<4) || (ready.peek() == null && numLoc < numProcesses)){
                    updateReadyQueueSJF(x,totalTime);
                }
            }
            out.write("Number of Processes: " + numProcesses);
            out.newLine();
            out.write("Total time: " + (((double)totalTime/(double)1000) + " seconds"));
            out.newLine();
            out.write("Average Wait in Ready Queue: " + (((double)qwait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.write("Average Total Wait: " + (((double)totalWait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.close();
            out2.close();
            out3.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        numLoc = 0;
    }
    
    /**
     * Method to simulate a Round Robin scheduling algorithm
     * @param void
     * @return void
     */
    public void roundRobin(){
        resetVals();
        createReadyQueue();
        int totalTime = 0;
        int totalWait = 0;
        int qwait = 0;
        int q = 8;  //timeslice for rr
        String fileStr = "Size\tWait in Queue\tTotal Wait\tTurnAround";
        try{
            //Setting up the FileWriter and BufferedWriter
            //saving output to file named "rr.txt"
            FileWriter fstream = new FileWriter("rr.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            
            FileWriter fstream2 = new FileWriter("rr-time_vs_readyLength.txt");
            BufferedWriter out2 = new BufferedWriter(fstream2);
            
            FileWriter fstream3 = new FileWriter("rr-size_vs_wait.txt");
            BufferedWriter out3 = new BufferedWriter(fstream3);
            
            out.write(fileStr);
            out.newLine();
           //While loop is where the scheduling algorithm does all its work
            while(ready.peek() != null){
                out2.write(totalTime+"\t"+readyLength);
                out2.newLine();
                
                Process proc = (Process)ready.remove();
                int size = proc.getSize();
                int x = size - q;
                
                //if the process does not complete it is added the the back of the 
                //queue with its new size. Else the process is kept off the queue
                //and its formatted string is added to the file rr.txt
                if(x > 0){
                    proc.setSize(x);
                    ready.add(proc);
                    totalTime += q;
                    readyLength -= q;
                }
                else{
                    proc.setWait(totalTime);
                    qwait += proc.getWait();
                    totalWait += totalTime;
                    totalTime += size;
                    readyLength -= size;
                    
                    out.write(proc.toStringRR());
                    out.newLine();
                    
                    out3.write(proc.toStringRR2());
                    out3.newLine();
                }
                
                Random generator = new Random();
                int y = generator.nextInt(12)+4;
                if((update() && ready.size()<4) || (ready.peek() == null && numLoc < numProcesses)){
                    updateReadyQueue(y,totalTime);
                }
            }
            
            out.write("Number of Processes: " + numProcesses);
            out.newLine();
            out.write("Time Slice: " + q + " milliseconds");
            out.newLine();
            out.write("Total time: " + (((double)totalTime/(double)1000) + " seconds"));
            out.newLine();
            out.write("Average Wait in Ready Queue: " + (((double)qwait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.write("Average Total Wait: " + (((double)totalWait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.close();
            out2.close();
            out3.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        numLoc = 0;
    }
    
    /**
     * Method to simulate a custom scheduling algorithm where the scheduler pulls the head
     * of the queue and takes a peek at the next process. Once both values have been found,
     * if the process that has been pulled is smaller it is executed, otherwise it thrown 
     * to the back of the queue and the process that is now the head is pulled out and
     * executed.
     * @param void
     * @return void 
     */
    public void custom(){
        resetVals();
        createReadyQueue();
        int totalTime = 0;
        int totalWait = 0;
        int qwait = 0;
        int wait = 0;
        String fileStr = "Size\tWait in Queue\tTotal Wait\tTurnAround";
        try{
            //Setting up the FileWriter and BufferedWriter
            //saving output to file named "custom.txt"
            FileWriter fstream = new FileWriter("custom.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            
            FileWriter fstream2 = new FileWriter("custom-time_vs_readyLength.txt");
            BufferedWriter out2 = new BufferedWriter(fstream2);
            
            FileWriter fstream3 = new FileWriter("custom-size_vs_wait.txt");
            BufferedWriter out3 = new BufferedWriter(fstream3);
            
            out.write(fileStr);
            out.newLine();
            //While loop is where the scheduling algorithm does all its work
            while(ready.peek() != null){
                out2.write(totalTime+"\t"+readyLength);
                out2.newLine();
                if(ready.size() >= 2){
                    Process proc1 = (Process)ready.remove();
                    Process proc2 = (Process)ready.peek();
                    if(proc1.getSize() <= proc2.getSize()){
                        proc1.setWait(totalTime);
                        qwait += proc1.getWait();
                        totalWait += totalTime;
                        totalTime += proc1.getSize();
                        readyLength -= proc1.getSize();
                        
                        out.write(proc1.toString());
                        out.newLine();
                        
                        out3.write(proc1.toString2());
                        out3.newLine();
                    }
                    else{
                        ready.add(proc1);
                        proc2 = (Process)ready.remove();
                        proc2.setWait(totalTime);
                        qwait += proc2.getWait();
                        totalWait += totalTime;
                        totalTime += proc2.getSize();
                        readyLength -= proc2.getSize();
                        
                        out.write(proc2.toString());
                        out.newLine();
                        
                        out3.write(proc2.toString2());
                        out3.newLine();
                    }
                    Random generator = new Random();
                    int x = generator.nextInt(12)+4;
                    if((update() && ready.size()<5) || (ready.peek() == null && numLoc < numProcesses)){
                        updateReadyQueue(x,totalTime);
                    }
                }
                else{
                    if(numLoc < numProcesses){
                       Random generator = new Random();
                       int x = generator.nextInt(12)+4;
                       updateReadyQueue(x,totalTime);
                    }
                    else{
                        Process proc1 = (Process)ready.remove();
                        proc1.setWait(totalTime);
                        qwait += proc1.getWait();
                        totalWait += totalTime;
                        totalTime += proc1.getSize();
                        out.write(proc1.toString());
                        out.newLine();
                    }
                }
            }
            
            out.write("Number of Processes: " + numProcesses);
            out.newLine();
            out.write("Total time: " + (((double)totalTime/(double)1000) + " seconds"));
            out.newLine();
            out.write("Average Wait in Ready Queue: " + (((double)qwait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.write("Average Total Wait: " + (((double)totalWait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.close();
            out2.close();
            out3.close();
            
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        numLoc = 0;
    }
    
    public void MRoundRobin(){
        resetVals();
        createReadyQueue();
        int totalTime = 0;
        int totalWait = 0;
        int qwait = 0;
        int q;  //timeslice for rr
        String fileStr = "Size\tWait in Queue\tTotal Wait\tTurnAround";
        try{
            //Setting up the FileWriter and BufferedWriter
            //saving output to file named "mrr.txt"
            FileWriter fstream = new FileWriter("mrr.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            
            FileWriter fstream2 = new FileWriter("mrr-time_vs_readyLength.txt");
            BufferedWriter out2 = new BufferedWriter(fstream2);
            
            FileWriter fstream3 = new FileWriter("mrr-size_vs_wait.txt");
            BufferedWriter out3 = new BufferedWriter(fstream3);
            
            out.write(fileStr);
            out.newLine();
           //While loop is where the scheduling algorithm does all its work
            while(ready.peek() != null){
                out2.write(totalTime+"\t"+readyLength);
                out2.newLine();
                
                q = (readyLength/ready.size());
                Process proc = (Process)ready.remove();
                int size = proc.getSize();
                int x = size - q;
                //if the process does not complete it is added the the back of the 
                //queue with its new size. Else the process is kept off the queue
                //and its formatted string is added to the file rr.txt
                if(x > 0){
                    proc.setSize(x);
                    ready.add(proc);
                    totalTime += q;
                    readyLength -= q; 
                }
                else{
                    proc.setWait(totalTime);
                    qwait += proc.getWait();
                    totalWait += totalTime;
                    totalTime += size;
                    readyLength -= size;
                    
                    out.write(proc.toStringRR());
                    out.newLine();
                    
                    out3.write(proc.toStringRR2());
                    out3.newLine();
                }
                Random generator = new Random();
                int y = generator.nextInt(12)+4;
                if((update() && ready.size()<4) || (ready.peek() == null && numLoc < numProcesses)){
                    updateReadyQueue(y,totalTime);
                }
            }
            out.write("Number of Processes: " + numProcesses);
            out.newLine();
            out.write("Total time: " + (((double)totalTime/(double)1000) + " seconds"));
            out.newLine();
            out.write("Average Wait in Ready Queue: " + (((double)qwait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.write("Average Total Wait: " + (((double)totalWait/(double)numProcesses)/1000) + " seconds");
            out.newLine();
            out.close();
            out2.close();
            out3.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        numLoc = 0;
    }
    
    public static void main(String[] args){
        Scheduler a = new Scheduler(500);
        a.fcfs();
        a.sjf();
        a.roundRobin();
        a.custom();
        a.MRoundRobin();
    }
}
