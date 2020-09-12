import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClauseSet extends HashSet<Clause> {
	
	public ClauseSet() {
		super();
	}
	
	public ClauseSet(Collection<Clause> clauses) {
		super(clauses);
	}
	
	public ClauseSet simplify() {
		
		ClauseSet out = new ClauseSet();
		
		cl: for (Clause possibleClause : this) {
			//for each possible clause
			
			//first, simplify the clause on its own
			Clause actualClause = new Clause();
			lit: for (Literal l : possibleClause) {
				//for each literal in the clause
					//if it already exists in the clause, don't include it
					//if its complement exists in the clause, trash the tautological clause
				for (Literal prevL : actualClause) {
					if (l.equals(prevL))
						continue lit;
					if (l.complements(prevL))
						continue cl;
				}
				actualClause.add(l);
			}
			
			//now check whether clause can be combined with others
			for (Clause prevClause : out) {
				//for each already existing clause
					//compare to new clause
					//if one clause is a subset of the other, scratch the larger one
				if (prevClause.isSubsetOf(actualClause)) {
					continue cl;
				}
				if (actualClause.isSubsetOf(prevClause)) {
					out.remove(prevClause);
					out.add(actualClause);
					continue cl;
				}
			}
			
			out.add(actualClause);
			//*/
		}
		return out;
	}
	
	public ClauseSet newSetWith(Clause newClause) {
		ClauseSet newClauseSet = new ClauseSet(this);
		
		Set<Clause> toRemove = new HashSet<Clause>();
		
		for (Clause prevClause : this) {
			if (prevClause.isSubsetOf(newClause)) {
				return this;
			}
			if (newClause.isSubsetOf(prevClause)) {
				toRemove.add(prevClause);
			}
		}
		newClauseSet.removeAll(toRemove);
		newClauseSet.add(newClause);
		return newClauseSet;
	}
	
	public ClauseSet newSetWithout(Clause toRemove) {
		ClauseSet out = new ClauseSet(this);
		out.remove(toRemove);
		return out;
	}
	
	public ClauseSet simpleNewSetWith(Clause newClause) {
		ClauseSet out = new ClauseSet(this);
		out.add(newClause);
		return out;
	}
	
	public boolean isSLDWorkable() {
		boolean goalFound = false;
		for (Clause cl : this) {
			if (cl.isDefiniteClause())
				;
			else if (cl.isNegativeClause()) {
				if (goalFound)
					return false;
				goalFound = true;
			}
			else
				return false;
		}
		return goalFound;
	}
	
	public boolean isAxiom() {
		List<Literal> litList = new ArrayList<>();
		for (Clause cl : this) {
			Literal l = cl.singleton();
			if (l != null)
				litList.add(l);
		}
		for (int i = 0; i < litList.size(); i++) {
			for (int j = i + 1; j < litList.size(); j++) {
				if (litList.get(i).complements(litList.get(j)))
					return true;
			}
		}
		return false;
	}
	
}
