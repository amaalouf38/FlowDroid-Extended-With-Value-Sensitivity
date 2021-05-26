package stringanalysis.stringinter.analyses;

import dk.brics.automaton.Automaton;
import stringanalysis.brics.stringoperations.*;
import stringanalysis.flowsets.*;
import stringanalysis.vasco.soot.DefaultJimpleRepresentation;
import soot.*;
import soot.jimple.*;

import java.util.*;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import stringanalysis.vasco.*;
import stringanalysis.vasco.Context;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;

public class StringAnalyis extends ForwardInterProceduralAnalysis<SootMethod, Unit, FlowSet> {

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

    private Map<instanceID, FlowSet[]> stringBranches = new HashMap<instanceID, FlowSet[]>();

    final int k = 100;
    final boolean BRANCHED = true;

    public StringAnalyis() {
        super();
        verbose = false;
        emptySet = new ValueArraySparseSet();
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
            if (SUtility.isString(l))
                bott.add(new StringVal(l, null, true, false, false));
        }
        return bott;
    }

    protected FlowSet constructTopSet(SootMethod m) {

        FlowSet top = emptySet.clone();
        for (Local l : getLocals(m)) {
            if (SUtility.isString(l))
                top.add(new StringVal(l, null, false, true, false));
        }
        return top;
    }

    protected StringVal StringValOfValue(Value v2, FlowSet inSet, SootMethod m) {
        StringVal retInt = null;

        if (v2 instanceof Local && SUtility.isString((Local) v2)) {
            retInt = SUtility.getstringvalForLocal(inSet, (Local) v2);
        } else if (v2 instanceof FieldRef && SUtility.isString(v2)) {
            FlowSet filedSet = emptySet.clone();
            if (((FieldRef) v2).getField().isStatic()) {
                SootClass className = ((FieldRef) v2).getField().getDeclaringClass();
                filedSet = staticFields.get(className);
            } else {
                Value instance = ((InstanceFieldRef) v2).getBase();
                filedSet = refFields.get(resolveInstanceID(m, instance));
            }
            //if (filedSet == null)
            //    retInt = new StringVal(null, null, false, true, false);
            //else
            retInt = SUtility.getstringvalForRefField((FieldRef) v2, filedSet);

        } else if (v2 instanceof StringConstant) {
            String rvalue = ((StringConstant) v2).value;
            retInt = new StringVal(null, Automaton.makeString(rvalue), false, false, false);
        }
        return retInt;
    }

    protected FlowSet restictNegCondition(InstanceInvokeExpr expr, SootMethod target, Value callee, List<Value> arguments, FlowSet inSet, SootMethod m) {
        FlowSet restricted = emptySet.clone();
        String Notexpr = negateCondition(target.getName());
        restricted = restrictCondition(expr, target, callee, arguments, inSet, m, Notexpr);
        return restricted;
    }

    protected String negateCondition(String methodName) {
        String ret = "not";

        switch (methodName) {
            case "equals":
                return "notequals";
            case "notequals":
                return "equals";
            case "contains":
                return "notcontains";
            case "notcontains":
                return "contains";
            case "isEmpty":
                return "isNotEmpty";
            case "isNotEmpty":
                return "isEmpty";
            case "startsWith":
                return "notStartsWith";
            case "notStartsWith":
                return "startsWith";
            case "endsWith":
                return "notendsWith";
            case "notendsWith":
                return "endsWith";
        }

        return ret;
    }


    protected FlowSet restrictExpression(Value e, StringVal i, FlowSet inSet, SootMethod m) {

        StringVal retInt = null;
        FlowSet outSet = emptySet.clone();

        if (e instanceof Local && SUtility.isString((Local) e)) {

            retInt = SUtility.getstringvalForLocal(inSet, (Local) e);
            retInt = (StringVal) retInt.pointWiseJoin(i);
            retInt.l = (Local) e;

            ((ValueArraySparseSet) inSet).copy((FlowSet) outSet);
            SUtility.replacestringvalForLocal((FlowSet) outSet, retInt);

        } else if (e instanceof StringConstant) {
            outSet = getTopSet(m).clone();
        }

        return outSet;
    }

    protected FlowSet restrictCondition(InstanceInvokeExpr expr, SootMethod target, Value callee, List<Value> arguments, FlowSet inSet, SootMethod m, String notM) {
        FlowSet outSet = emptySet.clone();

        String methodName = notM.equals("") ? target.getName() : notM;

        if (methodName.equals("equals")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);
            FlowSet left = restrictExpression(e1, StringValOfValue(e2, (FlowSet) inSet, m), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("notequals")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            FlowSet left = restrictExpression(e1, StringValOfValue(e2, (FlowSet) inSet, m).getComplement(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getComplement(), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("contains")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            FlowSet left = restrictExpression(e1, StringValOfValue(e2, (FlowSet) inSet, m).getStringContain(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getStringContained(), inSet, m);
            SUtility.intersection(left, right, outSet);


        } else if (methodName.equals("notcontains")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            FlowSet left = restrictExpression(e1, StringValOfValue(e2, (FlowSet) inSet, m).getStringContainComplement(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getStringContainedComplement(), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("isEmpty")) {
            Value e1 = callee;

            return restrictExpression(e1, new StringVal(null, Automaton.makeEmptyString(), false, false, false), inSet, m);

        } else if (methodName.equals("isNotEmpty")) {
            Value e1 = callee;

            return restrictExpression(e1, StringValOfValue(e1, (FlowSet) inSet, m).getNotEmptyString(), inSet, m);

        } else if (methodName.equals("startsWith")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            StringVal e2Val = StringValOfValue(e2, (FlowSet) inSet, m);

            FlowSet left = restrictExpression(e1, e2Val.getStartsWith(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getPrefixes(e2Val), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("notStartsWith")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            StringVal e2Val = StringValOfValue(e2, (FlowSet) inSet, m);

            FlowSet left = restrictExpression(e1, e2Val.getNotStartsWith(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getNotPrefixes(e2Val), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("endsWith")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            StringVal e2Val = StringValOfValue(e2, (FlowSet) inSet, m);

            FlowSet left = restrictExpression(e1, e2Val.getEndsWith(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getSuffixes(e2Val), inSet, m);
            SUtility.intersection(left, right, outSet);

        } else if (methodName.equals("notendsWith")) {
            Value e1 = callee;
            Value e2 = arguments.get(0);

            StringVal e2Val = StringValOfValue(e2, (FlowSet) inSet, m);

            FlowSet left = restrictExpression(e1, e2Val.getNotEndsWith(), inSet, m);
            FlowSet right = restrictExpression(e2, StringValOfValue(e1, (FlowSet) inSet, m).getNotSuffixes(e2Val), inSet, m);
            SUtility.intersection(left, right, outSet);
        }
        return outSet;
    }


    public Automaton translateMethodCall(InstanceInvokeExpr expr, SootMethod target, Value callee, List<Value> arguments, FlowSet inSet, SootMethod m) {
        String methodName = target.getName();
        int numArgs = arguments.size();
        SootClass declaringClass = target.getDeclaringClass();

        if (methodName.equals("getDeviceId") && declaringClass.getName().equals("android.telephony.TelephonyManager"))
            return Automaton.makeString("I1t359-f");
        //
        //	STRING
        //
        if (SUtility.isString(declaringClass)) {
            // String.toString()
            if (methodName.equals("toString") && numArgs == 0) {
                return StringValOfValue(callee, inSet, m).a;

                // String.intern()
            } else if (methodName.equals("intern") && numArgs == 0) {
                return StringValOfValue(callee, inSet, m).a;

                // String.concat(String)	[explicit, not invoked by + operator]
            } else if (methodName.equals("concat") && numArgs == 1 && SUtility.isString(target.getParameterType(0))) {
                // translate the argument
                Value rvar = arguments.get(0);
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                Automaton a2 = StringValOfValue(rvar, inSet, m).a;
                return a1.concatenate(a2);


                // String.replace(char,char)
            } else if (methodName.equals("replace") && numArgs == 2 &&
                    SUtility.isChar(target.getParameterType(0)) &&
                    SUtility.isChar(target.getParameterType(1))) {
                Integer arg1 = SUtility.trackInteger(expr.getArg(0));
                Integer arg2 = SUtility.trackInteger(expr.getArg(1));
                UnaryOperation op;
                if (arg1 != null) {
                    if (arg2 != null) {
                        op = new Replace1((char) arg1.intValue(), (char) arg2.intValue());
                    } else {
                        op = new Replace2((char) arg1.intValue());
                    }
                } else {
                    if (arg2 != null) {
                        op = new Replace3((char) arg2.intValue());
                    } else {
                        op = new Replace4();
                    }
                }
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);


                // String.replace(CharSequence, CharSequence)
            } else if (methodName.equals("replace") && numArgs == 2 &&
                    SUtility.isCharSequence(target.getParameterType(0)) &&
                    SUtility.isCharSequence(target.getParameterType(1))) {

                String arg1 = SUtility.trackString(expr.getArg(0));
                String arg2 = SUtility.trackString(expr.getArg(1));

                Automaton resutl = null;
                Operation rep;

                if (arg1 != null && arg2 != null) {
                    rep = new Replace6(arg1, arg2);
                    Automaton a1 = StringValOfValue(callee, inSet, m).a;
                    resutl = ((UnaryOperation) rep).op(a1);
                } else {
                    rep = new Replace7();
                    Automaton a1 = StringValOfValue(callee, inSet, m).a;
                    Automaton b1 = StringValOfValue(expr.getArg(0), inSet, m).a;
                    Automaton e1 = StringValOfValue(expr.getArg(1), inSet, m).a;
                    resutl = ((TernaryOperation) rep).op(a1, b1, e1);

                }
                return resutl;


                // String.trim()
            } else if (methodName.equals("trim") && numArgs == 0) {
                UnaryOperation op = new Trim();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);

                // String.substring(int)		[this method returns a suffix of the string, starting at the specified index]
            } else if (methodName.equals("substring") && numArgs == 1) {
                UnaryOperation op = null;
                Integer arg1 = SUtility.trackInteger(expr.getArg(0));
                if (arg1 != null && arg1.intValue() > 0) {
                    op = new Substring2(arg1.intValue(), -1);
                } else {
                    op = new Postfix();
                }
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);

                // String.substring(int,int)
            } else if (methodName.equals("substring") && numArgs == 2) {
                UnaryOperation op;
                Integer arg1 = SUtility.trackInteger(expr.getArg(0));
                Integer arg2 = SUtility.trackInteger(expr.getArg(1));

                if (arg1 != null && arg2 != null) {//&& arg1.intValue() >= 0&& arg2.intValue() >= 0) {
                    op = new Substring2(arg1.intValue(), arg2.intValue());
                } else if (arg1 != null && arg1.intValue() == 0) {
                    op = new Prefix();
                } else {
                    op = new Substring();
                }
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);

                // String.toLowerCase()
            } else if (methodName.equals("toLowerCase") && numArgs == 0) {
                UnaryOperation op = new ToLowerCase();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);

                // String.toUpperCase()
            } else if (methodName.equals("toUpperCase") && numArgs == 0) {
                UnaryOperation op = new ToUpperCase();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);

                // String.charAt(int)
            } else if (methodName.equals("charAt") && numArgs == 1) {
                UnaryOperation op;
                Integer arg = SUtility.trackInteger(expr.getArg(0));
                if (arg != null) {
                    op = new CharAt1(arg);
                } else {
                    op = new CharAt2();
                }
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // String.contentEquals(CharSequence) and String.contentEquals(StringBuffer)
            else if (methodName.equals("contentEquals") && numArgs == 1) {
                // we can't say anything meaningful except the argument is NOT corrupted
                // (the argument will be considered corrupted if we do not handle it here)
                Value rvar = arguments.get(0);
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                Automaton a2 = StringValOfValue(rvar, inSet, m).a;

                return a1.intersection(a2);
            }
            // String.toCharArray()
            else if (methodName.equals("toCharArray") && numArgs == 0) {
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                UnaryOperation op = new CharAt2();
                return op.op(a1);
            }
        }
        //
        //		STRINGBUFFER
        //
        else if (SUtility.isBufferOrBuilder(declaringClass)) {
            if (methodName.equals("<init>") && numArgs == 0) {
                return Automaton.makeEmptyString();
            } else if (methodName.equals("<init>") && numArgs == 1) {
                return StringValOfValue(arguments.get(0), inSet, m).a;

            } else if (methodName.equals("toString") && numArgs == 0) {
                return StringValOfValue(callee, inSet, m).a;
            }
            // StringBuffer.append(<any type>)
            else if (methodName.equals("append") && numArgs == 1) {
                Value rvar = arguments.get(0);
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                Automaton a2 = StringValOfValue(rvar, inSet, m).a;
                return a1.concatenate(a2);
            }
            // StringBuffer.insert(int, <any type>)
            else if (methodName.equals("insert") && numArgs == 2 &&
                    SUtility.isInt(target.getParameterType(0))) {
                Integer pos = SUtility.trackInteger(expr.getArg(0));
                Value rvar = arguments.get(1);

                BinaryOperation op = new Insert();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                Automaton a2 = StringValOfValue(rvar, inSet, m).a;
                return op.op(a1, a2);
            }
            // StringBuffer.delete(int,int)
            else if (methodName.equals("delete") && numArgs == 2) {
                UnaryOperation op = new Delete();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.deleteCharAt(int)
            else if (methodName.equals("deleteCharAt") && numArgs == 1) {
                UnaryOperation op = new DeleteCharAt();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.replace(int start, int end, String replacement)
            else if (methodName.equals("replace") && numArgs == 3) {
                BinaryOperation op = new Replace5();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                Automaton a2 = StringValOfValue(expr.getArg(2), inSet, m).a;

                return op.op(a1, a2);
            }
            // StringBuffer.reverse()
            else if (methodName.equals("reverse") && numArgs == 0) {
                UnaryOperation op = new Reverse();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.setCharAt(int, char)	[NOTE: This method returns void]
            else if (methodName.equals("setCharAt") && numArgs == 2) {
                Integer c = SUtility.trackInteger(expr.getArg(1));
                UnaryOperation op = null;
                if (c == null) {
                    op = new SetCharAt2();

                } else {
                    op = new SetCharAt1((char) c.intValue());

                }
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.setLength(int)			[NOTE: This method returns void]
            else if (methodName.equals("setLength") && numArgs == 1) {
                UnaryOperation op = new SetLength();
                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.substring(int)			[NOTE: Returns a string]
            else if (methodName.equals("substring") && numArgs == 1) {
                UnaryOperation op = new Postfix();

                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
            // StringBuffer.substring(int,int)		[NOTE: Returns a string]
            else if (methodName.equals("substring") && numArgs == 2) {
                UnaryOperation op;
                Integer arg1 = SUtility.trackInteger(expr.getArg(0));
                if (arg1 != null && arg1.intValue() == 0) {
                    op = new Prefix();
                } else {
                    op = new Substring();
                }

                Automaton a1 = StringValOfValue(callee, inSet, m).a;
                return op.op(a1);
            }
        }
        return null;
    }


    @Override
    public FlowSet normalFlowFunction(
            Context<SootMethod, Unit, FlowSet> context, Unit node,
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

            } else if (v1 instanceof Local && SUtility.isString((Local) v1)) {
                ((ValueArraySparseSet) in).copy((FlowSet) out);

                if (!(v2 instanceof NewExpr)) {
                    StringVal v2lInterval = StringValOfValue(v2, (FlowSet) in, context.getMethod());
                    if (v2lInterval != null) {
                        v2lInterval.l = (Local) v1;
                        SUtility.replacestringvalForLocal((FlowSet) out, v2lInterval);
                    }
                }

            } else if (v1 instanceof FieldRef && SUtility.isString(v1)) {

                if (!(v2 instanceof NewExpr)) {
                    StringVal v2lInterval = StringValOfValue(v2, (FlowSet) in, context.getMethod());

                    FlowSet filedSet = emptySet.clone();
                    if (((FieldRef) v1).getField().isStatic()) {
                        SootClass className = ((FieldRef) v1).getField().getDeclaringClass();
                        filedSet = staticFields.get(className);
                    } else {
                        Value instance = ((InstanceFieldRef) v1).getBase();
                        filedSet = refFields.get(resolveInstanceID(context.getMethod(), instance));
                    }

                    SUtility.replacestringvalForRefField((FieldRef) v1, v2lInterval, filedSet);

                    ((ValueArraySparseSet) in).copy((FlowSet) out);
                } else {
                    ((ValueArraySparseSet) in).copy((FlowSet) out);
                }

            } else if (((AssignStmt) u).getRightOp() instanceof NewExpr) {

                Integer ID = addInstanceID(context.getMethod(), (Local) v1, null);
                Type c = ((NewExpr) (((AssignStmt) u).getRightOp())).getType();
                computeFieldRefsIntervlasForM(((RefType) c).getSootClass(), ID);

                ((ValueArraySparseSet) in).copy((FlowSet) out);

            } else {
                ((ValueArraySparseSet) in).copy((FlowSet) out);
            }

        } else if (u instanceof IfStmt) {

            if (BRANCHED) {
                ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
                if (expr instanceof EqExpr || expr instanceof NeExpr) {
                    Value e1 = expr.getOp1();
                    Value e2 = expr.getOp2();

                    FlowSet out1 = emptySet.clone();
                    FlowSet out2 = emptySet.clone();

                    if (expr instanceof EqExpr) {
                        out1 = out;
                        out2 = outBranch;

                    } else if (expr instanceof NeExpr) {
                        out2 = out;
                        out1 = outBranch;
                    }

                    FlowSet restricted = getRestrictCondition((Local) e1, context.getMethod());//(expr, (FlowSet) in, context.getMethod(), "");

                    if (restricted != null)
                        ((ValueArraySparseSet) restricted).copy((FlowSet) out1);
                    else
                        ((ValueArraySparseSet) in).copy((FlowSet) out1);

                    //restrict the in flow based on the negation of the expression
                    restricted = getRestictNegCondition((Local) e1, context.getMethod());//(expr, (FlowSet) in, context.getMethod());
                    if (restricted != null)
                        ((ValueArraySparseSet) restricted).copy((FlowSet) out2);
                    else
                        ((ValueArraySparseSet) in).copy((FlowSet) out2);

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

            } else {
                ((ValueArraySparseSet) in).copy((FlowSet) out);
                ((ValueArraySparseSet) in).copy((FlowSet) outBranch);
            }

        } else if (u instanceof ReturnStmt) {

            Value rhsOp = ((ReturnStmt) u).getOp();
            addReturnLocaltoMethod(context.getMethod(), rhsOp);

            ((ValueArraySparseSet) in).copy((FlowSet) out);


        }
        //else if (u instanceof NewExpr) {}
        else {
            ((ValueArraySparseSet) in).copy((FlowSet) out);

        }
        // Return the data flow value at the OUT of the statement
        return out;
    }

    //Map<instanceID, FlowSet[]> stringBranches
    FlowSet getRestrictCondition(Local l, SootMethod m) {
        instanceID id = new instanceID(m, l);
        FlowSet r = null;
        if (stringBranches.containsKey(id)) {
            FlowSet[] a = stringBranches.get(id);
            if (a.length > 0) r = a[0];

        }
        return r;
    }

    FlowSet getRestictNegCondition(Local l, SootMethod m) {
        instanceID id = new instanceID(m, l);
        FlowSet r = null;
        if (stringBranches.containsKey(id)) {
            FlowSet[] a = stringBranches.get(id);
            if (a.length > 1) r = a[1];

        }
        return r;
    }

    void setRestrictCondition(Local l, SootMethod m, FlowSet f) {
        instanceID id = new instanceID(m, l);
        if (!stringBranches.containsKey(id)) {
            FlowSet[] a = new FlowSet[2];
            a[0] = f;
            stringBranches.put(id, a);
        } else {
            FlowSet[] a = stringBranches.get(id);
            a[0] = f;
        }
    }

    void setRestictNegCondition(Local l, SootMethod m, FlowSet f) {
        instanceID id = new instanceID(m, l);
        if (!stringBranches.containsKey(id)) {
            FlowSet[] a = new FlowSet[2];
            a[1] = f;
            stringBranches.put(id, a);
        } else {
            FlowSet[] a = stringBranches.get(id);
            a[1] = f;
        }
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
        return ++StringAnalyis.newID;
    }

    @Override
    public FlowSet callEntryFlowFunction(
            Context<SootMethod, Unit, FlowSet> context, SootMethod calledMethod, Unit unit,
            FlowSet in) {

        initiaizeMethod(calledMethod);
        // Initialise result to empty map
        FlowSet out = getTopSet(calledMethod).clone();

        // Map arguments to parameters
        InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
        for (int i = 0; i < ie.getArgCount(); i++) {
            Value arg = ie.getArg(i);
            Local param = calledMethod.getActiveBody().getParameterLocal(i);

            if (SUtility.isString(arg)) {
                StringVal v2lInterval = StringValOfValue(arg, (FlowSet) in, context.getMethod());
                v2lInterval.l = param;
                SUtility.replacestringvalForLocal((FlowSet) out, v2lInterval);
            }

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
        ((ValueArraySparseSet) in).merge((ValueArraySparseSet) in2, true);

    }

    private void refFAtInvocation(FlowSet in, Integer ID) {
        FlowSet in2 = refFields.get(ID);
        ((ValueArraySparseSet) in).merge((ValueArraySparseSet) in2, true);
    }

    @Override
    public FlowSet callExitFlowFunction(Context<SootMethod, Unit, FlowSet> context, SootMethod calledMethod, Unit unit, FlowSet exitValue) {
        // Initialise result to an empty value
        FlowSet afterCallValue = emptySet.clone();

        // Only propagate signs for return values
        if (unit instanceof AssignStmt) {
            Value lhsOp = ((AssignStmt) unit).getLeftOp();

            StringVal v2lInterval = unionReturnFor(calledMethod, exitValue);
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

    StringVal unionReturnFor(SootMethod m, FlowSet in) {
        StringVal v2lInterval = null;
        List<Value> vals = returnlocals.get(m);
        Iterator valsit = vals.iterator();
        while (valsit.hasNext()) {
            Value vv = (Value) valsit.next();
            if (SUtility.isString(vv)) {
                StringVal vvlInterval = StringValOfValue(vv, in, m);
                if (v2lInterval != null)
                    v2lInterval = (StringVal) vvlInterval.pointWiseJoin(v2lInterval);
                else
                    v2lInterval = vvlInterval;
            }

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

            if (!SUtility.isPantomMethod(((InvokeExpr) rhsOp).getMethod()))
                ((ValueArraySparseSet) afterCallValue).removeStringValFor((Local) lhsOp);
            else if (rhsOp instanceof InstanceInvokeExpr) {
                {
                    InstanceInvokeExpr expr = (InstanceInvokeExpr) rhsOp;
                    SootMethod target = expr.getMethod();

                    List<Value> arguments = new ArrayList<>();
                    for (int i = 0; i < expr.getArgCount(); i++) {
                        arguments.add(expr.getArg(i));
                    }

                    if (SUtility.isBoolean(expr.getType()) && SUtility.isString(expr.getBase().getType())) {

                        FlowSet restricted = restrictCondition(expr, target, expr.getBase(), arguments, (FlowSet) inValue, context.getMethod(), "");
                        FlowSet outBranch = emptySet.clone();
                        ((ValueArraySparseSet) restricted).copy((FlowSet) outBranch);
                        setRestrictCondition((Local) lhsOp, context.getMethod(), outBranch);

                        //restrict the in flow based on the negation of the expression
                        restricted = restictNegCondition(expr, target, expr.getBase(), arguments, (FlowSet) inValue, context.getMethod());
                        FlowSet out = emptySet.clone();
                        ((ValueArraySparseSet) restricted).copy((FlowSet) out);
                        setRestictNegCondition((Local) lhsOp, context.getMethod(), out);

                    } else {
                        Automaton a = translateMethodCall(expr, target, expr.getBase(), arguments, inValue, context.getMethod());
                        StringVal v2lInterval = new StringVal((Local) lhsOp, a, false, a == null, false);
                        SUtility.replacestringvalForLocal((FlowSet) afterCallValue, v2lInterval);
                    }
                }
            }

        } else if (unit instanceof InvokeStmt) {
            {
                InvokeExpr expr1 = ((InvokeStmt) unit).getInvokeExpr();
                if (expr1 instanceof InstanceInvokeExpr) {
                    InstanceInvokeExpr expr = (InstanceInvokeExpr) expr1;
                    SootMethod target = expr.getMethod();
                    List<Value> arguments = new ArrayList<>();
                    for (int i = 0; i < expr.getArgCount(); i++) {
                        arguments.add(expr.getArg(i));
                    }
                    Automaton a = translateMethodCall(expr, target, expr.getBase(), arguments, inValue, context.getMethod());
                    //if (a != null) {
                    StringVal v2lInterval = new StringVal((Local) expr.getBase(), a, false, a == null, false);
                    SUtility.replacestringvalForLocal((FlowSet) afterCallValue, v2lInterval);
                    //}
                }
            }
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
            if (!f.isStatic() && SUtility.isString(f.getType())) {
                fStringVal inter = new fStringVal(f, null, false, true);
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
            if (!f.isStatic() && SUtility.isString(f.getType())) {
                fStringVal inter = new fStringVal(f, null, false, true);
                FieldRefs.add(inter);
            }
        }
        refFields.put(res, FieldRefs);
    }

    private void computeStaticFieldIntervlasForM(SootClass c) {

        if (staticFields.get(c) != null) return;
        FlowSet Fieldstatic = emptySet.clone();
        for (SootField f : c.getFields()) {
            if (f.isStatic() && SUtility.isString(f.getType())) {
                fStringVal inter = new fStringVal(f, null, false, true);
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
    public FlowSet meet(FlowSet in1, FlowSet in2, Unit succNode, Context<SootMethod, Unit, FlowSet> context) {

        FlowSet inSet1 = (FlowSet) in1,
                inSet2 = (FlowSet) in2;

        FlowSet outSet = emptySet.clone();
        int countvisits = SUtility.isLoop(getLoops(context.getMethod()), (Unit) succNode);

        if (verbose)
            System.out.println(((Unit) succNode).toString() + " countvisits " + countvisits);

        if (countvisits >= k)
            SUtility.widen(inSet1, inSet2, outSet);
        else
            SUtility.union(inSet1, inSet2, outSet);

        return outSet;
    }

    private Collection<Loop> computeLoops(Body b) {
        LoopFinder loopFinder = new LoopFinder();
        loopFinder.transform(b);

        Collection<Loop> loops = loopFinder.getLoops(b);
        return loops;
    }


}
