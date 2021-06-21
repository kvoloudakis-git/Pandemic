package kvoloudakis.server;

import java.io.*; 
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.net.*; 

//Server class 
public class Server  
{
	static Vector<ClientHandler> playerVector = new Vector<>(); // Vector to store active players

	static int activePlayers = 0; // Counter for active players
	static final int portToSetUpServer = 64240;
	static final int numberOfPlayers = 4;
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException
	{
		//
		// Server setup is starting..
		//
		
		Socket s = null;
		ServerSocket ss = null;
		int serverPort = portToSetUpServer; // Port to listen to
		
		System.out.println("Atempting to start server... \n"); 
		try {
			ss = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.out.println("Problem with server creation.");
			e.printStackTrace();
		}   
		System.out.println("Server up & running!! Listening on port " + serverPort + "... \n");
		
		//
		// Server setup done! Waiting for players to join..
		//
		
		boolean allPlayersJoined = false; // Flag to stop accepting incoming requests
		
		System.out.println("Waiting for " + numberOfPlayers + " players to log in.. \n");
		
		while (!allPlayersJoined)  // Loop to accept incoming requests
		{  
		    try {
				s = ss.accept();
				
				System.out.println("New client request received : " + s); 
			       
			    // Obtaining input and output streams 
			    ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
			    ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
			    
			    System.out.println("Creating a new handler for this client..."); 
			    
			    // Create a new handler object for handling this request. 
			    ClientHandler mtch = new ClientHandler(s,"client " + activePlayers, dis, dos , activePlayers);
			    // Create a new Thread with this object.
			    Thread t = new Thread(mtch);  
			    System.out.println("Adding this client to active client list \n"); 
			    // Add this client to active clients list 
			    playerVector.add(mtch); 
			    // Start the thread.
			    t.start();  
			    
			    activePlayers++; // Increment activePlayers for new client 
			    
			    if (activePlayers == numberOfPlayers) {
			    	allPlayersJoined = true;
			    	System.out.println("All players are here!! We shall start the game! \n"); 
			    }
				
			} catch (IOException e) {
				System.out.println("Exception in accepting incoming client request");
				e.printStackTrace(); // Accept the incoming request
			} 
		} 
		
		//
		// All players are here! Initializing the game...
		//
		
		Board myBoard = new Board(numberOfPlayers);
		
		// Sending playerID to the Clients
		for (ClientHandler mc : Server.playerVector) 
			sendTo(mc.getPlayerNo(), mc);
		
		// Sending number of players to the Clients
		for (ClientHandler mc : Server.playerVector) 
			sendTo(numberOfPlayers, mc);
		
		// Sending roles to players
		myBoard.initializeRoles(false); // False == random, True == certain roles
		
		for (ClientHandler mc : Server.playerVector) 
			sendTo(myBoard.getRoleOf(mc.getPlayerNo()), mc);
		
		// Getting the usernames from the Clients
		for (ClientHandler mc : Server.playerVector) {
			String tmpString = (String)mc.dis.readObject();
			myBoard.setUsernames(tmpString, mc.getPlayerNo());
			System.out.println("Got the username from Client[" + mc.getPlayerNo() + "] and it's " + tmpString);
		}
		
		System.out.println("");
		myBoard.printOutRoles();
		System.out.println("");
		
		myBoard.deckSetup(0);
		
		//myBoard.initializeCities();
		//myBoard.initializeInfectedDeck();
		//myBoard.initializePlayersDeck();
		//myBoard.initializeGameVariables();
		//myBoard.initializePawnsPositions();
		//myBoard.manualBuildResearchStation("Atlanta");
		//myBoard.initializePlayersHands();
		//myBoard.addEpidemicCards();
		
		//myBoard.addEventCards();
		myBoard.initialInfection();
		
		System.out.println("");
		
		//
		// Testing testing
		//
		
		
		/*System.out.println("Testing all actions... \n");
		
		// Drive - ferry testing
		System.out.println("Testing drive / ferry to...");
		myBoard.driveTo(0, "Atlantagjaebflvb");
		myBoard.driveTo(0, "Atlanta");
		myBoard.driveTo(0, "Bogota");
		myBoard.driveTo(0, "Miami");
		myBoard.driveTo(0, "Washington");
		myBoard.driveTo(0, "Atlanta");
		
		// Direct flight testing
		System.out.println("\nTesting direct flight...");
		myBoard.directFlight(0, "Atlantagjaebflvb");
		myBoard.directFlight(0, "Manila");
		myBoard.directFlight(0, "Atlanta");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.directFlight(0, "Taipei");
		myBoard.getHandOf(0).add("Atlanta");
		myBoard.directFlight(0, "Atlanta");
		
		// Charter flight testing
		System.out.println("\nTesting charter flight...");
		myBoard.charterFlight(0, "Atlantagjaebflvb");
		myBoard.charterFlight(0, "Atlanta");
		myBoard.charterFlight(0, "Manila");
		myBoard.getHandOf(0).add("Atlanta");
		myBoard.charterFlight(0, "Manila");
		myBoard.getHandOf(0).add("Manila");
		myBoard.charterFlight(0, "Atlanta");
		
		// Shuttle flight
		System.out.println("\nTesting shuttle flight...");
		myBoard.manualBuildResearchStation("Lagos");
		myBoard.shuttleFlight(0, "Atlantagjaebflvb");
		myBoard.shuttleFlight(0, "Manila");
		myBoard.shuttleFlight(0, "Atlanta");
		myBoard.shuttleFlight(0, "Lagos");
		myBoard.shuttleFlight(0, "Atlanta");
		myBoard.manualRemoveResearchStation("Lagos");
		
		// Build Research Station testing
		System.out.println("\nTesting building RS...");
		myBoard.buildRS(0, "Atlantasad");
		myBoard.buildRS(0, "Atlanta");
		myBoard.buildRS(0, "Chicago");
		myBoard.driveTo(0, "Chicago");
		myBoard.buildRS(0, "Chicago");
		myBoard.getHandOf(0).add("Chicago");
		myBoard.buildRS(0, "Chicago");
		myBoard.driveTo(0, "Montreal");
		myBoard.getHandOf(0).add("Montreal");
		myBoard.buildRS(0, "Montreal");
		myBoard.driveTo(0, "Washington");
		myBoard.manualBuildResearchStation("Bogota");
		myBoard.manualBuildResearchStation("Lima");
		myBoard.manualBuildResearchStation("Madrid");
		myBoard.getHandOf(0).add("Washington");
		myBoard.buildRS(0, "Washington");
		myBoard.manualRemoveResearchStation("Lima");
		myBoard.buildRS(0, "Washington");
		myBoard.manualRemoveResearchStation("Chicago");
		myBoard.manualRemoveResearchStation("Montreal");
		myBoard.manualRemoveResearchStation("Bogota");
		myBoard.manualRemoveResearchStation("Madrid");
		myBoard.manualRemoveResearchStation("Washington");
		myBoard.driveTo(0, "Atlanta");
		
		// Treat disease testing
		System.out.println("\nTesting treat disease...");
		myBoard.treatDisease(0, "Atlanta", "Red");
		myBoard.searchForCity("Atlanta").setRedCubes(3);
		myBoard.treatDisease(0, "Atlanta", "Red");
		myBoard.treatDisease(0, "Atlanta", "Red");
		myBoard.treatDisease(0, "Atlanta", "Red");
		myBoard.searchForCity("Atlanta").setBlackCubes(1);
		myBoard.treatDisease(0, "Atlanta", "Red");
		myBoard.treatDisease(0, "Atlanta", "Black");
		
		// Discover Cure testing
		System.out.println("\nTesting discover cure...");
		myBoard.cureDisease(0, "Red");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.getHandOf(0).add("Taipei");
		myBoard.driveTo(0, "Miami");
		myBoard.cureDisease(0, "Red");
		myBoard.driveTo(0, "Atlanta");
		myBoard.cureDisease(0, "Red");
		myBoard.setCured(false, "Red");
		
		// Share knowledge testing
		System.out.println("\nTesting share knowledge...");
		myBoard.shareKnowledge(true, "Taipeijvkjab", 0, 1);
		myBoard.shareKnowledge(true, "Taipei", 0, 1);
		myBoard.shareKnowledge(false, "Taipei", 0, 1);
		myBoard.getHandOf(0).add("Taipei");
		myBoard.shareKnowledge(false, "Taipei", 0, 1);
		myBoard.shareKnowledge(true, "Taipei", 0, 1);
		myBoard.shareKnowledge(false, "Taipei", 0, 1);
		myBoard.getHandOf(0).remove("Taipei");
		
		System.out.println("\nDone testing actions.. \n"); */
		
		//myBoard = readActions("DT,0,Atlanta#TD,0,Atlanta,Red#CD,0,Red,City1,City2,City3,City4,City5#AP,0", myBoard);
		
		//myBoard.setGameEnded(true);
		
/*			myBoard.operationsExpertTravel(0, "Taipei");
		myBoard.operationsExpertTravel(1, "Taipei");
		myBoard.operationsExpertTravel(2, "Taipei");
		myBoard.operationsExpertTravel(3, "Taipei");
		
		//myBoard.printCitiesAndCubes();
		
		System.out.println(); */
		
		//myBoard.setGameEnded(true);
		
		//
		// Game logic starts here! Starting game..
		//
		
		myBoard.resetTalkedForThisTurn();
		
		while (myBoard.getGameEnded() == false)
		{
			System.out.println("*** This is round " + myBoard.getRound() + "." + myBoard.getWhoIsPlaying() + " ***\n");
			
			for (int i = 0 ; i < myBoard.getRSLocations().size() ; i++)
				System.out.println("RS in city of " + myBoard.getRSLocations().get(i));
			
			
			myBoard.resetCanNotOutbreak();
			myBoard.printCitiesAndCubes();
			myBoard.printRemainingCubesAndDiseaseStatus();
			myBoard.printWhoIsPlayngWithRole();
			
			myBoard.setMessageToAllClients("\n*** This is round " + myBoard.getRound() + "." + myBoard.getWhoIsPlaying() + " and " + myBoard.getUsernames(myBoard.getWhoIsPlaying()) + " is playing now *** \n");
			
			int currentlyCheckingClientNo = -1;
			
			for (ClientHandler mc : Server.playerVector) {
				
				currentlyCheckingClientNo = mc.getPlayerNo();
				
				if (currentlyCheckingClientNo != myBoard.getWhoIsPlaying()) {
					
					System.out.println(myBoard.getUsernames(currentlyCheckingClientNo) + " is thinking of a suggestion to make..");
					myBoard.setMessageToClient("Server: User" + currentlyCheckingClientNo + " please import your suggestion for this turn..", mc.getPlayerNo());
					
					myBoard.setWhoIsTalking(currentlyCheckingClientNo);
					
					sendTo(myBoard, mc);
					myBoard.resetPersonalizedMessages();
					
					//System.out.println("Trying to read suggestion");
					myBoard.setActions((String)mc.dis.readObject(), currentlyCheckingClientNo);
					//System.out.println("Suggestion has been read");
					myBoard.setTalkedForThisTurn(true, currentlyCheckingClientNo);
				}
			}
			
			for (ClientHandler mc : Server.playerVector) {
				
				currentlyCheckingClientNo = mc.getPlayerNo();
				
				if (currentlyCheckingClientNo == myBoard.getWhoIsPlaying()) {
					
					System.out.println(myBoard.getUsernames(currentlyCheckingClientNo) + " is thinking of an action to make..");
					System.out.println();
					myBoard.setMessageToClient("Server: User" + currentlyCheckingClientNo + " please import your action for this turn..", mc.getPlayerNo());
					myBoard.setWhoIsTalking(currentlyCheckingClientNo);
					
					sendTo(myBoard, mc);
					myBoard.resetAllMessages();
					
					//System.out.println("Trying to read action");
					myBoard.setActions((String)mc.dis.readObject(), currentlyCheckingClientNo);
					myBoard = readActions(myBoard.getActions(currentlyCheckingClientNo), myBoard); // this was new
					//System.out.println("Action has been read");
					myBoard.setTalkedForThisTurn(true, currentlyCheckingClientNo);
					myBoard.setRound(myBoard.getRound()+1);
				}	
			}
			
			System.out.println("\nPrinting actions of all players...");
			
			for (int i = 0 ; i < activePlayers ; i++)
				System.out.println(myBoard.getUsernames(i) + " : " + myBoard.getActions(i));
			System.out.println("");
			
			if (myBoard.checkIfWon())
				break;
			
			myBoard.drawCards(myBoard.getWhoIsPlaying(), 2);
			System.out.println("");
			
			if (!myBoard.getIsQuietNight())
				myBoard.infectCities(myBoard.getInfectionRate(),1);
			else 
				myBoard.setIsQuietNight(false);
			System.out.println("");
			
			myBoard.resetTalkedForThisTurn();
			
			if (myBoard.getWhoIsPlaying() == numberOfPlayers - 1)
				myBoard.setWhoIsPlaying(0); // Back to first player
			else
				myBoard.setWhoIsPlaying(myBoard.getWhoIsPlaying() + 1); // Next player
		}
		
		//
		// Game logic ends here!
		//
		
		System.out.println("Game has finished. Closing resources.. \n");
		
		// Stats
		int curedCount = 0;
		for (int i = 0 ; i < 4 ; i++)
			if (myBoard.getCured(i))
				curedCount++;
		
		int epidemicsPassed = myBoard.getNumberOfEpidemicCards();
		for (int i = 0 ; i < myBoard.getPlayersDeck().size() ; i++)
			if (myBoard.getPlayersDeck().get(i).equals("Epidemic"))
				epidemicsPassed--;
		
		int totalCubesLeft = myBoard.getCubesLeft(0) + myBoard.getCubesLeft(1) + myBoard.getCubesLeft(2) + myBoard.getCubesLeft(3);
		
		String reasonLost;
		
		if (myBoard.getOutbreaksCount() == myBoard.getOutbreakLimit())
			reasonLost = "Outbreaks";
		else if (myBoard.getPlayersDeck().size() == 0)
			reasonLost = "Cards";
		else if ((myBoard.getCubesLeft(0) <= 0) || (myBoard.getCubesLeft(1) <= 0) || (myBoard.getCubesLeft(2) <= 0) || (myBoard.getCubesLeft(3) <= 0))
			reasonLost = "Cubes";
		else 
			reasonLost = "Unknown";
		
		int lastedRounds = myBoard.getRound() - 1;
		
		System.out.println();
		
		if (myBoard.checkIfWon())
			System.out.println("*** Players won!! ***");
		else 
			System.out.println("*** Players lost... ***");
		
		System.out.println();
		
		if (!myBoard.checkIfWon())
			System.out.println("Reason lost : " + reasonLost);
		else
			reasonLost = "Won";
		
		System.out.println("Number of rounds last : " + lastedRounds);
		System.out.println("Number of epidemics passed : " + epidemicsPassed);
		System.out.println("Number of diseases cured : " + curedCount);
		System.out.println("Number of cubes left (total) : " + totalCubesLeft);
		
		System.out.println();
		
		String writeString = reasonLost + "," + lastedRounds + "," + epidemicsPassed + "," + curedCount + "," + totalCubesLeft + "\r\n";
		System.out.println(writeString);
		
		try {
		      FileWriter myWriter = new FileWriter("Results_" + numberOfPlayers + "p (2-3-3-3).csv", true);
		      myWriter.write(writeString);
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		
		myBoard.setGameEnded(true);
		sendToAll(myBoard);
		
		TimeUnit.SECONDS.sleep(1);
		
		s.close();
		ss.close();
		
		System.out.println("Recources closed succesfully. Goodbye! \n");
	} 
	
	// Useful functions
	
	public static Board readActions(String toRead, Board board)
	{
		String delimiterActions = "#";
		String delimiterVariables = ",";
		
		String[] actions;
		String[] variables;
		
		int actionCounter = 0;
		
		actions = toRead.split(delimiterActions);
		
		for (int i = 0 ; i < actions.length; i++)
		{
			variables = actions[i].split(delimiterVariables);
			
			if (variables[0].equals("DT"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " drives to " + variables[2]);
				board.driveTo(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("DF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a direct flight to " + variables[2]);
				board.directFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("CF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a charter flight to " + variables[2]);
				board.charterFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("SF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a shuttle flight to " + variables[2]);
				board.shuttleFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("BRS"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is building a Research Station to " + variables[2]);
				board.buildRS(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
			else if (variables[0].equals("RRS"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is removing a Reseaerch Station from " + variables[2]);
				board.removeRS(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("TD"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is treating the " + variables[3] + " disease from " + variables[2]);
				board.treatDisease(Integer.parseInt(variables[1]), variables[2], variables[3]);
				actionCounter++;
			}
			else if (variables[0].equals("CD1"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease");
				board.cureDisease(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
			else if (variables[0].equals("CD2"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease and throws " + variables[3] + variables[4] + variables[5] + variables[6] + variables[7]);
				board.cureDisease(Integer.parseInt(variables[1]), variables[2], variables[3], variables[4], variables[5], variables[6], variables[7]);
				actionCounter++;
			}
			else if (variables[0].equals("SK"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[3]) + " gives(" + variables[1] + ") the card of " + variables[2] + " to player " + Integer.parseInt(variables[4]));
				board.shareKnowledge(Boolean.parseBoolean(variables[1]), variables[2], Integer.parseInt(variables[3]), Integer.parseInt(variables[4]));
				actionCounter++;
			}
			else if (variables[0].equals("AP"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " decided to pass this action");
				board.actionPass(Integer.parseInt(variables[1]));
				actionCounter++;
			}
			else if (variables[0].equals("C"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " sends the following message: " + variables[2]);
				board.chatMessage(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("PGG"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Government Grand and builds a research station in " + variables[2]);
				board.playGovernmentGrant(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("PQN"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays One Quiet Night. Skipping next infection round");
				board.playOneQuietNight(Integer.parseInt(variables[1]));
			}
			else if (variables[0].equals("PA"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Airlift. Moving player " + Integer.parseInt(variables[2]) + " to " + variables[3]);
				
			}
			else if (variables[0].equals("PF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Forecast. Displaying next 6 cards from infection deck");
				board.playForecast(Integer.parseInt(variables[1]));
			}
			else if (variables[0].equals("PRP"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Resilient Population for " + variables[2]);
				board.playResilientPopulation(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("OET"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " travels to " + variables[2] + " as the Operations Expert");
				board.operationsExpertTravel(Integer.parseInt(variables[1]), variables[2], variables[3]);
				actionCounter++;
			}
			
			
			if (actionCounter >= 4)
			{
				System.out.println("\nYou reached the maximum actions for this turn..");
				break;
			}
		}
		return board;
	}
	
	// Sending desired object to specific client
	public static void sendTo(Object objToSend, ClientHandler player) throws IOException, InterruptedException {
		//System.out.println("Writing function!");
		player.dos.flush();
		player.dos.reset();
		player.dos.writeObject(objToSend);
		//System.out.println("End of writing function!");
	}
	
	// Sending desired object to all clients
	public static void sendToAll(Object objToSend) throws IOException {
		for (ClientHandler mc : Server.playerVector) {
			//System.out.println("Writing function!");
			mc.dos.flush();
			mc.dos.reset();
			mc.dos.writeObject(objToSend);
			//System.out.println("End of writing function!");
		}
	}
	
	// End of useful functions
		
} 

//ClientHandler class 
class ClientHandler implements Runnable  
{ 
	Scanner scn = new Scanner(System.in); 
	private String name; 
	final ObjectInputStream dis; 
	final ObjectOutputStream dos;
	Socket s; 
	boolean isloggedin; 
	private int playerNo;
	   
	// constructor 
	public ClientHandler(Socket s, String name, 
	                         ObjectInputStream dis, ObjectOutputStream dos, int playerNo) { 
	    this.dis = dis; 
	    this.dos = dos;  
	    this.name = name; 
	    this.s = s; 
	    this.isloggedin = true; 
	    this.playerNo = playerNo;
	} 
	
	public int getPlayerNo() {
		return this.playerNo;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	@Override
	public void run() {}
	
} 