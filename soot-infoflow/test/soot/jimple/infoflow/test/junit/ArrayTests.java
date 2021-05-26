/*******************************************************************************
 * Copyright (c) 2012 Secure Software Engineering Group at EC SPRIDE.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors: Christian Fritz, Steven Arzt, Siegfried Rasthofer, Eric
 * Bodden, and others.
 ******************************************************************************/
package soot.jimple.infoflow.test.junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

/**
 * These tests check taint propagation in arrays, for instance the propagation
 * for copied arrays and arrays converted to lists
 */
public class ArrayTests extends JUnitTests {


    private final EasyTaintWrapper easyWrapper;
    public ArrayTests() throws IOException {
        easyWrapper = new EasyTaintWrapper(new File("EasyTaintWrapperSource.txt"));
    }

    @Test
    @Ignore
    public void stringTest() {
        IInfoflow infoflow = initInfoflow();
        //infoflow.getConfig().setAliasingAlgorithm(InfoflowConfiguration.AliasingAlgorithm.None);
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void stringAnalysisTest1()>");
        infoflow.setTaintWrapper(easyWrapper);
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test
    //@Ignore //include
    public void concreteWriteReadDiffPosTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void concreteGlobalTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        //negativeCheckInfoflow(infoflow);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void concreteWriteReadSamePosTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void concreteWriteReadSamePosTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        //negativeCheckInfoflow(infoflow);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void concreteStaticTest2() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void concreteStaticTest2()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        //negativeCheckInfoflow(infoflow);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void concreteStaticTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void concreteStaticTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        //negativeCheckInfoflow(infoflow);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void customTestCase1() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void customTestCase1()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
        public void concreteGlobalTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void customTestCase3()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void arrayCopyTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void copyTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
        //negativeCheckInfoflow(infoflow);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void arrayAsFieldOfClassTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayAsFieldOfClass()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        //negativeCheckInfoflow(infoflow);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
   @Ignore
    public void arrayAsListTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayAsListTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void concreteNegativeTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void concreteWriteReadNegativeTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore //include
    public void arrayOverwriteTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayOverwriteTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore
    public void arrayLengthTest() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayLengthTest()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore
    public void arrayLengthTest2() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayLengthTest2()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 1);
    }

    @Test(timeout = 300000)
    @Ignore
    public void arrayLengthTest3() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayLengthTest3()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        checkInfoflow(infoflow, 2);
    }

    @Test(timeout = 300000)
    @Ignore
    public void arrayLengthTest4() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayLengthTest4()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        negativeCheckInfoflow(infoflow);
    }

    @Test(timeout = 300000)
    @Ignore
    public void arrayLengthTest5() {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<soot.jimple.infoflow.test.ArrayTestCode: void arrayLengthTest5()>");
        infoflow.computeInfoflow(appPath, libPath, epoints, sources, sinks);
        negativeCheckInfoflow(infoflow);
    }
}
