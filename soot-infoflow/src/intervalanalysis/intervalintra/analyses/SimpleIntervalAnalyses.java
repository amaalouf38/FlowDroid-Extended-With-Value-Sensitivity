package intervalanalysis.intervalintra.analyses;

import intervalanalysis.flowsets.*;

import soot.*;
import soot.jimple.AddExpr;
import soot.jimple.SubExpr;
import soot.jimple.DivExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.MulExpr;
import soot.jimple.NumericConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.UnopExpr;
import soot.jimple.internal.AbstractNegExpr;


import soot.jimple.IfStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.LtExpr;
import soot.jimple.GtExpr;
import soot.jimple.LeExpr;
import soot.jimple.GeExpr;
import soot.jimple.EqExpr;
import soot.jimple.NeExpr;
import soot.jimple.AndExpr;
import soot.jimple.OrExpr;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;

import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JOrExpr;
import soot.jimple.internal.JAndExpr;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.Constant;


//import soot.toolkits.graph.DirectedGraph;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.util.Chain;
import soot.util.Numberable;

import java.util.*;

public class SimpleIntervalAnalyses implements InterValAnalyses {
    private Map unitToIntervalsAfter;
    private Map unitToIntervalsBefore;
    private Map unitToIntervalsBranchAfter;

    public SimpleIntervalAnalyses(SootMethod m) {


        Body b = m.retrieveActiveBody();
        UnitGraph graph = new ExceptionalUnitGraph(b);
        Collection<Loop> loops = computeLoops(b);
        Chain<Local> locals = b.getLocals();

        SimpleInterValAnalysesWorkList analysis = new SimpleInterValAnalysesWorkList(graph, locals, loops);

        unitToIntervalsAfter = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToIntervalsBefore = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToIntervalsBranchAfter = new HashMap(graph.size() * 2 + 1, 0.7f);

        Iterator unitIt = graph.iterator();

        while (unitIt.hasNext()) {
            Unit s = (Unit) unitIt.next();

            FlowSet set = (FlowSet) analysis.getFlowBefore(s);
            unitToIntervalsBefore.put(s,
                    Collections.unmodifiableList(set.toList()));


            set = (FlowSet) analysis.getFallFlowAfter(s);
            unitToIntervalsAfter.put(s,
                    Collections.unmodifiableList(set.toList()));

            List set2 = analysis.getBranchFlowAfter(s);
            unitToIntervalsBranchAfter.put(s,
                    Collections.unmodifiableList(set2));

        }
    }

    private Collection<Loop> computeLoops(Body b) {
        LoopFinder loopFinder = new LoopFinder();
        loopFinder.transform(b);

        Collection<Loop> loops = loopFinder.getLoops( b);
        return loops;
    }

    public List getIntervalsBefore(Unit s) {

        return (List) unitToIntervalsBefore.get(s);
    }

    public List getIntervalsAfter(Unit s) {

        return (List) unitToIntervalsAfter.get(s);
    }

    public List getIntervalsBranchAfter(Unit s) {

        return (List) unitToIntervalsBranchAfter.get(s);
    }
}


class SimpleInterValAnalysesWorkList extends ForwardBranchedFlowAnalysis {

    private FlowSet emptySet;
    private FlowSet bottomSet;
    private FlowSet topSet;

    private Chain<Local> locals;
    private Collection<Loop> loops;
    private int k = 100;

    public SimpleInterValAnalysesWorkList(UnitGraph g, Chain<Local> locals, Collection<Loop> loops) {

        super(g);

        this.locals = locals;
        this.loops = loops;
        emptySet = new ValueArraySparseSet();
        bottomSet = constructBottomSet();
        topSet = constructTopSet();

        doAnalysis();
    }

    protected FlowSet constructBottomSet() {
        FlowSet bott = emptySet.clone();
        for (Local l : locals) {
            if (Utility.isNumeric(l))
                bott.add(new InterVal(l, 0, 0, true));
        }
        return bott;

    }

    protected FlowSet constructTopSet() {
        FlowSet top = emptySet.clone();
        for (Local l : locals) {
            if (Utility.isNumeric(l))
                top.add(new InterVal(l, true));
        }
        return top;
    }


