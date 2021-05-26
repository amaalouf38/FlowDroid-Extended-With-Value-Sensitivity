package intervalanalysis.flowsets.ArrayIndices;

import soot.Local;
import soot.SootMethod;

public class ArrayLocalRefs extends ArrayRefs {
    public SootMethod m;
    public Local v;

    @Override
    public String toString()
    {
        StringBuilder builder =new StringBuilder();
        builder.append(m.getName() + " " + v.getName());
        for(ArrayIndiceInterval itv :lstTaintedIntervalsForLocal )
        {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public ArrayLocalRefs(SootMethod m, Local v) {
        this.m = m;
        this.v = v;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayLocalRefs)) return false;
        ArrayLocalRefs instance = (ArrayLocalRefs) obj;

        if (this == obj)
            return true;
        else if (instance.m.equals(this.m) && instance.v.getName().equals(this.v.getName()))

            return true;

        else

            return false;

    }
}
