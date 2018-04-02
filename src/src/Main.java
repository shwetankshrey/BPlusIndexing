package src;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner sc = new Scanner(System.in);
        String dataFile = "C:\\Users\\shwet\\Desktop\\BPlusIndexing\\src\\resources\\data";
        System.out.println("Loading data...");
        System.out.println("Choose index to be used : ");
        System.out.println("Press 1 for Validity Tag (Dense)");
        System.out.println("Press 2 for Instructor ID (Dense)");
        System.out.println("Press 3 for Instructor Name (Dense)");
        System.out.println("Press 4 for Department (Sparse)");
        System.out.println("Press 5 for Salary (Sparse)");
        System.out.print("Enter you index of choice : ");
        int c = sc.nextInt();
        System.out.println("Creating indices...");
        BPlusTree tree = new BPlusTree(dataFile, c);
        System.out.println("Done.");
        System.out.println("Welcome to BPlusIndexingSimulator!");
        System.out.println("Follow the given keybindings : ");
        System.out.println("Find(V) : '1 V'");
        System.out.println("PrintAll(V) : '2 V'");
        System.out.println("FindRange(L,U) : '3 L U'");
        System.out.println("Insert(R) : '4' followed by details");
        System.out.println("Delete(R) : '5 Ri' where Ri is the ID");
        System.out.println("Exit : '6'");
        while (true) {
            System.out.print("Enter your operation : ");
            int s = sc.nextInt();
            switch (s) {
                case 1: {
                    tree.find(sc.next());
                    break;
                }
                case 2: {
                    tree.printAll(sc.next());
                    break;
                }
                case 3: {
                    tree.findRange(sc.next(), sc.next());
                    break;
                }
                case 4: {
                    String arr[] = new String[5];
                    System.out.print("Enter Validity Tag : ");
                    arr[0] = sc.next();
                    System.out.print("Enter Instructor ID : ");
                    arr[1] = sc.next();
                    System.out.print("Enter Instructor Name : ");
                    arr[2] = sc.next();
                    System.out.print("Enter Department : ");
                    arr[3] = sc.next();
                    System.out.print("Enter Salary : ");
                    arr[4] = sc.next();
                    tree.insert(arr, c);
                    break;
                }
                case 5: {
                    tree.delete(sc.next());
                    break;
                }
                case 6: {
                    System.out.println("Thanks for using!");
                    return;
                }
            }
        }
    }
}
