package implementation;

import java.util.*;
import java.io.*;

/*
 * The Banker class, also the main class
 * 
 * Using Monitor for Banker 
 * All the variables are private and use synchronized methods
 */
public class Banker {
	//all the variables needed
	 private int resource_num;
	 private int process_num ;
	 private int [] available;
	 private int[][] allocation;
	 private int[][] need;
	 //for finish state array, 0 means 'F' and 1 means 'T'
	 private int[] finish;
	 private int[][] max;
	 
	 
	 //constructor
	 public Banker(){
		 initialize();
	 }
	
	 /*
	  * This function initializes all the variables needed
	  */
	 private void initialize(){
		try {
			Scanner scanner = new Scanner(new File("data/example2.txt"));
			resource_num = scanner.nextInt();
			process_num = scanner.nextInt();
			available = new int[resource_num];
			finish = new int[process_num];
			max = new int[process_num][resource_num];
			allocation = new int[process_num][resource_num];
			need = new int[process_num][resource_num];
			
			//initialize available resources
			for(int i=0; i<resource_num; i++){
				available[i] = scanner.nextInt();
			}
			//initialize max resource for each process
			for (int i = 0; i < process_num; i++) {
				for (int j = 0; j < resource_num; j++) {
					max[i][j] = scanner.nextInt();
				}
			}
			//initialize allocation resource for each process
			for (int i = 0; i < process_num; i++) {
				for (int j = 0; j < resource_num; j++) {
					allocation[i][j] = scanner.nextInt();
				}
			}
			//initialize need resource for each process
			for (int i = 0; i < process_num; i++) {
				for (int j = 0; j < resource_num; j++) {
					need[i][j] = max[i][j] - allocation[i][j];
				}
			}
			
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	 /*
	  * This function initialize the finish states for all processes
	  */
	 private void initialize_state(){
			for (int i = 0; i < process_num; i++) {
				finish[i] = 0;
			}
	 }
	
	 /*
	  * This function finds the process that is qualified
	  */
	private int find_candidate(int[][] x,int[] y){
		boolean status;
		for (int i = 0; i < process_num; i++) {
			status = true;
			//the finish state is 0, which means 'F'
			if(finish[i]==0){
				//check that need is less than current work
				for(int j=0; j<resource_num; j++){
					if(x[i][j] > y[j]){
						status = false;
						break;
					}
				}
				//if found, return the index
				if (status == true) {
					return i;					
				}
			}
		}
		//if not fount return -1
		return -1;
	}
	
	/*
	 * This function updates 'work', and change the finish state to 'T' which is 1
	 */
	private void update(int index,int[] work){
		//update work
		for(int j=0; j<resource_num; j++){
			work[j] +=allocation[index][j];
		}
		//update index
		finish[index] = 1;
	}
	/*
	 * This function checks the finish state for each process
	 */
	private boolean check_status(){
		for (int i = 0; i < finish.length; i++) {
			if (finish[i] == 0) {
				return false;
			}
		}
		return true;
	}
	/*
	 * The key function for Banker:
	 * 	check whether the current state is safe or not
	 */
	public boolean check_safe(){
		//copy available array to work
		int[] work = new int[resource_num];
		System.arraycopy(available, 0, work, 0, resource_num);
		//initialize the states 
		initialize_state();
		
		int candidate = find_candidate(need,work);
		
		while(candidate != -1){
			update(candidate,work);
			candidate = find_candidate(need,work);
		}
		//check finish state after processing 
		return check_status();
	}
	
	/*
	 * synchronized function for request resource
	 * 
	 */
	private synchronized void Request(int ID, int[] resource){
		for (int i = 0; i < resource_num; i++) {
			available[i] -= resource[i];
			allocation[ID][i] += resource[i];
			need[ID][i] -= resource[i];
		}
	}
	/*
	 * synchronized function for request resource
	 * 
	 */
	private synchronized void Release(int ID, int[] resource){
		for (int i = 0; i < resource_num; i++) {
			available[i] += resource[i];
			allocation[ID][i] -= resource[i];
			need[ID][i] += resource[i];
		}
	}
	
	/*
	 * synchronized function Resource_request, return type boolean
	 * 
	 * called by customers to request resources
	 */
	public synchronized boolean Resource_request(int ID, int[] resource){
		boolean granted = true;
		
		//check if the request if valid
		for (int i = 0; i < resource_num; i++) {
			if (resource[i] > need[ID][i] || resource[i] > available[i]) {
				granted = false;
			}
		}
		
		//if it's valid
		if (granted) {
			//simulate request
			Request(ID, resource);
			//check result state is safe or not
			if (check_safe()) {
				//if it's safe, grant it and return true
				System.out.println("Customer "+ ID + " requests resource "+ resource_toString(resource)+"and granted");
				return true;
			}
			else {
				//if it's not safe, return to the original state
				Release(ID, resource);
				//print output and return false
				System.out.println("unsafe");
				System.out.println("Customer "+ ID + " requests resource "+ resource_toString(resource)+"but rejected");
				return false;
			}
		}
		//if the request is invalid
		else {
			//print output and return false
			System.out.println("Customer "+ ID + " requests resource "+ resource_toString(resource)+"but rejected");
			return false;
		}
	}
	/*
	 * synchronized function Resource_release, return type boolean
	 * 
	 * called by customers to release resources
	 */
	public synchronized boolean Resource_release(int ID, int[] resource){
		boolean granted = true;
		
		//check if the request is valid
		for (int i = 0; i < resource_num; i++) {
			if (resource[i] > allocation[ID][i]) {
				granted = false;
			}
		}
		//if it's valid
		if (granted) {
			//simulate release
			Release(ID, resource);
			//check the result state
			if (check_safe()) {
				//if it's safe, grant it and return true
				System.out.println("Customer "+ ID + " releases resource "+ resource_toString(resource)+"and granted");
				print_state();
				return true;
			}
			else {
				//if the result state is not safe, return false
				Request(ID, resource);
				System.out.println("unsafe");
				System.out.println("Customer "+ ID + " releases resource "+ resource_toString(resource)+"but rejected");
				return false;
			}
		}
		//if the request is invalid, return false
		else {
			System.out.println("Customer "+ ID + " releases resource "+ resource_toString(resource)+"but rejected");
			return false;
		}
	}
	
	/*
	 * This function turn the resource array to a string 
	 */
	public String resource_toString(int[] resource){
		String toreturnString = "";
		for (int i = 0; i < resource.length; i++) {
			toreturnString += (resource[i]+",");
		}
		return toreturnString;
	}
	
	/*
	 * This function print current state 
	 */
	public void print_state(){
		System.out.println("");
		System.out.println("------------State----------------------");
		System.out.println("Current available resource: ");
		for (int i = 0; i < resource_num; i++) {
			System.out.print(available[i]+", ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Allocation: ");
		for(int i=0 ;i<process_num; i++){
			for(int j=0 ;j<resource_num; j++){
				System.out.print(allocation[i][j]+", ");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("Need: ");
		for(int i=0 ;i<process_num; i++){
			for(int j=0 ;j<resource_num; j++){
				System.out.print(need[i][j]+", ");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("---------------State--------------------");
		System.out.println("");
	}
	
	/*
	 * This function get the 'max' array
	 * 
	 * called by customer to random generate the number of resources to request
	 */
	public int[] getMAX(int ID){
		return max[ID];
	}
	/*
	 * This function get the 'allocation array
	 * 
	 * called by customer to random generate the number of resources to request
	 */
	public int[] getAllocation(int ID){
		return allocation[ID];
	}
	
	
	/*
	 * main function
	 * 
	 */
	public static void main(String[]args) {
		Banker banker = new Banker();
		//check the initial state
		if(!banker.check_safe()) System.out.println("Initial state is not safe");
		else{
			banker.print_state();
			//create customers and run
			for (int i = 0; i < banker.process_num; i++) {
				Customer customer = new Customer(i, banker);
				customer.start();
			}
		}
	}
}
