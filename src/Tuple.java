public class Tuple<X, Y> { 
	public final X first; 
	public final Y second; 
	public Tuple(X x, Y y) { 
		this.first = x; 
		this.second = y; 
	} 
	
	public boolean piecewiseEquals(Tuple<X,Y> t) {
		return (this.first.equals(t.first) && this.second.equals(t.second));
	}
	
	public String toString() {
		return "( " + first + ", " + second + " )";
	}
} 

