import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class PLang {
	
	public static final Set<String> atomicProps = initializeAtomicProps();
	private static Set<String> initializeAtomicProps() {
		Set<String> out = new HashSet<>();
		for (int j = 0; j < 10; j++) {
			for (int i = 'A'; i <= 'Z'; i++) {
				String s = "";
				s += (char) i;
				if (j==0)
					out.add(s);
				else {
					s += Integer.toString(j);
					out.add(s);
				}
			}
		}
		return out;
	}
	
	public static final Set<String> connectives = new HashSet<>(Arrays.asList("~","&&","||","->","<->"));
	private static final List<String> precedenceList = Arrays.asList("~","&&","||","->","<->");
	public static final Set<String> parentheses = new HashSet<>(Arrays.asList("(",")"));
	
	
	public static boolean isSymbol(String s) {
		if (atomicProps.contains(s))
			return true;
		if (connectives.contains(s))
			return true;
		if (parentheses.contains(s))
			return true;
		return false;
	}
	
	public static String infixPrint(Tree<String> t) {
		if (atomicProps.contains(t.getHead()))
			return t.getHead();
		if (connectives.contains(t.getHead())) {
			if (t.getHead().equals("~"))
				return "~ "+infixPrint(t.getLeaf(0));
			return ("( " + infixPrint(t.getLeaf(0)) + " " + t.getHead() + " " + infixPrint(t.getLeaf(1)) + " )");
		}
		return null;
	}
	
	private static int getCloseParenthesis(List<String> s, int openParInd) { //returns -1 if index isn't open parenthesis, or no close exists
		if (!s.get(openParInd).equals("("))
			return -1;
		int pCount = 1;
		for (int i = openParInd + 1; i < s.size(); i++) {
			if (s.get(i).equals("("))
				pCount++;
			else if (s.get(i).equals(")"))
				pCount--;
			if (pCount == 0)
				return i;
		}
		return -1;
	}
	
	public static Tree<String> infixReader(String s) {
		return infixReader(infixReaderStringAnalyzer(s));
	}
	
	public static List<String> infixReaderStringAnalyzer(String s) {
		
		ArrayList<String> symbList = new ArrayList<String>();
		
		String spaceLess = s.replaceAll("\\s", "");
		
		if (isSymbol(spaceLess)) {
			symbList.add(spaceLess);
			return symbList;
		}
		
		final int maxSymbLen = 3;
		
		ArrayList<String> possibleFirstSymbs = new ArrayList<String>();
		
		for (int i = 1; i <= maxSymbLen && i < spaceLess.length(); i++) {
			String firstSymb = spaceLess.substring(0, i);
			if (isSymbol(firstSymb) && s.contains(firstSymb)) {
				possibleFirstSymbs.add(firstSymb);
			}
		}
		if (possibleFirstSymbs.isEmpty()) return null;
		
		String firstSymb = possibleFirstSymbs.get(possibleFirstSymbs.size() - 1);
		symbList.add(firstSymb);
		symbList.addAll(infixReaderStringAnalyzer(s.substring(s.indexOf(firstSymb)+firstSymb.length())));
		return symbList;
	}
	
	public static void sequentCalcProofsWithInput() {
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to PROP, the uncreatively named propositional logic automated theorem prover! "
				+ "PROP takes a user-inputted propositional formula and determines if it is a tautology "
				+ "or if it has a valuation that makes it false. Type either:\n-- A propositional formula\n"
				+ "-- \"help\"\n-- \"exit\"\n"
				+ "-- \"change mode\"");
		boolean seqMode = true;
		while (true) {
			
			System.out.print(">>:  ");
			String input = in.nextLine();
			//String input = "(A -> B ) <-> (~B -> ~A)";
			if (input.equals("exit"))
				break;
			if (input.equals("help")) {
				System.out.println("Blah blah help.");
				continue;
			}
			if (input.equals("change mode")) {
				seqMode = !seqMode;
				if (seqMode) {
					System.out.println("Current mode: sequent calculus");
				}
				else {
					System.out.println("Current mode: resolution");
				}
				continue;
			}
			Tree<String> formula = infixReader(input);
			if (formula == null) {
				System.out.println("Invalid input.");
				continue;
			}
			Tuple<Tree<Sequent>,Boolean> result0 = null;
			Tuple<List<ClauseSet>,Boolean> result1 = null;
			if (seqMode) {
				Sequent testSeq = new Sequent(new ArrayList<>(), Arrays.asList(formula));
				result0 = search(testSeq);
			}
			else {
				result1 = resolution(formula);
			}
			if (seqMode) {
				if (result0.second) {
					System.out.println("True! \nPROP found that this statement is a tautology. The following is a sequent tree showing so:");
				}
				else {
					System.out.println("False! \nPROP found that this statement is not a tautology. The following is a sequent tree showing so:");
				}
				System.out.println(result0.first);
			}
			else {
				if (result1.second) {
					System.out.println("True! \nPROP found that this statement is a tautology. The following is a resolution proof showing so:");
				}
				else {
					System.out.println("False! \nPROP found that this statement is not a tautology. The following is an attempted resolution proof showing so:");
				}
				for (ClauseSet a : result1.first) {
					System.out.println(a);
				}
			}
		}
		System.out.println("Goodbye!");
		in.close();
	}
	
	public static boolean isAtomic(Tree<String> t) {
		return atomicProps.contains(t.getHead());
	}
	
	public static Set<String> getSymbolsOf(Tree<String> t) {
		if (atomicProps.contains(t.getHead()))
			return Collections.singleton(t.getHead());
		if (connectives.contains(t.getHead())) {
			Set<String> out = new HashSet<>();
			for (Tree<String> l : t.getLeafs())
				out.addAll(getSymbolsOf(l));
			return out;
		}
		return null;
	}
	
	public static Tree<String> infixReader(List<String> s) {
		
		if (s==null) return null;
		
		if (s.isEmpty())
			return null;
				
		Stack<String> opStack = new Stack<String>();
		Stack<Tree<String>> termStack = new Stack<Tree<String>>();
		int parenthesisLevel = 0;
		
		for (int i = 0; i < s.size(); i++) {
			
			String symb = s.get(i);
			
			if (parenthesisLevel < 0)
				return null;
			if (!isSymbol(symb))
				return null;
			
			if (symb.equals("(")) {
				int jump = getCloseParenthesis(s, i);
				Tree<String> newT = infixReader(s.subList(i+1, jump));
				if (newT.equals(null)) return null;
				termStack.push(newT);
				i = jump;
			}
			
			else if (symb.equals(")")) {
				return null;
			}
			
			else if (atomicProps.contains(symb)) {
				termStack.push(new Tree<String>(symb));
			}
			
			else if (precedenceList.contains(symb)) {//for infix functions
				while (!opStack.empty()) {
					if (precedenceList.indexOf(symb) > precedenceList.indexOf(opStack.peek())) {
						if (opStack.peek().equals("~")) {
							if (termStack.empty())
								return null;
							Tree<String> newT = new Tree<String>(opStack.pop());
							newT.addLeaf(termStack.pop());
							termStack.push(newT);
						}
						else {
							if (termStack.size() < 2)
								return null;
							Tree<String> newT = new Tree<String>(opStack.pop());
							Tree<String> arg2 = termStack.pop();
							Tree<String> arg1 = termStack.pop();
							newT.addLeaf(arg1);
							newT.addLeaf(arg2);
							termStack.push(newT);
						}
					}
					else break;
				}
				opStack.push(symb);
			}
			
		}
		while (!opStack.empty()) {
			String op = opStack.pop();
			if (op.equals("~")) {
				Tree<String> newT = new Tree<String>(op);
				newT.addLeaf(termStack.pop());
				termStack.push(newT);
				continue;
			}
			else {
				if (termStack.size() < 2)
					return null;
				Tree<String> newT = new Tree<String>(op);
				Tree<String> arg2 = termStack.pop();
				Tree<String> arg1 = termStack.pop();
				newT.addLeaf(arg1);
				newT.addLeaf(arg2);
				termStack.push(newT);
			}
		}
		if (termStack.size() == 1)
			return termStack.pop();
		else
			return null;
	}
	
	public static Tuple<Tree<Sequent>,Boolean> search(Sequent seq) {
		//second element of output tuple is true if Tree proves input seq, false if input seq is falsifiable
		Tree<Sequent> seqTree = new Tree<Sequent>(seq);
		boolean allFinished = seq.isFinished();
		while (! allFinished) {
			for (Tree<Sequent> endLeaf : seqTree.getEnds()) {
				if (! endLeaf.getHead().isFinished()) {
					expand(endLeaf);
				}
			}
			allFinished = true;
			for (Sequent endSeq : seqTree.getEndHeads()) {
				if (! endSeq.isFinished()) {
					allFinished = false;
					break;
				}
			}
		}
		boolean allLeafsAreAxioms = true;
		for (Sequent endSeq : seqTree.getEndHeads()) {
			if (! endSeq.isAxiom()) {
				allLeafsAreAxioms = false;
				break;
			}
		}
		if (allLeafsAreAxioms)
			return new Tuple<>(seqTree,true);
		else
			return new Tuple<>(seqTree,false);
	}
	
	public static void expand(Tree<Sequent> seqTree) {
		for (int i = 0; i < seqTree.getHead().leftSide.size(); i++) {
			Tree<String> toReduce = seqTree.getHead().leftSide.get(i);
			if (isAtomic(toReduce))
				continue;
			for (Tree<Sequent> endLeaf : seqTree.getEnds()) {
				if (! endLeaf.getHead().isAxiom()) {
					for (Sequent newLeaf : endLeaf.getHead().reduceLeft(toReduce)) {
						endLeaf.addLeaf(newLeaf);
					}
				}
			}
		}
		for (int i = 0; i < seqTree.getHead().rightSide.size(); i++) {
			Tree<String> toReduce = seqTree.getHead().rightSide.get(i);
			if (isAtomic(toReduce))
				continue;
			for (Tree<Sequent> endLeaf : seqTree.getEnds()) {
				if (! endLeaf.getHead().isAxiom()) {
					for (Sequent newLeaf : endLeaf.getHead().reduceRight(toReduce)) {
						endLeaf.addLeaf(newLeaf);
					}
				}
			}
		}
	}
	
	public static boolean isNNF(Tree<String> f) {
		if (isAtomic(f))
			return true;
		if (f.getHead().equals("~"))
			return isAtomic(f.getLeaf(0));
		if (f.getHead().equals("&&") || f.getHead().equals("||"))
			return (isNNF(f.getLeaf(0)) && isNNF(f.getLeaf(1)));
		return false;
	}
	
	public static Tree<String> toNNF(Tree<String> f) {
		if (isNNF(f))
			return f;
		if (f.getHead().equals("->")) {
			return disjunction(toNNF(negation(f.getLeaf(0))), toNNF(f.getLeaf(1)));
		}
		if (f.getHead().equals("<->")) {
			Tree<String> A = f.getLeaf(0);
			Tree<String> B = f.getLeaf(1);
			return toNNF(conjunction(implication(A,B),implication(B,A)));
		}
		if (f.getHead().equals("&&")) {
			return conjunction(toNNF(f.getLeaf(0)), toNNF(f.getLeaf(1)));
		}
		if (f.getHead().equals("||")) {
			return disjunction(toNNF(f.getLeaf(0)), toNNF(f.getLeaf(1)));
		}
		if (f.getHead().equals("~")) {
			Tree<String> subF = f.getLeaf(0);
			if (isAtomic(subF))
				return f;
			if (subF.getHead().equals("~")) {
				return toNNF(subF.getLeaf(0));
			}
			if (subF.getHead().equals("&&")) {
				return disjunction(toNNF(negation(subF.getLeaf(0))), toNNF(negation(subF.getLeaf(1))));
			}
			if (subF.getHead().equals("||")) {
				return conjunction(toNNF(negation(subF.getLeaf(0))), toNNF(negation(subF.getLeaf(1))));
			}
			else {
				return toNNF(negation(toNNF(subF)));
			}
		}
		return null;
	}
	public static Tree<String> reduceToNegConjDisj(Tree<String> f) {
		if (isAtomic(f))
			return f;
		if (f.getHead().equals("~"))
			return negation(reduceToNegConjDisj(f.getLeaf(0)));
		if (f.getHead().equals("&&"))
			return conjunction(reduceToNegConjDisj(f.getLeaf(0)), reduceToNegConjDisj(f.getLeaf(1)));
		if (f.getHead().equals("||"))
			return disjunction(reduceToNegConjDisj(f.getLeaf(0)), reduceToNegConjDisj(f.getLeaf(1)));
		if (f.getHead().equals("->"))
			return disjunction(negation(reduceToNegConjDisj(f.getLeaf(0))), reduceToNegConjDisj(f.getLeaf(1)));
		if (f.getHead().equals("<->")) {
			Tree<String> A = reduceToNegConjDisj(f.getLeaf(0));
			Tree<String> B = reduceToNegConjDisj(f.getLeaf(1));
			return conjunction(disjunction(negation(A),B),disjunction(A,negation(B)));
		}
		return null;
	}
	public static boolean containsOnlyNegConjDisj(Tree<String> f) {
		for (String s : f.elementList()) {
			if (Arrays.asList("->","<->").contains(s))
				return false;
		}
		return true;
	}
	public static boolean isCNF(Tree<String> f) {
		if (! f.getHead().equals("&&"))
			return isDisjunctionOfLiterals(f);
		if (isDisjunctionOfLiterals(f.getLeaf(0)))
			return isCNF(f.getLeaf(1));
		if (isCNF(f.getLeaf(0)))
			return isDisjunctionOfLiterals(f.getLeaf(1)) || isCNF(f.getLeaf(1));
		return false;
	}
	private static boolean isDisjunctionOfLiterals(Tree<String> f) {
		if (isAtomic(f))
			return true;
		if (f.getHead().equals("~"))
			return isAtomic(f.getLeaf(0));
		if (f.getHead().equals("||"))
			return (isDisjunctionOfLiterals(f.getLeaf(0)) && isDisjunctionOfLiterals(f.getLeaf(1)));
		return false;
	}
	
	public static Tree<String> toCNF(Tree<String> f) {
		Tree<String> F = f;
		if (!containsOnlyNegConjDisj(F))
			F = reduceToNegConjDisj(F);
		if (!isNNF(F))
			F = toNNF(F);
		if (isCNF(F))
			return F;
		if (F.getHead().equals("&&"))
			return conjunction(toCNF(F.getLeaf(0)),toCNF(F.getLeaf(1)));
		if (F.getHead().equals("||")) {
			Tree<String> A = toCNF(F.getLeaf(0));
			Tree<String> B = toCNF(F.getLeaf(1));
			if (!B.getHead().equals("&&")) {
				if (!A.getHead().equals("&&"))
					return disjunction(A,B);
				else  {
					return conjunction(toCNF(disjunction(A.getLeaf(0),B)), toCNF(disjunction(A.getLeaf(1),B)));
				}
			}
			else {
				return conjunction(toCNF(disjunction(A,B.getLeaf(0))), toCNF(disjunction(A,B.getLeaf(1))));
			}
		}
		return null;
	}
	public static boolean isLiteral(Tree<String> f) {
		if (f.getHead().equals("~"))
			return isAtomic(f.getLeaf(0));
		return isAtomic(f);
	}
	
	public static Tree<String> negation(Tree<String> A) {
		Tree<String> out = new Tree<>("~");
		out.addLeaf(A);
		return out;
	}
	public static Tree<String> implication(Tree<String> A, Tree<String> B) {
		Tree<String> out = new Tree<>("->");
		out.addLeaf(A);
		out.addLeaf(B);
		return out;
	}
	public static Tree<String> dualImplication(Tree<String> A, Tree<String> B) {
		Tree<String> out = new Tree<>("<->");
		out.addLeaf(A);
		out.addLeaf(B);
		return out;
	}
	public static Tree<String> conjunction(Tree<String> A, Tree<String> B) {
		Tree<String> out = new Tree<>("&&");
		out.addLeaf(A);
		out.addLeaf(B);
		return out;
	}
	public static Tree<String> disjunction(Tree<String> A, Tree<String> B) {
		Tree<String> out = new Tree<>("||");
		out.addLeaf(A);
		out.addLeaf(B);
		return out;
	}
	
	public static ClauseSet formulaToClauseSet(Tree<String> f) {
		Tree<String> F;
		if (isCNF(f))
			F = f;
		else
			F = toCNF(f);
		if (!F.getHead().equals("&&")) {
			return new ClauseSet(Collections.singleton(disjunctionToClause(F)));
		}
		else {
			ClauseSet out = new ClauseSet();
			out.addAll(formulaToClauseSet(F.getLeaf(0)));
			out.addAll(formulaToClauseSet(F.getLeaf(1)));
			return out;
		}
	}
	private static Clause disjunctionToClause(Tree<String> f) {
		if (!isLiteral(f) && !f.getHead().equals("||"))
			return null;
		if (isAtomic(f))
			return new Clause(Collections.singleton(new Literal(f.getHead(),true)));
		if (f.getHead().equals("~"))
			return new Clause(Collections.singleton(new Literal(f.getLeaf(0).getHead(),false)));
		if (f.getHead().equals("||")) {
			Clause out = new Clause();
			out.addAll(disjunctionToClause(f.getLeaf(0)));
			out.addAll(disjunctionToClause(f.getLeaf(1)));
			return out;
		}
		return null;
	}
	
	public static Tuple<List<ClauseSet>,Boolean> resolution(Tree<String> f) {
		return resolution(new HashSet<>(), f);
	}
	
	public static Tuple<List<ClauseSet>,Boolean> resolution(Set<Tree<String>> axioms, Tree<String> f) {
		Tree<String> F = negation(f);
		for (Tree<String> axiom : axioms) {
			F = conjunction(F,axiom);
		}
		List<ClauseSet> proofSteps = new ArrayList<>();
		ClauseSet clauseSet = (new ClauseSet(formulaToClauseSet(F))).simplify();
		
		//building initial clause set
		
		
		//clauseSet built
		
		//System.out.println("Init clauseSet: " + clauseSet);
		
		proofSteps.add(clauseSet);
		Map<Clause,ClauseSet> checkMap = buildCheckMap(clauseSet);
		//checkMap maps clauseSets to other clauseSets that it has been checked with
		boolean result;
		step: while (true) {
			//System.out.println("ClauseSet: " + clauseSet);
			//System.out.println("CheckMap: " + checkMap);
			//System.out.println("step");
			for (Clause clause1 : clauseSet) {
				//System.out.println("clause1: " + clause1);
				for (Clause clause2 : clauseSet) {
					//System.out.println("clause2: " + clause2);
					if (clause1==clause2)
						continue;
					if (checkMap.get(clause1).contains(clause2) || checkMap.get(clause2).contains(clause1))
						continue;
					checkMap.get(clause1).add(clause2);
					checkMap.get(clause2).add(clause1);
					//System.out.println("CheckMap: " + checkMap);
					Clause resolvent = clause1.resolutionRule(clause2);
					
					if (resolvent == null)
						continue;
					
					//System.out.println("cl1: " + clause1);
					//System.out.println("cl2: " + clause2);
					//System.out.println("res: " + resolvent);
					
					ClauseSet newClauseSet = clauseSet.newSetWith(resolvent);
					if (newClauseSet == clauseSet)
						continue;
					
					checkMap.put(resolvent, new ClauseSet());
					for (Clause checkClause : newClauseSet) {
						if (checkClause == resolvent)
							continue;
						if ((checkMap.get(checkClause).contains(clause1) || checkMap.get(clause1).contains(checkClause))
							&& (checkMap.get(clause2).contains(checkClause) || checkMap.get(checkClause).contains(clause2))) {
								checkMap.get(checkClause).add(resolvent);
								checkMap.get(resolvent).add(checkClause);
						}
					}
					
					proofSteps.add(newClauseSet);
					clauseSet = newClauseSet;
					if (resolvent.isEmpty()) {
						result = true;
						break step;
					}
					else
						continue step;
				}
			}
			result = false;
			break step;
		}
		return new Tuple<>(proofSteps,result);
	}
	
	private static Map<Clause,ClauseSet> buildCheckMap(ClauseSet clauseSet) {
		Map<Clause,ClauseSet> out = new HashMap<>();
		for (Clause clause : clauseSet) {
			out.put(clause, new ClauseSet());
		}
		return out;
	}
	
	public static Tuple<Tree<ClauseSet>,Boolean> SLDformGCNFproof(String s) {
		return SLDformGCNFproof(infixReader(s));
	}
	
	public static Tuple<Tree<ClauseSet>, Boolean> SLDformGCNFproof(Tree<String> f) {
		return SLDformGCNFproof(formulaToClauseSet(negation(f)).simplify());
	}
	
	public static Tuple<Tree<ClauseSet>, Boolean> SLDformGCNFproof(ClauseSet inputClauseSet) {
		System.out.println("init ClauseSet: " + inputClauseSet);
		if (!inputClauseSet.isSLDWorkable()) {
			System.out.println("fail because not SLD workable");
			return null;
		}
		
		Tree<ClauseSet> deductionTree = new Tree<>(inputClauseSet);
		
		boolean change = false;
		
		step: while (true) {
			System.out.println(deductionTree);
			//check if all leaves are axioms, exit loop if so
			boolean proofComplete = true;
			for (ClauseSet endClauseSet : deductionTree.getEndHeads()) {
				if (!endClauseSet.isAxiom()) {
					proofComplete = false;
					break;
				}
			}
			if (proofComplete) {
				break step;
			}
			
			for (Tree<ClauseSet> endLeaf : deductionTree.getEnds()) {
				if (!endLeaf.getHead().isAxiom()) {
					for (Clause goalClause : endLeaf.getHead()) {
						if (!goalClause.isNegativeClause())
							continue;
						if (goalClause.isSingleton()) {
							Literal goalLit = goalClause.singleton();
							Clause defClause = null;
							Literal comp = null;
							defClause: for (Clause potentialDef : endLeaf.getHead()) {
								if (potentialDef == goalClause)
									continue;
								for (Literal l : potentialDef) {
									if (l.second) {
										if (l.complements(goalLit)) {
											defClause = potentialDef;
											comp = l;
											break defClause;
										}
										continue defClause;
									}
								}
							}
							if (defClause == null) {
								//failure because no definite clause can match the singleton negative literal
								System.out.println("failure because no definite clause can match the singleton negative literal "+ goalLit);
								System.out.println(deductionTree);
								return new Tuple<>(deductionTree, false);
							}
							
							
							ClauseSet newBranch1 = new ClauseSet();
							newBranch1.add(new Clause(Collections.singleton(comp)));
							newBranch1.add(new Clause(goalClause));
							
							ClauseSet newBranch2 = new ClauseSet(endLeaf.getHead());
							newBranch2.remove(goalClause);
							newBranch2.remove(defClause);
							Clause addClause = new Clause(defClause);
							addClause.remove(comp);
							newBranch2.add(addClause);
							
							endLeaf.addLeaf(newBranch1);
							endLeaf.addLeaf(newBranch2);
							
							change = true;
						}
						else {
							for (Literal l : goalClause) {
								ClauseSet newBranch = new ClauseSet(endLeaf.getHead());
								newBranch.remove(goalClause);
								newBranch.add(new Clause(Collections.singleton(l)));
								endLeaf.addLeaf(newBranch);
							}
							change = true;
						}
					}
					if (change)
						continue;
					//no goal clause exists, therefore proof fails
					System.out.println("failure because no goal clauses exist");
					return new Tuple<>(deductionTree, false);
				}
			}
			
			
			
		}
		
		return new Tuple<>(deductionTree,true);
		
		
	}
	
	public static Tuple<List<ClauseSet>,Boolean> resolution(String s) {
		return resolution(infixReader(s));
	}
	
	public static Tuple<Tree<Sequent>,Boolean> sequentProof(Tree<String> f) {
		return search(new Sequent(new ArrayList<>(), Collections.singletonList(f)));
	}
	
	
}
