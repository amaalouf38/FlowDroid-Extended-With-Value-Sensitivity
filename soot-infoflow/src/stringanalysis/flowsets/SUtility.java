package stringanalysis.flowsets;

import stringanalysis.flowsets.StringTaints.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.scalar.FlowSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SUtility {

    public static StringRefs FactoryStringRef(SootMethod m, Value v)
    {
        StringRefs retValue = null;
        if (v instanceof FieldRef ) {
            if (v instanceof StaticFieldRef)
            {
                SootClass c = ((FieldRef) v).getField().getDeclaringClass();
                retValue=new StringStaticRef(c,(StaticFieldRef)v);
            }
            else if (v instanceof InstanceFieldRef)
            {
                Value i = ((InstanceFieldRef) v).getBase();
                retValue=new StringFieldRefs(i,(InstanceFieldRef)v);
            }
        }
        else if (v instanceof Local)
        {
            retValue=new StringLocalRefs(m,(Local)v);
        }
        return retValue;
    }
    public static StringRefs FactoryStringRef( Value v)
    {
        StringRefs retValue = null;
        if (v instanceof FieldRef ) {
            if (v instanceof StaticFieldRef)
            {
                SootClass c = ((FieldRef) v).getField().getDeclaringClass();
                retValue=new StringStaticRef(c,(StaticFieldRef)v);
            }
            else if (v instanceof InstanceFieldRef)
            {
                Value i = ((InstanceFieldRef) v).getBase();
                retValue=new StringFieldRefs(i,(InstanceFieldRef)v);
            }
        }
        else if (v instanceof Local)
        {
            retValue=new StringLocalRefs((Local)v);
        }
        return retValue;
    }

    private static Map loopVisitCount = new HashMap();

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
            StringVal currInt = (StringVal) srouce.elements[i];
            for (int j = 0; j < other.numElements; j++) {
                StringVal otherInt = (StringVal) other.elements[j];
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
            StringVal otherInt = (StringVal) other.elements[j];
            for (int i = 0; i < dest.numElements; i++) {
                StringVal currInt = (StringVal) dest.elements[i];
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
            StringVal currInt = (StringVal) srouce.elements[i];
            for (int j = 0; j < other.numElements; j++) {
                StringVal otherInt = (StringVal) other.elements[j];
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
            StringVal otherInt = (StringVal) other.elements[j];
            for (int i = 0; i < dest.numElements; i++) {
                StringVal currInt = (StringVal) dest.elements[i];
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
            StringVal currInt = (StringVal) srouce.elements[i];

            for (int j = 0; j < other.numElements; j++) {
                StringVal otherInt = (StringVal) other.elements[j];
                if (currInt.sameLocal(otherInt)) {
                    dest.add(currInt.pointWiseJoin(otherInt));
                    break;
                }
            }
        }
    }

    public static StringVal getstringvalForLocal(FlowSet srouceFlow, Local l) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        StringVal retInterval = null;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((StringVal) srouce.elements[i]).sameLocal(l)) {
                StringVal sourceInterval = (StringVal) srouce.elements[i];
                retInterval = new StringVal(sourceInterval.l, sourceInterval.a.clone(), sourceInterval.bottom, sourceInterval.top, sourceInterval.isDummy);
                break;
            }
        }
        return retInterval;

    }

    public static void replacestringvalForLocal(FlowSet srouceFlow, StringVal newinter) {
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((StringVal) srouce.elements[i]).sameLocal(newinter.l)) {
                srouce.elements[i] = newinter;
                break;
            }
        }
    }

    public static boolean isNumeric(Local l) {

        Type s = l.getType();

        if (s.equals(IntType.v()) ||
                s.equals(ByteType.v()) ||
                s.equals(DoubleType.v()) ||
                s.equals(FloatType.v()) ||
                s.equals(LongType.v()) ||
                s.equals(ShortType.v())
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
        Type s = l.getType();

        if (s.equals(IntType.v()) ||
                s.equals(ByteType.v()) ||
                s.equals(DoubleType.v()) ||
                s.equals(FloatType.v()) ||
                s.equals(LongType.v()) ||
                s.equals(ShortType.v())
        )
            return true;
        else return false;


    }

    public static boolean isString(Value l) {
        return isString(l.getType());
    }

    public static boolean isString(Type t)
    {
        //||t.equals(ArrayType.v(CharType.v(),1))
        return t.equals(CharType.v()) ||t.equals(ArrayType.v(CharType.v(),1))||t.equals(RefType.v("java.lang.StringBuffer")) ||t.equals(RefType.v("java.lang.String")) || t.equals(RefType.v("java.lang.StringBuilder"));
    }
    public static boolean isBoolean(Type t)
    {
                return t.equals(BooleanType.v());
    }

    public static boolean isString(SootClass c) {

        return c.getName().equals("java.lang.String");
    }

    /**
     * Checks whether the given type is <code>char</code>.
     */
    public static boolean isChar(Type t) {
        return t.equals(CharType.v());
    }

    public static Integer trackInteger(Value val) {
        if (val instanceof IntConstant) {
            return ((IntConstant) val).value;
        }
        // TODO: make some more intelligent tracking of integers
        return null;
    }

    /**
     * Attempts to find a constant string value. Returns null if unable to determine constant
     */
    public static String trackString(Value val) {
        if (val instanceof StringConstant) {
            return ((StringConstant) val).value;
        }
        return null;
    }

    /**
     * Checks whether the given type is <code>java.lang.CharSequence</code>.
     */
    public static boolean isCharSequence(Type t) {
        return t.equals(RefType.v("java.lang.CharSequence"));
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


    public static void replacestringvalForRefField(FieldRef vf, StringVal vl, FlowSet srouceFlow) {
        SootField f = vf.getField();
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((fStringVal) srouce.elements[i]).sameRef(vf.getField())) {
                srouce.elements[i] = new fStringVal(vf.getField(), vl.a.clone(), vl.bottom, vl.top);
                break;
            }
        }
    }

    public static StringVal getstringvalForRefField(FieldRef vf, FlowSet srouceFlow) {
        SootField f = vf.getField();
        ValueArraySparseSet srouce = (ValueArraySparseSet) srouceFlow;
        StringVal retInterval = null;
        int i;
        for (i = 0; i < srouce.numElements; i++) {
            if (((fStringVal) srouce.elements[i]).sameRef(vf.getField())) {
                fStringVal sourceInterval = (fStringVal) srouce.elements[i];
                retInterval = new StringVal(null, sourceInterval.a.clone(), sourceInterval.bottom, sourceInterval.top, false);
            }
        }
        return retInterval;
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
        if (SUtility.isClassInSystemPackage(sm.getDeclaringClass().getName()))
            return true;

        // Exclude library classes
        if (sm.getDeclaringClass().isLibraryClass())
            return true;

        return false;
    }

    public static boolean isBufferOrBuilder(Type t) {
        return isStringBuffer(t) || isStringBuilder(t);
    }

    public static boolean isBufferOrBuilder(SootClass t) {
        return isStringBuffer(t) || isStringBuilder(t);
    }

    public static boolean isObjectClass(SootClass c) {
        return c.getName().equals("java.lang.Object");
    }

    /**
     * Checks whether the given type is <code>StringBuffer</code>.
     */
    public static boolean isStringBuffer(Type t) {
        return t.equals(RefType.v("java.lang.StringBuffer"));
    }

    /**
     * Checks whether the given class is <code>StringBuffer</code>.
     */
    public static boolean isStringBuffer(SootClass c) {
        return c.getName().equals("java.lang.StringBuffer");
    }

    /**
     * Checks whether the given type is <code>StringBuilder</code>.
     */
    public static boolean isStringBuilder(Type t) {
        return t.equals(RefType.v("java.lang.StringBuilder"));
    }

    /**
     * Checks whether the given class is <code>StringBuilder</code>.
     */
    public static boolean isStringBuilder(SootClass c) {
        return c.getName().equals("java.lang.StringBuilder");
    }

    public static boolean isInt(Type t) {
        return t.equals(IntType.v());
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

}
