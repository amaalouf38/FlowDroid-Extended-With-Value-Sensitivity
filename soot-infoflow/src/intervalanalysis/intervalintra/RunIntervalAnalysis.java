package intervalanalysis.intervalintra;
import java.util.Collection;
import intervalanalysis.intervalintra.analyses.*;


import intervalanalysis.flowsets.InterVal;
import soot.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.annotation.logic.Loop;
import java.util.Iterator;
import java.util.List;

public class RunIntervalAnalysis {
    public static boolean performAnalysis=true;

    public static void main(String[] args) {

        args = new String[]{"intervalanalysis.testers.IntervalClass"};

        if (args.length == 0) {
            System.out.println("Usage: java RunIntervalAnalysis class_to_analyse");
            System.exit(0);
        }

        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        sClass.setApplicationClass();

        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();

            Body b = m.retrieveActiveBody();
            System.out.println("=======================================");
            System.out.println(m.toString());
            System.out.println("=======================================");
            System.out.println(b.toString());
            System.out.println("=======================================");

            if(!performAnalysis)continue;

            InterValAnalyses vbe = new SimpleIntervalAnalyses(m);
            UnitGraph graph = new ExceptionalUnitGraph(b);
            Iterator gIt = graph.iterator();
            while (gIt.hasNext()) {
                Unit u = (Unit) gIt.next();
                List before = vbe.getIntervalsBefore(u);
                List after = vbe.getIntervalsAfter(u);
                List branchafter = vbe.getIntervalsBranchAfter(u);

                UnitPrinter up = new NormalUnitPrinter(b);
                up.setIndent("");

                System.out.println("---------------------------------------");
                u.toString(up);
                System.out.println(up.output());
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

                System.out.print("Intervals branch out: {");
                sep = "";
                Iterator brcIt = branchafter.iterator();
                while (brcIt.hasNext()) {
                    FlowSet brcoutIt = (FlowSet) brcIt.next();
                    Iterator tbrcoutIt = brcoutIt.iterator();
                    System.out.print(" {");
                    while (tbrcoutIt.hasNext()) {
                        InterVal e = (InterVal) tbrcoutIt.next();
                        System.out.print(sep);
                        System.out.print(e.toString());
                        sep = ", ";
                    }
                    System.out.println("}");
                }
                System.out.println("}");
                System.out.println("---------------------------------------");
            }

            System.out.println("=======================================");
        }
    }

}
