package src;

import java.io.Serializable;

public class Record implements Serializable {

    String tag;
    String id;
    String name;
    String dept;
    String sal;

    public Record(String arr[]) {
        tag = arr[0];
        id = arr[1];
        name = arr[2];
        dept = arr[3];
        sal = arr[4];
    }

    public String toString() {
        return(tag + "," + id + "," + name + "," + dept + "," + sal + "\n");
    }
}
