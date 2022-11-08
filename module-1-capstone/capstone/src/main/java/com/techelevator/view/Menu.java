package com.techelevator.view;

//expandable list of imports...
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

	//declare variables for output and input
	private PrintWriter out;
	private Scanner in;

	//constructor sets output and input references
	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	//displays menu options and collects user input
	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null; //establishes a null variable that will maintain the loop until assigned a value
		while (choice == null) { //loops the menu until a valid input is obtained from the user
			displayMenuOptions(options); //displays the menu options on the console
			choice = getChoiceFromUserInput(options); //requests input from the user
		}
		return choice; //returns user selection
	}

	//obtain user selection of menu item
	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null; //initialize a null variable to hold a selection once validated
		String userInput = in.nextLine(); //gather input from the user via the console
		try {
			int selectedOption = Integer.valueOf(userInput); //assign user input to an integer if possible
			if (selectedOption > 0 && selectedOption <= options.length) { //check if selection is available on the menu
				choice = options[selectedOption - 1]; //update null to selection
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) { //if selection was not valid display an error to the user
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice; //return selection
	}

	//displays menu options to the user
	private void displayMenuOptions(Object[] options) {
		out.println(); //prints a blank line to the console
		for (int i = 0; i < options.length - 1; i++) { //loops through all menu options
			int optionNum = i + 1; //offsets options from indexing standard
			out.println(optionNum + ") " + options[i]); //displays option to the user via the console
		}
		out.print(System.lineSeparator() + "Please choose an option >>> "); //prompts the user to make a selection
		out.flush(); //flushes the output
	}
}