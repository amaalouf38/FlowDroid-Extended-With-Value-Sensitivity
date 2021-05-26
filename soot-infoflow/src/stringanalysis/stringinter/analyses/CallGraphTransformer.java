package stringanalysis.stringinter.analyses;

import java.util.Map;
import soot.SceneTransformer;

public class CallGraphTransformer extends SceneTransformer {

    private StringAnalyis stringAnalysis;


    @SuppressWarnings("deprecation")
    @Override
    protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {

        stringAnalysis = new StringAnalyis();
        stringAnalysis.doAnalysis();
    }

    public StringAnalyis getIntervalAnalysis() {
        return stringAnalysis;
    }

}

