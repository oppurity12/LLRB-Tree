package data_structure.llrb_tree;

import java.util.*;

public class RedBlackTree<Key extends Comparable<Key>, Value> {
    private static final boolean RED = true;
    private static final boolean Black = false;
    private Node root;

    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        boolean color;

        public Node(Key k, Value v, boolean col) {
            key = k;
            val = v;
            color = col;
        }

    }

    private boolean isRed(Node n) {
        if (n == null)
            return false;

        return n.color == RED;
    }

    public Value get(Key k) {
        return get(root, k);
    }

    //같은 문자열 n번 출력
    private void printMultipleTime(int n, String str) {
        for (int i = 0; i < n; i++)
            System.out.print(str);

    }

    //tree 출력
    //LLRB tree는 완전 이진 트리는 아니지만 balance 왼쪽 서브트리와 오른쪽 서브트리의 높이차가 2를 넘지는 않음
    //root node의 idx는 1, 왼쪽 자식은 2 * idx, 오른쪽 자식은 2 * idx + 1로 배열형태로 저장.
    public void print() {
        //Node와 idx를 묶어주는 역할
        class idxNode {
            Node node;
            int idx;

            idxNode(Node node, int idx) {
                this.node = node;
                this.idx = idx;
            }
        }
        //레벨 순회
        Queue<idxNode> queue = new LinkedList<>();
        idxNode idxRoot = new idxNode(root, 1);
        queue.offer(idxRoot);
        int len = (int) Math.pow(2, height());
        String[] arr = new String[len];
        Arrays.fill(arr, "⬜️️");
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                idxNode cur = queue.poll();
                arr[cur.idx] = cur.node.color ? "\uD83D\uDFE5" : "⬛️️";
                if (cur.node.left != null) {
                    idxNode temp = new idxNode(cur.node.left, cur.idx * 2);
                    queue.offer(temp);
                }
                if (cur.node.right != null) {
                    idxNode temp = new idxNode(cur.node.right, cur.idx * 2 + 1);
                    queue.offer(temp);
                }
            }
        }

        int size = len - 1; //트리의 가장 아래부분(출력에서 제일 긴 사이즈);
        for (int i = 0; i < height(); i++) {
            int curLen = (int) Math.pow(2, i); //현재 레벨에서 노드의 수
            int leftPaddingSize = size / ((int) Math.pow(2, i + 1)); //왼쪽 패딩
            int middlePaddingSize = i == 0 ? 0 : (size - leftPaddingSize * 2 - curLen) / (curLen - 1); //중앙 패딩
            printMultipleTime(leftPaddingSize, "⬜");
            for (int j = (int) Math.pow(2, i); j < (int) Math.pow(2, i + 1); j++) {
                System.out.print(arr[j]);
                if (j == (int) Math.pow(2, i + 1) - 1)
                    continue;
                printMultipleTime(middlePaddingSize, "⬜");
            }
            printMultipleTime(leftPaddingSize, "⬜");
            System.out.println();
        }
    }

    public int height() {
        return height(root);
    }

    private int height(Node n) {
        if (n == null) return 0;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    public Value get(Node n, Key k) {
        if (n == null) return null;
        int t = n.key.compareTo(k);
        if (t > 0) return get(n.left, k);
        if (t < 0) return get(n.right, k);
        return n.val;
    }

    private Node rotateLeft(Node n) {
        Node x = n.right;
        n.right = x.left;
        x.left = n;
        x.color = n.color;
        n.color = RED;
        return x;
    }

    private Node rotateRight(Node n) {
        Node x = n.left;
        n.left = x.right;
        x.right = n;
        x.color = n.color;
        n.color = RED;
        return x;
    }


    //4-nodes 분리하고 red link를 한 레벨 위로 보낸다.
    private void flipColors(Node n) {
        n.color = !n.color;
        n.left.color = !n.left.color;
        n.right.color = !n.right.color;

    }

    public void insert(Key k, Value v) {
        root = insert(root, k, v);
        root.color = Black;
    }

    private Node insert(Node n, Key k, Value v) {
        if (n == null) return new Node(k, v, RED);

        //이부분에 존재시 2-3-4 tree
        //if (isRed(n.left) && isRed(n.right)) flipColors(n);

        int t = k.compareTo(n.key);
        if (t < 0) n.left = insert(n.left, k, v);
        else if (t > 0) n.right = insert(n.right, k, v);
        else n.val = v;

        if (!isRed(n.left) && isRed(n.right)) n = rotateLeft(n);  //오른쪽으로 red link가 치우친 경우 바로잡는다.
        if (isRed(n.left) && isRed(n.left.left)) n = rotateRight(n); //연속으로 red link가 2번 등장한 경우 바로잡는다.


        //4-nodes를 split해서 위로 보낸다
        //이 부분이 위쪽에 있을시 2-3-4 trees(4-nodes를 split하기 때문에 2-3 트리)
        if (isRed(n.left) && isRed(n.right)) flipColors(n);

        return n;
    }

    //node를 제거할 때 red이면 리프노드에서 루트 노드까지의 검정 노드의 개수에 영향을 미치지 않지만
    //검정노드이면 트리의 조건을 깨트린다. 제거해주려는 노드를 red 로 만들기 위해서 빨간 링크를 왼쪽으로 보내주는 역할
    private Node moveRedLeft(Node n) {
        flipColors(n);
        if (isRed(n.right.left)) {
            n.right = rotateRight(n.right);
            n = rotateLeft(n);
            flipColors(n);
        }
        return n;
    }

    //마찬가지로 제거해주려는 노드를 빨간 링크로 만들기 위해서 red link를 오른쪽으로 옮기는 역할.
    private Node moveRedRight(Node n) {
        flipColors(n);
        if (isRed(n.left.left)) {
            n = rotateRight(n);
            flipColors(n);
        }
        return n;
    }



    //Red Link들이 왼쪽으로 편향되게 fix up
    private Node fixUp(Node n) {
        //red link가 오른쪽에 있으므로 왼쪽으로 rotate
        if (isRed(n.right))
            n = rotateLeft(n);
        //연속으로 red link가 2번 나오는 케이스. 오른쪽으로 rotate하면 피할 수 있다.
        if (isRed(n.left) && isRed(n.left.left))
            n = rotateRight(n);
        //왼쪽 자식과 오른쪽 자식이 red 인경우 red link를 위로 올려보내다.(2-3-4 tree에서 4-nodes split 하는 경우)
        if (isRed(n.left) && isRed(n.right))
            flipColors(n);
        return n;
    }
    public void deleteMax() {
        root = deleteMax(root);
        root.color = Black;
    }

    private Node deleteMax(Node n) {
        if (isRed(n.left))
            n = rotateRight(n);
        if (n.right == null)
            return null;
        if (!isRed(n.right) && !isRed(n.right.left))
            n = moveRedRight(n);

        n.right = deleteMax(n.right);

        return fixUp(n);
    }

    public Node min() {
        if (root == null) return null;
        return min(root.left);
    }

    public Node min(Node n) {
        if (n.left == null)
            return n;
        return min(n.left);
    }

    public void deleteMin() {
        root = deleteMin(root);
        root.color = Black;
    }

    public Node deleteMin(Node n) {
        if (n.left == null)
            return null;
        if (!isRed(n.left) && !isRed(n.left.left))
            n = moveRedLeft(n);
        n.left = deleteMax(n.left);
        return fixUp(n);
    }

    public void delete(Key key) {
        root = delete(root, key);
        root.color = Black;
    }

    private Node delete(Node n, Key key) {
        int t = n.key.compareTo(key);
        //key < node.key
        if (t > 0) {
            if (!isRed(n.left) && !isRed(n.left.left))
                n = moveRedLeft(n);
            n.left = delete(n.left, key);
        //key >= node.ky
        } else {
            //레드 링크가 왼쪽에 있으므로 오른쪽으로 회전;
            if (isRed(n.left))
                n = rotateRight(n);
            if (t == 0 && n.right == null)
                return null;
            //레드 링크를 오른쪽으로 보내준다.
            if(!isRed(n.right) && !isRed(n.right.left))
                n = moveRedRight(n);

            if (t == 0) {
                Node target = min(n.right);
                n.key = target.key;
                n.val = target.val;
                n.right = deleteMin(n.right);
            }
            else {
                n.right = delete(n.right, key);
            }
        }
        //트리의 규칙에 위배되지 않도록 fix up
        return fixUp(n);
    }

}

