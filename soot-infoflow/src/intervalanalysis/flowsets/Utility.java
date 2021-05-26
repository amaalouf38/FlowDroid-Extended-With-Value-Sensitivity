package intervalanalysis.flowsets;

import intervalanalysis.flowsets.ArrayIndices.*;

import intervalanalysis.vasco.Context;
import soot.*;
import soot.jimple.*;


import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.scalar.FlowSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Utility {
    private static Map loopVisitCount = new HashMap();

    public static Boolean compareContexts(Context<SootMethod, Unit, FlowSet> context, FlowSet flow) {
        FlowSet entry = context.getExitValue();
        return entry.equals(flow);
    }

    public static ArrayRefs FactoryArrayRef(SootMethod m, Value v) {
        ArrayRefs retValue = null;
        if (v instanceof FieldRef) {
            if (v instanceof StaticFieldRef) {
                SootClass c = ((FieldRef) v).getField().getDeclaringClass();
                retValue = new ArrayStaticRef(c, (StaticFieldRef) v);
            } else if (v instanceof InstanceFieldRef) {
                Value i = ((InstanceFieldRef) v).getBase();
                retValue = new ArrayFieldRefs(i, (InstanceFieldRef) v);
            }
        } else if (v instanceof Local) {
            retValue = new ArrayLocalRefs(m, (Local) v);
        }
        return retValue;
    }


    public static void union(FlowSet srouceFlow, FlowSet otherFlow, FlowSet destFlow) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
        ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;

//        if (srouce.hasBottom()) {
//            srouce.setBottom();
//        }
//
//        if (other.hasBottom()) {
//            other.setBottom();
//        }

        if (srouce.isEmpty() && !otherFlow.isEmpty()) {
            for (int i = 0; i < other.numElements; i++) {
                dest.add(other.elements[i]);
            }
        }
        if (!srouce.isEmpty() && otherFlow.isEmpty()) {
            for (int i = 0; i < srouce.numElements; i++) {
                dest.add(srouce.elements[i]);
            }
        }
        boolean found = false;
        for (int i = 0; i < srouce.numElements; i++) {
            found = false;
            InterVal currInt = (InterVal) srouce.elements[i];
            for (int j = 0; j < other.numElements; j++) {
                InterVal otherInt = (InterVal) other.elements[j];
                if (currInt.sameLocal(otherInt)) {
                    found = true;
                    dest.add(currInt.pointWiseMeet(otherInt));
                    break;
                }
            }
            if (!found) dest.add(currInt);
        }

        for (int j = 0; j < other.numElements; j++) {
            found = false;
            InterVal otherInt = (InterVal) other.elements[j];
            for (int i = 0; i < dest.numElements; i++) {
                InterVal currInt = (InterVal) dest.elements[i];
                if (currInt.sameLocal(otherInt)) {
                    found = true;
                    break;
                }
            }
            if (!found) dest.add(otherInt);
        }
    }

    public static void widen(FlowSet srouceFlow, FlowSet otherFlow, FlowSet destFlow) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
        ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;

        if (srouce.isEmpty() && !otherFlow.isEmpty()) {
            for (int i = 0; i < other.numElements; i++) {
                dest.add(other.elements[i]);
            }
        }
        if (!srouce.isEmpty() && otherFlow.isEmpty()) {
            for (int i = 0; i < srouce.numElements; i++) {
                dest.add(srouce.elements[i]);
            }
        }
        boolean found = false;
        for (int i = 0; i < srouce.numElements; i++) {
            found = false;
            InterVal currInt = (InterVal) srouce.elements[i];
            for (int j = 0; j < other.numElements; j++) {
                InterVal otherInt = (InterVal) other.elements[j];
                if (currInt.sameLocal(otherInt)) {
                    found = true;
                    dest.add(currInt.pointWiseWiden(otherInt));
                    break;
                }
            }
            if (!found) dest.add(currInt);
        }

        for (int j = 0; j < other.numElements; j++) {
            found = false;
            InterVal otherInt = (InterVal) other.elements[j];
            for (int i = 0; i < dest.numElements; i++) {
                InterVal currInt = (InterVal) dest.elements[i];
                if (currInt.sameLocal(otherInt)) {
                    found = true;
                    break;
                }
            }
            if (!found) dest.add(otherInt);
        }
    }

    public static void intersection(FlowSet srouceFlow, FlowSet otherFlow, FlowSet destFlow) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
        ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;

        if (srouce.isEmpty() && !otherFlow.isEmpty()) {
            for (int i = 0; i < other.numElements; i++) {
                dest.add(other.elements[i]);
            }
        }
        if (!srouce.isEmpty() && otherFlow.isEmpty()) {
            for (int i = 0; i < srouce.numElements; i++) {
                dest.add(srouce.elements[i]);
            }
        }

        for (int i = 0; i < srouce.numElements; i++) {
            InterVal currInt = (InterVal) srouce.elements[i];

            for (int j = 0; j < other.numElements; j++) {
                InterVal otherInt = (InterVal) other.elements[j];
                if (currInt.sameLocal(otherInt)) {
                    dest.add(currInt.pointWiseJoin(otherInt));
                    break;
                }
            }
        }
    }

    public static InterVal getIntervalForLocal(FlowSet srouceFlow, Local l) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        InterVal retInterval = null;

        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((InterVal) srouce.elements[i]).sameLocal(l)) {
                InterVal sourceInterval = (InterVal) srouce.elements[i];
                retInterval = new InterVal(sourceInterval.l, sourceInterval.lower, sourceInterval.upper, sourceInterval.bottom);
                break;
            }
        }

        return retInterval;
    }

    public static void replaceIntervalForLocal(FlowSet srouceFlow, InterVal newinter) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((InterVal) srouce.elements[i]).sameLocal(newinter.l)) {
                srouce.elements[i] = newinter;
                break;
            }
        }
    }

    public static boolean isNumeric(Local l) {
        /*if (l instanceof IntType ||
        l instanceof ByteType ||
        l instanceof CharType||
        l instanceof DoubleType||
        l instanceof FloatType ||
        l instanceof IntType||
        l instanceof LongType||
        l instanceof ShortType)*/

        if (l.getType().toString().equals("int") ||
                l.getType().toString().equals("byte") ||
                l.getType().toString().equals("double") ||
                l.getType().toString().equals("float") ||
                l.getType().toString().equals("long") ||
                l.getType().toString().equals("short")
        )
            return true;
        else return false;
    }

    public static int isLoop(Collection<Loop> loops, Unit u) {
        int countvisits = -1;
        for (Loop loop : loops) {
            if (loop.getHead().equals(u)) {
                if (loopVisitCount.containsKey(loop.getHead())) {
                    countvisits = (int) loopVisitCount.get(loop.getHead()) + 1;
                    loopVisitCount.remove(loop.getHead());
                    loopVisitCount.put(loop.getHead(), countvisits);
                } else
                    loopVisitCount.put(loop.getHead(), 1);

            }
        }

        return countvisits;
    }

    public static boolean isNumeric(Value l) {
        /*if (l instanceof IntType ||
        l instanceof ByteType ||
        l instanceof CharType||
        l instanceof DoubleType||
        l instanceof FloatType ||
        l instanceof IntType||
        l instanceof LongType||
        l instanceof ShortType)*/
        String s = "";

        if (l instanceof FieldRef)
            s = ((FieldRef) l).getField().getType().toString();
        else if (l instanceof Local)
            s = ((Local) l).getType().toString();

        if (s.equals("int") ||
                s.equals("byte") ||
                s.equals("double") ||
                s.equals("float") ||
                s.equals("long") ||
                s.equals("short")
        )
            return true;
        else return false;


    }

    public static boolean isArray(Value l) {
        String s = "";

        if (l instanceof FieldRef)
            s = ((FieldRef) l).getField().getType().toString();
        else if (l instanceof Local)
            s = ((Local) l).getType().toString();

        if (s.contains("[]"))
            return true;
        else return false;

    }


    public static void replaceIntervalForRefField(FieldRef vf, InterVal vl, FlowSet srouceFlow) {
        SootField f = vf.getField();
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;

        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((fInterVal) srouce.elements[i]).sameRef(vf.getField())) {
                srouce.elements[i] = new fInterVal(vf.getField(), vl.lower, vl.upper, vl.bottom, vl.top);
                break;
            }
        }
    }

    public static InterVal getIntervalForRefField(FieldRef vf, FlowSet srouceFlow) {
        SootField f = vf.getField();
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;

        InterVal retInterval = null;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((fInterVal) srouce.elements[i]).sameRef(vf.getField())) {
                fInterVal sourceInterval = (fInterVal) srouce.elements[i];
                retInterval = new InterVal(null, sourceInterval.lower, sourceInterval.upper, sourceInterval.bottom, sourceInterval.top, false);
            }
        }

        return retInterval;
    }

    public static boolean isClassInSystemPackage(String className) {

        //return !className.startsWith("com.pierreduchemin.smsforward");
        //return !className.startsWith("de.bulling.barcodebuddyscanner");
        return !className.startsWith("com.greenaddress.greenbits");
        //return !className.startsWith("amaalouf.ou.taintofArrays");

        /*return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
                || className.startsWith("sun.") || className.startsWith("org.omg.")
                || className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
                || className.startsWith("com.android.");*/
    }

    /**
     * Checks whether the type belongs to a system package
     *
     * @param type The type to check
     * @return True if the given type belongs to a system package, otherwise false
     */
    public static boolean isClassInSystemPackage(Type type) {
        if (type instanceof RefType)
            return isClassInSystemPackage(((RefType) type).getSootClass().getName());
        return false;
    }

    // Exclude system and library classes
    public static boolean isPantomMethod(SootMethod sm) {

        // Exclude system classes
        if (Utility.isClassInSystemPackage(sm.getDeclaringClass().getName()))
            return true;

        // Exclude library classes
        if (sm.getDeclaringClass().isLibraryClass())
            return true;

        return false;
    }


}
