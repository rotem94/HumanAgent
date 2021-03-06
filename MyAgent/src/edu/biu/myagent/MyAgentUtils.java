package edu.biu.myagent;

import java.util.ArrayList;
import java.util.LinkedList;
import edu.biu.myagent.GameInfo.PlayerBehavior;
import edu.biu.myagent.GameInfo.PlayerCooperation;
import edu.usc.ict.iago.utils.Event;
import edu.usc.ict.iago.utils.GameSpec;
import edu.usc.ict.iago.utils.GeneralVH;
import edu.usc.ict.iago.utils.Offer;
import edu.usc.ict.iago.utils.Preference;
import edu.usc.ict.iago.utils.Preference.Relation;

public class MyAgentUtils {
	public final double LIE_THRESHOLD = 0.6; 

	private LinkedList<Preference>currentPlayerPreferences;
	private LinkedList<Preference>currentAgentPreferences;
	private MyAgentUtilsExtension currentGameAgentUtils;
	private GeneralVH agent;
	private GameInfo firstGameInfo;
	private GameInfo secondGameInfo;
	private boolean thirdGamePlayerLies;
	public boolean competitive;
	private int agentOwePreferences = 0;

	/**
	 * Constructor for the AUE.
	 * @param core The VH associated with this instance of AUE.
	 */
	public MyAgentUtils(GeneralVH core){
		this.agent = core;

		competitive = false;
		currentGameAgentUtils = new MyAgentUtilsExtension(core);
	}

	/**
	 * Configures initial parameters for the given game.
	 * @param game the game being played.
	 */
	public void configureGame(GameSpec game, int gameNumber)
	{	
		currentGameAgentUtils.configureGame(game);

		if(gameNumber == 1) {
			currentPlayerPreferences = new LinkedList<Preference>();
			currentAgentPreferences = new LinkedList<Preference>();
			firstGameInfo = new GameInfo(getMaxPossiblePoints(), currentPlayerPreferences, currentAgentPreferences);
		}
		else {
			currentPlayerPreferences = new LinkedList<Preference>();
			currentAgentPreferences = new LinkedList<Preference>();

			if(gameNumber == 2) {
				secondGameInfo = new GameInfo(getMaxPossiblePoints(), currentPlayerPreferences, currentAgentPreferences);
				secondGameInfo.setAgentOwesPreferences(agentOwePreferences);
			}

			agentOwePreferences = 0;
		}

		currentGameAgentUtils.setPlayerPreferences(currentPlayerPreferences);
	}

	public void setAgentGameIssues(Offer offer, int gameNumber) {
		if(!isFullOffer(offer))
			return;

		if(gameNumber == 1) {
			firstGameInfo.setAgentIssuesTotalPoints(getAgentGameIssues(offer));

			return;
		}

		if(gameNumber == 2) 
			secondGameInfo.setAgentIssuesTotalPoints(getAgentGameIssues(offer));
	}

	private int getAgentGameIssues(Offer offer) {
		return currentGameAgentUtils.myActualOfferValue(offer);
	}

	/**
	 * Sets the agent belief for when multiple opponent orderings are equally likely.  When true, it assumes it has the same preferences.  When false, it does not.
	 * @param fixedpie true if fixed pie belief is in effect, false otherwise (if method is not called, defaults to false)
	 */
	public void setAgentBelief(boolean fixedpie)
	{
		currentGameAgentUtils.setAgentBelief(fixedpie);
	}

	/**
	 * returns the GameSpec being used in the current game.
	 * @return the GameSpec currently being played.
	 */
	public GameSpec getSpec()
	{
		return currentGameAgentUtils.getSpec();
	}

	/**
	 * Adds the given preference to the list of preferences.
	 * @param p the preference to add
	 */
	public void addPref (Preference p, int gameNumber)
	{
		if(p == null || preferenceIsntValid(p) || playerLiedInTheCurrentGame(gameNumber))
			return;

		if(preferenceDoesntExist(currentPlayerPreferences, p))
			currentPlayerPreferences.add(p);
	}

