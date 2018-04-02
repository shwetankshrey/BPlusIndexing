package src;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class BPlusTree {

    BTreeNode root;
    String dataFile;
    int indexType;
    HashMap<String, Record> lookup;

    public BPlusTree(String dx, int i) throws IOException {
        dataFile = dx;
        List<String> data = Files.readAllLines(Paths.get(dataFile));
        root = new BTreeNode();
        indexType = i;
        lookup = new HashMap<>();
        IndexFile ind = new IndexFile();
        for(String d : data) {
            String[] line = d.split(",");
            Record r = new Record(line);
            lookup.put(line[0], r);
            if(line[0].equals("0000")) {
                continue;
            }
            ind.addRecord(line[i-1], r);
        }
        IndexFile.serialize(ind);
        Set<String> hs = ind.map.keySet();
        Iterator<String> itr = hs.iterator();
        while (itr.hasNext()) {
            String s = itr.next();
            insert(root, s);
        }
        ArrayList<BTreeNode> pq = new ArrayList<>();
        pq.add(root);
        print(pq);
    }

    public void find(String v) throws IOException, ClassNotFoundException {
        IndexFile ind = IndexFile.deserialize();
        Record r = ind.getRecord(v);
        if(r != null) {
            System.out.println("Record Found!");
            System.out.println("Validity Tag : " + r.tag);
            System.out.println("Instructor ID : " + r.id);
            System.out.println("Instructor Name : " + r.name);
            System.out.println("Department : " + r.dept);
            System.out.println("Salary : " + r.sal);
        }
    }

    public void printAll(String v) throws IOException, ClassNotFoundException {
        IndexFile ind = IndexFile.deserialize();
        ArrayList<Record> ar = ind.getAllRecords(v);
        int n = ar.size();
        int i = 1;
        Iterator<Record> itr = ar.iterator();
        while (itr.hasNext()){
            Record r = itr.next();
            System.out.println("Record " + i + " out of " + n + ":");
            System.out.println("Validity Tag : " + r.tag);
            System.out.println("Instructor ID : " + r.id);
            System.out.println("Instructor Name : " + r.name);
            System.out.println("Department : " + r.dept);
            System.out.println("Salary : " + r.sal);
            i++;
        }
    }

    public void findRange(String l, String u) throws IOException, ClassNotFoundException {
        ArrayList<Record> ar = new ArrayList<>();
        findRange(ar, l, u, root);
        int n = ar.size();
        int i = 1;
        Iterator<Record> itr = ar.iterator();
        while (itr.hasNext()){
            Record r = itr.next();
            System.out.println("Record " + i + " out of " + n + ":");
            System.out.println("Validity Tag : " + r.tag);
            System.out.println("Instructor ID : " + r.id);
            System.out.println("Instructor Name : " + r.name);
            System.out.println("Department : " + r.dept);
            System.out.println("Salary : " + r.sal);
            i++;
        }
    }

    private void findRange(ArrayList<Record> ar, String l, String u, BTreeNode root) throws IOException, ClassNotFoundException {
        IndexFile ind = IndexFile.deserialize();
        if(root.children[0] == null) {
            for(String s : root.values) {
                if(s == null) {
                    break;
                }
                if(s.compareTo(l) > -1 && s.compareTo(u) < 1) {
                    for(Record r : ind.getAllRecords(s)) {
                        ar.add(r);
                    }
                }
            }
        }
        else {
            for (BTreeNode r : root.children) {
                if(r == null) {
                    break;
                }
                findRange(ar, l, u, r);
            }
        }
    }

    public void insert(String arr[], int c) throws IOException, ClassNotFoundException {
        IndexFile ind = IndexFile.deserialize();
        Record r = new Record(arr);
        lookup.put(arr[0], r);
        Path p = Paths.get(dataFile);
        Files.write(p, r.toString().getBytes(), StandardOpenOption.APPEND);
        if(!ind.map.containsKey(arr[c-1])) {
            insert(root, arr[c-1]);
        }
        ind.addRecord(arr[c-1], r);
        IndexFile.serialize(ind);
        ArrayList<BTreeNode> pq = new ArrayList<>();
        pq.add(root);
        print(pq);
    }

    boolean found;
    public void delete(String r) throws IOException, ClassNotFoundException {
        Record rec = lookup.get(r);
        String ind = getIndex(rec);
        String vt = rec.tag;
        rec.tag = "0000";
        BufferedReader f = new BufferedReader(new FileReader(dataFile));
        String line;
        StringBuffer ip = new StringBuffer();
        while ((line = f.readLine()) != null) {
            ip.append(line);
            ip.append('\n');
        }
        f.close();
        String in = ip.toString();
        in = in.replace(vt, "0000");
        FileOutputStream fo = new FileOutputStream(dataFile);
        fo.write(in.getBytes());
        fo.close();
        IndexFile inf = IndexFile.deserialize();
        inf.getAllRecords(ind).remove(rec);
        if(inf.getAllRecords(ind).size() > 0) {
            ArrayList<BTreeNode> pq = new ArrayList<>();
            pq.add(root);
            print(pq);
            return;
        }
        found = false;
        delete(root, ind, 0);
        ArrayList<BTreeNode> pq = new ArrayList<>();
        pq.add(root);
        print(pq);
    }

    String getIndex(Record r) {
        switch (indexType) {
            case 1: {
                return r.tag;
            }
            case 2: {
                return r.id;
            }
            case 3: {
                return r.name;
            }
            case 4: {
                return r.dept;
            }
            case 5: {
                return r.sal;
            }
        }
        return "";
    }

    void print(ArrayList<BTreeNode> ar) {
        ArrayList<BTreeNode> n = new ArrayList<>();
        for(int i = 0 ; i < ar.size() ; i++) {
            BTreeNode b = ar.get(i);
            System.out.print("[|");
            int j = 0;
            while (j < b.number) {
                System.out.print(b.values[j] + "|");
                if(b.children[j] != null) {
                    n.add(b.children[j]);
                }
                j++;
            }
            if(b.values[j] == null && b.children[j] != null) {
                n.add(b.children[j]);
            }
            System.out.print("] ");
        }
        System.out.println();
        ar.clear();
        if(n.size() > 0) {
            print(n);
        }
    }

    void insert(BTreeNode block, String s) {
        for(int i = 0 ; i <= block.number ; i++) {
            if(block.children[i] != null) {
                if(block.values[i] == null) {
                    insert(block.children[i], s);
                    if (block.number == 10) {
                        splitInternalNode(block);
                    }
                    return;
                }
                else {
                    if(block.values[i] == null) {
                        insert(block.children[i], s);
                        if (block.number == 10) {
                            splitInternalNode(block);
                        }
                        return;
                    }
                    else if(s.compareTo(block.values[i]) == -1) {
                        insert(block.children[i], s);
                        if (block.number == 10) {
                            splitInternalNode(block);
                        }
                        return;
                    }
                }
            }
            else {
                if(block.values[i] == null) {
                    String tmp = block.values[i];
                    block.values[i] = s;
                    s = tmp;
                    if(i == block.number) {
                        block.number++;
                        break;
                    }
                }
                else {
                    if(block.values[i] == null) {
                        String tmp = block.values[i];
                        block.values[i] = s;
                        s = tmp;
                        if(i == block.number) {
                            block.number++;
                            break;
                        }
                    }
                    else if(s.compareTo(block.values[i]) == -1) {
                        String tmp = block.values[i];
                        block.values[i] = s;
                        s = tmp;
                        if(i == block.number) {
                            block.number++;
                            break;
                        }
                    }
                }
            }
        }
        if(block.number == 10) {
            splitLeafNode(block);
        }
    }

    void splitInternalNode(BTreeNode block) {
        //Create new bucket with same parent
        BTreeNode n = new BTreeNode();
        block.number = 5;
        n.number = 4;
        n.parent = block.parent;
        //Send half the values to the bucket
        int i = 5, j = 0;
        while (i <= 10) {
            n.values[j] = block.values[i];
            n.children[j] = block.children[i];
            block.values[i] = null;
            if(i != 5) {
                block.children[i] = null;
            }
            i++;
            j++;
        }
        //Set the parent of the above two buckets as the mid value
        String v =  n.values[0];
        //Remove the mid value from the children buckets
        for(i = 0 ; i  <= n.number ; i++) {
            n.values[i] = n.values[i+1];
            n.children[i] = n.children[i+1];
        }
        //Update parents for both children
        for(i = 0 ; block.children[i] != null ; i++) {
            block.children[i].parent = block;
        }
        for(i = 0 ; n.children[i] != null ; i++) {
            n.children[i].parent = n;
        }
        //If the original bucket was root create a new parent and set the children
        if(block == root) {
            BTreeNode par = new BTreeNode();
            par.number = 1;
            par.values[0] = v;
            par.children[0] = block;
            par.children[1] = n;
            block.parent = par;
            n.parent = par;
            root = par;
            return;
        }
        else {
            //Else add the mid value to the parent bucket
            BTreeNode par = block.parent;
            for(i = 0 ; i <= par.number ; i++) {
                if(par.values[i] == null) {
                    String tmp = par.values[i];
                    par.values[i] = v;
                    v = tmp;
                }
                else if(v.compareTo(par.values[i]) == -1) {
                    String tmp = par.values[i];
                    par.values[i] = v;
                    v = tmp;
                }
            }
            par.number++;
            //Update the children accordingly as well
            for(i = 0 ; i < par.number ; i++) {
                if(par.children[i].values[0] == null) {
                    BTreeNode tmp = par.children[i];
                    par.children[i] = n;
                    n = tmp;
                }
                else if(n.values[0].compareTo(par.children[i].values[0]) == -1) {
                    BTreeNode tmp = par.children[i];
                    par.children[i] = n;
                    n = tmp;
                }
            }
            par.children[i] = n;
            //Update parents for the child bucket
            for(i = 0 ; par.children[i] != null ; i++) {
                par.children[i].parent = par;
            }
        }
    }

    void splitLeafNode(BTreeNode block) {
        //Create new bucket with same parent
        BTreeNode n = new BTreeNode();
        block.number = 5;
        n.number = 5;
        n.parent = block.parent;
        //Send half the values to the bucket
        int i = 5, j = 0;
        while (i < 10) {
            n.values[j] = block.values[i];
            block.values[i] = null;
            i++;
            j++;
        }
        //Set the parent of the above two buckets as the mid value
        String v = n.values[0];
        //If the original bucket was root create a new parent and set the children
        if(block == root) {
            BTreeNode par = new BTreeNode();
            par.number = 1;
            par.values[0] = v;
            par.children[0] = block;
            par.children[1] = n;
            block.parent = par;
            n.parent = par;
            root = par;
            return;
        }
        else {
            //Else add the mid value to the parent bucket
            BTreeNode par = block.parent;
            for(i = 0 ; i <= par.number ; i++) {
                if(par.values[i] == null) {
                    String tmp = par.values[i];
                    par.values[i] = v;
                    v = tmp;
                }
                else if(v.compareTo(par.values[i]) == -1) {
                    String tmp = par.values[i];
                    par.values[i] = v;
                    v = tmp;
                }
            }
            par.number++;
            //Update the children accordingly as well
            for(i = 0 ; i < par.number ; i++) {
                if(par.children[i].values[0] == null) {
                    BTreeNode tmp = par.children[i];
                    par.children[i] = n;
                    n = tmp;
                }
                else if(n.values[0].compareTo(par.children[i].values[0]) == -1) {
                    BTreeNode tmp = par.children[i];
                    par.children[i] = n;
                    n = tmp;
                }
            }
            par.children[i] = n;
            for(i = 0 ; par.children[i] != null ; i++) {
                par.children[i].parent = par;
            }
        }
    }
    void delete(BTreeNode block, String s, int level) {
        boolean leaf = false;
        if(block.children[0] == null) {
            leaf = true;
        }
        String lft = block.values[0];
        for(int i = 0 ; !found && i <= block.number ; i++) {
            if(block.values[i] == null && block.children[i] != null) {
                delete(block.children[i], s, i);
            }
            else if(s.compareTo(block.values[i]) == -1 && block.children[i] != null) {
                delete(block.children[i], s, i);
            }
            else if(s == block.values[i] && block.children[i] == null) {
                for (int j = i; j < block.number; j++) {
                    block.values[j] = block.values[j + 1];
                }
                block.number--;
                found = true;
                break;
            }
        }
        if(block.parent == null && block.children[0] == null) {
            return;
        }
        if(block.parent == null && block.children[0] != null && block.number == 0) {
            root = block.children[0];
            root.parent = null;
            return;
        }
        if(leaf && block.parent != null) {
            if(level == 0) {
                BTreeNode n = block.parent.children[1];
                if(n != null && n.number > 5) {
                    split(block, n, leaf, 0, 0);
                }
                else if(n != null && block.number+n.number < 10) {
                    merge(block, n, leaf, 1);
                }
            }
            else {
                BTreeNode n1 = block.parent.children[level - 1];
                BTreeNode n2 = block.parent.children[level + 1];
                if(n1 != null && n1.number > 5) {
                    split(n1, block, leaf, level-1, 1);
                }
                else if(n2 != null && n2.number > 5) {
                    split(n2, block, leaf, level, 0);
                }
                else if(n1 != null && block.number+n1.number < 10) {
                    merge(n1, block, leaf, level);
                }
                else if(n2 != null && block.number+n2.number < 10) {
                    merge(n2, block, leaf, level+1);
                }
            }
        }
        else if(!leaf && block.parent != null) {
            if (level == 0) {
                BTreeNode n = block.parent.children[1];
                if (n != null && n.number > 5) {
                    split(block, n, leaf, 0, 0);
                } else if (n != null && n.number + n.number < 9) {
                    merge(block, n, leaf, 1);
                }
            }
            else {
                BTreeNode n1 = block.parent.children[level - 1];
                BTreeNode n2 = block.parent.children[level + 1];
                if (n1 != null && n1.number > 5) {
                    split(n1, block, leaf, level - 1, 1);
                } else if (n2 != null && n2.number > 5) {
                    split(n2, block, leaf, level, 0);
                } else if (n1 != null && block.number + n1.number < 9) {
                    merge(n1, block, leaf, level);
                } else if (n2 != null && block.number + n2.number < 9) {
                    merge(n2, block, leaf, level + 1);
                }
            }
        }
        BTreeNode tmp = block.parent;
        while (tmp != null) {
            for(int i = 0 ; i < tmp.number ; i++) {
                if(tmp.values[i] == lft) {
                    tmp.values[i] = block.values[0];
                    break;
                }
            }
            tmp = tmp.parent;
        }
    }
    void split(BTreeNode n1, BTreeNode n2, boolean leaf, int lft, int crr) {
        String rht = n2.values[0];
        if(crr == 0) {
            if(!leaf) {
                n1.values[n1.number] = n1.parent.values[lft];
                n1.children[n1.number+1] = n2.children[0];
                n1.number++;
                n1.parent.values[lft] = n2.values[0];
                for(int i = 0 ; i < n2.number+1 ; i++) {
                    n2.values[i] = n2.values[i+1];
                    n2.children[i] = n2.children[i+1];
                }
                n2.number--;
            }
            else {
                n1.values[n1.number] = n2.values[0];
                n1.number++;
                for(int i = 0 ; i < n2.number+1 ; i++) {
                    n2.values[i] = n2.values[i+1];
                }
                n2.number--;
                n1.parent.values[lft] = n2.values[0];
            }
        }
        else {
            if(!leaf) {
                for(int i = 0 ; i < n2.number+1 ; i++) {
                    n2.values[i+1] = n2.values[i];
                    n2.children[i] = n2.children[i-1];
                }
                n2.values[0] = n1.parent.values[lft];
                n2.children[0] = n1.children[n1.number];
                n2.number++;
                n1.parent.values[lft] = n1.values[n1.number-1];
                n1.values[n1.number-1] = null;
                n1.children[n1.number] = null;
                n1.number--;
            }
            else {
                for(int i = 0 ; i < n2.number+1 ; i++) {
                    n2.values[i+1] = n2.values[i];
                }
                n2.values[0] = n1.values[n1.number-1];
                n2.number++;
                n1.values[n1.number-1] = null;
                n1.number--;
                n1.parent.values[lft] = n2.values[0];
            }
        }
    }

    void merge(BTreeNode n1, BTreeNode n2, boolean leaf, int rht) {
        if(!leaf) {
            n1.values[n1.number] = n1.parent.values[rht-1];
            n1.number++;
        }
        int j = n1.number;
        for(int i = 0 ; i < n2.number+1 ; i++) {
            n1.values[j] = n2.values[0];
            n1.children[j] = n2.children[0];
        }
        n1.number += n2.number;
        for(int i = rht ; i < n1.parent.number+1 ; i++) {
            n1.parent.values[i-1] = n1.parent.values[i];
            n1.parent.children[i] = n1.parent.children[i+1];
        }
        n1.parent.number--;
        for(int i = 0 ; n1.children[i] != null ; i++) {
            n1.children[i].parent = n1;
        }
    }
}
