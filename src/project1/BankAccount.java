/* Name: Gregory Flynn
   Course: CNT 4714 Summer 2022
   Assignment title: Project 1 – Synchronized, Cooperating Threads Under Locking
   Due Date: June 5, 2022
*/

// this class is needed since the threads need to modify the same space in memory.
// primitives in java are "pass by value" so pointing all threads to an int is akin to
// pointing the threads to temporary places in memory.
// objects in java are "pass by reference" so, even though a "bank account" is simply one integer,
// all the threads will point to the same place in memory if passed an object

/* synchronization */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* file I/O */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/* timestamp generation */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankAccount {
  private int balance = 0;
  private File logFile = new File("");

  /* optional: redirect all output to a file */
  private File outputDump = new File("outputDump.out");

  /* create time objects to flag "threshold" deposits and withdrawals */
  // use the 'now()' method for 'LocalDateTime' objects to get a timestamp
  // now() will need to be invoked to make a timestamp everytime a threshold value is generated
  LocalDateTime currentTime = LocalDateTime.now();
  // the 'DateTimeFormatter' object takes in the 'LocalDateTime' object and formats it into a desired pattern
  DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu kk:mm:ss");

  /* create synchronization objects to coordinate threads via mutual exclusion */
  // lock to control mutually exclusive access to the buffer
  private Lock mutex = new ReentrantLock();

  // condition representing the ability to withdrawal if funds exist
  private Condition sufficientBalance = mutex.newCondition();

  // constructor
  public BankAccount(File oFile) {
    logFile = oFile;
  }

  // mutator: subtract from balance
  public void withdrawalFunds(int amountToWithdrawal, String agentName) {

    // lock out any other threads from accessing the bank account object
    mutex.lock();

    if(amountToWithdrawal <= balance) {
      balance -= amountToWithdrawal;

      try {
        FileWriter outputFileWriter = new FileWriter(outputDump, true);
        outputFileWriter.write("\t\t\t\tAgent " + agentName + " withdraws $" + amountToWithdrawal + "\t\t(-) Balance is $" + balance + "\n");
        outputFileWriter.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }

      // System.out.print("\t\t\t\tAgent " + agentName + " withdraws $" + amountToWithdrawal);
      // System.out.print("\t\t(-) Balance is $" + balance + "\n");

      if(amountToWithdrawal > 75) {
        currentTime = LocalDateTime.now();

        try {
          FileWriter outputFileWriter = new FileWriter(outputDump, true);
          outputFileWriter.write("\n* * * Flagged Transaction - Withdrawal Agent " + agentName + " Made A Withdrawal In Excess Of $75.00 USD - See Flagged Transaction Log.\n\n");
          outputFileWriter.close();
        }
        catch(IOException e) {
          e.printStackTrace();
        }

        // System.out.print("\n* * * Flagged Transaction - Withdrawal Agent " + agentName + " Made A Withdrawal In Excess Of $75.00 USD - See Flagged Transaction Log.\n\n");
  
        // instantiate a file writer and print threshold record to log file
        try {
          FileWriter logFileWriter = new FileWriter(logFile, true);
          logFileWriter.write("\tWithdrawal Agent " + agentName + " issued withdrawal of $" + amountToWithdrawal + " at: " + timeFormatter.format(currentTime) + " EDT\n");
          logFileWriter.close();
        }
        catch(IOException e) {
          e.printStackTrace();
        }

      }
    }
    else {

      try {
        FileWriter outputFileWriter = new FileWriter(outputDump, true);
        outputFileWriter.write("\t\t\t\tAgent " + agentName + " withdraws $" + amountToWithdrawal + "\t\t(******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!\n");
        outputFileWriter.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }

      // System.out.print("\t\t\t\tAgent " + agentName + " withdraws $" + amountToWithdrawal);
      // System.out.print("\t\t(******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!!\n");
  
      try {
        // wait for balance to change, hopefully to a sufficient level 
        // to withdrawal from without incurring a negative balance
        sufficientBalance.await();
      }
      catch(InterruptedException exception){
        exception.printStackTrace();
      }
    }

    // unlock the lock, allowing other threads to access bank account object
    mutex.unlock();
  }

  // mutator: add to balance
  public void depositFunds(int amountToDeposit, String agentName) {

    // lock out any other threads from accessing the bank account object
    mutex.lock();

    balance += amountToDeposit;

    try {
      FileWriter outputFileWriter = new FileWriter(outputDump, true);
      outputFileWriter.write("Agent " + agentName + " deposits $" + amountToDeposit + "\t\t\t\t\t\t(+) Balance is $" + balance + "\n");
      outputFileWriter.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }

    // System.out.print("Agent " + agentName + " deposits $" + amountToDeposit);
    // System.out.print("\t\t\t\t\t\t(+) Balance is $" + balance + "\n");

    if(amountToDeposit > 350) {

      try {
        FileWriter outputFileWriter = new FileWriter(outputDump, true);
        outputFileWriter.write("\n* * * Flagged Transaction - Depositor Agent " + agentName + " Made A Deposit In Excess Of $350.00 USD - See Flagged Transaction Log.\n\n");
        outputFileWriter.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }

      // System.out.print("\n* * * Flagged Transaction - Depositor Agent " + agentName + " Made A Deposit In Excess Of $350.00 USD - See Flagged Transaction Log.\n\n");

      // instantiate a file writer and print threshold record to file
      try {
        FileWriter logFileWriter = new FileWriter(logFile, true);
        logFileWriter.write("Depositor Agent " + agentName + " issued deposit of $" + amountToDeposit + " at: " + timeFormatter.format(currentTime) + " EDT\n");
        logFileWriter.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }
      
    }

    // signal to all withdrawal agents that they can try to withdrawal again
    sufficientBalance.signalAll();

    // unlock the lock, allowing other threads to access bank account object
    mutex.unlock();
  }

  // accessor: see balance
  public int getBalance() {
    return balance;
  }
}