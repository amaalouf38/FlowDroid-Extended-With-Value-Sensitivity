package stringanalysis.flowsets.StringTaints;

import soot.SootClass;
import soot.jimple.StaticFieldRef;

public class StringStaticRef extends StringRefs {
    StaticFieldRef v;
    SootClass c;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(c.getName() + " " + v.getField().getName());
        for (StringTaint itv : lstTaintedIntervalsForLocal) {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public StringStaticRef(SootClass c, StaticFieldRef v) {
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
        if (!(obj instanceof StringStaticRef)) return false;
        StringStaticRef instance = (StringStaticRef) obj;
        if (this == obj)
            return true;
        else if (instance.c.equals(this.c) && instance.v.getField().getName().equals(this.v.getField().getName()))

            return true;

        else

            return false;

    }
}
