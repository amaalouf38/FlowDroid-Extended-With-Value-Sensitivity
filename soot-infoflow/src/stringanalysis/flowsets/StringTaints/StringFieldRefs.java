package stringanalysis.flowsets.StringTaints;

import soot.Value;
import soot.jimple.InstanceFieldRef;

public class StringFieldRefs extends StringRefs {
    InstanceFieldRef v ;
    Value instance;

    @Override
    public String toString()
    {
        StringBuilder builder =new StringBuilder();
        builder.append(instance.toString() + " " + v.getField().getName());
        for(StringTaint itv :lstTaintedIntervalsForLocal )
        {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public StringFieldRefs(Value instance, InstanceFieldRef v) {
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
        if (!(obj instanceof StringFieldRefs)) return false;
        StringFieldRefs instance = (StringFieldRefs) obj;

        if (this == obj)
            return true;
        else if (instance.instance.equals(this.instance) && instance.v.getField().getName().equals(this.v.getField().getName()))

            return true;

        else

            return false;

    }
}