    @Override
    protected void merge(Object succNode,
                         Object in1,
                         Object in2,
                         Object out) {
        FlowSet inSet1 = (FlowSet) in1,
                inSet2 = (FlowSet) in2,
                outSet = (FlowSet) out;

        int countvisits = Utility.isLoop(loops, (Unit) succNode);

        System.out.println(((Unit) succNode).toString() + " countvisits " + countvisits);

        if (countvisits >= k)
            Utility.widen(inSet1, inSet2, outSet);
        else
            Utility.union(inSet1, inSet2, outSet);
    }


    @Override
    protected void merge(Object in1, Object in2, Object out) {
       /*FlowSet inSet1 = (FlowSet) in1,
                inSet2 = (FlowSet) in2,
                outSet = (FlowSet) out;

        SUtility.union(inSet1, inSet2, outSet);*/
    }

    @Override
    protected void copy(Object source, Object dest) {
        FlowSet srcSet = (FlowSet) source,
                destSet = (FlowSet) dest;
        srcSet.copy(destSet);
    }

    /**
     * Used to initialize the in and out sets for each node. In
     * our case we want to build up the sets as we go, so we
     * initialize with the empty set.
     * </p><p>
     * Note: If we had information about all the possible values
     * the sets could contain, we could initialize with that and
     * then remove values during the analysis.
     *
     * @return an empty set
     */
    @Override
    protected Object newInitialFlow() {
        return emptySet.clone();
    }

    /**
     * Returns a flow set representing the initial set of the entry
     * node. In our case the entry node is the last node and it
     * should contain the empty set.
     *
     * @return an empty set
     */
    @Override
    protected Object entryInitialFlow() {
        return topSet.clone();
    }

    /**
     * Adds to the out set the values that flow through the node
     * d from the in set.
     * </p><p>
     * This method has two phases, a kill phase and a gen phase.
     * The kill phase performs the following:<br />
     * out = (in - expressions containing a reference to any local
     * defined in the node) union out.<br />
     * The gen phase performs the following:<br />
     * out = out union binary operator expressions used in the
     * node.
     *
     * @param in   the in-set of the current node
     * @param node the current node of the control flow graph
     * @param out  the out-set of the current node
     */
    @Override
    //protected void flowThrough(Object in, Object node, Object out) {
    // normalFlow((FlowSet) in, (Unit) node, (FlowSet) out);
    protected void flowThrough(Object in, Unit node, List fallOut, List branchOuts) {
        FlowSet out = emptySet.clone();
        FlowSet outBranch = emptySet.clone();

        Unit u = (Unit) node;

        if (u instanceof AssignStmt) {

            Value v1 = ((AssignStmt) u).getLeftOp();

            if (v1 instanceof Local  && Utility.isNumeric((Local) v1)) {

                Value v2 = ((AssignStmt) u).getRightOp();
                InterVal v2lInterval = IntervalOfValue(v2, (FlowSet) in);

                v2lInterval.l = (Local) v1;
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                Utility.replaceIntervalForLocal((FlowSet) out, v2lInterval);
            } else {
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
            }

        } else if (u instanceof IfStmt) {

            ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
            FlowSet restricted = restrictCondition(expr, (FlowSet) in);
            ((ValueArraySparseSet) restricted).copy((FlowSet) outBranch);

            //restrict the in flow based on the negation of the expression
            restricted = restictNegCondition(expr, (FlowSet) in);

            ((ValueArraySparseSet) restricted).copy((FlowSet) out);

        } else {
            ((ValueArraySparseSet) in).copy((FlowSet) out);
            ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
        }

        // now copy the computed info to all successors
        for (Iterator it = fallOut.iterator(); it.hasNext(); ) {
            copy(out, it.next());
        }
        for (Iterator it = branchOuts.iterator(); it.hasNext(); ) {
            copy(outBranch, it.next());
        }
    }

