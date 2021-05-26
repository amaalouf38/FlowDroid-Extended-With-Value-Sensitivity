package stringanalysis.brics.stringoperations;

import dk.brics.automaton.*;
import dk.brics.string.charset.CharSet;

import java.util.*;

/**
 * Automaton operation for {@link StringBuffer#replace(int, int, String)}.
 */
public class Replace7 extends TernaryOperation {

    char p1 = '[';
    char p2 = ']';


    /**
     * Constructs new operation object.
     */
    public Replace7() {
    }

    //replaces any occurence of a word from L(a2) in L(a1) with a word from L(a3)
    @Override
    public Automaton op(Automaton a1, Automaton a2, Automaton a3) {

        //remove any transitions on the special characters we are
        //using as separatos and replacethem with any character

        a1 = SpecialOperations.subst(a1, p1, "#");
        a1 = SpecialOperations.subst(a1, p2, "#");

        a2 = SpecialOperations.subst(a2, p1, "#");
        a2 = SpecialOperations.subst(a2, p2, "#");

        a3 = SpecialOperations.subst(a3, p1, "#");
        a3 = SpecialOperations.subst(a3, p2, "#");

        //decompose returns an automaton that accepts any decomposition
        // in the original automaton
        //take for example w=abbc in the original language, the resulting automaton accepts a[bbc] a[bb]c [ab]bc and so forth
        Automaton a = decompose(a1);

        //returns an automaton that accepts words in the for from (w[x]w)* where w is a word
        //that does not contain any substrings from the original automaton
        // and x is a word in the oringial automaton
        Automaton b = decomposeInM2(a2);

        //intersects the resulting automatons from the first two operations to return an automaton that
        //accepts words from a1 that can be decomposed in the form (w[x]w)* where w does not contain
        //a substring from a2 and x is a substring from a2

        Automaton d = a.intersection(b);

        //searchs for a path of the from [x] in the automaton d and replaces it
        //with a word from a3

        Automaton e = replaceReach(d, a3);

        //the resulting language is words from a1 where any occurence of a2 is replaced with a3
        return e;
    }


    private Automaton decompose(Automaton a1) {

        Automaton a = a1.clone();
        Automaton b = new Automaton();
        Map<State, State> map = new HashMap<State, State>();
        for (State s : a.getStates()) {
            State ss = new State();
            map.put(s, ss);
        }

        for (State s : a.getStates()) {
            State ss = map.get(s);
            for (Transition t : s.getTransitions()) {
                State pp = map.get(t.getDest());
                ss.addTransition(new Transition(t.getMin(), t.getMax(), pp));
            }
        }

        for (State s : a.getStates()) {
            State ss = map.get(s);
            ss.addTransition(new Transition(p2, s));
            s.addTransition(new Transition(p1, ss));
        }

        a.restoreInvariant();
        a.reduce();
        a.determinize();
        a.minimize();
        return a;
    }


    private Automaton decomposeInM2(Automaton a1) {

        List<StateTransitionPair> newTransitions = new LinkedList<StateTransitionPair>();
        Set<StatePair> epsilons = new HashSet<StatePair>();

        Automaton a = a1.clone();
        Automaton ah = Automaton.makeAnyString().concatenate(a).concatenate(Automaton.makeAnyString());
        ah = ah.complement();

        ah = SpecialOperations.subst(ah, p1, "#");
        ah = SpecialOperations.subst(ah, p2, "#");

        Automaton m2 = a1.clone();
        for (State s : ah.getAcceptStates()) {
            newTransitions.add(new StateTransitionPair(s, new Transition(p1, m2.getInitialState())));
        }

        for (State s : m2.getAcceptStates()) {
            newTransitions.add(new StateTransitionPair(s, new Transition(p2, ah.getInitialState())));
        }

        for (State s : m2.getAcceptStates()) {
            s.setAccept(false);
        }

        for (StateTransitionPair pair : newTransitions) {
            pair.state.addTransition(pair.transition);
        }

        ah.restoreInvariant();
        ah.reduce();
        ah.determinize();
        ah.minimize();
        return ah;
    }


    private LinkedList<State> reach(State start) {
        LinkedList<State> path = new LinkedList<State>();
        Set<State> queue = new HashSet<State>();
        Set<State> queue2 = new HashSet<State>();
        Set<State> visited = new HashSet<State>();

        State s = start;
        s = s.step(p1);
        if (s == null) return null;

        queue.add(start);
        while (!queue.isEmpty()) {
            for (State state : queue) {
                if (visited.contains(state)) continue;
                visited.add(state);
                for (Transition t : state.getTransitions()) {
                    s = t.getDest();
                    s = s.step(p2);
                    if (s != null)
                        path.add(s);
                    queue2.add(t.getDest());
                }
            }
            // switch buffers
            queue.clear();
            Set<State> tmp = queue;
            queue = queue2;
            queue2 = tmp;
        }
        return path;
    }

