package intervalanalysis.testers;

public class IntervalClass {
    String[] data = new String[100];
    int index = 0;

    public void push(String val) {
        data[index] = val;
        index=index+1;
    }

    public String pop() {
        String retValue= data[index];
        index=index-1;
        return retValue;
    }

    public static void main(String[] args) {

        IntervalClass s1 = new IntervalClass();
        IntervalClass s2 = new IntervalClass();

        s1.push("deviceId");
        s1.push("neutral");

        s2.push("deviceId".substring(3));

        String untaintedElement = s1.pop();
        String taintedElement = s2.pop();
    }
////double version
//        double a = 1.5;
//        double b = 35.6;
//
//        while (b >=2*a) {
//            a =a+ 1;
//        }
//        a = a + 1;

//int version
//        int a = 1;
//        int b = Math.abs(4);
//        //int b=100;
//
//        while (b >=2*a) {
//            a =a+ 1;
//        }
//        a = a + 1;

    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////



/*
static int myStaticInt;
protected int myInt;
protected int myInt2;

protected int add(int int1,int int2)
{
    if(int1>int2)
    {
      myInt=  int1+int2;
      myInt2=int1-int2;
    }
    else
    {
        myInt=  int1-int2;
        myInt2=int1+int2;
    }
    return 10;
}
    //test program 3

    public static void main(String[] args) {

        int a = 0;
        int b = 1;

        IntervalClass inst1=new IntervalClass();
        inst1.myInt=a;
        inst1.myInt2=b;
        int c =inst1.add(inst1.myInt,inst1.myInt2);
        if(inst1.myInt >inst1.myInt2){

        }

        IntervalClass inst2=new IntervalClass();
        a=1;
        b=2;
        c =inst2.add(a,b);

        if(c>0){

        }
    }
/*

    //test program 1

    public static void main(String[] args) {
        int x = 10;
        int y = 5;
        y = foo(x, y);

        if (y > 0) y = 1;


        x = 11;
        y = 51;
        y = foo(x, y);

        if (y > 0) y = 1;


        x = 10;
        y = 5;
        y = foo(x, y);

        if (y > 0) y = 1;

    }

    public static int foo(int a, int b) {
        return a + b;
    }


    //test program 2

    public static void main(String[] args) {

        int a = 0;
        int b = 1;

        while (a < 3) {
            if (a < 2) {
                b = b + 1;
            }
            a = a + 1;
        }
        a = a + 1;

        int x = 10;
        int y = 5;

        if (x >= 90) {
            y = 10;
        } else if (x >= 80) {
            y = 9;
        } else if (x >= 70) {
            y = 8;
        } else if (x >= 60) {
            y = 7;
        } else {
            y = 6;
        }

        a = 0;
        b = 1;

        x = 10;
        y = 5;

        if (x >= 90) {
            y = 10;
            while (a < 3) {
                a = a + 1;
            }
        } else {
            x = 6;
        }
        b = b - 1;


        y=y+10;
        y=y+11;
        y=y+12;

        if (a < 2) {
            b = b + 1;
        }
        else
        {
            b = b - 1;
        }
        a = a + 1;


}


    */
}
