package edu.biu.myagent;

import edu.usc.ict.iago.utils.BehaviorPolicy;
import edu.usc.ict.iago.utils.History;
import edu.usc.ict.iago.utils.Offer;

public abstract class IAGOCoreBehavior implements BehaviorPolicy
{
	/**
	 * Update the internal representation of what offers are considered "firm" currently.
	 * @param update the total summed offer to save
	 */
	public abstract void updateAllocated (Offer update);
	
	/**
	 * Retrieves the internal representation of what offers are considered "firm" currently.
	 * @return the total summed offer
	 */
	public abstract Offer getAllocated ();
	
	/**
	 * Gets the offer that comes when you're nearly out of time.
	 * @param history the history to use
	 * @return the final offer
	 */
	public abstract Offer getFinalOffer(History history);
	
	/**
	 * Helper for adding an AgentUtilsExtension.
	 * @param utils the utils to add
	 */
	public abstract void setUtils(MyAgentUtils utils);

	/**
	 * Gets the offer that comes when you've been idle.
	 * @param history the history to use
	 * @return the idle offer
	 */
	public abstract Offer getTimingOffer(History history);

	/**
	 * Gets the offer that comes after the agent's adversary accepts an offer.
	 * @param history the history to use
	 * @return the followup offer
	 */
	public abstract Offer getAcceptOfferFollowup(History history);

	/**
	 * Gets that is proposed immediately once the game starts.
	 * @param history the history to use
	 * @return the first offer
	 */
	public abstract Offer getFirstOffer(History history);

	/**
	 * Gets the amount of points that the agent requires to be ahead of its opponent before accepting.
	 * @return the point margin (a negative means an agent won't accept offers that are worse for it than an opponent in any circumstance)
	 */
	public abstract int getAcceptMargin();

	/**
	 * Gets that is proposed after the agent's adversary rejects an offer.
	 * @param history the history to use
	 * @return the first offer
	 */
	public abstract Offer getRejectOfferFollowup(History history);

	/**
	 * Retrieves the internal representation of what the agent is maintaining as its heuristic currently.
	 * @return the total summed offer
	 */
	public abstract Offer getConceded();
	
	/**
	 * Returns the value of the agent belief, true if fixed pie, false if integrative
	 * @return the agent belief, true if fixed pie, false if integrative
	 */
	public boolean getAgentBelief()
	{
		return false;
	}
	
	/**
	 * Allows you to change the adverse Events counter, which weakens the agent's margin
	 * @param change the amount to change by
	 */
	public abstract void updateAdverseEvents (int change);
}