    protected InterVal IntervalOfValue(Value v2, FlowSet inSet) {
        InterVal retInt = null;
        if (v2 instanceof Local && Utility.isNumeric((Local) v2)) {
            retInt = Utility.getIntervalForLocal(inSet, (Local) v2);

        } else if (v2 instanceof NumericConstant) {
            Double rvalue = new Double(((NumericConstant) v2).toString());
            retInt = new InterVal(null, (int) Math.floor(rvalue), (int) Math.ceil(rvalue));

        } else if (v2 instanceof BinopExpr) {

            if (v2 instanceof AddExpr) {
                retInt = IntervalOfValue(((AddExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((AddExpr) v2).getOp2(), inSet);
                retInt = retInt.sum(intop2);
            } else if (v2 instanceof SubExpr) {
                retInt = IntervalOfValue(((SubExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((SubExpr) v2).getOp2(), inSet);
                retInt = retInt.diff(intop2);
            } else if (v2 instanceof MulExpr) {
                retInt = IntervalOfValue(((MulExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((MulExpr) v2).getOp2(), inSet);
                retInt = retInt.prod(intop2);
            } else if (v2 instanceof DivExpr) {
                retInt = IntervalOfValue(((DivExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((DivExpr) v2).getOp2(), inSet);
                retInt = retInt.div(intop2);
            }
            else if (v2 instanceof CmpExpr ) {
                retInt = IntervalOfValue(((CmpExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((CmpExpr) v2).getOp2(), inSet);
                retInt = retInt.diff(intop2);
            }
            else if (v2 instanceof CmpgExpr ) {
                retInt = IntervalOfValue(((CmpgExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((CmpgExpr) v2).getOp2(), inSet);
                retInt = retInt.diff(intop2);
            }
            else if (v2 instanceof CmplExpr ) {
                retInt = IntervalOfValue(((CmplExpr) v2).getOp1(), inSet);
                InterVal intop2 = IntervalOfValue(((CmplExpr) v2).getOp2(), inSet);
                retInt = retInt.diff(intop2);
            }
            else
                retInt =new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE,false,true,false);
        } else if (v2 instanceof UnopExpr) {
            if (v2 instanceof AbstractNegExpr) {
                retInt = IntervalOfValue(((AbstractNegExpr) v2).getOp(), inSet);
                retInt = retInt.negate();
            }
            else
                retInt =new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE,false,true,false);
        }
        else
            retInt =new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE,false,true,false);
        return retInt;

    }

    protected FlowSet restrictExpression(Value e, InterVal i, FlowSet inSet) {

        InterVal retInt = null;
        FlowSet outSet = emptySet.clone();

        if (e instanceof Local && Utility.isNumeric((Local) e)) {

            retInt = Utility.getIntervalForLocal(inSet, (Local) e);
            retInt = (InterVal) retInt.pointWiseJoin(i);
            retInt.l = (Local) e;

            ((ValueArraySparseSet) inSet).copy((FlowSet) outSet);
            Utility.replaceIntervalForLocal((FlowSet) outSet, retInt);

        } else if (e instanceof NumericConstant) {
            outSet = topSet.clone();

        } else if (e instanceof BinopExpr) {
            Value e1 = ((BinopExpr) e).getOp1();
            Value e2 = ((BinopExpr) e).getOp2();

            if (e instanceof AddExpr) {

                FlowSet left = restrictExpression(e1, i.diff(IntervalOfValue(e2, (FlowSet) inSet)), inSet);
                FlowSet right = restrictExpression(e2, i.diff(IntervalOfValue(e1, (FlowSet) inSet)), inSet);
                Utility.intersection(left, right, outSet);

            } else if (e instanceof SubExpr) {

                FlowSet left = restrictExpression(e1, i.sum(IntervalOfValue(e2, (FlowSet) inSet)), inSet);
                FlowSet right = restrictExpression(e2, (IntervalOfValue(e1, (FlowSet) inSet)).diff(i), inSet);
                Utility.intersection(left, right, outSet);


            } else if (e instanceof MulExpr) {

                FlowSet left = restrictExpression(e1, i.div(IntervalOfValue(e2, (FlowSet) inSet)), inSet);
                FlowSet right = restrictExpression(e2, i.div(IntervalOfValue(e1, (FlowSet) inSet)), inSet);
                Utility.intersection(left, right, outSet);

            } else if (e instanceof DivExpr) {

                FlowSet left = restrictExpression(e1, i.prod(IntervalOfValue(e2, (FlowSet) inSet)), inSet);
                FlowSet right = restrictExpression(e2, i.prod(IntervalOfValue(e1, (FlowSet) inSet)), inSet);
                Utility.intersection(left, right, outSet);
            }
        }
        return outSet;
    }

    protected FlowSet restrictCondition(ConditionExpr e, FlowSet inSet) {
        FlowSet outSet = emptySet.clone();
        Value e1 = e.getOp1();
        Value e2 = e.getOp2();
        InterVal One = new InterVal(null, 1, 1);
//<
        if (e instanceof LtExpr) {

            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet).below().diff(One), inSet);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet).above().sum(One), inSet);
            Utility.intersection(left, right, outSet);

//>
        } else if (e instanceof GtExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet).above().sum(One), inSet);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet).below().diff(One), inSet);
            Utility.intersection(left, right, outSet);

            //<=
        } else if (e instanceof LeExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet).below(), inSet);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet).above(), inSet);
            Utility.intersection(left, right, outSet);

