package intervalanalysis.intervalinter.analyses;

import intervalanalysis.flowsets.*;
import intervalanalysis.vasco.CallSite;
import intervalanalysis.vasco.Context;
import intervalanalysis.vasco.DataFlowSolution;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.scalar.FlowSet;

import java.util.*;

public class OutputIntervalAnalysis {
    static IntervalAnalyis pointsToAnalysis;
    static DataFlowSolution<Unit, FlowSet> solution;
    static Map<Unit, List<FlowSet>> solutionForUnit;

    public static void initOutputIntervalAnalysis(IntervalAnalyis pointsToAnalysis) {
        OutputIntervalAnalysis.pointsToAnalysis = pointsToAnalysis;
        OutputIntervalAnalysis.solution = pointsToAnalysis.getMeetOverValidPathsSolution();

        OutputIntervalAnalysis.solutionForUnit = new HashMap<Unit, List<FlowSet>>();
        for (SootMethod method : pointsToAnalysis.getMethods()) {
            for (Unit node : pointsToAnalysis.programRepresentation().getControlFlowGraph(method)) {

                FlowSet b = solution.getValueBefore(node);
                FlowSet a = solution.getValueAfter(node);
                List<FlowSet> lst = new ArrayList<FlowSet>();
                lst.add(b.clone());
                lst.add(a.clone());

                solutionForUnit.put(node, lst);

            }
        }

    }

    //attempt to fix the context sensitivity of the product
    //the traverse of the icfg bottom up does not resolve context
    //traverse top down and keep context information
    //contextTransitions at a callsite and a current context returns the new context and so forth
    public static InterVal outputIntervalAnalysisWithContext(Unit n, Value leftIndex, SootMethod m, List<Unit> callStack) {
        InterVal e = null;
        FlowSet before = null;
        List<Context<SootMethod, Unit, FlowSet>> cntxtsMethod = pointsToAnalysis.getContexts(m);
        if (cntxtsMethod.size() > 1) {

            Context<SootMethod, Unit, FlowSet> cntxt = resolveContext(n, m, callStack);
            //FlowSet argCall = constructArgsCall(callSite, m, callerContext);
            //Context<SootMethod, Unit, FlowSet> cntxt = null;
            //for (Context<SootMethod, Unit, FlowSet> calleecntxt : cntxtsMethod) {
            //   if (Utility.compareContexts(calleecntxt, argCall)) {
            //       cntxt = calleecntxt;
            //       break;
            //   }
            //}

            before = cntxt.getValueBefore(n);
        } else if (cntxtsMethod.size() == 1) {
            before = cntxtsMethod.get(0).getValueBefore(n);

        }
        if (before != null) {
            e = Utility.getIntervalForLocal(before, (Local) leftIndex);
        }
        return e;
    }

    private static Context<SootMethod, Unit, FlowSet> resolveContext(Unit callSite, SootMethod mcallSite, List<Unit> callStack) {
        List<Context<SootMethod, Unit, FlowSet>> cntxtsMethod = pointsToAnalysis.getContexts(mcallSite);
        Context<SootMethod, Unit, FlowSet> rtrnCntxt = null;

        if (cntxtsMethod.size() > 1) {
            Unit ncallSite = null;
            SootMethod nmcallSite = null;
            if (callStack.size() > 1) {
                ncallSite = callStack.get(callStack.size() - 1);
                callStack.remove(callStack.size() - 1);
            }
            Context<SootMethod, Unit, FlowSet> callingContext = resolveContext(ncallSite, nmcallSite, callStack);

            CallSite<SootMethod, Unit, FlowSet> clsSite = new CallSite(callingContext, ncallSite);
            Map<SootMethod, Context<SootMethod, Unit, FlowSet>> a = pointsToAnalysis.getCallersOfAContext(clsSite);
            rtrnCntxt = a.get(mcallSite);

        } else if (cntxtsMethod.size() == 1) {
            rtrnCntxt = cntxtsMethod.get(0);

        }
        return rtrnCntxt;
    }

//    private static void resolveCallSite( Context<SootMethod, Unit, FlowSet> currentContext) {
//        Set<CallSite<SootMethod, Unit, FlowSet>> callers = pointsToAnalysis.getCallersOfAContext(currentContext);
//        if (callers != null) {
//            for (CallSite<SootMethod, Unit, FlowSet> callSite : callers) {
//                // Extract the calling context and node from the caller site.
//                Context<SootMethod, Unit, FlowSet> callingContext = callSite.getCallingContext();
//                Unit callNode = callSite.getCallNode();
//            }
//        }
//    }

//    private static FlowSet constructArgsCall(Unit callSite, SootMethod calledMethod, Context<SootMethod, Unit, FlowSet> callerContext) {
//        FlowSet in = callerContext.getValueBefore(callSite);
//        FlowSet out = pointsToAnalysis.getTopSet(calledMethod).clone();
//
//        InvokeExpr ie = ((Stmt) callSite).getInvokeExpr();
//        for (int i = 0; i < ie.getArgCount(); i++) {
//            Value arg = ie.getArg(i);
//            Local param = calledMethod.getActiveBody().getParameterLocal(i);
//
//            InterVal v2lInterval = Utility.getIntervalForLocal(in, (Local) arg);
//            v2lInterval.l = param;
//            Utility.replaceIntervalForLocal(out, v2lInterval);
//        }
//        return out;
//    }
//end of section attempt to fix the context sensitivity of the product