	public boolean preferenceIsntValid(Preference p) {
		if(p.getIssue1() == -1)
			return true;

		if(p.getIssue2() == -1 && p.getRelation() != Relation.BEST && p.getRelation() != Relation.WORST)
			return true;

		return false;
	}

	private boolean playerLiedInTheCurrentGame(int gameNumber) {
		if(gameNumber == 1)
			return firstGameInfo.getGameLiesPercent() == 1f;

		if(gameNumber == 2)
			return secondGameInfo.getGameLiesPercent() == 1f;

		return thirdGamePlayerLies;
	}

	/**
	 * Removes the 0th element in the preferences queue.
	 * @return the preference removed, or throws IndexOutOfBoundException 
	 */
	public Preference dequeuePref()
	{
		return currentPlayerPreferences.remove(0);
	}

	/**
	 * Returns the value of an offer with respect to the caller. 
	 * @param o the offer
	 * @return the total value (how many points the agent will get)
	 */
	public int myActualOfferValue(Offer o) 
	{
		return currentGameAgentUtils.myActualOfferValue(o);
	}

	/**
	 * Returns the VH value of an ordering (of preferences?).
	 * @param o the ordering
	 * @return the total value
	 */
	public int myActualOrderValue(ArrayList<Integer> o) 
	{
		return currentGameAgentUtils.myActualOrderValue(o);
	}

	/**
	 * Check to see if the offer is a full offer.
	 * @param o the offer
	 * @return is full offer
	 */
	public boolean isFullOffer(Offer o)
	{
		return currentGameAgentUtils.isFullOffer(o);
	}

	/**
	 * Returns the normalized ordering of VH preferences (e.g., a point value of {3, 7, 2} would return {2, 1, 3}), with 1 being the highest
	 * @return an ArrayList of preferences
	 */
	public ArrayList<Integer> getMyOrdering() 
	{
		return currentGameAgentUtils.getMyOrdering();
	}

	/**
	 * Returns the expected value for this agent's adversary on an offer for a given ordering of preferences.
	 * @param o the offer
	 * @param ordering the ordering
	 * @return the total value
	 */
	public int adversaryValue(Offer o, ArrayList<Integer> ordering) 
	{
		return currentGameAgentUtils.adversaryValue(o, ordering);
	}

	/**
	 * Returns the maximum possible value for an adversary on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	public int adversaryValueMax(Offer o)
	{
		return currentGameAgentUtils.adversaryValueMax(o);
	}

	public int getMyPresentedBATNA() {
		return currentGameAgentUtils.myPresentedBATNA;
	}

	/**
	 * Returns the minimum possible value for an adversary on an offer for all current orderings of preferences.
	 * @param o the offer
	 * @return the total value
	 */
	public int adversaryValueMin(Offer o)
	{
		return currentGameAgentUtils.adversaryValueMin(o);
	}

	/***
	 * Finds the adversary's highest ranked item in the most ideal ordering
	 * @return index of the best item (for opposing agent)
	 */
	protected int findAdversaryIdealBest()
	{
		return currentGameAgentUtils.findAdversaryIdealBest();
	}

	/***
	 * Finds the adversary's second highest ranked item in the most ideal ordering
	 * @return index of the second best item (for opposing agent)
	 */
	protected int findAdversaryIdealSecondBest()
	{
		return currentGameAgentUtils.findAdversaryIdealSecondBest();
	}

	/***
	 * Finds the adversary's lowest ranked item in the most ideal ordering
	 * @return index of the worst item (for opposing agent)
	 */
	protected int findAdversaryIdealWorst(GameSpec game)
	{
		return currentGameAgentUtils.findAdversaryIdealWorst(game);
	}

