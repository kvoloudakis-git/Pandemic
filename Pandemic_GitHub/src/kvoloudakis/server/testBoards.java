package kvoloudakis.server;

import java.io.FileWriter;
import java.io.IOException;

public class testBoards {
	
	public static void main(String[] args)
	{
		String writeString;
		char spChar = '"';
		
		Board myBoard = new Board(4);
		
		myBoard.initializeCities();

		myBoard.initializeInfectedDeck();
		
		myBoard.initializePlayersDeck();
		
		for (int i = 0 ; i < myBoard.getInfectedDeck().size() ; i ++)
		{
			writeString = "infectedDeck.add(" + spChar + myBoard.getInfectedDeck().get(i) + spChar + ");\r\n";
			
			try {
				FileWriter myWriter = new FileWriter("PreMade Deck1.csv", true);
				myWriter.write(writeString);
			    myWriter.close();
			} 
			catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
		    }
		}
		
		writeString = "\r\n";
		
		try {
			FileWriter myWriter = new FileWriter("PreMade Deck1.csv", true);
			myWriter.write(writeString);
		    myWriter.close();
		} 
		catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
	    }
		
		for (int i = 0 ; i < myBoard.getPlayersDeck().size() ; i ++)
		{
			writeString = "playersDeck.add(" + spChar + myBoard.getPlayersDeck().get(i) + spChar + ");\r\n";
			
			try {
				FileWriter myWriter = new FileWriter("PreMade Deck1.csv", true);
				myWriter.write(writeString);
			    myWriter.close();
			} 
			catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
		    }
		}
		
		writeString = "\r\n";
		
		try {
			FileWriter myWriter = new FileWriter("PreMade Deck1.csv", true);
			myWriter.write(writeString);
		    myWriter.close();
		} 
		catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
	    }
		
		myBoard.initializePlayersHands();
		
		myBoard.addEpidemicCards();
		
		for (int i = 0 ; i < myBoard.getPlayersDeck().size() ; i ++)
		{
			writeString = "playersDeck.add(" + spChar + myBoard.getPlayersDeck().get(i) + spChar + ");\r\n";
			
			try {
				FileWriter myWriter = new FileWriter("PreMade Deck1.csv", true);
				myWriter.write(writeString);
			    myWriter.close();
			} 
			catch (IOException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
		    }
		}
		
		System.out.println("DONE");
		
	}
}