//>=
        } else if (e instanceof GeExpr) {

            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet).above(), inSet);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet).below(), inSet);
            Utility.intersection(left, right, outSet);
//==
        } else if (e instanceof EqExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet), inSet);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet), inSet);
            Utility.intersection(left, right, outSet);
//!=
        } else if (e instanceof NeExpr) {

            FlowSet left = restrictCondition(new JLtExpr(e1, e2), inSet);
            FlowSet right = restrictCondition(new JGtExpr(e1, e2), inSet);
            Utility.union(left, right, outSet);

        } else if (e instanceof AndExpr) {

            FlowSet left = restrictCondition((ConditionExpr) e1, inSet);
            FlowSet right = restrictCondition((ConditionExpr) e2, inSet);
            Utility.intersection(left, right, outSet);

        } else if (e instanceof OrExpr) {

            FlowSet left = restrictCondition((ConditionExpr) e1, inSet);
            FlowSet right = restrictCondition((ConditionExpr) e2, inSet);
            Utility.union(left, right, outSet);

        }


        return outSet;
    }

    protected FlowSet restictNegCondition(ConditionExpr expr, FlowSet inSet) {
        FlowSet restricted = emptySet.clone();
        if (expr instanceof EqExpr) {
            Value e1 = expr.getOp1();
            Value e2 = expr.getOp2();

            FlowSet left = restrictCondition(new JLtExpr(e1, e2), inSet);
            FlowSet right = restrictCondition(new JGtExpr(e1, e2), inSet);
            Utility.union(left, right, restricted);
            return restricted;

        }

        ConditionExpr Notexpr = negateCondition(expr);
        restricted = restrictCondition(Notexpr, inSet);
        return restricted;
    }
    /*
    "==":  JEqExpr
    ">=" : JGeExpr
    ">" : JGtExpr
    "<=" : JLeExpr
    "<" : JLtExpr
    "!=" : JNeExpr
    */

    protected ConditionExpr negateCondition(ConditionExpr e) {
        Value e1 = e.getOp1();
        Value e2 = e.getOp2();
        ConditionExpr ret = null;

        if (e instanceof LtExpr) {
            ret = new JGeExpr(e1, e2);

        } else if (e instanceof GtExpr) {
            ret = new JLeExpr(e1, e2);

        } else if (e instanceof LeExpr) {

            ret = new JGtExpr(e1, e2);
        } else if (e instanceof GeExpr) {
            ret = new JLtExpr(e1, e2);

        } else if (e instanceof EqExpr) {

            ret = new JNeExpr(e1, e2);

        } else if (e instanceof NeExpr) {
            ret = new JEqExpr(e1, e2);
        } else if (e instanceof AndExpr) {

            ConditionExpr ne1 = negateCondition((ConditionExpr) e1);
            ConditionExpr ne2 = negateCondition((ConditionExpr) e2);
            ret = (ConditionExpr) (new JOrExpr(ne1, ne1));

        } else if (e instanceof OrExpr) {

            ConditionExpr ne1 = negateCondition((ConditionExpr) e1);
            ConditionExpr ne2 = negateCondition((ConditionExpr) e2);
            ret = (ConditionExpr) (new JAndExpr(ne1, ne1));
        }
        return ret;
    }
}