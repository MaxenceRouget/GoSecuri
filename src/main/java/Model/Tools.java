package Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Tools {
    private String Name;
    private ArrayList Owner;
    private long Total;
    private long Used;

    public Tools() {
    }

    public Tools(String name, ArrayList owner, long total, long used) {
        Name = name;
        Owner = owner;
        Total = total;
        Used = used;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList getOwner() {
        return Owner;
    }

    public void setOwner(ArrayList owner) {
        Owner = owner;
    }

    public long getTotal() {
        return Total;
    }

    public void setTotal(long total) {
        Total = total;
    }

    public long getUsed() {
        return Used;
    }

    public void setUsed(long used) {
        Used = used;
    }
}
