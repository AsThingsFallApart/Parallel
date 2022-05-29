/* Name: Gregory Flynn
   Course: CNT 4714 Summer 2022
   Assignment title: Project 1 – Synchronized, Cooperating Threads Under Locking
   Due Date: June 5, 2022
*/

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Project1Driver {

  public static void main(String[] args) {
    // instantiate a bank account object to be shared amongst threads
    BankAccount account = new BankAccount();

    // create FIVE deposit agent threads
    Thread DT0 = new Thread(new DepositAgent("DT0", account));
    Thread DT1 = new Thread(new DepositAgent("DT1", account));
    Thread DT2 = new Thread(new DepositAgent("DT2", account));
    Thread DT3 = new Thread(new DepositAgent("DT3", account));
    Thread DT4 = new Thread(new DepositAgent("DT4", account));
    
    // create TEN withdrawal agent threads
    Thread WT0 = new Thread(new WithdrawalAgent("WT0", account));
    Thread WT1 = new Thread(new WithdrawalAgent("WT1", account));
    Thread WT2 = new Thread(new WithdrawalAgent("WT2", account));
    Thread WT3 = new Thread(new WithdrawalAgent("WT3", account));
    Thread WT4 = new Thread(new WithdrawalAgent("WT4", account));
    Thread WT5 = new Thread(new WithdrawalAgent("WT5", account));
    Thread WT6 = new Thread(new WithdrawalAgent("WT6", account));
    Thread WT7 = new Thread(new WithdrawalAgent("WT7", account));
    Thread WT8 = new Thread(new WithdrawalAgent("WT8", account));
    Thread WT9 = new Thread(new WithdrawalAgent("WT9", account));
    
    System.out.print("Deposit Agents\t\t\t   Withdrawal Agents   \t\t        Balance\n");
    System.out.print("--------------\t\t\t   -----------------   \t\t------------------------\n");
    

    // create ExecutorService to manage threads
    ExecutorService threadExecutor = Executors.newCachedThreadPool();

    // start threads
    threadExecutor.execute(DT0);
    threadExecutor.execute(DT1);
    threadExecutor.execute(DT2);
    threadExecutor.execute(DT3);
    threadExecutor.execute(DT4);

    threadExecutor.execute(WT0);
    threadExecutor.execute(WT1);
    threadExecutor.execute(WT2);
    threadExecutor.execute(WT3);
    threadExecutor.execute(WT4);
    threadExecutor.execute(WT5);
    threadExecutor.execute(WT6);
    threadExecutor.execute(WT7);
    threadExecutor.execute(WT8);
    threadExecutor.execute(WT9);
  }
}
