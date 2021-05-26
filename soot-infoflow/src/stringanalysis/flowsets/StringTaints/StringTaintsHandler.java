package stringanalysis.flowsets.StringTaints;

import stringanalysis.flowsets.*;
import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class StringTaintsHandler {
    static Boolean Verbose = false;
    static List<StringRefs> lstTaintedArrayLocals = new ArrayList<StringRefs>();


    public static String ToString() {
        StringBuilder builder = new StringBuilder();

        for (StringRefs lcl : lstTaintedArrayLocals) {
            builder.append(lcl.toString());
        }
        return builder.toString();
    }
    public static List<StringTaint> getResonsOfTaint(Value v)
    {
        StringRefs lcls = SUtility.FactoryStringRef( v);
        boolean found =false;
        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found =true;
                lcls=l;
                break;
            }
        }
        return found?lcls.lstTaintedIntervalsForLocal:null;
    }
    public static void duplicate(Value leftValue, Value rightValue) {
        Boolean found = false;
        StringRefs lclR = SUtility.FactoryStringRef( rightValue);
        StringRefs lclsL = SUtility.FactoryStringRef( leftValue);

        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lclR)) {
                found = true;
                lclR = l;
                break;
            }
        }
        if (found) {
            //lclsL.lstTaintedIntervalsForLocal=lclR.lstTaintedIntervalsForLocal;
            found = false;
            for (StringRefs l : lstTaintedArrayLocals) {
                if (l.equals(lclsL)) {
                    found = true;
                    lclsL = l;
                    break;
                }
            }
            if (!found) {
                lclsL.lstTaintedIntervalsForLocal = lclR.lstTaintedIntervalsForLocal;
                lstTaintedArrayLocals.add(lclsL);
            } else {
                //for (StringTaint itv : lclR.lstTaintedIntervalsForLocal)
                //    lclsL.add(new StringTaint(itv.lower, itv.upper));

                lclsL.lstTaintedIntervalsForLocal = lclR.lstTaintedIntervalsForLocal;

            }
        } else {
            found = false;
            for (StringRefs l : lstTaintedArrayLocals) {
                if (l.equals(lclsL)) {
                    found = true;
                    lclsL = l;
                    break;
                }
            }
            if (!found) {
                lclsL.lstTaintedIntervalsForLocal = lclR.lstTaintedIntervalsForLocal;

                lstTaintedArrayLocals.add(lclsL);
                lstTaintedArrayLocals.add(lclR);
            } else {

                lclsL.lstTaintedIntervalsForLocal = lclR.lstTaintedIntervalsForLocal;
                lstTaintedArrayLocals.add(lclR);
            }
        }
        if (Verbose) {
            System.out.println("==duplicate ===="  + leftValue.toString() + " " + rightValue.toString());
            System.out.println(ToString());
        }
    }

    public static void add(Value v, Value taint) {
        Boolean found = false;

        StringRefs lcls = SUtility.FactoryStringRef( v);
        lcls.add(new StringTaint(taint));

        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                l.add(new StringTaint(taint));
                break;
            }
        }

        if (!found) lstTaintedArrayLocals.add(lcls);
        if (Verbose) {
            System.out.println("==add ====" + " " + v.toString() + " " + taint.toString());
            System.out.println(ToString());
        }
    }

    public static void add(Value v, List<StringTaint> taints) {
        Boolean found = false;

        StringRefs lcls = SUtility.FactoryStringRef( v);
        lcls.add(taints);

        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                found = true;
                l.add(taints);
                break;
            }
        }

        if (!found) lstTaintedArrayLocals.add(lcls);
        if (Verbose) {
            System.out.println("==add ====" + " " + v.toString() + " " + taints.toString());
            System.out.println(ToString());
        }
    }


    public static void empty( Value v) {
        Boolean found = false;
        StringRefs lcls = SUtility.FactoryStringRef( v);

        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                l.empty();
                break;
            }
        }
        if (Verbose) {
            System.out.println("==empty ====" + " " + v.toString());
            System.out.println(ToString());
        }
    }

    public static void remove(Value v, Value taint) {
        Boolean found = false;
        StringRefs lcls = SUtility.FactoryStringRef( v);

        for (StringRefs l : lstTaintedArrayLocals) {
            if (l.equals(lcls)) {
                l.remove(new StringTaint(taint));
                break;
            }
        }
        if (Verbose) {
            System.out.println("==remove ====" + " " + v.toString() + " " + taint.toString());
            System.out.println(ToString());
        }
    }

}
