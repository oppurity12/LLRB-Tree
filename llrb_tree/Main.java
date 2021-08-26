package data_structure.llrb_tree;

public class Main {
    static class Node {
        int i;
        Node(int i) {
            this.i = i;
        }
    }
    public static void main(String[] args) {
        RedBlackTree<Integer, Integer> tree = new RedBlackTree<>();
        for (int i = 1; i < 31; i++)
            tree.insert(i, i);
        tree.print();
        tree.delete(5);
        System.out.println();
        tree.print();
    }
}
