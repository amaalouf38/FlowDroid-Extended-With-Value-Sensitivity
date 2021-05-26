package stringanalysis.flowsets.StringTaints;

import soot.Local;


import java.util.ArrayList;
import java.util.List;

public abstract class StringRefs {

    public List<StringTaint> lstTaintedIntervalsForLocal = new ArrayList<StringTaint>();

    public StringRefs() {

    }

    public void add(StringTaint i) {
        if (!lstTaintedIntervalsForLocal.contains(i)) {
            lstTaintedIntervalsForLocal.add(i);
        }
    }

    public void add(List<StringTaint> taints) {
        for (StringTaint i : taints) {
            if (!lstTaintedIntervalsForLocal.contains(i))
                lstTaintedIntervalsForLocal.add(i);
        }
    }

    public void empty() {
        lstTaintedIntervalsForLocal.clear();
    }

    public void remove(StringTaint i) {
        StringTaint remove = null;
        for (StringTaint l : lstTaintedIntervalsForLocal) {
            if (l.equals(i)) {
                remove = l;
                break;
            }
        }
        if (remove != null) {
            lstTaintedIntervalsForLocal.remove(remove);
        }

    }
}

