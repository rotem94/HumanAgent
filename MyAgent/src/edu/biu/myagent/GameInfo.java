package edu.biu.myagent;

import java.util.LinkedList;
import edu.usc.ict.iago.utils.Preference;

public class GameInfo {
	public enum PlayerBehavior {
		VERY_POLITE, POLITE, NOT_POLITE, RUDE
	}

	public enum PlayerCooperation {
		COOPERATIVE, NEUTRAL, NOT_COOPERATIVE, LIAR, SUPER_LIAR
	}

	private static final int DEFAULT_POLITE_POINTS = 2;
	private static final int THREATS_EXTRA_NEGATIVE_PERCENT = 3;
	private static final float LIE_PERCENT_THRESHOLD = 0.9f;
	private static final float PLAYER_PREFS_BONUS_PERCENT = 1.5f;
	private static final float LIED_POINTS = -1000f;
	private static final float COOPERATIVE_POINTS_THRESHOLD = 1.5f;
	private static final float NEUTRAL_POINTS_THRESHOLD = 0;
	private static final float VERY_POLITE_POINTS_THRESHOLD = 2;
	private static final float POLIE_POINTS_THRESHOLD = 0;
	private static final float NOT_POLITE_POINTS_THRESHOLD = -5;
	private static final float GREAT_DEAL_PERCENT_THRESHOLD = 85;

	private LinkedList<Preference> gamePlayerPreferences;
	private LinkedList<Preference> gameAgentPreferences;
	private int numberOfGameThreats;
	private int numberOfGameAngryFaces;
	private int allIssuesTotalPoints;
	private int agentIssuesTotalPoints;
	private int agentOwePreferencesInGame;
	private float gameLiesPercent;