	/**
	 * Returns the last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	public Event lastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		return currentGameAgentUtils.lastEvent(history, type);
	}

	/**
	 * Returns the last event of type type that was received, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event lastEventReceived(LinkedList<Event> history, Event.EventClass type)
	{
		return currentGameAgentUtils.lastEventReceived(history, type);
	}

	/**
	 * Returns the second to last event of type type, or null if nothing found.
	 * @param history the history to search
	 * @param type the type of EventClass to search for
	 * @return the event found, or null
	 */
	protected Event secondLastEvent(LinkedList<Event> history, Event.EventClass type)
	{
		return currentGameAgentUtils.secondLastEvent(history, type);
	}

	/**
	 * Eliminates invalid orderings by looking at preferences, the oldest ones first.
	 * @param currentGameCount 
	 * @return true if there are no valid orderings, false otherwise
	 */
	public boolean reconcileContradictions(int currentGameCount)
	{
		if(currentGameAgentUtils.reconcileContradictions()) {
			switch(currentGameCount) {
			case 1:
				firstGameInfo.playerLied();
				break;
			case 2:
				secondGameInfo.playerLied();
				break;
			default:
				thirdGamePlayerLies = true;
			}

			return true;
		}

		return false;
	}

	/**
	 * Finds the BATNA of an agent, takes into consideration whether it's lying or not
	 * @param game
	 * @param lieThreshold the gauge of how much an agent exaggerates its BATNA
	 * @param liar whether or not the agent actually wants to lie 
	 * @return the agent's BATNA, either true or a lie.
	 */
	public int getLyingBATNA(GameSpec game, double lieThreshold, boolean liar)
	{	
		return currentGameAgentUtils.getLyingBATNA(game, lieThreshold, liar);
	}

	/**
	 * Finds the ordering among possible orderings that is most/least different than the VH's ordering, based on the value of isFixedPie (set by separate method).
	 * @return the chosen ordering.
	 */
	public ArrayList<Integer> getMinimaxOrdering()
	{
		return currentGameAgentUtils.getMinimaxOrdering();
	}

	/***
	 * Finds the maximum amount of total points to be had in a certain game (i.e., agent's score if they received every item).
	 * @return max possible points
	 */
	public int getMaxPossiblePoints() 
	{
		return currentGameAgentUtils.getMaxPossiblePoints();
	}

	/**
	 * determines if a pair of given BATNA's are in conflict, i.e. an offer doesn't exist to satisfy both BATNAs.
	 * @param agentBATNA		can be either the true BATNA, or the presented BATNA, depending on the circumstances
	 * @param adversaryBATNA	this should always be the stored value of the opponent's BATNA.
	 * @return true if there is a conflict, false otherwise.
	 */
	public boolean conflictBATNA(int agentBATNA, int adversaryBATNA) 
	{
		return currentGameAgentUtils.conflictBATNA(agentBATNA, adversaryBATNA);
	}

	/**
	 * determines if a pair of given BATNA's are in conflict, i.e. an offer doesn't exist to satisfy both BATNAs.
	 * If the BATNAs conflict, it returns a decreased value for the passed in BATNA.
	 * @param vhPresentedBATNA			can be either the true BATNA, or the presented BATNA, depending on the circumstances
	 * @return an updated BATNA for the VH, possibly lower, if the opponent and VH BATNAs conflict. Always at least the actual VH BATNA.
	 */
	public int lowerBATNA(int vhPresentedBATNA) 
	{
		return currentGameAgentUtils.lowerBATNA(vhPresentedBATNA, competitive);
	}

	/***
	 * Helper to easily return expected/predicted value of an offer for an agent's adversary
	 * @param o an offer to look at
	 * @return the predicted value of the offer to the adversary.
	 */
	public int getAdversaryValue(Offer o) 
	{
		return currentGameAgentUtils.getAdversaryValue(o);
	}

	/***
	 * Getter for the agent's ID (0 for user, 1 for computer agent)
	 * @return current agent's ID 
	 */
	public int getID() 
	{
		return agent.getID();
	}

