package intervalanalysis.flowsets.ArrayIndices;

import soot.SootClass;
import soot.jimple.StaticFieldRef;

public class ArrayStaticRef extends ArrayRefs {
    StaticFieldRef v;
    SootClass c;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(c.getName() + " " + v.getField().getName());
        for (ArrayIndiceInterval itv : lstTaintedIntervalsForLocal) {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public ArrayStaticRef(SootClass c, StaticFieldRef v) {
        this.c = c;
        this.v = v;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayStaticRef)) return false;
        ArrayStaticRef instance = (ArrayStaticRef) obj;
        if (this == obj)
            return true;
        else if (instance.c.equals(this.c) && instance.v.getField().getName().equals(this.v.getField().getName()))

            return true;

        else

            return false;

    }
}
