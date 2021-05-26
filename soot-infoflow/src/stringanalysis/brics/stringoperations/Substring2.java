package stringanalysis.brics.stringoperations;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.StatePair;
import dk.brics.automaton.Transition;
import dk.brics.string.charset.CharSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Automaton operation for {@link StringBuffer#substring(int, int)}.
 */
public class Substring2 extends UnaryOperation {

    int index1=-1;
    int index2=-1;

    /**
     * Constructs new operation object.
     */
    //base 1
    //returns an over approximation of the strings that can occur in the orinal machine
    // from substringStartIndex (exluded) till substringEndIndex(included)

    public Substring2(int substringStartIndex, int substringEndIndex) {
        this.index1 = substringStartIndex;
        this.index2 = substringEndIndex;
    }

    /**
     * Automaton operation.
     * Constructs new automaton as copy of <tt>a</tt> with a new initial state <i>p</i> and a new accept state <i>s</i>.
     * Epsilon transitions are added from <i>p</i> to every other state and from all those to <i>s</i>.
     *
     * @param a input automaton
     * @return resulting automaton
     */
    @Override
    public Automaton op(Automaton a) {
        Automaton b = a.clone();
        if(this.index2!=-1)b=substringEndingAtEndIndex(b);
        if(this.index1!=-1)b=substringStartingfromStartIndex(b);
        return b;
    }

    private Automaton substringStartingfromStartIndex(Automaton a1) {
        Automaton a = a1.clone();
        Set<State> queue = new HashSet<State>();
        Set<State> queue2 = new HashSet<State>();
        Set<StatePair> epsilons = new HashSet<StatePair>();
        Set<State> live=a.getLiveStates();

        queue.add(a.getInitialState());
        for (int i = 0; i < index1; i++) {
            for (State state : queue) {
                for (Transition t : state.getTransitions()) {
                    //if(live.contains(t.getDest()))
                        queue2.add(t.getDest());
                }
            }

            // switch buffers
            queue.clear();
            Set<State> tmp = queue;
            queue = queue2;
            queue2 = tmp;
        }

        State initial = new State();
        a.setInitialState(initial);

        for (State state : queue) {
            epsilons.add(new StatePair(initial, state));
        }

        a.restoreInvariant();
        a.addEpsilons(epsilons);
        a.reduce();
        a.determinize();
        a.minimize();
        return a;
    }

    private Automaton substringEndingAtEndIndex(Automaton a1) {
        Automaton a = a1.clone();
        Set<State> queue = new HashSet<State>();
        Set<State> queue2 = new HashSet<State>();
        Set<StatePair> epsilons = new HashSet<StatePair>();
        Set<State> live=a.getLiveStates();

        queue.add(a.getInitialState());
        for (int i = 0; i < index2; i++) {
            for (State state : queue) {
                for (Transition t : state.getTransitions()) {
                    //if(live.contains(t.getDest()))
                        queue2.add(t.getDest());
                }
            }

            // switch buffers
            queue.clear();
            Set<State> tmp = queue;
            queue = queue2;
            queue2 = tmp;
        }

        State accpet = new State();
        accpet.setAccept(true);

        for (State state : queue) {
            epsilons.add(new StatePair( state,accpet));
        }
        for (State s : a.getAcceptStates()) {
            s.setAccept(false);
        }
        a.addEpsilons(epsilons);

        a.restoreInvariant();
        a.reduce();
        a.determinize();
        a.minimize();
        return a;
    }
    @Override
    public String toString() {
        return "substring2";
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public CharSet charsetTransfer(CharSet a) {
        return a;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Substring2;
    }
}
