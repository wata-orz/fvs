import tc.wata.data.*;
import tc.wata.debug.*;

public class Test {
	
	public static void test(int[][] adj, int[] FVS) {
		int n = adj.length;
		IntArray stack = new IntArray();
		boolean[] bs = new boolean[n];
		for (int i : FVS) {
			Debug.check(!bs[i]);
			bs[i] = true;
		}
		int N = 0, M = 0, C = 0;
		for (int i = 0; i < n; i++) if (!bs[i]) {
			N++;
			for (int j : adj[i]) {
				Debug.check(i != j);
				if (!bs[j]) M++;
			}
		}
		M /= 2;
		for (int i = 0; i < n; i++) if (!bs[i]) {
			bs[i] = true;
			C++;
			stack.add(i);
			while (stack.length > 0) {
				int v = stack.popLast();
				for (int u : adj[v]) if (!bs[u]) {
					bs[u] = true;
					stack.add(u);
				}
			}
		}
		Debug.check(M + C == N);
	}
	
}
