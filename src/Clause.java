import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Clause extends HashSet<Literal> {
	
	public Clause() {
		super();
	}
	
	public Clause(Collection<Literal> literals) {
		super(literals);
	}
	
	public boolean isHornClause() {
		int n = 0;
		for (Literal lit : this)
			if (lit.second) {
				n++;
				if (n > 1)
					return false;
			}
		return true;
	}
	
	public boolean isDefiniteClause() {
		int n = 0;
		for (Literal lit : this)
			if (lit.second) {
				n++;
				if (n > 1)
					return false;
			}
		return (n == 1);
	}
	
	public boolean isNegativeClause() {
		for (Literal lit : this)
			if (lit.second)
				return false;
		return true;
	}
	
	public boolean isSubsetOf(Clause cl) {
		//returns true iff small subset of big
		Set<Literal> duplicate = new HashSet<>(cl);
		
		l1: for (Literal l1 : this) {
			for (Literal l2 : duplicate) {
				if (l1.equals(l2)) {
					duplicate.remove(l2);
					continue l1;
				}
			}
			return false;
		}
		return true;
	}
	
	public Clause resolutionRule(Clause cl) { //assumes both clauses are simplified
		Set<Literal> resLits = new HashSet<>();
		
		boolean compsFound = false;
		Literal taggedLit = null;
		
		l1: for (Literal l1 : this) {
			for (Literal prevL : resLits) {
				if (prevL.equals(l1))
					continue l1;
				if (prevL.complements(l1))
					return null;
			}
			resLits.add(l1);
			l2: for (Literal l2 : cl) {
				if (taggedLit != null)
					if (taggedLit.equals(l2))
						continue;
				if (l1.complements(l2)) {
					if (!compsFound)
						compsFound = true;
					else
						return null;
					resLits.remove(l1);
					resLits.remove(l2);
					taggedLit = l2;
					continue l1;
				}
				else {
					for (Literal prevL : resLits) {
						if (prevL.equals(l2))
							continue l2;
						if (prevL.complements(l2))
							return null;
					}
					resLits.add(l2);
				}
			}
		}
		if (compsFound)
			return new Clause(resLits);
		
		return null;
		
	}
	
	public Literal singleton() {
		//returns null if not singleton, returns Literal inside if is singleton
		if (this.size() == 1) {
			for (Literal l : this)
				return l;
		}
		return null;
	}

	public boolean isSingleton() {
		return this.size() == 1;
	}
	/*
	public Tuple<Clause,Clause> SLDresolve(Clause defClause, Literal comp) {
		//DOESN'T WORK
		return null;
		/*
		//assumes safe conditions
		//"this" is the singleton negative clause
		Clause clause1 = new Clause(Arrays.asList(comp, this.singleton()));
		Clause clause2 = new Clause();
		for (Literal l : defClause)
			if (l != comp)
				clause2.add(l);
		return new Tuple<>(clause1,clause2);
	}*/
	
}
