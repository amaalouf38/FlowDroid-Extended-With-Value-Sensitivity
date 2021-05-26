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
package soot.jimple.infoflow.test;

import java.util.Arrays;
import java.util.List;

import soot.jimple.infoflow.test.android.ConnectionManager;
import soot.jimple.infoflow.test.android.TelephonyManager;
import soot.jimple.infoflow.test.utilclasses.*;
import soot.jimple.infoflow.test.android.*;

/**
 * tests array tainting
 *
 * @author Christian
 */
public class ArrayTestCode {


    static String[] staticTainted;
    transient String[] transTainted;
    String[] globalTainted=new String[10];
    int[] globalIntTainted = new int[30];

    public void customTestCase3() {

        String deviceId = TelephonyManager.getDeviceId();

        String[] array = new String[10];
        array[0] = deviceId;
        String[] arrayAlias = array;
        arrayAlias[2] = deviceId;

        ConnectionManager cm = new ConnectionManager();
        cm.publish(arrayAlias[2]);
    }


    public void customTestCase2() {

        String tainted1 = TelephonyManager.getDeviceId();
        String tainted2 = TelephonyManager.getDeviceId();

        String[] array = new String[3];

        array[0] = tainted1;
        array[1] = tainted2;
        array[2] = "neutral";

        String taintedElement = array[0];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(taintedElement);
    }

    public void customTestCase1() {

        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[1];
        String taintedElement = array[0];
        array[0] = tainted;
        array[0] = "neutral";
        array[0] = tainted;

        taintedElement = array[0];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(taintedElement);

        /*
        int tainted = TelephonyManager.getIMEI();
        globalIntTainted[3] = tainted;
        int taintedElement = globalIntTainted[3];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(taintedElement);
        */
        /*
        String s="netural";
        int x=1,y=1,z=1;
        if(s.contains("w"))
        {
            x=10;y=5;z=10;
        }
        else
        {
            x=1;y=1;z=1;
        }

        if(y>=2*x )
        {
         z=5;
        }
        else
        {
            z=6;
        }
        System.out.println(s);
        */
    }

    public void stringAnalysisTest1() {
        //StringBuffer sb = new StringBuffer("12");
        //String test=sb.toString().replace("12",TelephonyManager.getDeviceId());

        String s = TelephonyManager.getDeviceId();

        ConnectionManager cm = new ConnectionManager();
        if (s.isEmpty())
            cm.publish(s);


        //test=test.concat("neutral");
        //test=test.concat(tainted);
        //test=test.concat("neutral");
        //test=test.replace("I1t359-f","12");
        //test=test.substring(7,15);

        //StringBuffer sb = new StringBuffer();
        //sb.append("neutral");
        //sb.append(tainted);
        //sb.append("neutral");
        //String test=sb.toString();
        //test = test.substring(7,15);
        //test=test.replace(tainted,tainted.concat(tainted));

        //s=tainted.replace("abc","def");
        //System.out.println(test);
    }

/*    public void concreteWriteReadSamePosIntArrayTest() {

        //StringBuffer sb = new StringBuffer("12");
        //String test=sb.toString().replace("12",TelephonyManager.getDeviceId());

        String tainted = TelephonyManager.getDeviceId();

        String test ="";
        test=test.concat("neutral");
        test=test.concat(tainted);
        test=test.concat("neutral");
        //test=test.replace("I1t359-f","12");
        test=test.substring(7,15);

        //StringBuffer sb = new StringBuffer();
        //sb.append("neutral");
        //sb.append(tainted);
        //sb.append("neutral");
        //String test=sb.toString();
        //test = test.substring(7,15);
        //test=test.replace(tainted,tainted.concat(tainted));

        //s=tainted.replace("abc","def");
        System.out.println(test);
        ConnectionManager cm = new ConnectionManager();
        cm.publish(test);
    }*/

    public void concreteWriteReadSamePosTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;
        String taintedElement = array[1];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(taintedElement);
    }

    public void concreteWriteReadDiffPosTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;

        //because whole list is tainted, even untainted elements are tainted if they are fetched from the list
        String taintedElement2 = array[1];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(taintedElement2);
    }


    public void concreteStaticTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;
        staticTainted = array;
        String[] tainted123 = staticTainted;
        ConnectionManager cm = new ConnectionManager();
        cm.publish(tainted123[1]);
    }

    public void concreteStaticTest2() {
        String tainted = TelephonyManager.getDeviceId();

        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;

        staticTainted = array;
        ConnectionManager cm = new ConnectionManager();
        cm.publish(staticTainted[1]);
    }

    public void concreteTransientTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;
        transTainted = array;
        String[] tainted456 = transTainted;
        ConnectionManager cm = new ConnectionManager();
        cm.publish(tainted456[1]);
    }

    public void concreteGlobalTest() {
        String tainted = TelephonyManager.getDeviceId();
        set(0,tainted);
        set(1,tainted);
        set(2,tainted);
        ConnectionManager cm = new ConnectionManager();
        cm.publish(get(0));
    }

    void set( int index,String value)
    {
        //String[] array = new String[10];
        //array[index] = value;

        globalTainted[index] =value;
        get(0);
    }

    String get(int index)
    {
        return globalTainted[index];
    }

    public void concreteGlobalTest2() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = "neutral";
        array[1] = tainted;
        globalTainted = array;
        String[] tainted789 = globalTainted;
        ConnectionManager cm = new ConnectionManager();
        cm.publish(tainted789[1]);
    }


    public void copyTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[2];
        array[0] = tainted;
        String[] copyTainted = Arrays.copyOf(array, 100);

        ConnectionManager cm = new ConnectionManager();
        cm.publish(copyTainted[0]);
    }

    public void arrayAsFieldOfClass() {
        String tainted = TelephonyManager.getDeviceId();

        String[] array = new String[2];
        array[1] = "neutral";
        array[0] = tainted;

        ClassWithFinal<String> c = new ClassWithFinal<String>(array);
        String[] taintTaint = c.a;
        String y = taintTaint[0];

        ConnectionManager cm = new ConnectionManager();
        cm.publish(y);
    }

    public void arrayAsListTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] array = new String[1];
        array[0] = tainted;
        List<String> list = Arrays.asList(array);
        String taintedElement = list.get(0);
        String dummyString = taintedElement;

        ConnectionManager cm = new ConnectionManager();
        cm.publish(dummyString);

    }

    public void concreteWriteReadNegativeTest() {
        String tainted = TelephonyManager.getDeviceId();
        String[] notRelevant = new String[1];
        String[] array = new String[2];
        array[0] = tainted;
        array[1] = "neutral2";

        notRelevant[0] = tainted;

        String taintedElement = notRelevant[0];
        String untaintedElement = array[0];
        taintedElement.toString();

        ConnectionManager cm = new ConnectionManager();
        cm.publish(untaintedElement);
    }

    public void arrayOverwriteTest() {
        String tainted = TelephonyManager.getDeviceId();

        String[] array = new String[2];
        array[0] = tainted;
        array[1] = "neutral";

        ConnectionManager cm = new ConnectionManager();
        cm.publish(array[0]);
    }

    public void arrayLengthTest() {
        String tainted = TelephonyManager.getDeviceId();
        char[] array = tainted.toCharArray();

        ConnectionManager cm = new ConnectionManager();
        cm.publish(array.length);
    }

    public void arrayLengthTest2() {
        char[] array = new char[TelephonyManager.getIMEI()];
        ConnectionManager cm = new ConnectionManager();
        cm.publish(array.length);
    }

    public void arrayLengthTest3() {
        String[] array = new String[TelephonyManager.getIMEI()];
        array[0] = TelephonyManager.getDeviceId();
        ConnectionManager cm = new ConnectionManager();
        cm.publish(array.length);
        cm.publish(array[0]);
    }

    public void arrayLengthTest4() {
        String[] array = new String[TelephonyManager.getIMEI()];
        array[0] = "foo";
        ConnectionManager cm = new ConnectionManager();
        cm.publish(array[0]);
    }

    public void arrayLengthTest5() {
        String[] array = new String[TelephonyManager.getIMEI()];
        String[] array2 = array;
        array2[0] = "foo";
        ConnectionManager cm = new ConnectionManager();
        cm.publish(array[0]);
    }

}