    public static List<FlowSet> outputIntervalAnalysis(Unit node) {

        if (solutionForUnit.containsKey(node))
            return OutputIntervalAnalysis.solutionForUnit.get(node);
        else
            return new ArrayList<FlowSet>();
    }

    public static InterVal outputIntervalAnalysis(Unit node, Value leftIndex, List<Unit> callStack) {
        List<FlowSet> lst = outputIntervalAnalysis(node);
        InterVal e = null;
        if (!lst.isEmpty()) {
            FlowSet before = lst.get(0);
            e = Utility.getIntervalForLocal(before, (Local) leftIndex);
        }

        return (e==null)?
                new InterVal((Local) leftIndex, true)
                :e;
    }

    public static void output() {

        //DataFlowSolution<Unit, FlowSet> solution = pointsToAnalysis.getMeetOverValidPathsSolution();
        System.out.println("=======================================");
        System.out.println("Output of Interval Analysis");
        System.out.println("=======================================");

        for (SootMethod method : pointsToAnalysis.getMethods()) {
            System.out.println("=======================================");
            System.out.println(method.toString());
            System.out.println("=======================================");
            System.out.println(method.getActiveBody().toString());
            System.out.println("=======================================");
            System.out.println("==Method Local Variable=================");
            System.out.println("=======================================");
            for (Local l : method.getActiveBody().getLocals())
                System.out.println(l.toString());

            System.out.println("=======================================");

            for (Context<SootMethod, Unit, FlowSet> context : pointsToAnalysis.getContexts(method)) {
                System.out.println("=======================================");
                System.out.println("Context " + context.toString());


                System.out.println("context.getEntryValue()");
                FlowSet entryVal = context.getEntryValue();
                Iterator entryValIt = entryVal.iterator();
                while (entryValIt.hasNext()) {
                    InterVal e = (InterVal) entryValIt.next();
                    System.out.print(e.toString());
                }
                System.out.println("context.getExitValue()");
                FlowSet exitVal = context.getExitValue();
                Iterator exitValIt = entryVal.iterator();
                while (exitValIt.hasNext()) {
                    InterVal e = (InterVal) exitValIt.next();
                    System.out.print(e.toString());
                }
                System.out.println("=======================================");

                for (Unit node : pointsToAnalysis.programRepresentation().getControlFlowGraph(method)) {
                    FlowSet before = context.getValueBefore(node);
                    FlowSet after = context.getValueAfter(node);

                    //node, context

                    System.out.println("---------------------------------------");
                    System.out.println(node.toString());
                    System.out.print("Intervals in: {");
                    String sep = "";
                    Iterator befIt = before.iterator();
                    while (befIt.hasNext()) {
                        InterVal e = (InterVal) befIt.next();
                        System.out.print(sep);
                        System.out.print(e.toString());
                        sep = ", ";
                    }
                    System.out.println("}");
                    System.out.print("Intervals out: {");
                    sep = "";
                    Iterator aftIt = after.iterator();
                    while (aftIt.hasNext()) {
                        InterVal e = (InterVal) aftIt.next();
                        System.out.print(sep);
                        System.out.print(e.toString());
                        sep = ", ";
                    }
                    System.out.println("}");
                    System.out.println("---------------------------------------");

                }
                System.out.println("End of Context " + context.toString());
                System.out.println("=======================================");
            }
            System.out.println("=======================================");

        }
        for (SootMethod method : pointsToAnalysis.getMethods()) {
            //Body body = method.getActiveBody();
            System.out.println("Meet over all Valid paths solution");
            System.out.println("=======================================");
            System.out.println(method.toString());
            //System.out.println("=======================================");
            //System.out.println(body.toString());
            //System.out.println("=======================================");

            for (Unit node : pointsToAnalysis.programRepresentation().getControlFlowGraph(method)) {

                FlowSet before = solution.getValueBefore(node);
                FlowSet after = solution.getValueAfter(node);

                System.out.println("---------------------------------------");
                System.out.println(node.toString());
                System.out.print("Intervals in: {");
                String sep = "";
                Iterator befIt = before.iterator();
                while (befIt.hasNext()) {
                    InterVal e = (InterVal) befIt.next();
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Intervals out: {");
                sep = "";
                Iterator aftIt = after.iterator();
                while (aftIt.hasNext()) {
                    InterVal e = (InterVal) aftIt.next();
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.println("=======================================");
            }
        }

    }
}
