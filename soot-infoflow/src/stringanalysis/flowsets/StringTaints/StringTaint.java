package stringanalysis.flowsets.StringTaints;
import stringanalysis.flowsets.*;
import soot.Value;

public class StringTaint {
    public Value reason;


    StringTaint(Value reason)
    {
        this.reason=reason;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        StringTaint instance = (StringTaint) obj;
        if (this == obj)
            return true;
        else if (instance.reason== this.reason )
            return true;
        else
            return false;
    }

    @Override
    public String toString()
    {
        return "{" + this.reason +"}";
    }
}
