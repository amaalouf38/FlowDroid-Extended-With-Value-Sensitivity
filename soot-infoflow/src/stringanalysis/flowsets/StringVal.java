package stringanalysis.flowsets;

import dk.brics.automaton.Automaton;

import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.SpecialOperations;
import stringanalysis.autorex.Autorex;
import soot.*;
import stringanalysis.brics.stringoperations.Basic;

import java.lang.Math;

public class StringVal
        implements EquivTo {


    public Local l;
    public Automaton a;

    public boolean bottom = false;
    public boolean top = false;
    public boolean isDummy = false;

    public boolean isBottom() {
        return bottom;
    }

    public boolean isTop() {
        return top;
    }

    public StringVal(Local l, Automaton a) {
        this.l = l;
        this.a = a;
    }


    public StringVal(Local l, Automaton a, boolean bottom, boolean top, boolean isDummy) {
        this.l = l;
        this.a = a;
        this.bottom = bottom;
        this.top = top;
        this.isDummy = isDummy;

        if (top) {
            this.a = topA();
        } else if (bottom) {
            this.a = bottomA();
        }
    }

    public Automaton bottomA() {
        return Automaton.makeEmpty();
    }

    public Automaton topA() {
        return Automaton.makeAnyString();
    }

    public boolean isLessthanOrEqual(StringVal o) {
        if (this.isBottom()) return true;
        else if (o.isBottom()) return false;
        else return (this.a.subsetOf(o.a));

    }

    public StringVal getComplement() {
        return new StringVal(this.l, this.a != null ? this.a.complement() : null, this.bottom, this.top, this.isDummy);
    }

    public StringVal getStringContain() {
        Automaton x = this.a != null ? Automaton.makeAnyString().concatenate(this.a).concatenate(Automaton.makeAnyString()) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getStringContained() {
        Automaton x = this.a != null ? Basic.getSubstringsOf(this.a) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getStringContainComplement() {
        Automaton x = this.a != null ? (Automaton.makeAnyString().concatenate(this.a).concatenate(Automaton.makeAnyString())).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getStringContainedComplement() {
        Automaton x = this.a != null ? (Basic.getSubstringsOf(this.a)).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getNotEmptyString() {
        Automaton x = this.a != null ? this.a.minus(Automaton.makeEmptyString()) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getStartsWith() {
        Automaton x = this.a != null ? this.a.concatenate(Automaton.makeAnyString()) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getPrefixes(StringVal e2Val) {
        Automaton x = (e2Val.a != null && this.a != null) ? this.a.intersection(Basic.getPrefixesOf(e2Val.a)) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getNotStartsWith() {
        Automaton x = this.a != null ? (this.a.concatenate(Automaton.makeAnyString())).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getNotPrefixes(StringVal e2Val) {
        Automaton x = (e2Val.a != null && this.a != null) ? (this.a.intersection(Basic.getPrefixesOf(e2Val.a))).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }


    public StringVal getEndsWith() {
        Automaton x = this.a != null ? Automaton.makeAnyString().concatenate(this.a) : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getSuffixes(StringVal e2Val) {
        Automaton x = (e2Val.a != null && this.a != null) ? (this.a.intersection(Basic.getSuffixesOf(e2Val.a))).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getNotEndsWith() {
        Automaton x = this.a != null ? (Automaton.makeAnyString().concatenate(this.a)).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }

    public StringVal getNotSuffixes(StringVal e2Val) {
        Automaton x = (e2Val.a != null && this.a != null) ? (this.a.intersection(Basic.getSuffixesOf(e2Val.a))).complement() : null;
        return new StringVal(this.l, x, this.bottom, this.top, this.isDummy);
    }


    public boolean sameLocal(StringVal o) {
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
    public Object pointWiseMeet(StringVal o) {
        //if(!sameLocal(o)) return null;
        if (this == o) return this;
        else if (this.isBottom()) return o;
        else if (o.isBottom()) return this;
        else {

            if (this.a != null && o.a != null)
                return new StringVal(this.l, this.a.union(o.a));
            else if (this.a == null)
                return new StringVal(this.l, o.a.clone());
            else if (o.a == null)
                return new StringVal(this.l, this.a.clone());
        }
        return null;
    }

    //glb n
    public Object pointWiseJoin(StringVal o) {
        //if(!sameLocal(o)) return null;
        if (this.isBottom()) return this;
        else if (o.isBottom()) return o;
        else {

            if (this.a != null && o.a != null)
                return new StringVal(this.l, this.a.intersection(o.a));
            else if (this.a == null)
                return new StringVal(this.l, bottomA());
            else if (o.a == null)
                return new StringVal(this.l, bottomA());
        }
        return null;

    }

    // widening
    public Object pointWiseWiden(StringVal o) {
        if (this.isBottom()) return o;
        else if (o.isBottom()) return this;
        else return new StringVal(this.l, widen(this.a, o.a));

    }

    private Automaton widen(Automaton a, Automaton b) {
        Automaton r = SpecialOperations.widen(a, b);
        r.minimize();
        return r;

    }

    @Override
    public boolean equivTo(Object o) {
        if (this == o) return true;
        else if (this.l == null || ((StringVal) o).l == null) return false;
        else if (this.l.getName().equals(((StringVal) o).l.getName()))
            if ((this.a == null && ((StringVal) o).a == null) || (this.a != null && ((StringVal) o).a != null && this.a.subsetOf(((StringVal) o).a) && (((StringVal) o).a).subsetOf(this.a)))

                return true;
        return false;
    }

    @Override
    public int equivHashCode() {
        return this.hashCode();
    }

    @Override
    public String toString() {
        String var = this.l == null ? "[ NULL" : "[ " + this.l.getName();

        if (this.isDummy)
            var += " ,isDummy]";
        else if (this.top)
            var += " ,top]";
        else if (this.bottom)
            var += " ,bottom]";
        else if (this.a != null) {
            //var += " ,{" + this.a.toString() + "}";
            //var += " ,{" + Autorex.getRegexFromAutomaton(this.a) + "}]";
            var += " ,{accepted:" + BasicOperations.getShortestExample(this.a, true) + "}";
            var += " ,{rejected:" + BasicOperations.getShortestExample(this.a, false) + "}]";
        } else
            var += " ,{NULL}";


        return var;
    }

}
