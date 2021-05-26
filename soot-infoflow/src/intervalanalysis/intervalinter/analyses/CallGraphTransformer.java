package intervalanalysis.intervalinter.analyses;

import java.util.Map;
import soot.SceneTransformer;
import soot.SootClass;

public class CallGraphTransformer extends SceneTransformer {

    private IntervalAnalyis intervaLAnalysis;


    @SuppressWarnings("deprecation")
    @Override
    protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {

        intervaLAnalysis = new IntervalAnalyis();
        intervaLAnalysis.doAnalysis();
    }

    public IntervalAnalyis getIntervalAnalysis() {
        return intervaLAnalysis;
    }

}

