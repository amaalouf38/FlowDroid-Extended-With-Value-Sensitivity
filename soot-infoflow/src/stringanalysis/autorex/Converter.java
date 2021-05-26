/**
 * autorex - fsm state eliminator
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package stringanalysis.autorex;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stringanalysis.autorex.autograph.Gnfa;
import stringanalysis.autorex.autograph.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SUtility class for converting an dk.brics automaton into our gnfa
 * representation
 */
public enum Converter {

    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(Converter.class);

    public Gnfa getGnfaFromAutomaton(Automaton auto) {
        return getGnfaFromAutomaton(auto, new DefaultLabelTranslator());
    }

    /**
     * convert dk.brics automaton into gnfa
     * @param auto dk.brics automaton
     * @param ltrans label translator
     *
     * @return generalized non-deterministic finite automaton
     */
    public Gnfa getGnfaFromAutomaton(Automaton auto, LabelTranslator ltrans) {

        Gnfa ag = new Gnfa();
        AutomatonTrans at = new AutomatonTrans(auto, ltrans);

        Map<State,stringanalysis.autorex.autograph.State> smap = new HashMap<>();
        Set<stringanalysis.autorex.autograph.State> accepting = new HashSet<>();
        Set<stringanalysis.autorex.autograph.State> start = new HashSet<>();


        stringanalysis.autorex.autograph.State end =  new stringanalysis.autorex.autograph
                .State(stringanalysis.autorex
                .autograph.State.Kind.ACCEPT);

        stringanalysis.autorex.autograph.State init =  new stringanalysis.autorex.autograph
                .State(stringanalysis.autorex
                .autograph.State.Kind.START);

        ag.addVertex(end);
        ag.addVertex(init);


        for(FullTransition ft : at.transitions){
            State src = ft.getSourceState();
            State dst = ft.getTargetState();

            stringanalysis.autorex.autograph.State arxsrc = getStateFrom(start,
                    accepting, smap, at, src);
            stringanalysis.autorex.autograph.State arxdst = getStateFrom(start,
                    accepting, smap, at, dst);

            Transition t = null;
            if(ag.containsEdge(arxsrc, arxdst)) {
                t = ag.getEdge(arxsrc, arxdst);
                t.getLabel().append("|");
                t.getLabel().append(ft
                        .getTransitionLabel());
            } else {
                t = new Transition(arxsrc, arxdst, Transition.Kind
                        .MATCH,ft
                        .getTransitionLabel());
            }

            ag.addVertex(arxdst);
            ag.addVertex(arxsrc);
            ag.addEdge(t);
        }

        accepting.forEach(
            a -> {
                Transition t = new Transition(a,end,Transition.Kind
                        .EPSILON);
                ag.addEdge(t);
            }
        );

        start.forEach(
                a -> {
                    Transition t = new Transition(init,a, Transition.Kind
                            .EPSILON);
                    ag.addEdge(t);
                }
        );

        return ag;
    }


    private stringanalysis.autorex.autograph.State getStateFrom
            (Set<stringanalysis.autorex.autograph.State> start,
             Set<stringanalysis.autorex.autograph.State> accepting,
             Map<State,stringanalysis.autorex.autograph.State> smap,
             AutomatonTrans a, State s) {

        if(smap.containsKey(s))
            return smap.get(s);

        stringanalysis.autorex.autograph.State ret = new stringanalysis.autorex.autograph
                .State(stringanalysis.autorex
                .autograph.State.Kind.NORMAL);


        if(s.isAccept()) {
            accepting.add(ret);
        }

        if(s.equals(a.init)) {
            start.add(ret);
        }

        smap.put(s,ret);

        return ret;
    }

}