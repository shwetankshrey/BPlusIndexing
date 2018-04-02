package src;

public class BTreeNode {

    int number;
    BTreeNode parent;
    String values[];
    BTreeNode children[];

    public BTreeNode() {
        number = 0;
        parent = null;
        values = new String[15];
        children = new BTreeNode[15];
    }
}
