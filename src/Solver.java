import tc.wata.data.*;
import tc.wata.debug.*;

public abstract class Solver {
	
	public int ub = Integer.MAX_VALUE;
	public int[] res;
	public boolean outputUB = false;
	public int add = 0;
	
	public abstract void solve(Graph g);
	
	void update(int[] res) {
		ub = res.length;
		this.res = res;
		if (outputUB) System.err.printf("ub = %d%n", add + ub);
	}
	
}
