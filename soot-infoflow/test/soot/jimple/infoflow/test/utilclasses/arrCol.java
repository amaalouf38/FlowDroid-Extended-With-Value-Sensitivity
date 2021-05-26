package soot.jimple.infoflow.test.utilclasses;

public class arrCol {
    public String[] data=new String[100];

    public void insertAt(String val,int index) {
        data[index] = val;
    }

    public String returnAt(int index) {
        return data[index];
    }
}
