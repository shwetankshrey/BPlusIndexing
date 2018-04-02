package src;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexFile implements Serializable {

    public HashMap<String, ArrayList<Record>> map;

    public IndexFile() {
        map = new HashMap<String, ArrayList<Record>>();
    }

    public void addRecord(String s, Record r) {
        if(!map.containsKey(s)) {
            map.put(s, new ArrayList<Record>());
        }
        map.get(s).add(r);
        return;
    }

    public Record getRecord(String s) {
        return map.get(s).get(0);
    }

    public ArrayList<Record> getAllRecords(String s) {
        return map.get(s);
    }

    public void deleteRecord(String s, Record r) {
        map.get(s).remove(r);
        if(map.get(s).isEmpty()) {
            map.remove(s);
        }
    }

    public static void serialize(IndexFile ind) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("C:\\Users\\shwet\\Desktop\\BPlusIndexing\\src\\resources\\index"));
        out.writeObject(ind);
        out.close();
    }

    public static IndexFile deserialize() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("C:\\Users\\shwet\\Desktop\\BPlusIndexing\\src\\resources\\index"));
        IndexFile x = (IndexFile) in.readObject();
        in.close();
        return x;
    }
}
