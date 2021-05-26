package intervalanalysis.intervalinter.analyses;

import intervalanalysis.flowsets.*;


import intervalanalysis.vasco.*;
import intervalanalysis.vasco.soot.DefaultJimpleRepresentation;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;

import java.util.*;

import soot.IntType;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
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
import soot.jimple.internal.JimpleLocal;
import intervalanalysis.vasco.Context;
import intervalanalysis.vasco.ForwardInterProceduralAnalysis;
import intervalanalysis.vasco.ProgramRepresentation;
import intervalanalysis.vasco.soot.DefaultJimpleRepresentation;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;

public class IntervalAnalyis extends ForwardInterProceduralAnalysis<SootMethod, Unit, FlowSet> {

    static int newID = 0;

    private FlowSet emptySet;

    private Map<SootMethod, List<Value>> returnlocals = new HashMap<SootMethod, List<Value>>();
    private Map<SootMethod, Chain<Local>> mlocals = new HashMap<SootMethod, Chain<Local>>();
    private Map<SootMethod, FlowSet> mbottomSet = new HashMap<SootMethod, FlowSet>();
    private Map<SootMethod, FlowSet> mtopSet = new HashMap<SootMethod, FlowSet>();
    private Map<SootMethod, Collection<Loop>> mloops = new HashMap<SootMethod, Collection<Loop>>();

    private Map<Integer, FlowSet> refFields = new HashMap<Integer, FlowSet>();
    private Map<SootClass, FlowSet> staticFields = new HashMap<SootClass, FlowSet>();
    private Map<instanceID, Integer> refinstanceID = new HashMap<instanceID, Integer>();

    final int k = 100;
    final boolean BRANCHED = true;
    final boolean INCREMEET = true;

    public IntervalAnalyis() {
        super();
        verbose = false;
        emptySet = new ValueArraySparseSet();
        InterVal.INCREMEET = this.INCREMEET;
    }

    protected FlowSet getBottomSet(SootMethod m) {

        FlowSet bott = emptySet.clone();
        if (mbottomSet.containsKey(m))
            bott = mbottomSet.get(m);
        return bott;
    }

    protected void setBottomSet(SootMethod m, FlowSet bott) {
        if (!mbottomSet.containsKey(m))
            mbottomSet.put(m, bott);
        return;
    }

    protected FlowSet getTopSet(SootMethod m) {

        FlowSet topp = emptySet.clone();
        if (mtopSet.containsKey(m))
            topp = mtopSet.get(m);
        return topp;
    }

    protected void setTopSet(SootMethod m, FlowSet topp) {
        if (!mtopSet.containsKey(m))
            mtopSet.put(m, topp);
        return;
    }

    protected Chain<Local> getLocals(SootMethod m) {

        Chain<Local> locals = null;
        if (mlocals.containsKey(m))
            locals = mlocals.get(m);
        return locals;
    }

    protected void setLocals(SootMethod m, Chain<Local> locals) {
        if (!mlocals.containsKey(m))
            mlocals.put(m, locals);
        return;
    }

    protected Collection<Loop> getLoops(SootMethod m) {

        Collection<Loop> loops = null;
        if (mloops.containsKey(m))
            loops = mloops.get(m);
        return loops;
    }

    protected void setLoops(SootMethod m, Collection<Loop> loops) {
        if (!mloops.containsKey(m))
            mloops.put(m, loops);
        return;
    }

    protected FlowSet constructBottomSet(SootMethod m) {

        FlowSet bott = emptySet.clone();
        for (Local l : getLocals(m)) {
            if (Utility.isNumeric(l))
                bott.add(new InterVal(l, 0, 0, true));
        }
        return bott;
    }

    protected FlowSet constructTopSet(SootMethod m) {

        FlowSet top = emptySet.clone();
        for (Local l : getLocals(m)) {
            if (Utility.isNumeric(l))
                top.add(new InterVal(l, true));
        }
        return top;
    }

