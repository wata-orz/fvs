import static java.util.Arrays.*;

import tc.wata.debug.*;

public class LowerBound {
	
	public static int LEVEL = 3;
	
	public static int lowerBound(Graph g) {
		int lb = g.k;
		if (LEVEL == 2) lb = degree(g);
		else if (LEVEL == 3) lb = degreeEx(g);
		return lb;
	}
	
	public static int degree(Graph g) {
		int[] deg = new int[g.n];
		int n = 0, p = 0, m = 0;
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0) {
			n++;
			m += g.adj[i].length;
			if (g.used[i] != 'F') deg[p++] = g.adj[i].length;
		}
		m /= 2;
		if (n == 0 || m < n) return g.k;
		sort(deg, 0, p);
		int sum = 0;
		for (int k = 1; ; k++) {
			sum += deg[p - k];
			if (sum - k > m - n) return k + g.k;
		}
	}
	
	public static int degreeEx(Graph g) {
		int k = 0, sum = 0;
		int n = 0, p = 0, m = 0;
		int[] deg = new int[g.n];
		boolean[] adjF = new boolean[g.n];
		for (int s = 0; s < g.n; s++) if (g.adj[s].length > 0 && g.used[s] == 'F') {
			for (int v : g.adj[s]) adjF[v] = true;
		}
		long[] ls = new long[g.n];
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0) {
			m += g.adj[i].length;
			ls[n++] = ((long)g.adj[i].length) << 32 | i;
		}
		sort(ls, 0, n);
		int[] num = new int[g.n];
		boolean[] match = new boolean[g.n], used = new boolean[g.n];
		for (int i = 0; i < n; i++) {
			int v = (int)ls[i];
			used[v] = true;
			if (g.used[v] != 'F') {
				int t = -1;
				for (int j = 0; j < g.adj[v].length; j++) if (!match[g.adj[v][j]] && g.used[g.adj[v][j]] == 0) {
					int u = g.adj[v][j];
					if (j + 1 < g.adj[v].length && g.adj[v][j + 1] == u || adjF[v] && adjF[u]) {
						if (!used[u]) {
							num[v]++;
						} else {
							num[u]--;
							if (t < 0 || num[t] > num[u]) t = u;
						}
						if (j + 1 < g.adj[v].length && g.adj[v][j + 1] == u) j++;
					}
				}
				if (t >= 0) {
					match[v] = match[t] = true;
					k++;
					sum += g.adj[v].length;
				} else {
					deg[p++] = g.adj[v].length;
				}
			}
		}
		m /= 2;
		if (n == 0 || m < n) return g.k;
		for (int k0 = 0; ; ) {
			if (sum - k > m - n) {
				return k + g.k;
			}
			k++;
			k0++;
			sum += deg[p - k0];
		}
	}
	
}
