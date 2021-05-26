/**
 * Copyright (C) 2013 Rohan Padhye
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package intervalanalysis.vasco.soot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import intervalanalysis.vasco.ProgramRepresentation;

/**
 * A default program representation for Soot using the Jimple IR. This 
 * representation uses control-flow graphs of individual units including exceptional 
 * control flow, and resolves virtual calls using the default context-insensitive
 * call graph.
 * 
 * <p><strong>Note</strong>: This class follows the Singleton pattern. The singleton 
 * object is available through {@link #v()}.</p>
 * 
 * @author Rohan Padhye
 *
 */
public class DefaultJimpleRepresentation implements ProgramRepresentation<SootMethod, Unit> {
	
	// Cache for control flow graphs
	private Map<SootMethod, DirectedGraph<Unit>> cfgCache;
	
	// Private constructor, see #v() to retrieve singleton object
	private DefaultJimpleRepresentation() {
		cfgCache = new HashMap<SootMethod, DirectedGraph<Unit>>();
	}
	
	/**
	 * Returns a singleton list containing the <code>main</code> method.
	 * @see Scene#getMainMethod()
	 */
	@Override
	public List<SootMethod> getEntryPoints() {
		//return Collections.singletonList(Scene.v().getMainMethod());
		return Scene.v().getEntryPoints();

	}

	/**
	 * Returns an {@link ExceptionalUnitGraph} for a given method.
	 */
	@Override
	public DirectedGraph<Unit> getControlFlowGraph(SootMethod method) {
		if (cfgCache.containsKey(method) == false) {
			cfgCache.put(method, new ExceptionalUnitGraph(method.getActiveBody()));
		}
		return cfgCache.get(method);
	}

	/**
	 * Returns <code>true</code> iff the Jimple statement contains an
	 * invoke expression.
	 */
	@Override
	public boolean isCall(Unit node) {
		return ((soot.jimple.Stmt) node).containsInvokeExpr();
	}

	/**
	 * Resolves virtual calls using the default call graph and returns
	 * a list of methods which are the targets of explicit edges.
	 * TODO: Should we consider thread/clinit edges?
	 */
	@Override
	public List<SootMethod> resolveTargets(SootMethod method, Unit node) {
		List<SootMethod> targets = new LinkedList<SootMethod>();
		Iterator<Edge> it = Scene.v().getCallGraph().edgesOutOf(node);
		while(it.hasNext()) {
			Edge edge = it.next();
			if (edge.isExplicit()&& !isPhantomMethod(edge.tgt())) {
				targets.add(edge.tgt());
			}
		}
		return targets;
	}

	// The singleton object
	private static DefaultJimpleRepresentation singleton = new DefaultJimpleRepresentation();
	
	/**
	 * Returns a reference to the singleton object of this class.
	 */
	public static DefaultJimpleRepresentation v() { return singleton; }

	@Override
	public boolean isPhantomMethod(SootMethod sm) {

		// Exclude system classes
		if ( isClassInSystemPackage(sm.getDeclaringClass().getName()))
			return true;

		// Exclude library classes
		if (sm.getDeclaringClass().isLibraryClass())
			return true;

		return false;
	}

	public boolean isClassInSystemPackage(String className) {
		//return !className.startsWith("com.pierreduchemin.smsforward");
		//return !className.startsWith("de.bulling.barcodebuddyscanner");
		return !className.startsWith("com.greenaddress.greenbits");
		//return !className.startsWith("amaalouf.ou.taintofArrays");

		/*return className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.")
				|| className.startsWith("sun.") || className.startsWith("org.omg.")
				|| className.startsWith("org.w3c.dom.") || className.startsWith("com.google.")
				|| className.startsWith("com.android.");*/
	}
}