	/***
	 * Finds the position on the board (1 through num issues) of an item with a particular ranking for current agent
	 * @param game
	 * @param num the ranking of the item within an ordering (i.e., 1 means best, 2 means second best, etc.)
	 * @return the index of the item
	 */
	public int findMyItemIndex(GameSpec game, int num) 
	{
		return currentGameAgentUtils.findMyItemIndex(game, num);
	}

	/***
	 * Finds the position on the board (1 through num issues) of an item with a particular ranking for the agent's adversary.
	 * @param game
	 * @param num the ranking of the item within an ordering (i.e., 1 means best, 2 means second best, etc.)
	 * @return the index of the item
	 */
	public int findAdversaryItemIndex(GameSpec game, int num) 
	{
		return currentGameAgentUtils.findAdversaryItemIndex(game, num);
	}

	/**
	 * Finds the name of an item that's ranked a certain way by the agent.
	 * @param order The rank of the item.
	 * @param game The GameSpec being used.
	 * @return the name of the item the agent ranks according to the given order.
	 */
	String findMyItem(int order, GameSpec game)
	{
		return currentGameAgentUtils.findMyItem(order, game);
	}

	/***
	 * Finds a random, accurate preference
	 * @return random preference
	 */
	public Preference randomPref() 
	{
		return currentGameAgentUtils.randomPref();
	}

	/**
	 * Determines if there is a "particularly" valuable item this time around.
	 * @return a yes or no answer to that question
	 */
	public boolean isImportantGame()
	{
		return currentGameAgentUtils.isImportantGame();
	}

	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent.  Can be negative.  Persists across games.
	 * @return the ledger
	 */
	public int getLedger()
	{
		return ((MyCoreAgent) this.agent).getLedger();
	}

	/**
	 * Returns a simple int representing the internal "ledger" of favors done for the agent, including all pending values.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	protected int getTotalLedger()
	{
		return ((MyCoreAgent) this.agent).getTotalLedger();
	}

	/**
	 * Returns a simple int representing the potential "ledger" of favors verbally agreed to.  Can be negative.  Does not persist across games.
	 * @return the ledger
	 */
	public int getVerbalLedger()
	{
		return ((MyCoreAgent) this.agent).getVerbalLedger();
	}

	/**
	 * Allows you to modify the agent's internal "ledger" of favors done for it.  
	 * @param increment value (negative ok)
	 */
	public void modifyVerbalLedger(int increment)
	{
		((MyCoreAgent) this.agent).modifyVerbalLedger(increment);
	}

	/**
	 * Allows you to modify the agent's internal "ledger" of favors done for it.  
	 * @param increment value (negative ok)
	 */
	public void modifyOfferLedger(int increment)
	{
		((MyCoreAgent) this.agent).modifyOfferLedger(increment);
	}

	public LinkedList<Event> getSecondGameGreetMessages() {
		boolean didGetGreatDeal = firstGameInfo.gotGreatDeal();

		return eventsBasedOnCooperationBehavior(didGetGreatDeal, getFirstGameCooperationPoints(), getFirstGamePolitePoints(), false);
	}

	private void dealWithVeryPolite(PlayerCooperation cooperation, LinkedList<Event> events, boolean gotGreatDeal, boolean thirdGame) {
		if(gotGreatDeal)
			dealWithVeryPoliteGreatDeal(cooperation, events, thirdGame);
		else
			dealWithVeryPoliteNotGreatDeal(cooperation, events, thirdGame);
	}

	private void dealWithVeryPoliteNotGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression = null;
		Event preferenceEvent = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message = "You lied to me about your preferences before, I hope you will be more honest in this game...";

			if(thirdGame)
				manOfMyWord();

			break;
		case NOT_COOPERATIVE:
		case NEUTRAL:
			startExpression = getHappyStartExpression();
			message = "You were really nice to me before, ";

			if(thirdGame) {
				message += "I won't forget my promise, I owe you a favor for this game";
				message += getModifiedLedgerFromBehavior(-1);
			}
			else {
				message += "as a token of gratitude, I will tell you about one of my preferences:";
				preferenceEvent = getRandomPreference();
			}

