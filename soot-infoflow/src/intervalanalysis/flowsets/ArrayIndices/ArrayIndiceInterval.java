package intervalanalysis.flowsets.ArrayIndices;
import intervalanalysis.flowsets.*;

public class ArrayIndiceInterval {
    public int lower;
    public int upper;
    public Boolean bottom=false;
    public Boolean openLeft=false;
    public Boolean openRight=false;

    public ArrayIndiceInterval(int lower,int upper,Boolean bottom)
    {
        this.lower=lower;
        this.upper=upper;
        this.bottom=bottom;
    }

    public ArrayIndiceInterval(int lower,int upper,Boolean openLeft,Boolean openRight,Boolean bottom)
    {
        this.lower=lower;
        this.upper=upper;
        this.openLeft=openLeft;
        this.openRight=openRight;
        this.bottom=bottom;
    }

    public ArrayIndiceInterval(InterVal i)
    {
        this.lower=i.lower;
        this.upper=i.upper;
        this.bottom=i.bottom;
    }


    @Override
    public int hashCode() {

        return 0;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        ArrayIndiceInterval instance = (ArrayIndiceInterval) obj;

        if (this == obj)
            return true;
        else if (instance.bottom== true && this.bottom==true)
            return true;
        else if (instance.lower== this.lower
                && instance.upper== this.upper
                && instance.openLeft== this.openLeft
                && instance.openRight== this.openRight
                && instance.bottom== this.bottom
        )

            return true;

        else

            return false;


    }
    @Override
    public String toString()
    {
        String var=" [";

        if (this.bottom)
            var += " bottom]";
        else
            var += " "+ this.lower + "," + this.upper + "]";

        return var;
    }
}
