package edu.biu.myagent;

import javax.websocket.Session;
import edu.usc.ict.iago.utils.GameSpec;

/**
 * @author mell
 * 
 */
public class MyAgent extends MyCoreAgent {

	/**
	 * @author mell
	 * Instantiates a new  VH.
	 *
	 * @param name: agent's name
	 * @param game: gamespec value
	 * @param session: the session
	 */
	public MyAgent(String name, GameSpec game, Session session)
	{
		super("AgentRotem", game, session, new MyBehavior(MyBehavior.LedgerBehavior.LIMITED), new MyExpressions(), 
				new MyMessagesBehavior(false, false, MyBehavior.LedgerBehavior.LIMITED, game));	

		super.safeForMultiAgent = true;
	}

	public String getArtName() {
		return "Rens";
	}

	public String agentDescription() {
		return "<h1>Rotem</h1><p>He is ready to nagotiate!</p>";
	}
}