			break;
		default:
			startExpression = getHappyStartExpression();
			message = "You were really nice and cooperative before, so as a token of gratitude, I will do you a favor.";
			message += getModifiedLedgerFromBehavior(-1);
		}

		sendEvents(events, message, preferenceEvent, startExpression);
	}

	private void dealWithVeryPoliteGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games! You made it clear that you cannot be trusted.." 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR: 
			startExpression = getNeutralStartExpression();

			message = "You were very polite and I received a great deal from you, but you still lied to me about your preferences. "
					+ "Hope you will be more honest in the upcoming game.";

			if(thirdGame) 
				message += manOfMyWord();
			break;
		default:
			message = "You were very polite and I received a great deal from you, ";
			message += makeAFavor(thirdGame);
		}

		sendEvents(events, message, null, startExpression);
	}

	private String makeAFavor(boolean thirdGame) {
		String message;

		if(thirdGame)
			message = "I haven't forgotten my promise, I owe you a favor for this game.";
		else
			message = " so as a token of gratitude, I will do you a favor.";

		return message + getModifiedLedgerFromBehavior(-1);
	}

	private Event getNeutralStartExpression() {
		String neutralExpression = ((MyCoreAgent) agent).neutralExpression();

		return getStartExpression(neutralExpression);
	}

	private void dealWithPolite(PlayerCooperation cooperation, LinkedList<Event> events, boolean gotGreatDeal, boolean thirdGame) {
		if(gotGreatDeal)
			dealWithPoliteGreatDeal(cooperation, events, thirdGame);
		else
			dealWithPoliteNotGreatDeal(cooperation, events, thirdGame);
	}

	private void dealWithPoliteGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message = "You were polite and I received a great deal from you, but you still lied to me about your preferences. "
					+ "Hope you will be more honest in the upcoming game";

			if(thirdGame)
				message += manOfMyWord();

			break;
		default:
			startExpression = getHappyStartExpression();
			message = "You were polite and I received a great deal from you, ";

			makeAFavor(thirdGame);
		}

		sendEvents(events, message, null, startExpression);
	}

	private void dealWithPoliteNotGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event preferenceEvent = null;
		Event startExpression = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message = "You lied to me about your preferences before .. Could you tell me about your real preferences now?";

			if(thirdGame)
				message += manOfMyWord();

			agentOwePreferences--;
			break;
		case NOT_COOPERATIVE:
			startExpression = getSadStartExpression();
			message = "I Hope you will be more cooperative in this game than before...";

			if(thirdGame)
				message += manOfMyWord();

			break;
		case NEUTRAL:
			startExpression = getHappyStartExpression();
			message = "You were nice to me before, ";

			if(thirdGame) {
				message += "I won't forget my promise. I owe you a favor for this game.";
				message += getModifiedLedgerFromBehavior(-1);
			}
			else {
				message += "as a token of gratitude, I will tell you about one of my preferences:";
				preferenceEvent = getRandomPreference();
			}

			break;
		default:
			startExpression = getHappyStartExpression();
			message = "You were nice and cooperative before, ";

			makeAFavor(thirdGame);
		}

		sendEvents(events, message, preferenceEvent, startExpression);
	}

	private Event getHappyStartExpression() {
		String happyExpression = ((MyCoreAgent) agent).happyExpression();

		return getStartExpression(happyExpression);
	}

	private Event getSadStartExpression() {
		String sadExpression = ((MyCoreAgent) agent).sadExpression();

		return getStartExpression(sadExpression);
	}

	private Event getStartExpression(String expression) {
		GameSpec game = currentGameAgentUtils.getGame();

		return new Event(this.getID(), Event.EventClass.SEND_EXPRESSION, expression, 8000, (int) (700*game.getMultiplier()));
	}

	private Event getRandomPreference() {
		return ((MyCoreAgent) agent).getRandomPreference();
	}

	private void sendEvents(LinkedList<Event> events, String message, Event preferenceEvent, Event expression) {
		GameSpec game = currentGameAgentUtils.getGame();
		Event messageEvent = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.NONE, message, (int) (2000*game.getMultiplier()));

		if(expression != null)
			events.add(expression);

		events.add(messageEvent);

		if(preferenceEvent != null) {
			agentOwePreferences++;

			String requestMessage = "Can you also send me one of your preferences now?";
			Event requestEvent = new Event(this.getID(), Event.EventClass.SEND_MESSAGE, Event.SubClass.PREF_REQUEST, requestMessage, (int) 
					(2000*game.getMultiplier()));

			events.add(preferenceEvent);
			events.add(requestEvent);
		}
	}

	private void dealWithNotPolite(PlayerCooperation cooperation, LinkedList<Event> events, boolean gotGreatDeal, boolean thirdGame) {
		if(gotGreatDeal) 
			dealWithNotPoliteGreatDeal(cooperation, events, thirdGame);
		else 
			dealWithNoPoliteNotGreatDeal(cooperation, events, thirdGame);
	}

	private void dealWithNoPoliteNotGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression = null;
		Event preferenceEvent = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message =  "You lied to me about your preferences before.. as far as I concern, ";

			if(thirdGame) 
				message += "I don't owe you a favor for the third game anymore..";
			else
				message += playerOweAfavor(); 

			break;
		case NOT_COOPERATIVE:
			startExpression = getSadStartExpression();
			message =  "You weren't very polite or cooperaive before.. I'm willing to forget about it if you'll tell me about "
					+ "your preferences..";

			if(thirdGame)
				message += manOfMyWord();

			agentOwePreferences--;
			break;
		case NEUTRAL:
			startExpression = getSadStartExpression();
			message =  "You weren't really polite to me before, hope you will be more polite in the upcoming game";

			if(thirdGame)
				message += manOfMyWord();

			break;
		default:
			startExpression = getHappyStartExpression();

			if(thirdGame) {
				message = "You were cooperative before, and I won't forget my promise. I owe you a favor for this game";
				message += getModifiedLedgerFromBehavior(1);
			}
			else {
				message = "I received a great deal from you, as a token of gratitude, I will tell you about one of my preferences:";
				preferenceEvent = getRandomPreference();
			}
		}

		sendEvents(events, message, preferenceEvent, startExpression);
	}

	private void dealWithNotPoliteGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression = null;
		Event preferenceEvent = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message = "I received a great deal from you, but you still lied to me about your preferences and wasn't polite. "
					+ "I'm willing to forget about it if you'll tell me about your preferences..";

			if(thirdGame) 
				message += manOfMyWord();

			agentOwePreferences--;
			break;
		default:
			startExpression = getHappyStartExpression();

			if(thirdGame) {
				message = "I received a great deal from you, and I won't forget my promise. I owe you a favor for this game";
				message += getModifiedLedgerFromBehavior(1);
			}
			else {
				message = "I received a great deal from you, as a token of gratitude, I will tell you about one of my preferences:";
				preferenceEvent = getRandomPreference();
			}
		}

		sendEvents(events, message, preferenceEvent, startExpression);
	}

	private String getModifiedLedgerFromBehavior(int favors) {
		modifyLedger(favors);

		if(getLedger() > favors) {
			if(favors > 0)
				return " And " + getLedger() + " favors in total!";
			else {
				if(getLedger() == -1)
					return " So in total, I owe you one favor";

				if(getLedger() == 0) 
					return " So in total. Neither of us owes favors to another";

				if(getLedger() == 1)
					return " So in total, you owe me one favor";

				if(getLedger() > 1)
					return " So in total, you owe me " + getLedger() + " favors";
			}
		}

		return "";
	}

	private void modifyLedger(int favors) {
		((MyCoreAgent) this.agent).modifyLedgerAfterGame(favors);
	}

	private void dealWithRude(PlayerCooperation cooperation, LinkedList<Event> events, boolean gotGreatDeal, boolean thirdGame) {
		if(gotGreatDeal) 
			dealWithRudeGreatDeal(cooperation, events, thirdGame);
		else 
			dealWithRudeAndNotGreatDeal(events, cooperation, thirdGame);
	}

	private void dealWithRudeAndNotGreatDeal(LinkedList<Event> events, PlayerCooperation cooperation,
			boolean thirdGame) {
		Event startExpression = null;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences before. In addition to that, you were also rude. as far as I concern, ";

			if(thirdGame) 
				message += "I don't owe you a favor for the third game anymore..";
			else 
				message += playerOweTwoFavors();

			break;
		case NOT_COOPERATIVE:
			startExpression = getSadStartExpression();
			message = "You were rude to me before. In addition to that, you weren't really cooperaitve. as far as I concern, ";

			if(thirdGame)
				message += "I don't owe you a favor for the third game anymore..";
			else 
				message += playerOweAfavor();

			break;
		case NEUTRAL:
			startExpression = getSadStartExpression();
			message = "You were rude to me before. I'm willing to forget about it if you'll tell me about one of your preferences..";

			if(thirdGame) 
				message += manOfMyWord();


			agentOwePreferences--;
			break;
		default:
			startExpression = getSadStartExpression();
			message = "You weren't really polite to me before, I hope you will be more polite in the upcoming game.";

			if(thirdGame) 
				message += manOfMyWord();
		}

		sendEvents(events, message, null, startExpression);
	}


	private String playerOweTwoFavors() {
		String message = "you owe me two small favors.";

		return message + getModifiedLedgerFromBehavior(2);
	}

	private String manOfMyWord() {
		String message = " Don't worry, I'm a man of my word. I will give you the favor I promised to give you.";

		return message + getModifiedLedgerFromBehavior(-1);
	}

	private void dealWithRudeGreatDeal(PlayerCooperation cooperation, LinkedList<Event> events, boolean thirdGame) {
		Event startExpression;
		String message = null;

		switch(cooperation) {
		case SUPER_LIAR:
			thirdGamePlayerLies = true;

			startExpression = getAngryStartExpression();
			message = "You lied to me about your preferences in the previous two games!" 
					+ "as far as I concern, I don't owe you a favor for the third game anymore..";
			break;
		case LIAR:
			startExpression = getSadStartExpression();
			message = "I received a great deal from you, but you still lied to me about your preferences and you were rude. as far as I concern, ";

			if(thirdGame)
				message += "I don't owe you a favor for the third game anymore...";
			else 
				message += playerOweAfavor();
		default:
			startExpression = getNeutralStartExpression();
			message = "I received a great deal from you, but you still were rude. I hope you will be more polite in the upcoming game.";

			if(thirdGame) 
				message += manOfMyWord();
		}

		sendEvents(events, message, null, startExpression);
	}

	private String playerOweAfavor() {
		String message = "you owe me a favor...";

		return message + getModifiedLedgerFromBehavior(1);
	}

	private Event getAngryStartExpression() {
		String angryExpression = ((MyCoreAgent) agent).angryExpression();

		return getStartExpression(angryExpression);
	}

	private PlayerBehavior getFirstGamePolitePoints() {
		return firstGameInfo.behavior();
	}

	private PlayerCooperation getFirstGameCooperationPoints() {
		return firstGameInfo.cooperative();
	}

	public void addPlayerThreat() {
		firstGameInfo.addPlayerThreat();
	}

	public void addAngryExpression() {
		firstGameInfo.addPlayerAngryExpression();
	}

	public int getMyRow() {
		return currentGameAgentUtils.myRow;
	}

	public int getAdversaryBATNA() {
		return currentGameAgentUtils.adversaryBATNA;
	}

	public int getAdversaryRow() {
		return currentGameAgentUtils.adversaryRow;
	}

	public void setMyPresentedBATNA(int lowerBATNA) {
		currentGameAgentUtils.myPresentedBATNA = lowerBATNA;
	}

	public void setAdersaryBATNA(int value) {
		currentGameAgentUtils.adversaryBATNA = value;
	}

	public void setAgentUtilsExtension(MyAgentUtilsExtension aue) {
		currentGameAgentUtils = aue;
	}

	public void resetAgentUtilsExtension(GameSpec game, int gameCount) {
		currentGameAgentUtils = new MyAgentUtilsExtension(agent);

		configureGame(game, gameCount);
	}

	public boolean preferenceDoesntExist(LinkedList<Preference> prefs, Preference p) {
		for (Preference preference : prefs) {
			if(preferencesAreEqual(preference, p))
				return false;
		}

		return true;
	}

	private boolean preferencesAreEqual(Preference preference, Preference p) {
		if(exactlyTheSame(preference, p))
			return true;

		switch(p.getRelation()) {
		case WORST:
		case BEST:
			return false;
		case LESS_THAN:
			return checkReverseEquality(preference, p, Relation.GREATER_THAN);
		case GREATER_THAN:
			return checkReverseEquality(preference, p, Relation.LESS_THAN);
		case EQUAL:
			return checkReverseEquality(preference, p, Relation.EQUAL);
		}

		return false;
	}

	private boolean exactlyTheSame(Preference preference, Preference p) {
		if(preference.getIssue1() != p.getIssue1())
			return false;

		if(preference.getIssue2() != p.getIssue2())
			return false;

		return preference.getRelation().equals(p.getRelation());
	}

	private boolean checkReverseEquality(Preference preference, Preference p, Relation relation) {
		if(preference.getRelation() == relation) {
			if(preference.getIssue1() == p.getIssue2() && preference.getIssue2() == p.getIssue1())
				return true;
		}

		return false;
	}

	public void addAgentPreference(Preference preference) {
		if(preference == null || preferenceIsntValid(preference))
			return;

		if(preferenceDoesntExist(currentAgentPreferences, preference))
			currentAgentPreferences.add(preference);
	}

	public int getAgentPreferencesSize() {
		return currentAgentPreferences.size();
	}

	public int getPlayerPreferencesSize() {
		return currentPlayerPreferences.size();
	}

	public LinkedList<Event> getThirdGameGreetMessages() {
		PlayerCooperation cooperation = GameInfo.getTotalCooperationPoints(firstGameInfo.cooperative(), secondGameInfo.cooperative());
		PlayerBehavior behavior = GameInfo.getTotalBehaviorPoints(firstGameInfo.behavior(), secondGameInfo.behavior());
		boolean gotGreatDeal = secondGameInfo.gotGreatDeal();

		return eventsBasedOnCooperationBehavior(gotGreatDeal, cooperation, behavior, true);
	}

	private LinkedList<Event> eventsBasedOnCooperationBehavior(boolean gotGreatDeal, PlayerCooperation cooperation, PlayerBehavior behavior, 
			boolean thirdGame) {
		LinkedList<Event>events = new LinkedList<Event>();

		switch(behavior) {
		case RUDE:
			dealWithRude(cooperation, events, gotGreatDeal, thirdGame);
			break;
		case NOT_POLITE:
			dealWithNotPolite(cooperation, events, gotGreatDeal, thirdGame);
			break;
		case POLITE:
			dealWithPolite(cooperation, events, gotGreatDeal, thirdGame);
			break;
		default:
			dealWithVeryPolite(cooperation, events, gotGreatDeal, thirdGame);
		}

		return events;
	}

	public void resetPlayerPreferences() {
		currentPlayerPreferences = new LinkedList<Preference>();
	}

	public boolean didPlayerLie(int currentGameCount) {
		return playerLiedInTheCurrentGame(currentGameCount);
	}

	public boolean didPlayerLie() {
		return playerLiedInTheCurrentGame(((MyCoreAgent) agent).getGameCount());
	}

	public int getCurrentGameAgentPrefsSize() {
		return currentAgentPreferences.size() - agentOwePreferences;
	}
}
