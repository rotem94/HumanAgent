package edu.biu.myagent;

import java.util.LinkedList;
import edu.usc.ict.iago.utils.Preference;

public class GameInfo {
	public enum PlayerBehavior {
		VERY_POLITE, POLITE, NOT_POLITE, RUDE
	}

	public enum PlayerCooperation {
		COOPERATIVE, NEUTRAL, NOT_COOPERATIVE, LIER
	}

	private static final int DEFAULT_POLITE_POINTS = 2;
	private static final int THREATS_EXTRA_NEGATIVE_PERCENT = 3;
	private static final float LIE_PERCENT_THRESHOLD = 0.9f;
	private static final float PLAYER_PREFS_BONUS_PERCENT = 1.5f;
	private static final float LIED_POINTS = -1000f;

	private LinkedList<Preference> gamePlayerPreferences;
	private LinkedList<Preference> gameAgentPreferences;
	private int numberOfGameThreats;
	private int numberOfGameAngryFaces;
	private float gameLiesPercent;
	private LinkedList<GameIssue> allIssues;
	private LinkedList<GameIssue> agentIssues;

	public GameInfo(LinkedList<GameIssue> allIssues, LinkedList<Preference> gamePlayerPreferences, LinkedList<Preference> gameAgentPreferences) {
		numberOfGameThreats = 0;
		numberOfGameAngryFaces = 0;
		gameLiesPercent = 0f;

		this.allIssues = allIssues;
		this.gamePlayerPreferences = gamePlayerPreferences;
		this.gameAgentPreferences = gameAgentPreferences;

	}

	public float getGameLiesPercent() {
		return gameLiesPercent;
	}

	public void setGameLiesPercent(float gameLiesPercent) {
		this.gameLiesPercent = gameLiesPercent;
	}

	public void addPlayerThreat() {
		numberOfGameThreats++;
	}

	public void addPlayerAngryExpression() {
		numberOfGameAngryFaces++;
	}

	public void playerLied() {
		setGameLiesPercent(1f);
	}

	public void setAgentIssues(LinkedList<GameIssue> agentIssues) {
		this.agentIssues = agentIssues;
	}

	public int getPlayerPreferencesSize() {
		return gamePlayerPreferences.size();
	}

	public int getAgentPreferencesSize() {
		return gameAgentPreferences.size();
	}

	public PlayerCooperation cooperative() {
		float cooperationPoints;

		if(gameLiesPercent > LIE_PERCENT_THRESHOLD)
			cooperationPoints = LIED_POINTS;
		else 
			cooperationPoints = (float) (getPlayerPreferencesSize() * PLAYER_PREFS_BONUS_PERCENT) - (float) (getAgentPreferencesSize());

		if(cooperationPoints >= 3)
			return PlayerCooperation.COOPERATIVE;

		if(cooperationPoints > 0)
			return PlayerCooperation.NEUTRAL;

		if(cooperationPoints > LIED_POINTS)
			return PlayerCooperation.NOT_COOPERATIVE;

		return PlayerCooperation.LIER;
	}

	public PlayerBehavior behavior() {
		int politePoints =  DEFAULT_POLITE_POINTS - numberOfGameAngryFaces - THREATS_EXTRA_NEGATIVE_PERCENT * numberOfGameThreats;

		if(politePoints >= 2)
			return PlayerBehavior.VERY_POLITE;

		if(politePoints >= 0)
			return PlayerBehavior.POLITE;

		if(politePoints > -5)
			return PlayerBehavior.NOT_POLITE;

		return PlayerBehavior.RUDE;
	}
}
