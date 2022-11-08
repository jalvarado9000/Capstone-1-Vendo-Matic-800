package com.techelevator;

//expandable list of imports...
import com.techelevator.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class VendingMachineCLI {

	//establish constants (magic numbers) for use by reference
	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items"; //shows stock
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase"; //adds money and select purchase
	private static final String MAIN_MENU_OPTION_EXIT = "Exit"; //shuts the machine down
	private static final String MAIN_MENU_OPTION_REPORT = ""; //hidden option for generating sales report
	private static final String[] MAIN_MENU_OPTIONS = { //assembles choices into an array
			MAIN_MENU_OPTION_DISPLAY_ITEMS,
			MAIN_MENU_OPTION_PURCHASE,
			MAIN_MENU_OPTION_EXIT,
			MAIN_MENU_OPTION_REPORT
	};
	private static final int MAX_STOCK_LEVEL = 5; //sets slot capacity for each item

	//instantiate a Scanner for obtaining console inut from the user
	private Scanner userInput = new Scanner(System.in);

	//initialize values that will be persistent during operation
	private double moneyInserted = 0; //keeps count of the money inserted by user;
	private double grossBalance = 0; //keeps count of the total earnings of the machine since last restart

	private Menu menu; //instantiates a menu object to drive options selection menu
	private List<Vendable> stock = new ArrayList<>(); //instantiates a stock object to hold all items in stock

	private static final DecimalFormat f = new DecimalFormat("0.00"); //establishes a format for money output

	//default constructor for primary object
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	//MAIN method - launch machine operations
	public static void main(String[] args) {

		//instantiate a new menu object for menu operations
		Menu menu = new Menu(System.in, System.out);

		//instantiate a new object for the vending machine
		VendingMachineCLI cli = new VendingMachineCLI(menu);

		//commence operations
		cli.run();
	}

	//run method for primary operation of machine
	public void run() {

		//establish path where the initial stock file is saved
		File dataFile = new File("C:\\Users\\johne\\OneDrive\\Desktop\\Capstone\\module-1-capstone\\capstone\\vendingmachine.csv");

		//stock the machine with all items invoiced in the stock file
		try (Scanner dataInput = new Scanner(dataFile)) {
			while (dataInput.hasNextLine()) { //loops through all line items in the stock file
				String lineOfInput = dataInput.nextLine(); //sets focus on next item in stock
				String[] splitInput = lineOfInput.split("\\|"); //splits line into array of item properties
				Vendable stockItem = new Vendable( //assigns each property to a new item object
						splitInput[0], //slot ID
						splitInput[1], //item name
						Double.parseDouble(splitInput[2]), //item price
						splitInput[3], //item type
						MAX_STOCK_LEVEL //sets stock level to maximum capacity
				);
				stock.add(stockItem); //adds item object to list of stocked items
			}
		} catch (FileNotFoundException e) {
			System.err.println("The file does not exist.");
		}

		//set machine to run continuously until exited
		while (true) {

			//capture user input for main menu selection
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			//evaluate input for menu option selected
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				vendingMachineItems(); //displays vending machine items
			}
			else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				purchasingProcessMenu(); //displays purchase menu
			}
			else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.exit(0); //exits the machine
			}
			else if (choice.equals(MAIN_MENU_OPTION_REPORT)) {
				reportSales(); //executes hidden sales report option
			}
		}
	}

	//selected option [1] displays a list of vending machine items
	public void vendingMachineItems() {

		//loop through each item in stock
		for(Vendable item : stock) { //set focus on current item

			//set up convenience reference variables for item properties
			String slotID = item.getSlotLocation(); //stores item's ID
			String itemName = item.getProductName(); //stores item's name
			double itemPrice = item.getPurchasePrice(); //stores item's price
			String priceString = f.format(itemPrice); //formats price $
			String itemType = item.getProductType(); //stores item's type
			int numberRemaining = item.getNumberInStock(); //stores number of items left in stock
			String remaining = "SOLD OUT"; //initializes amount remaining display with zero stock
			if(numberRemaining > 0) remaining = Integer.toString(numberRemaining); //adjusts amount remaining display with stock level if not zero

			//display statistics for stock item in focus
			System.out.println(slotID + "|" + itemName + "|" + priceString + "|" + itemType + "|" + remaining + " in stock");
		}
	}

	//selected option [2] displays options for purchasing menu
	public void purchasingProcessMenu() {

		//initialize array with purchase submenu options
		String[] purchasingOptions = {"(1) Feed Money\n" + "(2) Select Product\n" + "(3) Finish Transaction"};

		//initialize a boolean indicating that a valid selection has not yet been made
		boolean valid = false;

		//loop the menu until a valid option has been recognized
		while (!valid) {

			//loop through options array to display all menu options
			for (String options : purchasingOptions) System.out.println(options);

			//get input from the user via the console
			String keyboard = userInput.nextLine();

			//initialize selection variable
			int menuSelection = 0;

			//check if user input is a valid integer
			try {
				menuSelection = Integer.parseInt(keyboard); //if valid integer, assigns selection
			} catch (Exception e) {
				valid = false; //assures that loop does not break
			}

			//evaluate selection and navigate to selected function
			if (menuSelection == 1) {
				feedMoney(); //routes user to money feeding menu
			}
			else if (menuSelection == 2) {
				purchaseProduct(); //routes user to product selection menu
			}
			else if (menuSelection == 3) {
				finishedTransaction(moneyInserted); //finishes transaction
				valid = true; //sets valid selection to exit selection loop
			}
			else { //indicates to the user that selection was not valid and return to start of loop to retry input
				System.out.println("Invalid selection.  Please select an available menu option.");
			}
		}
	}

	//feed money into the machine and update amount of user's money pending action
	public void feedMoney(){

		//initialize boolean indicating that money-feeding is in progress
		boolean feeding = true;

		//loop through money-feeding cycle while money-feeding is in progress
		while (feeding) {
			System.out.println("Please insert a dollar amount: 1, 2, 5, 10 or DONE"); //prompts user for an amount to feed in
			String tendered = userInput.nextLine(); //captures user input
			boolean safeToLog = false; //instantiates a boolean indicating whether or not the transaction should be written to the log
			if(moneyAccepted(tendered) > 0.0) safeToLog = true; //determines if user input is acceptable then adjusts money fed in if it is and flags transaction for logging
			feeding = keepFeeding(tendered); //determines if user should be prompted again to feed money in
			System.out.println("Current Money Provided: $" + f.format(moneyInserted)); //displays total money in escrow
			if (safeToLog) AuditLog.log("FEED MONEY: $" + f.format(Double.valueOf(tendered)) + " $" + f.format(moneyInserted)); //logs current feeding attempt if applicable
		}
	}

	//determine if user input of money fed into the machine is a valid input and return amount as a double
	public double moneyAccepted(String tendered){

		//test for valid input type
		try {
			if (tendered.equalsIgnoreCase("done")) return 0.00; //if finished, returns no money added
			Integer.parseInt(tendered); //tests if user input is a valid integer
		}
		catch( NumberFormatException e ){ //user input was not a valid selection
			System.out.println("Please select an available menu option."); //prompts user to make a valid selection
			return 0.00; //returns no money added due to invalid selection
		}

		//initialize dollar amount fed in based on valid user input
		int dollar = Integer.parseInt(tendered);

		//filter valid input to specified dollar amounts
		if(dollar == 1 || dollar == 2 || dollar == 5 || dollar  == 10) {
			moneyInserted += Double.parseDouble(tendered); //converts input to double and adds to total money fed in
			return moneyInserted; //returns total money fed in
		}
		return 0.00; //if tendered amount is not a qualifying bill value then zero is returned
	}

	//determine if user needs to be prompted to keep feeding money
	public boolean keepFeeding(String option) {
		if (option.equalsIgnoreCase("done")) {
			return false; //user is finished feeding money
		}
		else if(option.equals("1") || option.equals("2") || option.equals("5") || option.equals("10")) {
			return true; //valid bill amount was selected but user has not indicated 'done' yet
		}
		else {
			System.out.println("Invalid input. Please select a listed option.\n"); //prompts user that input was invalid
			return true; //user remains in money-feeding mode
		}
	}

	//select a product to purchase
	public void purchaseProduct(){

		//display all stock items to the user
		vendingMachineItems();

		//prompt user to select an item by its slot
		System.out.println("Please select an item by its slot ID");

		//capture user selection
		String productSelected = userInput.nextLine();

		//initialize a variable indicating if the selection reflects a valid slot (assumes false to start with)
		boolean exists = false;

		//loop through all items in stock
		for (int i = 0; i < stock.size(); i++) {
			String slotName = stock.get(i).getSlotLocation(); //stores slotID in a convenience variable
			exists = isSlotFound(productSelected, slotName); //assigns boolean value if user input matches slot ID for this item or not
			int inStock = stock.get(i).getNumberInStock(); //stores stock level of item in a convenience variable

			//check if item exists and is not sold out and if it is valid execute the purchase
			if(isItemFound(exists, inStock)){
				double price = stock.get(i).getPurchasePrice(); //assigns item's price to a convenience variable
				completePurchase(moneyInserted, i); //executes the purchase attempt
				i = stock.size(); //sets iterator to end the loop
			}
		}

		//handle a selection that is not a valid slot ID
		if (!exists) System.out.println("This product code does not exist."); //informs user and return to menu
	}

	//determine if slot provided by user input matches slot ID of item called
	public boolean isSlotFound(String userSlot, String actualSlot) {
		if (userSlot.equals(actualSlot)) return true; //user input matches slot, returns true
		else return false; //user input does not match slot, returns false
	}

	//return true if item exists and has not sold out
	public boolean isItemFound(boolean found, int inventory){
		if(found) { //item exists
			if (inventory <= 0 ){ //item has sold out
				System.out.println("This item is sold out"); //indicates sold out status to user
				return false; //returns false because item is not available to purchase
			}
			else {
				return true; //returns true because item is not sold out
			}
		}
		else {
			return false; //returns false because item does not exist
		}
	}

	//attempt purchase based on money in escrow and item selected
	public void completePurchase(double money, int position){

		//determine if money in escrow is adequate for purchase
		if (moneyInserted >= stock.get(position).getPurchasePrice()) { //money in escrow is adequate

			//display transaction to user
			System.out.println("Vending: " //delivery is underway
					+ stock.get(position).getProductName() //slot
					+ " $" //dollar prefix
					+ f.format(stock.get(position).getPurchasePrice()) //formatted purchase price
					+ " Funds Remaining: $" //remainder prefix
					+ f.format(money - stock.get(position).getPurchasePrice()) //formatted remainder in escrow
			);

			//process transaction
			String type = stock.get(position).getProductType(); //stores product type to a convenience variable
			System.out.println(snackSound(type)); //displays onomatopoeia output message to user
			double moneyLeftOver = money - stock.get(position).getPurchasePrice(); //stores remainder in escrow to a convenience variable

			//log the transaction
			AuditLog.log(stock.get(position).getProductName() //product name
					+ " " //spacer
					+ stock.get(position).getSlotLocation() //slot ID
					+ " $" //dollar prefix
					+ f.format(money) //formatted dollar amount of escrow before transaction
					+ " $" //dollar prefix
					+ f.format(moneyLeftOver) //formatted dollar amount of escrow after transaction
			);

			//adjust coffers
			grossBalance += stock.get(position).getPurchasePrice(); //add purchase to machine's total revenue
			moneyInserted -= stock.get(position).getPurchasePrice(); //subtract purchase from escrow amount
			int newValue = stock.get(position).getNumberInStock() - 1; //store stock level (one unit sold) of purchased item
			stock.get(position).setNumberInStock(newValue); //apply stored stock level to update sold item
		} else { //money in escrow is inadequate to complete this transaction
			System.out.println("This item costs more than the amount tendered."); //prompts user that item cannot be purchased
		}
	}

	//display onomatopoeia to user
	public String snackSound(String type){
		String outputMessage = ""; //initializes output message

		//add appropriate onomatopoeia to output message
		if (type.equals("Chip")) outputMessage = "Crunch Crunch, Yum!"; //chip onomatopoeia
		else if (type.equals("Candy")) outputMessage = "Munch Munch, Yum!"; //candy onomatopoeia
		else if (type.equals("Drink")) outputMessage = "Glug Glug, Yum!"; //drink onomatopoeia
		else if (type.equals("Gum")) outputMessage = "Chew Chew, Yum!"; //gum onomatopoeia

		return outputMessage; //returns assembled output message
	}

	//finalize transaction and distribute change
	public String finishedTransaction(double change) {
		String changeString = f.format(change); //converts formatted change to a String for convenience
		System.out.println("Total change: $" + changeString); //display the change to be distributed to the user
		AuditLog.log("GIVE CHANGE: $" + changeString + " $0.00"); //log the change to be distributed to the user

		//calculate change (total to be distributed in coins)
		int[] quartersDimesNickles = new int[3]; //establishes an array to store coin counts {quarters, dimes, nickels}
		int quarterCount = 0; //initializes number of quarters to distribute
		int dimeCount = 0; //initializes number of dimes to distribute
		int nickelCount = 0; //initializes number of nickels to distribute

		BigDecimal amount = new BigDecimal(changeString); //converts change into BiGDecimal type for penny-precision

		//establish a loop that continues until all change has been accounted for
		while(amount.compareTo(BigDecimal.valueOf(0.00)) > 0) {

			if(amount.compareTo(BigDecimal.valueOf(0.25)) >= 0) { //if the amount left to account for is at least a quarter
				amount = amount.subtract(BigDecimal.valueOf(0.25)); //reduce change remaining by a quarter
				quarterCount++; //add one quarter to the number of quarters to be returned to the user
			}
			else if(amount.compareTo(BigDecimal.valueOf(0.10)) >= 0) { //if the amount left to account for is at least a dime
				amount = amount.subtract(BigDecimal.valueOf(0.10)); //reduce change remaining by a dime
				dimeCount++; //add one dime to the number of dimes to be returned to the user
			}
			else if(amount.compareTo(BigDecimal.valueOf(0.05)) >= 0) { //if the amount left to account for is at least a nickel
				amount = amount.subtract(BigDecimal.valueOf(0.05)); //reduce change remaining by a nickel
				nickelCount++; //add one nickel to the number of nickels to be returned to the user
			}
			else {
				System.out.println("Tendering error: cannot return $" + amount); //the remaining amount is in pennies (should never happen)
			}
		}

		//populate the coins array with the coin counts tallied
		quartersDimesNickles[0] = quarterCount;
		quartersDimesNickles[1] = dimeCount;
		quartersDimesNickles[2] = nickelCount;

		boolean returnedChange = false; //establishes a boolean indicating if any change has been returned to the user
		if(quartersDimesNickles[0] > 0) { //if any quarters were returned
			System.out.println("Quarters returned: " + quartersDimesNickles[0]); //displays quarters returned to the user
		}
		if(quartersDimesNickles[1] > 0) { //if any dimes were returned
			System.out.println("Dimes returned: " + quartersDimesNickles[1]); //displays dimes returned to the user
		}
		if(quartersDimesNickles[2] > 0) { //if any nickels were returned
			System.out.println("Nickels returned: " + quartersDimesNickles[2]); //displays nickels returned to the user
		}
		moneyInserted = 0.00; //assures that money in escrow is reset after returning change to the user
		return Arrays.toString(quartersDimesNickles); //returns the array as a String for unit test validation
	}

	public void reportSales() {

		// set up format for timestamp on sales report filename
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss_a");
		Date timeStamp = new Date(System.currentTimeMillis());

		//build path and filename for sales report file (adapt this to the local directory structure)
		File dataPath = new File("C:\\Users\\johne\\OneDrive\\Desktop\\Capstone\\module-1-capstone\\capstone\\src\\main\\java\\com");
		String dataFile = "Vending_Machine_Sales_Report_" + formatter.format(timeStamp) + ".txt";

		//write statistics to sales report
		File reportFile = new File(dataPath, dataFile);
		try (PrintWriter dataOutput = new PrintWriter(reportFile)) {

			//loop through all stock items and report number of each item sold since restarting the vending machine
			for(Vendable stockItem : stock) {

				//build report for each individual stock item
				String stockName = stockItem.getProductName();
				int stockSold = MAX_STOCK_LEVEL - stockItem.getNumberInStock();

				//write the item's statistics to the sales report file
				dataOutput.println(stockName + "\\|" + stockSold);
			}
			//add a final line to the sales report displaying total gross sales since the last restart of the vending machine
			dataOutput.println("\nTOTAL SALES: $" + f.format(grossBalance));

		} catch (Exception e) {
			System.out.println("An error has occurred.");
		}
	}
}