    protected InterVal IntervalOfValue(Value v2, FlowSet inSet, SootMethod m) {
        InterVal retInt = null;
        if (v2 instanceof Local && Utility.isNumeric((Local) v2)) {
            retInt = Utility.getIntervalForLocal(inSet, (Local) v2);
        } else if (v2 instanceof FieldRef && Utility.isNumeric(v2)) {
            FlowSet filedSet = emptySet.clone();
            if (((FieldRef) v2).getField().isStatic()) {
                SootClass className = ((FieldRef) v2).getField().getDeclaringClass();
                filedSet = staticFields.get(className);
            } else {
                Value instance = ((InstanceFieldRef) v2).getBase();
                filedSet = refFields.get(resolveInstanceID(m, instance));

            }
            retInt = Utility.getIntervalForRefField((FieldRef) v2, filedSet);

        } else if (v2 instanceof NumericConstant) {
            Double rvalue;
            if (v2 instanceof LongConstant)
                rvalue = new Double(((LongConstant) v2).value);
            else
                rvalue = new Double(((NumericConstant) v2).toString());
            retInt = new InterVal(null, (int) Math.floor(rvalue), (int) Math.ceil(rvalue));


        } else if (v2 instanceof BinopExpr) {
            if (v2 instanceof AddExpr) {
                retInt = IntervalOfValue(((AddExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((AddExpr) v2).getOp2(), inSet, m);
                retInt = retInt.sum(intop2);
            } else if (v2 instanceof SubExpr) {
                retInt = IntervalOfValue(((SubExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((SubExpr) v2).getOp2(), inSet, m);
                retInt = retInt.diff(intop2);
            } else if (v2 instanceof MulExpr) {
                retInt = IntervalOfValue(((MulExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((MulExpr) v2).getOp2(), inSet, m);
                retInt = retInt.prod(intop2);
            } else if (v2 instanceof DivExpr) {
                retInt = IntervalOfValue(((DivExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((DivExpr) v2).getOp2(), inSet, m);
                retInt = retInt.div(intop2);
            } else if (v2 instanceof CmpExpr) {
                retInt = IntervalOfValue(((CmpExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((CmpExpr) v2).getOp2(), inSet, m);
                retInt = retInt.diff(intop2);
            } else if (v2 instanceof CmpgExpr) {
                retInt = IntervalOfValue(((CmpgExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((CmpgExpr) v2).getOp2(), inSet, m);
                retInt = retInt.diff(intop2);
            } else if (v2 instanceof CmplExpr) {
                retInt = IntervalOfValue(((CmplExpr) v2).getOp1(), inSet, m);
                InterVal intop2 = IntervalOfValue(((CmplExpr) v2).getOp2(), inSet, m);
                retInt = retInt.diff(intop2);
            } else
                retInt = new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE, false, true, false);
        } else if (v2 instanceof UnopExpr) {
            if (v2 instanceof AbstractNegExpr) {
                retInt = IntervalOfValue(((AbstractNegExpr) v2).getOp(), inSet, m);
                retInt = retInt.negate();
            } else
                retInt = new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE, false, true, false);
        } else
            retInt = new InterVal(null, Integer.MIN_VALUE, Integer.MAX_VALUE, false, true, false);

        return retInt;
    }

    protected FlowSet restrictExpression(Value e, InterVal i, FlowSet inSet, SootMethod m) {

        InterVal retInt = null;
        FlowSet outSet = emptySet.clone();

        if (e instanceof Local && Utility.isNumeric((Local) e)) {
            retInt = Utility.getIntervalForLocal(inSet, (Local) e);
            retInt = (InterVal) retInt.pointWiseJoin(i);
            retInt.l = (Local) e;

            ((ValueArraySparseSet) inSet).copy((FlowSet) outSet);
            Utility.replaceIntervalForLocal((FlowSet) outSet, retInt);


        } else if (e instanceof NumericConstant) {
            outSet = getTopSet(m).clone();

        } else if (e instanceof BinopExpr) {
            Value e1 = ((BinopExpr) e).getOp1();
            Value e2 = ((BinopExpr) e).getOp2();

            if (e instanceof AddExpr) {

                FlowSet left = restrictExpression(e1, i.diff(IntervalOfValue(e2, (FlowSet) inSet, m)), inSet, m);
                FlowSet right = restrictExpression(e2, i.diff(IntervalOfValue(e1, (FlowSet) inSet, m)), inSet, m);
                Utility.intersection(left, right, outSet);

            } else if (e instanceof SubExpr) {

                FlowSet left = restrictExpression(e1, i.sum(IntervalOfValue(e2, (FlowSet) inSet, m)), inSet, m);
                FlowSet right = restrictExpression(e2, (IntervalOfValue(e1, (FlowSet) inSet, m)).diff(i), inSet, m);
                Utility.intersection(left, right, outSet);


            } else if (e instanceof MulExpr) {

                FlowSet left = restrictExpression(e1, i.div(IntervalOfValue(e2, (FlowSet) inSet, m)), inSet, m);
                FlowSet right = restrictExpression(e2, i.div(IntervalOfValue(e1, (FlowSet) inSet, m)), inSet, m);
                Utility.intersection(left, right, outSet);

            } else if (e instanceof DivExpr) {

                FlowSet left = restrictExpression(e1, i.prod(IntervalOfValue(e2, (FlowSet) inSet, m)), inSet, m);
                FlowSet right = restrictExpression(e2, i.prod(IntervalOfValue(e1, (FlowSet) inSet, m)), inSet, m);
                Utility.intersection(left, right, outSet);
            }
        }
        return outSet;
    }

    protected FlowSet restrictCondition(ConditionExpr e, FlowSet inSet, SootMethod m) {
        FlowSet outSet = emptySet.clone();
        Value e1 = e.getOp1();
        Value e2 = e.getOp2();
        InterVal One = new InterVal(null, 1, 1);
//<
        if (e instanceof LtExpr) {

            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet, m).below().diff(One), inSet, m);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet, m).above().sum(One), inSet, m);
            Utility.intersection(left, right, outSet);

//>
        } else if (e instanceof GtExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet, m).above().sum(One), inSet, m);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet, m).below().diff(One), inSet, m);
            Utility.intersection(left, right, outSet);

            //<=
        } else if (e instanceof LeExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet, m).below(), inSet, m);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet, m).above(), inSet, m);
            Utility.intersection(left, right, outSet);

//>=
        } else if (e instanceof GeExpr) {

            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet, m).above(), inSet, m);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet, m).below(), inSet, m);
            Utility.intersection(left, right, outSet);
