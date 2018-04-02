DBMS Assignment 3
B+ Tree Indexing

Authors:

Rohan Chhokra 2016080
Shwetank Shrey 2016095

File Structure: 

src
 `-+-- resources
   |    `-+-- data
   |       `- index
   +-- src
   |    `-+-- BPlusTree.java
   |      +-- BTreeNode.java
   |      +-- IndexFile.java
   |      +-- Main.java
   |       `- Record.java
    `- readme.txt

Implementation:

resources ->    data contains initial dataset and represents disk
	     	index contains serialised hashmap between the index and the record and represents the memory
src	  -> 	Main is the inital CLI class
	     	Record is the class to store record data
		IndexFile contains the map between index and record
		BTreeNode is the node of a B+ Tree
		BPlusTree is the B+ Tree with the necessary functions

Reference for B+ Tree : http://sketchingdream.com/blog/b-plus-tree-tutorial/