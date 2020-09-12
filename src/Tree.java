import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;



public class Tree<T> {

	private T head;

	private ArrayList<Tree<T>> leafs = new ArrayList<Tree<T>>();

	private Tree<T> parent = null;

	public Tree() {
		this.head = null;
	}
	
	public Tree(T head) {
		this.head = head;
	}

	public void addLeaf(int rootNum, T leaf) {
			leafs.get(rootNum).addLeaf(leaf);
	}

	public void addLeaf(T leaf) {
		Tree<T> t = new Tree<T>(leaf);
		leafs.add(t);
		t.parent = this;
	}
	
	public void addLeaf(Tree<T> t) {
		leafs.add(t);
		t.parent = this;
	}
	
	public void addLeaf(int rootNum, Tree<T> t) {
		leafs.get(rootNum).addLeaf(t);
	}

	public Tree<T> setAsParent(T parentRoot) {
		Tree<T> t = new Tree<T>(parentRoot);
		t.leafs.add(this);
		this.parent = t;
		return t;
	}

	public T getHead() {
		return head;
	}

	public Tree<T> getParent() {
		return parent;
	}

	public ArrayList<T> elementList() {
		ArrayList<T> out = new ArrayList<T>();
		out.add(head);
		for (Tree<T> leaf : leafs)
			out.addAll(leaf.elementList());
		return out;
	}

	public ArrayList<Tree<T>> getLeafs() {
		return leafs;
	}
	
	public Tree<T> getLeaf(int i) {
		return leafs.get(i);
	}

	public List<Tree<T>> getEnds() {
		List<Tree<T>> out = new ArrayList<Tree<T>>();
		if (leafs.isEmpty()) {
			out.add(this);
			return out;
		}
		for (Tree<T> leaf : leafs) {
			out.addAll(leaf.getEnds());
		}
		return out;
	}
	
	public List<T> getEndHeads() {
		List<T> out = new ArrayList<T>();
		for (Tree<T> end : getEnds())
			out.add(end.head);
		return out;
	}

	public boolean equals(Tree<T> t) {
		if (t == null)
			return false;
		if (!(head.equals(t.head)))
			return false;
		for (int i = 0; i < leafs.size(); i++)
			if (! this.leafs.get(i).equals(t.leafs.get(i)))
				return false;
		return true;
	}
	
	public String toString() {
		return printTree(0);
	}

	private static final int indent = 8;

	private String printTree(int increment) {
		String s = "";
		String inc = "";
		for (int i = 0; i < increment; ++i) {
			inc = inc + " ";
		}
		s = inc + head;
		for (Tree<T> child : leafs) {
			s += "\n" + child.printTree(increment + indent);
		}
		return s;
	}
}