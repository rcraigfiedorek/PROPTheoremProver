# PROPTheoremProver
A Theorem Prover for Propositional Logic


Compile and run Main.java to run the prover. The prover will prompt you to enter a formula of propositional logic. Enter formulas according to the following BNF:

	<atomic_formula> ::= "A" | "B" | ... | "Z"
	       <formula> ::= <atomic_formula> | "~" <formula> | <formula> "&&" <formula> |
	                     <formula> "||" <formula> | <formula> "->" <formula> |
	                     <formula> "<->" <formula> | "(" <formula> ")"

All whitespace in the input will be ignored. Order of operations is <code>~</code>, <code>&&</code>, <code>||</code>, <code>-></code>, <code><-></code>. Upon inputting a formula, the prover will use the sequent calculus for propositional logic to either provide a proof of the formula or determine that no proof exists.
	
Most work for this project was completed between January 2016 - April 2016.
