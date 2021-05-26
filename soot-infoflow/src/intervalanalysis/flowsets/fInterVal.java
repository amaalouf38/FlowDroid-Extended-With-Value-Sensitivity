package intervalanalysis.flowsets;
import soot.*;

public class fInterVal {

        public SootField l;
        public int lower;
        public int upper;

        public boolean bottom = false;
        public boolean top = false;

    public fInterVal(SootField l, int lower, int upper,boolean bottom ,boolean top) {
        this.l = l;
        this.lower = lower;
        this.upper = upper;

        this.bottom = bottom;
        this.top = top;
    }

    public boolean sameRef(SootField vf)
    {
        return vf.getName().equals(l.getName());
    }
}
