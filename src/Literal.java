public class Literal extends Tuple<String,Boolean> {

	public Literal(String x, Boolean y) {
		super(x, y);
	}
	
	public String toString() {
		String out = "";
		if (!second)
			out += "~";
		out += first;
		return out;
	}
	
	public boolean complements(Literal other) {
		if (!this.first.equals(other.first))
			return false;
		return (this.second.compareTo(other.second) != 0);
	}
	
	public boolean equals(Literal l) {
		return this.first.equals(l.first) && this.second.equals(l.second);
	}

	
	
}
