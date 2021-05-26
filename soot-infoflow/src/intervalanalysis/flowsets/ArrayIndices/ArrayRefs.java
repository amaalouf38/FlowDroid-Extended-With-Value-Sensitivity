package intervalanalysis.flowsets.ArrayIndices;

import soot.Local;
import soot.SootMethod;


import java.util.ArrayList;
import java.util.List;

public abstract class ArrayRefs {

    public Local reason;
    public List<ArrayIndiceInterval> lstTaintedIntervalsForLocal = new ArrayList<ArrayIndiceInterval>();

    public ArrayRefs() {

    }

     public void add(ArrayIndiceInterval i) {
        if (!lstTaintedIntervalsForLocal.contains(i)) {
            lstTaintedIntervalsForLocal.add(i);
        }
    }

    public void empty() {
        lstTaintedIntervalsForLocal.clear();
        reason=null;
    }

    public void remove(ArrayIndiceInterval i) {

        List<ArrayIndiceInterval> lstIntervalsToRemove = new ArrayList<ArrayIndiceInterval>();
        List<ArrayIndiceInterval> lstIntervalsToAdd = new ArrayList<ArrayIndiceInterval>();

        for (ArrayIndiceInterval t : lstTaintedIntervalsForLocal) {

            if (i.lower <= t.lower && t.upper <= i.upper) {
                lstIntervalsToRemove.add(t);
            } else if (i.upper <= t.lower || i.lower >= t.upper) {
                if (i.upper == t.lower) {
                    t.openLeft = true;
                }
                if (i.lower == t.upper) {
                    t.openRight = true;
                }
                //do nothing
            } else if (t.lower <= i.lower && i.upper <= t.upper) {
                if (t.lower == i.lower) {
                    lstIntervalsToRemove.add(t);
                    lstIntervalsToAdd.add(new ArrayIndiceInterval(i.upper, t.upper,false));
                } else if (i.upper == t.upper) {
                    lstIntervalsToRemove.add(t);
                    lstIntervalsToAdd.add(new ArrayIndiceInterval(t.lower, i.lower,false));

                } else {
                    lstIntervalsToRemove.add(t);
                    lstIntervalsToAdd.add(new ArrayIndiceInterval(t.lower, i.lower,false));
                    lstIntervalsToAdd.add(new ArrayIndiceInterval(i.upper, t.upper,false));
                }
            } else if (i.lower <= t.lower && t.lower <= i.upper && i.upper <= t.upper) {
                lstIntervalsToRemove.add(t);
                lstIntervalsToAdd.add(new ArrayIndiceInterval(i.upper, t.upper, false, true,false));
            } else if (t.lower <= i.lower && i.lower <= t.upper && t.upper <= i.upper) {
                lstIntervalsToRemove.add(t);
                lstIntervalsToAdd.add(new ArrayIndiceInterval(t.lower, i.lower, true, false,false));
            }


        }
        for (ArrayIndiceInterval t : lstIntervalsToRemove) {
           lstTaintedIntervalsForLocal.remove(t);
        }
        for (ArrayIndiceInterval t : lstIntervalsToAdd) {
           lstTaintedIntervalsForLocal.add(t);
        }
    }

    public Boolean intersect(ArrayIndiceInterval i) {
        if(i.bottom)return false;
        for (ArrayIndiceInterval t : lstTaintedIntervalsForLocal) {

            if (!t.bottom && !(i.upper < t.lower || i.lower > t.upper)) {
                return true;
            }
        }
        return false;
    }
}

