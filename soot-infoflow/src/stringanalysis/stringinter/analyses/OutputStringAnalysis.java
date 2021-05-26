package stringanalysis.stringinter.analyses;

import stringanalysis.flowsets.*;
import stringanalysis.vasco.Context;
import stringanalysis.vasco.DataFlowSolution;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.toolkits.scalar.FlowSet;

import java.util.*;

public class OutputStringAnalysis {
    static StringAnalyis dataflowAnalysis;
    static DataFlowSolution<Unit, FlowSet> solution;
    static Map<Unit, List<FlowSet>> solutionForUnit;

    public static void initOutputIntervalAnalysis(StringAnalyis analysis) {
        OutputStringAnalysis.dataflowAnalysis = analysis;
        OutputStringAnalysis.solution = dataflowAnalysis.getMeetOverValidPathsSolution();
        OutputStringAnalysis.solutionForUnit = new HashMap<Unit, List<FlowSet>>();
        for (SootMethod method : dataflowAnalysis.getMethods()) {
            for (Unit node : dataflowAnalysis.programRepresentation().getControlFlowGraph(method)) {

                FlowSet b = solution.getValueBefore(node);
                FlowSet a = solution.getValueAfter(node);
                List<FlowSet> lst = new ArrayList<FlowSet>();
                lst.add(b.clone());
                lst.add(a.clone());

                solutionForUnit.put(node,lst);

            }
        }

    }

    public static List<FlowSet>  outputIntervalAnalysis(Unit node) {

        if (solutionForUnit.containsKey(node))
            return OutputStringAnalysis.solutionForUnit.get(node);
        else
            return new ArrayList<FlowSet>();
    }

    public static StringVal outputIntervalAnalysis(Unit node,Value leftIndex,Boolean b) {
        List<FlowSet> lst=outputIntervalAnalysis( node);
        StringVal e = new StringVal((Local) leftIndex, null, false, true,false);
        if(!lst.isEmpty())
        {
            int i=b?0:1;
            FlowSet before=lst.get(i);
            e= SUtility.getstringvalForLocal(before, (Local) leftIndex);
        }
         return e;
    }
    public static void output()
    {

        //DataFlowSolution<Unit, FlowSet> solution = dataflowAnalysis.getMeetOverValidPathsSolution();

        System.out.println("=======================================");
        System.out.println("Output of String Analysis");
        System.out.println("=======================================");

        for (SootMethod method : dataflowAnalysis.getMethods()) {
            System.out.println("=======================================");
            System.out.println(method.toString());
            System.out.println("=======================================");
            System.out.println(method.getActiveBody().toString());
            System.out.println("=======================================");

            for (Context<SootMethod, Unit, FlowSet> context : dataflowAnalysis.getContexts(method)) {
                System.out.println("=======================================");
                System.out.println("Context " + context.toString());
                System.out.println("=======================================");

                for (Unit node : dataflowAnalysis.programRepresentation().getControlFlowGraph(method)) {
                    FlowSet before = context.getValueBefore(node);
                    FlowSet after = context.getValueAfter(node);

                    //node, context

                    System.out.println("---------------------------------------");
                    System.out.println(node.toString());
                    System.out.print("Strings in: {");
                    String sep = "";
                    Iterator befIt = before.iterator();
                    while (befIt.hasNext()) {
                        StringVal e = (StringVal) befIt.next();
                        System.out.print(sep);
                        System.out.print(e.toString());
                        sep = ", ";
                    }
                    System.out.println("}");
                    System.out.print("Strings out: {");
                    sep = "";
                    Iterator aftIt = after.iterator();
                    while (aftIt.hasNext()) {
                        StringVal e = (StringVal) aftIt.next();
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
        for (SootMethod method : dataflowAnalysis.getMethods()) {
            //Body body = method.getActiveBody();
            System.out.println("Meet over all Valid paths solution");
            System.out.println("=======================================");
            System.out.println(method.toString());
            //System.out.println("=======================================");
            //System.out.println(body.toString());
            //System.out.println("=======================================");

            for (Unit node : dataflowAnalysis.programRepresentation().getControlFlowGraph(method)) {

                FlowSet before = solution.getValueBefore(node);
                FlowSet after = solution.getValueAfter(node);

                System.out.println("---------------------------------------");
                System.out.println(node.toString());
                System.out.print("Strings in: {");
                String sep = "";
                Iterator befIt = before.iterator();
                while (befIt.hasNext()) {
                    StringVal e = (StringVal) befIt.next();
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Strings out: {");
                sep = "";
                Iterator aftIt = after.iterator();
                while (aftIt.hasNext()) {
                    StringVal e = (StringVal) aftIt.next();
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