    private Automaton replaceReach(Automaton a, Automaton replacement) {

        Automaton result = a.clone();
        Set<StatePair> epsilons = new HashSet<StatePair>();
        List<StateTransitionPair> killedTransitions = new LinkedList<StateTransitionPair>();
        List<StateTransitionPair> newTransitions = new LinkedList<StateTransitionPair>();

        for (State origin : result.getStates()) {
            LinkedList<State> paths = reach(origin);
            if (paths == null) continue;
            epsilons.add(new StatePair(origin, replacement.getInitialState()));

            for (State p : paths) {

                for (State s : replacement.getAcceptStates()) {

                    epsilons.add(new StatePair(s, p));
                     /*// set accept states of first and last
                    if (origin.isAccept()) {
                        replacement.getInitialState().setAccept(true);
                    }
                    if (p.isAccept()) {
                        s.setAccept(true);
                    }*/
                }
            }


            // remove the transition with the special character of
            // the search string from the origin state
            char first = p1;
            for (Transition tr : origin.getTransitions()) {
                if (tr.getMin() <= first && tr.getMax() >= first) {
                    killedTransitions.add(new StateTransitionPair(origin, tr));

                    // add back the remaining characters from the interval
                    if (tr.getMin() < first) {
                        newTransitions.add(new StateTransitionPair(origin, new Transition(tr.getMin(), (char) (first - 1), tr.getDest())));
                    }
                    if (tr.getMax() > first) {
                        newTransitions.add(new StateTransitionPair(origin, new Transition((char) (first + 1), tr.getMax(), tr.getDest())));
                    }
                }
            }
        }

        for (State s : replacement.getAcceptStates()) {
            s.setAccept(false);
        }

        // apply the first character removal first
        for (StateTransitionPair pair : killedTransitions) {
            pair.state.getTransitions().remove(pair.transition);
        }
        for (StateTransitionPair pair : newTransitions) {
            pair.state.addTransition(pair.transition);
        }

        result.addEpsilons(epsilons);

        result.restoreInvariant();
        result.reduce();
        result.determinize();
        result.minimize();

        return result;
    }

    //not used in the code
    private Automaton removeSpecialChars(Automaton a) {
        Automaton result = a.clone();
        List<StateTransitionPair> killedTransitions = new LinkedList<StateTransitionPair>();
        List<StateTransitionPair> newTransitions = new LinkedList<StateTransitionPair>();
        Set<State> queue = new HashSet<State>();
        Set<State> queue2 = new HashSet<State>();

        Set<State> visited = new HashSet<State>();
        char first = p1;
        char last = p2;
        State deadState = new State();
        queue.add(result.getInitialState());
        while (!queue.isEmpty()) {
            for (State state : queue) {
                if (visited.contains(state)) continue;

                visited.add(state);
                for (Transition tr : state.getTransitions()) {

                    if (tr.getMin() <= first && tr.getMax() >= first) {
                        killedTransitions.add(new StateTransitionPair(state, tr));
                        newTransitions.add(new StateTransitionPair(state, new Transition(first, deadState)));

                        // add back the remaining characters from the interval
                        if (tr.getMin() < first) {
                            newTransitions.add(new StateTransitionPair(state, new Transition(tr.getMin(), (char) (first - 1), tr.getDest())));
                        }
                        if (tr.getMax() > first) {
                            newTransitions.add(new StateTransitionPair(state, new Transition((char) (first + 1), tr.getMax(), tr.getDest())));
                        }
                    }

                    if (tr.getMin() <= last && tr.getMax() >= last) {
                        killedTransitions.add(new StateTransitionPair(state, tr));
                        newTransitions.add(new StateTransitionPair(state, new Transition(last, deadState)));
                        // add back the remaining characters from the interval
                        if (tr.getMin() < last) {
                            newTransitions.add(new StateTransitionPair(state, new Transition(tr.getMin(), (char) (last - 1), tr.getDest())));
                        }
                        if (tr.getMax() > last) {
                            newTransitions.add(new StateTransitionPair(state, new Transition((char) (last + 1), tr.getMax(), tr.getDest())));
                        }
                    }

                    queue2.add(tr.getDest());
                }

            }
            queue.clear();
            queue = queue2;
        }

        // apply the first character removal first
        for (StateTransitionPair pair : killedTransitions) {
            pair.state.getTransitions().remove(pair.transition);
        }
        for (StateTransitionPair pair : newTransitions) {
            pair.state.addTransition(pair.transition);
        }
        result.restoreInvariant();
        result.reduce();
        result.determinize();
        result.minimize();
        return result;
    }

    @Override
    public String toString() {
        return "replace7";
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public CharSet charsetTransfer(CharSet a1, CharSet a2) {
        return a1.union(a2);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Replace7;
    }


    /**
     * A state and a transition. The transition will typically be
     * outgoing from the state, though it is not necessary.
     * Does NOT currently have equals() and hashCode()!
     */
    private static final class StateTransitionPair {
        public State state;
        public Transition transition;

        public StateTransitionPair(State state, Transition transition) {
            this.state = state;
            this.transition = transition;
        }
    }

}

