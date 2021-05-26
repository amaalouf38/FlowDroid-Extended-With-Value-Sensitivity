package stringanalysis.flowsets;
import dk.brics.automaton.Automaton;
import soot.*;

public class fStringVal {

        public SootField l;
        public Automaton a;

        public boolean bottom = false;
        public boolean top = false;

    public fStringVal(SootField l, Automaton a, boolean bottom , boolean top) {
        this.l = l;
        this.a = a;

        this.bottom = bottom;
        this.top = top;
    }

    public boolean sameRef(SootField vf)
    {
        return vf.getName().equals(l.getName());
    }
}
