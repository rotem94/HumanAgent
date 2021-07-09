package edu.biu.myagent;

import java.util.LinkedList;
import edu.usc.ict.iago.utils.Preference;

public class GameInfo {
	private LinkedList<Preference> gamePlayerPreferences;
	private LinkedList<Preference> gameAgentPreferences;
	private int numberOfGameThreats;
	private int numberOGameAngryFaces;
	private float gameLiesPercent;
	private LinkedList<GameIssue> allIssues;
	private LinkedList<GameIssue> agentIssues;

	public GameInfo(LinkedList<GameIssue> allIssues, LinkedList<Preference> gamePlayerPreferences, LinkedList<Preference> gameAgentPreferences) {
		numberOfGameThreats = 0;
		numberOGameAngryFaces = 0;
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
		numberOGameAngryFaces++;
	}

	public void playerLied() {
		setGameLiesPercent(1f);
	}

	public void setAgentIssues(LinkedList<GameIssue> agentIssues) {
		this.agentIssues = agentIssues;
	}
}
