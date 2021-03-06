package edu.biu.myagent;

import edu.usc.ict.iago.utils.ExpressionPolicy;

public abstract class IAGOCoreExpression implements ExpressionPolicy
{
	public abstract String getSemiFairEmotion();
	
	public abstract String getFairEmotion();
	
	public abstract String getUnfairEmotion();
	
	public abstract String getAngryEmotion();

	public abstract String getNeutralEmotion();
}
