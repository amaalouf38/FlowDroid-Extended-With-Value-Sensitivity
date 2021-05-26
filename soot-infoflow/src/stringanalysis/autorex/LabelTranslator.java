package stringanalysis.autorex;


import dk.brics.automaton.Transition;

public interface LabelTranslator {
    String getTransitionString(Transition t);
}