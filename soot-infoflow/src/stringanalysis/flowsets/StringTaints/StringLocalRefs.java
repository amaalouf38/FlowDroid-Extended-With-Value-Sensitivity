package stringanalysis.flowsets.StringTaints;

import soot.Local;
import soot.SootMethod;

public class StringLocalRefs extends StringRefs {
    public SootMethod m;
    public Local v;

    @Override
    public String toString()
    {
        StringBuilder builder =new StringBuilder();
        builder.append(m==null?"":m.getName() + " " + v.getName());
        for(StringTaint itv :lstTaintedIntervalsForLocal )
        {
            builder.append(itv.toString());
        }
        return builder.toString();
    }

    public StringLocalRefs(SootMethod m, Local v) {
        this.m = m;
        this.v = v;
    }
    public StringLocalRefs( Local v) {
        this.v = v;
    }

    @Override
    public int hashCode() {

        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StringLocalRefs)) return false;
        StringLocalRefs instance = (StringLocalRefs) obj;

        if (this == obj)
            return true;
        else if(m==null) return this.v==instance.v;
        else if (instance.m.equals(this.m) && instance.v.getName().equals(this.v.getName()))

            return true;

        else

            return false;

    }
}
