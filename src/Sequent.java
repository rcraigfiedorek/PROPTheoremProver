import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Sequent {

	public List<Tree<String>> leftSide;
	public List<Tree<String>> rightSide;
	
	public Sequent(List<Tree<String>> leftSide, List<Tree<String>> rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}
	
	public boolean isAxiom() {
		for (Tree<String> lf : leftSide) {
			for (Tree<String> rf : rightSide) {
				if (lf.equals(rf))
					return true;
			}
		}
		return false;
	}
	
	public Set<Tree<String>> allFormulas() {
		Set<Tree<String>> out = new HashSet<>();
		out.addAll(leftSide);
		out.addAll(rightSide);
		return out;
	}
	
	public boolean isFinished() {
		if (isAxiom())
			return true;
		for (Tree<String> t : allFormulas()) {
			if (! PLang.isAtomic(t))
				return false;
		}
		return true;
	}
	
	public String toString() {
		String out = "";
		for (Tree<String> lf : leftSide) {
			out += PLang.infixPrint(lf);
			out += "  ,  ";
		}
		if (!leftSide.isEmpty())
			out = out.substring(0, out.length() - 5) + "  ";
		out += "|--  ";
		for (Tree<String> rf : rightSide) {
			out += PLang.infixPrint(rf);
			out += "  ,  ";
		}
		if (!rightSide.isEmpty())
			out = out.substring(0, out.length() - 5);
		return out;
	}
	
	public List<Sequent> reduceLeft(Tree<String> f) {
		if (!leftSide.contains(f))
			return new ArrayList<>();
		if (PLang.isAtomic(f))
			return null;
		if (f.getHead().equals("~")) {
			List<Tree<String>> L = new ArrayList<>();
			L.addAll(leftSide);
			L.remove(f);
			List<Tree<String>> R = new ArrayList<>();
			R.add(f.getLeaf(0));
			R.addAll(rightSide);
			return Arrays.asList(new Sequent(L, R));
		}
		if (f.getHead().equals("&&")) {
			List<Tree<String>> L = new ArrayList<>();
			for (Tree<String> lf : leftSide) {
				if (! lf.equals(f))
					L.add(lf);
				else {
					L.add(f.getLeaf(0));
					L.add(f.getLeaf(1));
				}
			}
			return Arrays.asList(new Sequent(L, rightSide));
		}
		if (f.getHead().equals("||")) {
			List<Tree<String>> L1 = new ArrayList<>();
			List<Tree<String>> L2 = new ArrayList<>();
			for (Tree<String> lf : leftSide) {
				if (! lf.equals(f)) {
					L1.add(lf);
					L2.add(lf);
				}
				else {
					L1.add(f.getLeaf(0));
					L2.add(f.getLeaf(1));
				}
			}
			return Arrays.asList(new Sequent(L1, rightSide), new Sequent(L2, rightSide));
		}
		if (f.getHead().equals("->")) {
			List<Tree<String>> L1 = new ArrayList<>();
			List<Tree<String>> R = new ArrayList<>();
			List<Tree<String>> L2 = new ArrayList<>();
			R.add(f.getLeaf(0));
			R.addAll(rightSide);
			L1.addAll(leftSide);
			L1.remove(f);
			L2.add(f.getLeaf(1));
			L2.addAll(leftSide);
			L2.remove(f);
			return Arrays.asList(new Sequent(L1, R), new Sequent(L2, rightSide));
		}
		if (f.getHead().equals("<->")) {
			List<Tree<String>> L1 = new ArrayList<>();
			List<Tree<String>> R = new ArrayList<>();
			List<Tree<String>> L2 = new ArrayList<>();
			R.add(f.getLeaf(0));
			R.add(f.getLeaf(1));
			R.addAll(rightSide);
			L1.addAll(leftSide);
			L1.remove(f);
			L2.add(f.getLeaf(0));
			L2.add(f.getLeaf(1));
			L2.addAll(leftSide);
			L2.remove(f);
			return Arrays.asList(new Sequent(L1, R), new Sequent(L2, rightSide));
		}
		return null;
	}
	
	public List<Sequent> reduceRight(Tree<String> f) {
		if (!rightSide.contains(f)) {
			return new ArrayList<>();
		}
		if (PLang.isAtomic(f)) {
			System.out.println("err143");
			return null;
		}
		if (f.getHead().equals("~")) {
			List<Tree<String>> R = new ArrayList<>();
			R.addAll(rightSide);
			R.remove(f);
			List<Tree<String>> L = new ArrayList<>();
			L.add(f.getLeaf(0));
			L.addAll(leftSide);
			return Arrays.asList(new Sequent(L, R));
		}
		if (f.getHead().equals("&&")) {
			List<Tree<String>> R1 = new ArrayList<>();
			List<Tree<String>> R2 = new ArrayList<>();
			for (Tree<String> rf : rightSide) {
				if (! rf.equals(f)) {
					R1.add(rf);
					R2.add(rf);
				}
				else {
					R1.add(f.getLeaf(0));
					R2.add(f.getLeaf(1));
				}
			}
			return Arrays.asList(new Sequent(leftSide, R1), new Sequent(leftSide, R2));
		}
		if (f.getHead().equals("||")) {
			List<Tree<String>> R = new ArrayList<>();
			for (Tree<String> rf : rightSide) {
				if (! rf.equals(f)) {
					R.add(rf);
				}
				else {
					R.add(f.getLeaf(0));
					R.add(f.getLeaf(1));
				}
			}
			return Arrays.asList(new Sequent(leftSide, R));
		}
		if (f.getHead().equals("->")) {
			List<Tree<String>> L = new ArrayList<>();
			List<Tree<String>> R = new ArrayList<>();
			L.add(f.getLeaf(0));
			L.addAll(leftSide);
			R.add(f.getLeaf(1));
			R.addAll(rightSide);
			R.remove(f);
			return Arrays.asList(new Sequent(L, R));
		}
		if (f.getHead().equals("<->")) {
			List<Tree<String>> L1 = new ArrayList<>();
			List<Tree<String>> R1 = new ArrayList<>();
			List<Tree<String>> L2 = new ArrayList<>();
			List<Tree<String>> R2 = new ArrayList<>();
			L1.add(f.getLeaf(0));
			L1.addAll(leftSide);
			R1.add(f.getLeaf(1));
			R1.addAll(rightSide);
			R1.remove(f);
			L2.add(f.getLeaf(1));
			L2.addAll(leftSide);
			R2.add(f.getLeaf(0));
			R2.addAll(rightSide);
			R2.remove(f);
			return Arrays.asList(new Sequent(L1, R1), new Sequent(L2, R2));
		}
		System.out.println("err211");
		return null;
	}
	
}
