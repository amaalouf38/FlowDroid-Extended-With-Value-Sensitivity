package intervalanalysis.flowsets.ArrayIndices;

import intervalanalysis.flowsets.*;
import soot.SootMethod;
import soot.Local;
import soot.Value;
import soot.jimple.NumericConstant;

import java.util.ArrayList;
import java.util.List;

public class ArrayTaintIndicesHandler {
    static Boolean Verbose = false;
    static List<ArrayRefs> lstTaintedArrayLocals = new ArrayList<ArrayRefs>();


    public static String ToString() {
        StringBuilder builder = new StringBuilder();

        for (ArrayRefs lcl : lstTaintedArrayLocals) {
            builder.append(lcl.toString());
        }
        return builder.toString();
    }

    public static void duplicate(SootMethod m, Value leftValue, Value rightValue) {
        Boolean found = false;
        ArrayRefs lclR = Utility.FactoryArrayRef(m, rightValue);
        ArrayRefs lclsL = Utility.FactoryArrayRef(m, leftValue);

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lclR)) {
                found = true;
                lclR = l;
                break;
            }
        }
        lclsL.reason=lclR.reason;
        if (found) {
            //lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;
            found = false;
            for (ArrayRefs l : lstTaintedArrayLocals) {
                if (l.equals(lclsL)) {
                    found = true;
                    lclsL = l;
                    break;
                }
            }
            if (!found) {
                lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;
                lstTaintedArrayLocals.add(lclsL);
            } else {
                //for (StringTaint itv : lclR.lstTaintedIntervalsForLocal)
                //    lclsL.add(new StringTaint(itv.lower, itv.upper));

                lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;

            }
        }
        else
        {
            found = false;
            for (ArrayRefs l : lstTaintedArrayLocals) {
                if (l.equals(lclsL)) {
                    found = true;
                    lclsL = l;
                    break;
                }
            }
            if (!found) {
                lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;

                lstTaintedArrayLocals.add(lclsL);
                lstTaintedArrayLocals.add(lclR);
            } else {

                lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;
                lstTaintedArrayLocals.add(lclR);
            }
        }
        if (Verbose) {
            System.out.println("==duplicate ====" + m.getName() + " " + leftValue.toString() + " " + rightValue.toString());
            System.out.println(ToString());
        }
    }
    public static Local getResonOfTaint(SootMethod m, Value v)
    {
        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                lcls=l;
                break;
            }
        }
        return lcls.reason;

    }
    public static void setResonOfTaint(SootMethod m, Value v,Local reason)
    {
        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);
        lcls.reason=reason;

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                lcls=l;
                break;
            }
        }
        if (!found) lstTaintedArrayLocals.add(lcls);
        else lcls.reason=reason;

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.lstTaintedIntervalsForLocal==lcls.lstTaintedIntervalsForLocal){
                l.reason=lcls.reason;
            }
        }

    }
    public static void add(SootMethod m, Value v, InterVal i) {
        Boolean found = false;

        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);
        lcls.add(new ArrayIndiceInterval(i));

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                l.add(new ArrayIndiceInterval(i));
                break;
            }
        }

        if (!found) lstTaintedArrayLocals.add(lcls);
        if (Verbose) {
            System.out.println("==add ====" + m.getName() + " " + v.toString() + " " + i.toString());
            System.out.println(ToString());
        }
    }

    public static void add(SootMethod m, Value v, NumericConstant v2) {

        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        Double rvalue = new Double(((NumericConstant) v2).toString());
        lcls.add(new ArrayIndiceInterval((int) Math.floor(rvalue), (int) Math.ceil(rvalue),false));

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                l.add(new ArrayIndiceInterval((int) Math.floor(rvalue), (int) Math.ceil(rvalue),false));
                break;
            }
        }

        if (!found) lstTaintedArrayLocals.add(lcls);

        if (Verbose) {
            System.out.println("==add ====" + m.getName() + " " + v.toString() + " " + v2.toString());
            System.out.println(ToString());
        }
    }

    public static void empty(SootMethod m, Value v) {
        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                l.empty();
                break;
            }
        }
        if (Verbose) {
            System.out.println("==empty ====" + m.getName() + " " + v.toString());
            System.out.println(ToString());
        }
    }

    public static Boolean intersects(SootMethod m, Value v, InterVal i) {
        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                lcls = l;
                break;
            }
        }
        if (Verbose) {
            System.out.println("==intersects ====" + m.getName() + " " + v.toString() + " " + i.toString());
            System.out.println(ToString());
        }

        return lcls.intersect(new ArrayIndiceInterval(i));
    }

    public static Boolean intersects(SootMethod m, Value v, NumericConstant v2) {
        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        Double rvalue = new Double(((NumericConstant) v2).toString());

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                lcls = l;
                break;
            }
        }
        if (Verbose) {
            System.out.println("==intersects ====" + m.getName() + " " + v.toString() + " " + v2.toString());
            System.out.println(ToString());
        }

        return lcls.intersect(new ArrayIndiceInterval((int) Math.floor(rvalue), (int) Math.ceil(rvalue),false));
    }

    public static void remove(SootMethod m, Value v, InterVal i) {
        Boolean found = false;
        ArrayRefs lcls =Utility.FactoryArrayRef(m, v);

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                l.remove(new ArrayIndiceInterval(i));
                break;
            }
        }
        if (Verbose) {
            System.out.println("==remove ====" + m.getName() + " " + v.toString() + " " + i.toString());
            System.out.println(ToString());
        }
    }

    public static void remove(SootMethod m, Value v, NumericConstant v2) {

        Boolean found = false;
        ArrayRefs lcls = Utility.FactoryArrayRef(m, v);

        Double rvalue = new Double(((NumericConstant) v2).toString());

        for (ArrayRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                l.remove(new ArrayIndiceInterval((int) Math.floor(rvalue), (int) Math.ceil(rvalue),false));
                break;
            }
        }
        if (Verbose) {
            System.out.println("==remove ====" + m.getName() + " " + v.toString() + " " + v2.toString());
            System.out.println(ToString());
        }
    }
}
