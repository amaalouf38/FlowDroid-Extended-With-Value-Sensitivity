package stringanalysis.flowsets;

import soot.SootMethod;
import soot.Local;

public class instanceID {
    SootMethod m;
    Local v;

    public instanceID(SootMethod m, Local v) {
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
        instanceID instance = (instanceID) obj;

        if (this == obj)
            return true;
        else if (instance.m.equals(this.m) && instance.v.getName().equals(this.v.getName()))

            return true;

        else

            return false;


    }


}
