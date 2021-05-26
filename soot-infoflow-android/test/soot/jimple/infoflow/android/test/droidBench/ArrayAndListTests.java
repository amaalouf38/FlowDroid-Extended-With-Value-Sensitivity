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
package soot.jimple.infoflow.android.test.droidBench;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import soot.jimple.infoflow.results.InfoflowResults;

public class ArrayAndListTests extends JUnitTests {

    /*@Test(timeout = 30000000)
    //@Ignore //include
    public void ArrayAccess2() throws IOException, XmlPullParserException {
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI01.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI02.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI03.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI04.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI05.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI06.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI07.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI08.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI09.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI10.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI11.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI12.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI13.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI14.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI15.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI16.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI17.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI18.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI19.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI20.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI21.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI22.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI23.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI24.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI25.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI26.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI27.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI28.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI29.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI30.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI31.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI32.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI33.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI34.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI35.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI36.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI37.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI38.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI39.apk");

        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB01.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB02.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB03.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB04.apk");
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB05.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB06.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB07.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB08.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB09.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB10.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB15.apk");

        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }*/

    @Test(timeout = 30000000)
    //@Ignore //include
    public void test00() throws IOException, XmlPullParserException {
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/smsforward.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/barcodebuddyscanner.apk");
        //InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/InsecureBank.apk");
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/androidwallet.apk");

        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test01() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI01.apk");
        if (res != null)
        	Assert.assertEquals(0, res.size());

        //Assert.assertNotNull(res);
        //Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test02() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI02.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test03() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI03.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test04() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI04.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test05() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI05.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test06() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI06.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test07() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI07.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test08() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI08.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test09() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI09.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test10() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI10.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test11() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI11.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test12() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI12.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test13() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI13.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test14() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI14.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test15() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI15.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test16() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI16.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test17() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI17.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test18() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI18.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test19() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI19.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test20() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI20.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test21() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI21.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test22() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI22.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test23() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI23.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test24() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI24.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test25() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI25.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test26() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI26.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test27() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI27.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test28() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI28.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test29() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI29.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test30() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI30.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test31() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI31.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test32() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI32.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test33() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI33.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test34() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI34.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test35() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI35.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test36() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI36.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test37() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI37.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test38() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI38.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test39() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/TAI/TAI39.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test41() throws IOException, XmlPullParserException {

        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB01.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test42() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB02.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test43() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB03.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test44() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB04.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test45() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB05.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test46() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB06.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test47() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB07.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test48() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB08.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test49() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB09.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test50() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB10.apk");
        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 30000000)
    @Ignore //include
    public void test55() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("AndroidBenchMarks/apk/DB/DB15.apk");


        //if (res != null)
        //	Assert.assertEquals(0, res.size());

        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void ArrayAccess1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayAccess1.apk");
        if (res != null)
            Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestArrayAccess3() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayAccess3.apk");
        Assert.assertNotNull(res);
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestArrayAccess4() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayAccess4.apk");
        if (res != null)
            Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestArrayAccess5() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayAccess5.apk");
        if (res != null)
            Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestArrayCopy1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayCopy1.apk");
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestArrayToString1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ArrayToString1.apk");
        Assert.assertEquals(1, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestHashMapAccess1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/HashMapAccess1.apk");
        Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestListAccess1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/ListAccess1.apk");
        if (res != null)
            Assert.assertEquals(0, res.size());
    }

    @Test(timeout = 300000)
    @Ignore
    public void runTestMultidimensionalArray1() throws IOException, XmlPullParserException {
        InfoflowResults res = analyzeAPKFile("ArraysAndLists/MultidimensionalArray1.apk");
        Assert.assertEquals(1, res.size());
    }

}
