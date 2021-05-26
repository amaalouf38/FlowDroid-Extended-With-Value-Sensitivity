package soot.jimple.infoflow.problems.rules;

import java.util.*;

import intervalanalysis.flowsets.ArrayIndices.ArrayIndiceInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import intervalanalysis.intervalinter.analyses.OutputIntervalAnalysis;
import intervalanalysis.flowsets.ArrayIndices.ArrayTaintIndicesHandler;
import intervalanalysis.flowsets.InterVal;
import soot.jimple.NumericConstant;
import soot.toolkits.scalar.FlowSet;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.LengthExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowManager;
import soot.jimple.infoflow.aliasing.Aliasing;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.AccessPath.ArrayTaintType;
import soot.jimple.infoflow.problems.TaintPropagationResults;
import soot.jimple.infoflow.util.ByReferenceBoolean;

/**
 * Rule for propagating array accesses
 *
 * @author Steven Arzt
 */
public class ArrayPropagationRule extends AbstractTaintPropagationRule {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public ArrayPropagationRule(InfoflowManager manager, Abstraction zeroValue, TaintPropagationResults results) {
        super(manager, zeroValue, results);
    }

    @Override
    public Collection<Abstraction> propagateNormalFlow(Abstraction d1, Abstraction source, Stmt stmt, Stmt destStmt,
                                                       ByReferenceBoolean killSource, ByReferenceBoolean killAll, SootMethod m) {
        // Get the assignment
        if (!(stmt instanceof AssignStmt))
            return null;
        AssignStmt assignStmt = (AssignStmt) stmt;
//      List<FlowSet> lst = OutputIntervalAnalysis.outputIntervalAnalysis(assignStmt);

        Abstraction newAbs = null;
        final Value leftVal = assignStmt.getLeftOp();
        final Value rightVal = assignStmt.getRightOp();

        if (rightVal instanceof LengthExpr) {
            LengthExpr lengthExpr = (LengthExpr) rightVal;
            if (getAliasing().mayAlias(source.getAccessPath().getPlainValue(), lengthExpr.getOp())) {
                // Is the length tainted? If only the contents are tainted, we
                // the
                // incoming abstraction does not match
                if (source.getAccessPath().getArrayTaintType() == ArrayTaintType.Contents)
                    return null;

                // Taint the array length
                AccessPath ap = getManager().getAccessPathFactory().createAccessPath(leftVal, null, IntType.v(),
                        (Type[]) null, true, false, true, ArrayTaintType.ContentsAndLength);
                newAbs = source.deriveNewAbstraction(ap, assignStmt);
            }
        }
        // y = x[i] && x tainted -> x, y tainted
        else if (rightVal instanceof ArrayRef) {
            try {
                Value rightBase = ((ArrayRef) rightVal).getBase();
                Value rightIndex = ((ArrayRef) rightVal).getIndex();

                Abstraction arrayAbs = source.getAccessPath().getPlainValue() == rightBase ? source : null;

                if (source.getAccessPath().getArrayTaintType() != ArrayTaintType.Length
                        && rightBase != null && getAliasing().mayAlias(rightBase, source.getAccessPath().getPlainValue())) {

                    Boolean propagateTaint = true;
                    ArrayIndiceInterval e = null;
                    if (rightIndex instanceof Local) {

                        List<Unit> callStack = new ArrayList<>();
                        Abstraction crAbs = source;
                        while (crAbs != null) {
                            if (crAbs.callSiteABS != null) {
                                callStack.add(crAbs.callSiteABS);
                            }
                            crAbs = crAbs.SourceABS;
                        }
                        e = new ArrayIndiceInterval(OutputIntervalAnalysis.outputIntervalAnalysis(assignStmt, (Local) rightIndex, callStack));

                    } else if (rightIndex instanceof NumericConstant) {
                        Double rvalue = new Double(((NumericConstant) rightIndex).toString());
                        e = new ArrayIndiceInterval((int) Math.floor(rvalue), (int) Math.ceil(rvalue), false);
                    }

                    propagateTaint = source.intersect(e);
                    logger.info("==intersects ====" + assignStmt.toString() + " e:" + e.toString() + " source:" + source.toString() + " propagateTaint:" + propagateTaint);

                    if (propagateTaint) {
                        // We must remove one layer of array typing, e.g., A[][] -> A[]
                        Type targetType = source.getAccessPath().getBaseType();
                        assert targetType instanceof ArrayType;
                        targetType = ((ArrayType) targetType).getElementType();

                        // Create the new taint abstraction
                        ArrayTaintType arrayTaintType = source.getAccessPath().getArrayTaintType();
                        AccessPath ap = getManager().getAccessPathFactory().copyWithNewValue(source.getAccessPath(), leftVal,
                                targetType, false, true, arrayTaintType);
                        newAbs = source.deriveNewAbstraction(ap, assignStmt);
                    } else {
                        newAbs = source;
                        //.deriveNewAbstraction(source.clone().getAccessPath(), assignStmt);
                    }
                }
                // y = x[i] with i tainted
                else if (source.getAccessPath().getArrayTaintType() != ArrayTaintType.Length
                        && rightIndex == source.getAccessPath().getPlainValue()
                        && getManager().getConfig().getImplicitFlowMode().trackArrayAccesses()) {
                    // Create the new taint abstraction
                    ArrayTaintType arrayTaintType = ArrayTaintType.ContentsAndLength;
                    AccessPath ap = getManager().getAccessPathFactory().copyWithNewValue(source.getAccessPath(), leftVal,
                            null, false, true, arrayTaintType);
                    newAbs = source.deriveNewAbstraction(ap, assignStmt);
                }
            } catch (Exception ex) {
                logger.error("==Exception ====" + ex.toString());
            }

        }
        // y = new A[i] with i tainted
        else if (rightVal instanceof NewArrayExpr &&

                getManager().

                        getConfig().

                        getEnableArraySizeTainting()) {
            NewArrayExpr newArrayExpr = (NewArrayExpr) rightVal;
            if (getAliasing().mayAlias(source.getAccessPath().getPlainValue(), newArrayExpr.getSize())) {
                // Create the new taint abstraction
                AccessPath ap = getManager().getAccessPathFactory().copyWithNewValue(source.getAccessPath(), leftVal,
                        null, false, true, ArrayTaintType.Length);
                newAbs = source.deriveNewAbstraction(ap, assignStmt);
            }
        }

        if (newAbs == null)
            return null;

        Set<Abstraction> res = new HashSet<>();
        if (!newAbs.isAbstractionActive()) newAbs = newAbs.getActiveCopy();
        res.add(newAbs);

        // Compute the aliases
        if (Aliasing.canHaveAliases(assignStmt, leftVal, newAbs))

            getAliasing().

                    computeAliases(d1, assignStmt, leftVal, res, getManager().

                                    getICFG().

                                    getMethodOf(assignStmt),

                            newAbs);

        return res;
    }

    @Override
    public Collection<Abstraction> propagateCallFlow(Abstraction d1, Abstraction source, Stmt stmt, SootMethod dest,
                                                     ByReferenceBoolean killAll) {
        return null;
    }

    @Override
    public Collection<Abstraction> propagateCallToReturnFlow(Abstraction d1, Abstraction source, Stmt stmt,
                                                             ByReferenceBoolean killSource, ByReferenceBoolean killAll) {
        return null;
    }

    @Override
    public Collection<Abstraction> propagateReturnFlow(Collection<Abstraction> callerD1s, Abstraction source, Stmt stmt,
                                                       Stmt retSite, Stmt callSite, ByReferenceBoolean killAll) {
        return null;
    }

}
