package intervalanalysis.flowsets;

import soot.*;

import java.lang.Math;

public class InterVal
        implements EquivTo {
    public static boolean INCREMEET=true;
    public Local l;
    public int lower;
    public int upper;
    public boolean bottom = false;
    public boolean top = false;
    public boolean isDummy = false;

    public boolean isBottom() {
        return bottom;
    }

    public boolean isTop() {
        return top||(!this.bottom && this.lower == Integer.MIN_VALUE && this.upper == Integer.MAX_VALUE);
    }

    Integer[] belowConstants = new Integer[]{-10, -50, -100, -150, -200, -250, 300, 350, 400, 500, 600, 700, 1000};
    Integer[] aboveConstants = new Integer[]{10, 50, 100, 150, 200, 250, 300, 350, 400, 500, 600, 700, 1000};

    public InterVal(Local l, int lower, int upper) {
        this.l = l;
        this.lower = lower;
        this.upper = upper;
    }

    public InterVal(Local l, int lower, int upper, boolean bottom) {
        this.l = l;
        this.lower = lower;
        this.upper = upper;
        this.bottom = bottom;
    }
    public InterVal(Local l, int lower, int upper, boolean bottom,boolean top,boolean isDummy) {
        this.l = l;
        this.lower = lower;
        this.upper = upper;
        this.bottom = bottom;
        this.top = top;
        this.isDummy=isDummy;
    }

    public InterVal(Local l, boolean top) {
        this.l = l;
        this.top = top;
        if (top) {
            this.lower = Integer.MIN_VALUE;
            this.upper = Integer.MAX_VALUE;
        }
    }

    public boolean isLessthanOrEqual(InterVal o) {
        if (this.isBottom()) return true;
        else if (o.isBottom()) return false;
        else return (this.lower >= o.lower && this.upper <= o.upper);

    }

    public boolean sameLocal(InterVal o) {
        if (this.l == null || o.l == null) return false;
        if (this.l.getName().equals(o.l.getName()))
            return true;
        return false;

    }

    public boolean sameLocal(Local l) {
        if (this.l == null || l == null) return false;
        if (this.l.getName().equals(l.getName()))
            return true;
        return false;

    }

    // lup u
    public Object pointWiseMeet(InterVal o) {
        //if(!sameLocal(o)) return null;
        if (this.isBottom()) return o;
        else if (o.isBottom()) return this;
        else return new InterVal(this.l, Math.min(this.lower, o.lower), Math.max(this.upper, o.upper));

    }

    //glb n
    public Object pointWiseJoin(InterVal o) {
        //if(!sameLocal(o)) return null;
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        else if (Math.max(this.lower, o.lower) > Math.min(this.upper, o.upper)) return new InterVal(this.l, 0, 0, true);
        else return new InterVal(this.l, Math.max(this.lower, o.lower), Math.min(this.upper, o.upper));

    }

    // widening
    public Object pointWiseWiden(InterVal o) {
        if (this.isBottom()) return o;
        else if (o.isBottom()) return this;
        else
            return new InterVal(this.l, o.lower < this.lower ? nrstCstBelow(Math.min(this.lower, o.lower)) : this.lower, o.upper > this.upper ? nrstCstAbove(Math.max(this.upper, o.upper)) : this.upper);

    }

    int nrstCstAbove(int uperbound) {
        if(INCREMEET) {

            int i = 0;
            while (i < aboveConstants.length && uperbound > aboveConstants[i]) {
                i++;
            }
            if (i < aboveConstants.length) return aboveConstants[i];
            return Integer.MAX_VALUE;
        }
        else
        return Integer.MAX_VALUE;


    }

    int nrstCstBelow(int lowerbound) {

if(INCREMEET) {
    int i = 0;
    while (i < belowConstants.length && lowerbound < belowConstants[i]) {
        i++;
    }
    if (i < belowConstants.length) return belowConstants[i];
    return Integer.MIN_VALUE;
}
else
        return Integer.MIN_VALUE;

    }

    @Override
    public boolean equivTo(Object o) {
        if (this.l == null) return false;
        if (this.l.getName().equals(((InterVal) o).l.getName())
                && this.lower == ((InterVal) o).lower
                && this.upper == ((InterVal) o).upper)
            return true;
        return false;
    }

    @Override
    public int equivHashCode() {
        return this.hashCode();
    }

    int add(int a, int b) {
        long result = ((long) a) + b;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) result;
    }

    int diff(int a, int b) {
        long result = ((long) a) - b;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) result;
    }

    //abstract sum of intervals
    public InterVal sum(InterVal o) {
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        if (this.isTop()) return this;
        else if (o.isTop()) return o;
        else return new InterVal(this.l, add(this.lower, o.lower), add(this.upper, o.upper));

    }

    //abstract difference of intervals
    public InterVal diff(InterVal o) {
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        if (this.isTop()) return this;
        else if (o.isTop()) return o;
        return new InterVal(this.l, diff(this.lower, o.upper), diff(this.upper, o.lower));

    }

    //abstract product of intervals
    public InterVal prod(InterVal o) {
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        if (this.isTop()) return this;
        else if (o.isTop()) return o;
        return new InterVal(this.l, minimum(new int[]{prod(this.lower , o.lower),prod( this.lower , o.upper), prod(this.upper , o.lower), prod(this.upper , o.upper)}), maximum(new int[]{prod(this.lower , o.lower),prod( this.lower , o.upper), prod(this.upper , o.lower), prod(this.upper , o.upper)}));

    }

    int prod(int a, int b) {

        double result = ((double) a) * b;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) result;
    }

    //abstract division of intervals
    public InterVal div(InterVal o) {
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        if (this.isTop()) return this;
        else if (o.isTop()) return o;
        else if (o.upper < 0 || o.lower > 0)
            return new InterVal(this.l, minimum(new int[]{div(this.lower , o.lower), div(this.lower , o.upper), div(this.upper , o.lower), div(this.upper , o.upper)}), maximum(new int[]{div(this.lower,o.lower), div(this.lower , o.upper),div( this.upper , o.lower), div(this.upper , o.upper)}));
        else return new InterVal(this.l, 0, 0, true);

    }
    int div(int a, int b) {
        double result = ((double) a) / b;
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (result < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) result;
    }


    public InterVal negate() {
        return new InterVal(this.l, -this.lower, -this.upper);
    }

    private int minimum(int[] args) {
        int min = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] < min) min = args[i];
        }
        return min;
    }

    private int maximum(int[] args) {
        int max = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] > max) max = args[i];
        }
        return max;
    }

    @Override
    public String toString() {
        String var = this.l == null ? "[ NULL" : "[ " + this.l.getName();

        if(this.isDummy )
            var += " ,isDummy]";
        else if (!this.bottom && this.lower == Integer.MIN_VALUE && this.upper == Integer.MAX_VALUE)
            var += " ,top]";
        else if (this.bottom)
            var += " ,bottom]";
        else
            var += " ,{" + this.lower + "," + this.upper + "}]";


        return var;
    }

    public InterVal above() {
        return new InterVal(this.l, this.lower, Integer.MAX_VALUE);
    }

    public InterVal below() {
        return new InterVal(this.l, Integer.MIN_VALUE, this.upper);
    }


}