//==
        } else if (e instanceof EqExpr) {
            FlowSet left = restrictExpression(e1, IntervalOfValue(e2, (FlowSet) inSet, m), inSet, m);
            FlowSet right = restrictExpression(e2, IntervalOfValue(e1, (FlowSet) inSet, m), inSet, m);
            Utility.intersection(left, right, outSet);
//!=
        } else if (e instanceof NeExpr) {

            FlowSet left = restrictCondition(new JLtExpr(e1, e2), inSet, m);
            FlowSet right = restrictCondition(new JGtExpr(e1, e2), inSet, m);
            Utility.union(left, right, outSet);

        } else if (e instanceof AndExpr) {

            FlowSet left = restrictCondition((ConditionExpr) e1, inSet, m);
            FlowSet right = restrictCondition((ConditionExpr) e2, inSet, m);
            Utility.intersection(left, right, outSet);

        } else if (e instanceof OrExpr) {

            FlowSet left = restrictCondition((ConditionExpr) e1, inSet, m);
            FlowSet right = restrictCondition((ConditionExpr) e2, inSet, m);
            Utility.union(left, right, outSet);

        }
        return outSet;
    }

    protected FlowSet restictNegCondition(ConditionExpr expr, FlowSet inSet, SootMethod m) {
        FlowSet restricted = emptySet.clone();
        if (expr instanceof EqExpr) {
            Value e1 = expr.getOp1();
            Value e2 = expr.getOp2();

            FlowSet left = restrictCondition(new JLtExpr(e1, e2), inSet, m);
            FlowSet right = restrictCondition(new JGtExpr(e1, e2), inSet, m);
            Utility.union(left, right, restricted);
            return restricted;

        }

        ConditionExpr Notexpr = negateCondition(expr);
        restricted = restrictCondition(Notexpr, inSet, m);
        return restricted;
    }

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


    @Override
    public FlowSet normalFlowFunction(
            intervalanalysis.vasco.Context<SootMethod, Unit, FlowSet> context, Unit node,
            FlowSet in) {

        FlowSet out = emptySet.clone();
        FlowSet outBranch = emptySet.clone();

        Unit u = (Unit) node;
        if (u instanceof AssignStmt) {

            Value v1 = ((AssignStmt) u).getLeftOp();
            Value v2 = ((AssignStmt) u).getRightOp();

            if (v1 instanceof Local && v2 instanceof Local && resolveInstanceID(context.getMethod(), v2) != null) {
                Integer globalID = resolveInstanceID(context.getMethod(), v2);
                addInstanceID(context.getMethod(), (Local) v1, globalID);

                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);

            } else if (v1 instanceof Local && Utility.isNumeric((Local) v1)) {

                InterVal v2lInterval = IntervalOfValue(v2, (FlowSet) in, context.getMethod());
                v2lInterval.l = (Local) v1;
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                Utility.replaceIntervalForLocal((FlowSet) out, v2lInterval);

            } else if (v1 instanceof FieldRef && Utility.isNumeric(v1)) {
                //Value v2 = ((AssignStmt) u).getRightOp();
                InterVal v2lInterval = IntervalOfValue(v2, (FlowSet) in, context.getMethod());

                FlowSet filedSet = emptySet.clone();
                if (((FieldRef) v1).getField().isStatic()) {
                    SootClass className = ((FieldRef) v1).getField().getDeclaringClass();
                    filedSet = staticFields.get(className);
                } else {
                    Value instance = ((InstanceFieldRef) v1).getBase();
                    filedSet = refFields.get(resolveInstanceID(context.getMethod(), instance));
                }

                Utility.replaceIntervalForRefField((FieldRef) v1, v2lInterval, filedSet);

                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);

            } else if (((AssignStmt) u).getRightOp() instanceof NewExpr) {

                Integer ID = addInstanceID(context.getMethod(), (Local) v1, null);
                Type c = ((NewExpr) (((AssignStmt) u).getRightOp())).getType();
                computeFieldRefsIntervlasForM(((RefType) c).getSootClass(), ID);

                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);

            } else {
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
            }

        } else if (u instanceof IfStmt) {

            if (BRANCHED) {
                ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
                FlowSet restricted = restrictCondition(expr, (FlowSet) in, context.getMethod());
                ((ValueArraySparseSet) restricted).copy((FlowSet) outBranch);

                //restrict the in flow based on the negation of the expression
                restricted = restictNegCondition(expr, (FlowSet) in, context.getMethod());
                ((ValueArraySparseSet) restricted).copy((FlowSet) out);

                Unit branchout = ((IfStmt) u).getTarget();
                if (u.fallsThrough()) {
                    Unit succ = (Unit) context.getMethod().retrieveActiveBody().getUnits().getSuccOf(u);

                    if (succ == branchout) {
                        outBranch = meet(out, outBranch, node, context);
                    }
                }

                FlowSet predOut = context.getEdgeValue(u, branchout);
                if (predOut != null)
                    outBranch = meet(outBranch, predOut, node, context);

                context.setEdgeValue(u, branchout, outBranch);

                if (outBranch.equals(predOut) == false) {
                    context.setEdgeConvergenceValue(u, branchout, false);
                } else {
                    context.setEdgeConvergenceValue(u, branchout, true);
                }

            } else {
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
            }

        } else if (u instanceof ReturnStmt) {

            Value rhsOp = ((ReturnStmt) u).getOp();
            addReturnLocaltoMethod(context.getMethod(), rhsOp);

            ((ValueArraySparseSet) in).copy((FlowSet) out);
            ((ValueArraySparseSet) in).copy((FlowSet) outBranch);

        }
        //else if (u instanceof NewExpr) {}
        else {
            ((ValueArraySparseSet) in).copy((FlowSet) out);
            ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
        }
        // Return the data flow value at the OUT of the statement
        return out;
    }

    public Integer resolveInstanceID(SootMethod m, Value v) {
        instanceID id = new instanceID(m, (Local) v);
        Integer idIn = null;
        if (refinstanceID.containsKey(id)) {
            idIn = refinstanceID.get(id);
        }
        return idIn;
    }

    public Integer addInstanceID(SootMethod m, Local v, Integer ID) {
        instanceID id = new instanceID(m, v);
        if (ID == null) ID = getNewID();
        refinstanceID.put(id, ID);
        return ID;
    }

    public synchronized Integer getNewID() {
        return ++IntervalAnalyis.newID;
    }

    @Override
    public FlowSet callEntryFlowFunction(
            intervalanalysis.vasco.Context<SootMethod, Unit, FlowSet> context, SootMethod calledMethod, Unit unit,
            FlowSet in) {

        initiaizeMethod(calledMethod);
        // Initialise result to empty map
        FlowSet out = getTopSet(calledMethod).clone();

        // Map arguments to parameters
        InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
        for (int i = 0; i < ie.getArgCount(); i++) {
            Value arg = ie.getArg(i);
            Local param = calledMethod.getActiveBody().getParameterLocal(i);

            InterVal v2lInterval = IntervalOfValue(arg, (FlowSet) in, context.getMethod());
            v2lInterval.l = param;
            Utility.replaceIntervalForLocal((FlowSet) out, v2lInterval);

            //dummy values to distinguish contexts of invocation
            staticFAtInvocation(out, calledMethod.getDeclaringClass());

        }

        // And instance of the this local
        if (ie instanceof InstanceInvokeExpr) {
            Value instance = ((InstanceInvokeExpr) ie).getBase();
            Local thisLocal = calledMethod.getActiveBody().getThisLocal();

            Integer ID = resolveInstanceID(context.getMethod(), instance);
            addInstanceID(calledMethod, thisLocal, ID);

            //dummy values to distinguish contexts of invocation
            refFAtInvocation(out, ID);

        }

        // Return the entry value at the called method
        return out;
    }

    private void staticFAtInvocation(FlowSet in, SootClass cls) {
        FlowSet in2 = staticFields.get(cls);
        //((ValueArraySparseSet) in).merge((ValueArraySparseSet) in2, true);

    }

    private void refFAtInvocation(FlowSet in, Integer ID) {
        FlowSet in2 = refFields.get(ID);
        //((ValueArraySparseSet) in).merge((ValueArraySparseSet) in2, true);
    }

    @Override
    public FlowSet callExitFlowFunction(intervalanalysis.vasco.Context<SootMethod, Unit, FlowSet> context, SootMethod calledMethod, Unit unit, FlowSet exitValue) {
        // Initialise result to an empty value
        FlowSet afterCallValue = emptySet.clone();

        // Only propagate signs for return values
        if (unit instanceof AssignStmt) {
            Value lhsOp = ((AssignStmt) unit).getLeftOp();

            InterVal v2lInterval = unionReturnFor(calledMethod, exitValue);
            if (v2lInterval != null) {
                v2lInterval.l = (Local) lhsOp;
                afterCallValue.add(v2lInterval);
            }
        }

        return afterCallValue;
    }

    void addReturnLocaltoMethod(SootMethod m, Value v) {
        if (!returnlocals.containsKey(m)) {
            List<Value> vals = new ArrayList<Value>();
            vals.add(v);
            returnlocals.put(m, vals);
        } else {
            ((List<Value>) returnlocals.get(m)).add(v);
        }
    }

    InterVal unionReturnFor(SootMethod m, FlowSet in) {
        InterVal v2lInterval = null;
        List<Value> vals = returnlocals.get(m);
        Iterator valsit = vals.iterator();
        while (valsit.hasNext()) {
            Value vv = (Value) valsit.next();
            InterVal vvlInterval = IntervalOfValue(vv, in, m);
            if (v2lInterval != null)
                //vvlInterval = (InterVal) vvlInterval.pointWiseJoin(v2lInterval);
                v2lInterval = (InterVal) vvlInterval.pointWiseMeet(v2lInterval);
            else
                v2lInterval = vvlInterval;

        }

        return v2lInterval;
    }

    @Override
    public FlowSet callLocalFlowFunction(Context<SootMethod, Unit, FlowSet> context, Unit unit, FlowSet inValue) {
        // Initialise result to the input
        FlowSet afterCallValue = emptySet.clone();
        ((ValueArraySparseSet) inValue).copy(afterCallValue);

        // Remove information for return value (as it's value will flow from the call)
        if (unit instanceof AssignStmt) {
            Value lhsOp = ((AssignStmt) unit).getLeftOp();
            Value rhsOp = ((AssignStmt) unit).getRightOp();

            if (!Utility.isPantomMethod(((InvokeExpr) rhsOp).getMethod()))
                ((ValueArraySparseSet) afterCallValue).removeIntervalFor((Local) lhsOp);
        }

        // Rest of the map remains the same
        return afterCallValue;

    }

    @Override
    public FlowSet boundaryValue(SootMethod m) {

        initiaizeMethod(m);
        return getTopSet(m).clone();
    }

    protected void initiaizeMethod(SootMethod m) {
            SootClass className = m.getDeclaringClass();

            if (verbose) {
                Body b0 = m.retrieveActiveBody();
                System.out.println("=======================================");
                System.out.println(m.toString());
                System.out.println("=======================================");
                System.out.println(b0.toString());
                System.out.println("=======================================");
            }

            if (!m.isStatic()) {
                Local ll = m.getActiveBody().getThisLocal();
                if (refFields.get(resolveInstanceID(m, ll)) == null)
                    computeFieldRefsIntervlasForM(m);
            }

            if (staticFields.get(className) == null)
                computeStaticFieldIntervlasForM(m);

            if (!mlocals.containsKey(m)) {
                Body b = m.retrieveActiveBody();
                UnitGraph graph = new ExceptionalUnitGraph(b);
                Collection<Loop> loops = computeLoops(b);
                Chain<Local> locals = b.getLocals();

                setLocals(m, locals);
                setLoops(m, loops);

                FlowSet topp = constructTopSet(m);
                FlowSet bbtm = constructBottomSet(m);
                setTopSet(m, topp);
                setBottomSet(m, bbtm);

            }
    }

    private void computeFieldRefsIntervlasForM(SootMethod m) {
        if (m.isStatic()) return;

        Value instance = m.getActiveBody().getThisLocal();
        if (instance == null) return;
        Integer res = resolveInstanceID(m, instance);
        if (res == null || refFields.get(res) != null) return;

        FlowSet FieldRefs = emptySet.clone();

        for (SootField f : m.getDeclaringClass().getFields()) {
            if (!f.isStatic()) {
                fInterVal inter = new fInterVal(f, 0, 0, false, true);
                FieldRefs.add(inter);
            }
        }


        refFields.put(resolveInstanceID(m, instance), FieldRefs);

    }

    private void computeStaticFieldIntervlasForM(SootMethod m) {
        computeStaticFieldIntervlasForM(m.getDeclaringClass());
    }

    private void computeFieldRefsIntervlasForM(SootClass c, Integer res) {
        if (res == null || refFields.get(res) != null) return;
        FlowSet FieldRefs = emptySet.clone();

        for (SootField f : c.getFields()) {
            if (!f.isStatic()) {
                fInterVal inter = new fInterVal(f, 0, 0, false, true);
                FieldRefs.add(inter);
            }
        }
        refFields.put(res, FieldRefs);
    }

    private void computeStaticFieldIntervlasForM(SootClass c) {

        if (staticFields.get(c) != null) return;
        FlowSet Fieldstatic = emptySet.clone();
        for (SootField f : c.getFields()) {
            if (f.isStatic()) {
                fInterVal inter = new fInterVal(f, 0, 0, false, true);
                Fieldstatic.add(inter);
            }

        }
        staticFields.put(c, Fieldstatic);
    }

    @Override
    public FlowSet copy(FlowSet src) {
        return src.clone();
    }

    @Override
    public FlowSet topValue() {
        return emptySet.clone();
    }

    @Override
    public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
        return DefaultJimpleRepresentation.v();
    }

    @Override
    public FlowSet meet(FlowSet in1, FlowSet in2, Unit succNode, intervalanalysis.vasco.Context<SootMethod, Unit, FlowSet> context) {

        FlowSet inSet1 = (FlowSet) in1,
                inSet2 = (FlowSet) in2;

        FlowSet outSet = emptySet.clone();
        int countvisits = Utility.isLoop(getLoops(context.getMethod()), (Unit) succNode);

        if (verbose)
            System.out.println(((Unit) succNode).toString() + " countvisits " + countvisits);

        if (countvisits >= k)
            Utility.widen(inSet1, inSet2, outSet);
        else
            Utility.union(inSet1, inSet2, outSet);

        return outSet;
    }

    private Collection<Loop> computeLoops(Body b) {
        LoopFinder loopFinder = new LoopFinder();
        loopFinder.transform(b);

        Collection<Loop> loops = loopFinder.getLoops(b);
        return loops;
    }


}
