package edu.usc.ict.iago.agent;

import edu.usc.ict.iago.utils.ExpressionPolicy;
import edu.usc.ict.iago.utils.History;
/**
 * This Expression policy is a non-policy, that never returns expressions.
 * @author jmell
 *
 */
public class IAGODefaultExpression extends IAGOCoreExpression implements ExpressionPolicy {

	@Override
	public String getExpression(History history) 
	{
		return null;
	}

	@Override
	public String getSemiFairEmotion() {
		return null;
	}

	@Override
	public String getFairEmotion() {
		return null;
	}

	@Override
	public String getUnfairEmotion() {
		return null;
	}

	@Override
	public String getAngryEmotion() {
		// TODO Auto-generated method stub
		return null;
	}

}
