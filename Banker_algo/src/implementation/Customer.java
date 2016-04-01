package implementation;

/*
 * Customer class
 * 
 * extends Thread
 */
public class Customer extends Thread{
	//variables needed
	private Banker banker;
	private int ID;
	
	/*
	 * Constructor
	 */
	public Customer(int ID, Banker banker){
		this.banker = banker;
		this.ID = ID;
	}
	
	/*
	 * This function generates random number between 1 and input
	 */
	private static  int random(int input){
		return (int)(Math.random()*(input))+1;
	}
	
	@Override
	public void run() {
		try {
			while(true){
				//get the max array and allocation array
				int[] max = banker.getMAX(ID);
				int[] allocation = banker.getAllocation(ID);
				//create request 
				int[] request = new int[max.length];
				for (int i = 0; i < request.length; i++) {
					request[i] = random(max[i]);
				}
				//call resource_request
				banker.Resource_request(ID, request);
				
				//sleep 3 seconds 
				sleep(3000);
				
				//create resource to release
				int[] release = new int[allocation.length];
				for (int i = 0; i < allocation.length; i++) {
					release[i] = random(allocation[i]);
				}
				//class reource_release
				banker.Resource_release(ID, release);
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
