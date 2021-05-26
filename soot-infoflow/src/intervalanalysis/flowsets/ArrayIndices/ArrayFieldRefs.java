package intervalanalysis.flowsets.ArrayIndices;

import soot.Value;
import soot.jimple.InstanceFieldRef;

public class ArrayFieldRefs extends ArrayRefs {
    InstanceFieldRef v ;
    Value instance;

    @Override
    public String toString()
    {
        StringBuilder builder =new StringBuilder();
        builder.append(instance.toString() + " " + v.getField().getName());
        for(ArrayIndiceInterval itv :lstTaintedIntervalsForLocal )
        {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public ArrayFieldRefs(Value instance, InstanceFieldRef v) {
        this.instance = instance;
        this.v = v;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayFieldRefs)) return false;
        ArrayFieldRefs instance = (ArrayFieldRefs) obj;

        if (this == obj)
            return true;
        else if (instance.instance.equals(this.instance) && instance.v.getField().getName().equals(this.v.getField().getName()))

            return true;

        else

            return false;

    }
}
