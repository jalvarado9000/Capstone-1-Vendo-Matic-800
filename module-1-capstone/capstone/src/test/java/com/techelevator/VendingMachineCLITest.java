package com.techelevator;

//expandable list of imports...
import com.techelevator.view.Menu;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VendingMachineCLITest {

    private Menu menu; //instantiates a new Menu object for constructing a VendingMachineCLI object
    VendingMachineCLI machine = new VendingMachineCLI(menu); //instantiates a new VendingMachineCLI object for testing
    List<Vendable> testProducts = new ArrayList<>(); //instantiates a new product object for testing

    @Test
    public void feedMoneyInvalidInput() {
        assertEquals(true, machine.keepFeeding("3"));
        assertEquals(true, machine.keepFeeding("4"));
        assertEquals(true, machine.keepFeeding("6"));
        assertEquals(true, machine.keepFeeding("7"));
        assertEquals(true, machine.keepFeeding("8"));
        assertEquals(true, machine.keepFeeding("9"));
        assertEquals(true, machine.keepFeeding("Don"));
        assertEquals(true, machine.keepFeeding("One"));
        assertEquals(true, machine.keepFeeding("Doe"));
        assertEquals(true, machine.keepFeeding("De"));
    }

    @Test
    public void feedMoneyValidInput() {
        assertEquals(true, machine.keepFeeding("1"));
        assertEquals(true, machine.keepFeeding("2"));
        assertEquals(true, machine.keepFeeding("5"));
        assertEquals(true, machine.keepFeeding("10"));
        assertEquals(false, machine.keepFeeding("DONE"));
        assertEquals(false, machine.keepFeeding("DONe"));
        assertEquals(false, machine.keepFeeding("DOnE"));
        assertEquals(false, machine.keepFeeding("DoNE"));
        assertEquals(false, machine.keepFeeding("dONE"));
    }

    @Test
    public void feedMoneyAcceptedOneTwoFiveTen() {
        assertEquals(1.00, machine.moneyAccepted("1"));
        assertEquals(3.00, machine.moneyAccepted("2"));
        assertEquals(8.00, machine.moneyAccepted("5"));
        assertEquals(18.00, machine.moneyAccepted("10"));
    }

    @Test
    public void feedMoneyNotAccepted() {
        assertEquals(0.00, machine.moneyAccepted("3"));
        assertEquals(0.00, machine.moneyAccepted("4"));
        assertEquals(0.00, machine.moneyAccepted("6"));
        assertEquals(0.00, machine.moneyAccepted("7"));
    }

    @Test
    void snackSoundVendingMachine() {
        //product.run();
        assertEquals("Crunch Crunch, Yum!", machine.snackSound("Chip"));
    }

    @Test
    void distributionOfCoinsReturned() {
        int[] expectedResult = {5, 1, 0};
        assertEquals(Arrays.toString(expectedResult), machine.finishedTransaction(1.35));
    }

    @Test
    void doesSlotExist(){
        assertEquals(true, machine.isSlotFound("C1","C1"));
        assertEquals(true, machine.isSlotFound("C2","C2"));
        assertEquals(true, machine.isSlotFound("C3","C3"));
    }

    @Test
    void slotDoesNotMatch(){
        assertEquals(false, machine.isSlotFound("C1","C2"));
        assertEquals(false, machine.isSlotFound("C2","C68"));
        assertEquals(false, machine.isSlotFound("C3","c3"));
    }

    @Test
    void isItemInStock(){
        assertEquals(true, machine.isItemFound(true,5));
        assertEquals(true, machine.isItemFound(true,4));
        assertEquals(true, machine.isItemFound(true,3));
        assertEquals(true, machine.isItemFound(true,2));
        assertEquals(true, machine.isItemFound(true,1));
        assertEquals(false, machine.isItemFound(true,0));
        assertEquals(true, machine.isItemFound(true,5));
        assertEquals(false, machine.isItemFound(false,5));
    }
}