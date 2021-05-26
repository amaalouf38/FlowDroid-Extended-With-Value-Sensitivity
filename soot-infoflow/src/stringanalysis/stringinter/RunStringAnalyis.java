package stringanalysis.stringinter;


/**
 * Copyright (C) 2013 Rohan Padhye
 * <p>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


import stringanalysis.brics.stringoperations.*;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

import stringanalysis.flowsets.StringVal;
import stringanalysis.stringinter.analyses.*;

import java.util.Iterator;

//import org.junit.Test;
import soot.*;
import stringanalysis.vasco.Context;
import soot.toolkits.scalar.FlowSet;
import stringanalysis.vasco.DataFlowSolution;

/**
 * A main class for testing call graph construction using a Flow and Context
 * Sensitive Points-to Analysis (FCPA).
 *
 * <p>Usage: <tt>java vasco.callgraph.CallGraphTest [-cp CLASSPATH] [-out DIR] [-k DEPTH] MAIN_CLASS</tt></p>
 *
 * @author Rohan Padhye
 */
public class RunStringAnalyis {

    private static String outputDirectory;

    public static void main(String args[]) {

        /*
        UnaryOperation rep = new Substring2(3,7);
        Automaton a1 = new RegExp("abc[0-9]+abc").toAutomaton();
        Automaton e = rep.op(a1);*/

        Automaton a10 = Automaton.makeString("neutral");
        Automaton a11 = Automaton.makeString("I1t359-f");
        Automaton a12 = Automaton.makeString("neutral");
        Automaton a1 = a10.concatenate(a11).concatenate(a12);
        UnaryOperation rep = new Substring2(    15,-1);
        Automaton e = rep.op(a1);


        /*
        Replace7 rep = new Replace7();
        Automaton a10 = Automaton.makeString("abbbc");
        Automaton a11 = Automaton.makeString("dbbbf");
        Automaton a1 = a10.union(a11);
        Automaton b1 = new RegExp("b+").toAutomaton();
        Automaton e1 = new RegExp("e+").toAutomaton();
        Automaton e = rep.op(a1, b1, e1);

        Replace7 rep = new Replace7();
        Automaton a1 = new RegExp("[A-Za-z][A-Za-z0-9_.]*").toAutomaton();
        Automaton b1 = new RegExp("-?[0-9]+").toAutomaton();
        Automaton e1 = Automaton.makeString("***");
        Automaton e = rep.op(a1, b1, e1);
        */
    }


    public static void _main(String args[]) {
        outputDirectory = ".";
        String classPath = System.getProperty("java.class.path");
        String mainClass = "stringanalysis.testers.IntervalClass";
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
        StringAnalyis pointsToAnalysis = cgt.getIntervalAnalysis();

        DataFlowSolution<Unit, FlowSet> solution = pointsToAnalysis.getMeetOverValidPathsSolution();

        for (SootMethod method : pointsToAnalysis.getMethods()) {
            System.out.println("=======================================");
            System.out.println(method.toString());

            for (Context<SootMethod, Unit, FlowSet> context : pointsToAnalysis.getContexts(method)) {
                System.out.println("=======================================");
                System.out.println("Context " + context.toString());
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
                        StringVal e = (StringVal) befIt.next();
                        System.out.print(sep);
                        System.out.print(e.toString());
                        sep = ", ";
                    }
                    System.out.println("}");
                    System.out.print("Intervals out: {");
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
                    StringVal e = (StringVal) befIt.next();
                    System.out.print(sep);
                    System.out.print(e.toString());
                    sep = ", ";
                }
                System.out.println("}");
                System.out.print("Intervals out: {");
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



