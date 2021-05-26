package intervalanalysis.intervalintra.analyses;

import soot.Unit;

import java.util.List;

/**
 * Provides an interface for querying the expressions that are very busy
 * before and after a unit in a method.
 * @author �rni Einarsson
 */
public interface InterValAnalyses {
    /**
     *   Returns the list of expressions that are very busy before the specified
     *   Unit. 
     *   @param s the Unit that defines this query.
     *   @return a list of expressions that are busy before the specified unit in the method.
     */
    public List getIntervalsBefore(Unit s);

    /**
     *   Returns the list of expressions that are very busy after the specified
     *   Unit. 
     *   @param s the Unit that defines this query.
     *   @return a list of expressions that are very busy after the specified unit in the method.
     */
    public List getIntervalsAfter(Unit s);

    public List getIntervalsBranchAfter(Unit s);
}