	public GameInfo(int allIssuesTotalPoints, LinkedList<Preference> gamePlayerPreferences, LinkedList<Preference> gameAgentPreferences) {
		numberOfGameThreats = 0;
		numberOfGameAngryFaces = 0;
		agentIssuesTotalPoints = 0;
		agentOwePreferencesInGame = 0;
		gameLiesPercent = 0f;

		this.allIssuesTotalPoints = allIssuesTotalPoints;
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

	public int getPlayerPreferencesSize() {
		return gamePlayerPreferences.size();
	}

	public int getAgentPreferencesSize() {
		return gameAgentPreferences.size() - agentOwePreferencesInGame;
	}

	public int getAgentIssuesTotalPoints() {
		return agentIssuesTotalPoints;
	}

	public void setAgentIssuesTotalPoints(int agentIssuesTotalPoints) {
		this.agentIssuesTotalPoints = agentIssuesTotalPoints;
	}

	public PlayerCooperation cooperative() {
		float cooperationPoints;

		if(gameLiesPercent > LIE_PERCENT_THRESHOLD)
			cooperationPoints = LIED_POINTS;
		else {
			cooperationPoints = (float) (getPlayerPreferencesSize() * PLAYER_PREFS_BONUS_PERCENT) - 
					(float) (getAgentPreferencesSize());
		}

		if(cooperationPoints >= COOPERATIVE_POINTS_THRESHOLD)
			return PlayerCooperation.COOPERATIVE;

		if(cooperationPoints >= NEUTRAL_POINTS_THRESHOLD)
			return PlayerCooperation.NEUTRAL;

		if(cooperationPoints > LIED_POINTS)
			return PlayerCooperation.NOT_COOPERATIVE;

		return PlayerCooperation.LIAR;
	}

	public PlayerBehavior behavior() {
		int politePoints =  DEFAULT_POLITE_POINTS - numberOfGameAngryFaces - THREATS_EXTRA_NEGATIVE_PERCENT * numberOfGameThreats;

		if(politePoints >= VERY_POLITE_POINTS_THRESHOLD)
			return PlayerBehavior.VERY_POLITE;

		if(politePoints >= POLIE_POINTS_THRESHOLD)
			return PlayerBehavior.POLITE;

		if(politePoints > NOT_POLITE_POINTS_THRESHOLD)
			return PlayerBehavior.NOT_POLITE;

		return PlayerBehavior.RUDE;
	}

	public boolean gotGreatDeal() {
		float percentPoints = (float) (agentIssuesTotalPoints) / (float) (allIssuesTotalPoints);

		return percentPoints >= (GREAT_DEAL_PERCENT_THRESHOLD / 100f);
	}

	public static PlayerCooperation getTotalCooperationPoints(PlayerCooperation firstGameCooperation, PlayerCooperation secondGameCooperation) {
		switch(firstGameCooperation) {
		case LIAR:
			return getTotalCooperationFirstLier(secondGameCooperation);
		case NOT_COOPERATIVE:
			return getTotalCooperationFirstNotCooperative(secondGameCooperation);
		case NEUTRAL:
			return secondGameCooperation;
		default:
			return getTotalCooperationFirstCooperative(secondGameCooperation);
		}
	}

	private static PlayerCooperation getTotalCooperationFirstCooperative(PlayerCooperation secondGameCooperation) {
		switch(secondGameCooperation) {
		case LIAR:
			return PlayerCooperation.LIAR;
		case NOT_COOPERATIVE:
			return PlayerCooperation.NEUTRAL;
		default:
			return PlayerCooperation.COOPERATIVE;
		}
	}

	private static PlayerCooperation getTotalCooperationFirstNotCooperative(PlayerCooperation secondGameCooperation) {
		switch(secondGameCooperation) {
		case LIAR:
			return PlayerCooperation.LIAR;
		case NOT_COOPERATIVE:
			return PlayerCooperation.NOT_COOPERATIVE;
		default:
			return PlayerCooperation.NEUTRAL;
		}
	}

	private static PlayerCooperation getTotalCooperationFirstLier(PlayerCooperation secondGameCooperation) {
		switch(secondGameCooperation) {
		case LIAR:
			return PlayerCooperation.SUPER_LIAR;
		case NOT_COOPERATIVE:
		case NEUTRAL:
			return PlayerCooperation.NOT_COOPERATIVE;
		default:
			return PlayerCooperation.NEUTRAL;
		}
	}

	public static PlayerBehavior getTotalBehaviorPoints(PlayerBehavior firstGameBehavior, PlayerBehavior secondGameBehavior) {
		switch(firstGameBehavior) {
		case RUDE:
			return getTotalBehaviorFirstRude(secondGameBehavior);
		case NOT_POLITE:
			return getTotalBehaviorFirstNotPolite(secondGameBehavior);
		case POLITE:
			return getTotalBehaviorFirstPolite(secondGameBehavior);
		default:
			return getTotalBehaviorFirstVeryPolite(secondGameBehavior);
		}
	}

	private static PlayerBehavior getTotalBehaviorFirstVeryPolite(PlayerBehavior secondGameBehavior) {
		switch(secondGameBehavior) {
		case RUDE:
			return PlayerBehavior.NOT_POLITE;
		case NOT_POLITE:
			return PlayerBehavior.POLITE;
		default:
			return PlayerBehavior.VERY_POLITE;
		}
	}

	private static PlayerBehavior getTotalBehaviorFirstPolite(PlayerBehavior secondGameBehavior) {
		switch(secondGameBehavior) {
		case RUDE:
		case NOT_POLITE:
			return PlayerBehavior.NOT_POLITE;
		case POLITE:
			return PlayerBehavior.POLITE;
		default:
			return PlayerBehavior.VERY_POLITE;
		}
	}

	private static PlayerBehavior getTotalBehaviorFirstNotPolite(PlayerBehavior secondGameBehavior) {
		switch(secondGameBehavior) {
		case RUDE:
		case NOT_POLITE:
			return PlayerBehavior.RUDE;
		default:
			return PlayerBehavior.POLITE;
		}
	}

	private static PlayerBehavior getTotalBehaviorFirstRude(PlayerBehavior secondGameBehavior) {
		switch(secondGameBehavior) {
		case RUDE:
			return PlayerBehavior.RUDE;
		case NOT_POLITE:
			return PlayerBehavior.NOT_POLITE;
		default:
			return PlayerBehavior.POLITE;
		}
	}

	public void setAgentOwesPreferences(int agentOwePreferences) {
		this.agentOwePreferencesInGame = agentOwePreferences;
	}
}
