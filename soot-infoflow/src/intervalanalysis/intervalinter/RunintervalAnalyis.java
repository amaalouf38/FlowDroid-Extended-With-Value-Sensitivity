package intervalanalysis.intervalinter;



/**
 * Copyright (C) 2013 Rohan Padhye
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */


import intervalanalysis.flowsets.InterVal;
import intervalanalysis.intervalinter.analyses.*;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.PrintWriter;
        import java.util.HashSet;
        import java.util.Iterator;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;
        import java.util.Stack;

        //import org.junit.Test;
        import soot.*;
        import soot.jimple.toolkits.callgraph.Edge;
        import intervalanalysis.vasco.CallSite;
        import intervalanalysis.vasco.Context;
        import intervalanalysis.vasco.ContextTransitionTable;
        import soot.toolkits.scalar.FlowSet;
import intervalanalysis.vasco.DataFlowSolution;
import soot.toolkits.scalar.FlowSet;

/**
 * A main class for testing call graph construction using a Flow and Context
 * Sensitive Points-to Analysis (FCPA).
 *
 * <p>Usage: <tt>java vasco.callgraph.CallGraphTest [-cp CLASSPATH] [-out DIR] [-k DEPTH] MAIN_CLASS</tt></p>
 *
 * @author Rohan Padhye
 */
public class RunintervalAnalyis {

    private static String outputDirectory;

    public static void main(String args[]) {
        outputDirectory = ".";
        String classPath = System.getProperty("java.class.path");
        String mainClass = "intervalanalysis.testers.IntervalClass";
        int callChainDepth = 10;

		/* ------------------- SOOT OPTIONS ---------------------- */
        String[] sootArgs = {
                "-cp", classPath, "-pp",
                "-w", "-app",
                "-keep-line-number",
                "-keep-bytecode-offset",
                "-p", "cg", "implicit-entry:false",
                "-p", "cg.spark", "enabled",
                "-p", "cg.spark", "simulate-natives",
                "-p", "cg", "safe-forname",
                "-p", "cg", "safe-newinstance",
                "-main-class", mainClass,
                "-f", "none", mainClass
        };


		/* ------------------- ANALYSIS ---------------------- */
        CallGraphTransformer cgt = new CallGraphTransformer();
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.fcpa", cgt));
        soot.Main.main(sootArgs);
        IntervalAnalyis pointsToAnalysis = cgt.getIntervalAnalysis();

        DataFlowSolution<Unit,FlowSet> solution =pointsToAnalysis.getMeetOverValidPathsSolution();

        for (SootMethod method : pointsToAnalysis.getMethods()) {
            System.out.println("=======================================");
            System.out.println(method.toString());
            System.out.println("=======================================");

            for (Context<SootMethod, Unit, FlowSet> context : pointsToAnalysis.getContexts(method)) {
                System.out.println("=======================================");
                System.out.println("Context " + context.toString());
                System.out.println("=======================================");

                for (Unit node : pointsToAnalysis.programRepresentation().getControlFlowGraph(method)) {
                    FlowSet before = context.getValueBefore(node);
                    FlowSet after = context.getValueAfter(node);

                    //node, context

                    System.out.println("---------------------------------------");
                    System.out.println( node.toString());
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

                FlowSet before= solution.getValueBefore(node);
                FlowSet after=solution.getValueAfter(node);

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



