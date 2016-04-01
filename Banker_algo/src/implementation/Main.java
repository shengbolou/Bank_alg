package implementation;

import java.util.*;
import java.io.*;


public class Main {
	 private int resource_num;
	 private int process_num ;
	 private int [] available;
	 private int[][] allocation;
	 private int[][] need;
	 private int[] finish;
	 private int[][] max;
	
	public void initialize(){
		try {
			Scanner scanner = new Scanner(new File("data/example1.txt"));
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
			//initialize the inital states for each process
			for (int i = 0; i < process_num; i++) {
				finish[i] = 0;
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
			// TODO: handle exception
		}
	}
	
	public int find_candidate(int[][] x,int[] y){
		boolean status;
		for (int i = 0; i < process_num; i++) {
			status = true;
			if(finish[i]==0){
				for(int j=0; j<resource_num; j++){
					if(x[i][j] > y[j]){
						status = false;
						break;
					}
				}
				if (status == true) {
					return i;					
				}
			}
		}
		return -1;
	}
	
	public void update(int index,int[] work){
		//update work
		for(int j=0; j<resource_num; j++){
			work[j] +=allocation[index][j];
		}
		//update index
		finish[index] = 1;
	}
	public boolean check_status(){
		for (int i = 0; i < finish.length; i++) {
			if (finish[i] == 0) {
				return false;
			}
		}
		return true;
	}
	public void process(){
		int[] work = new int[resource_num];
		System.arraycopy(available, 0, work, 0, resource_num);
		int candidate = find_candidate(need,work);
		
		while(candidate != -1){
			update(candidate,work);
			candidate = find_candidate(need,work);
		}
		if (check_status()) {
			System.out.println("Safe State");
		}
		else {
			System.out.println("Unsafe State");
		}
	}
	
	
	
	public static void main(String[]args) {
		Main main = new Main();
		main.initialize();
		main.process();
	}
}
