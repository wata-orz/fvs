import static java.util.Arrays.*;

import tc.wata.data.*;
import tc.wata.debug.*;

public class HalfIntegralRelax {
	
	int s;
	int[][] adj;
	int[][] es;
	int[] f;
	int[] prevE;
	int[] prevV;
	char[] type;
	
	public double[] solve(Graph g, int s) {
		this.s = s;
		adj = g.adj;
		es = initE(adj);;
		f = new int[g.m()];
		prevE = new int[g.n];
		prevV = new int[g.n];
		type = new char[g.n];
		int[] que = new int[g.n];
		int[] list1 = new int[g.n * 2 + 1], list2 = new int[g.n * 2 + 1];
		FastSet used = new FastSet(f.length);
		loop : for (;;) {
			int qs = 0, qt = 0;
			que[qt++] = s;
			fill(prevE, -1);
			fill(type, '-');
			while (qs < qt) {
				int u = que[qs++];
				char typeU = type(u);
				for (int i = 0; i < adj[u].length; i++) {
					int v = adj[u][i], e = es[u][i];
					if (e == prevE[u]) continue;
					if ((u == s || v == s) && f[e] == 2) continue;
					if (typeU == 'I' && prevE[u] >= 0 && f[prevE[u]] == 0 && f[e] == 0) continue;
					char typeV = type(v);
					if (typeV == 'H' || typeV == 'T') {
						prevE[v] = e;
						prevV[v] = u;
						int L = computeWalk(v, list1);
						for (int j = 0; j < L; j++) f[list1[j]] = 2 - f[list1[j]];
						int prev = -1;
						int t = v;
						int F = 0;
						do {
							for (int j = 0; j < adj[v].length; j++) {
								e = es[v][j];
								if (e != prev && f[e] == 1) {
									int F2 = (adj[v][j] != t && type(adj[v][j]) == 'T') ? (2 - F) : F;
									f[e] = F;
									F = F2;
									v = adj[v][j];
									prev = e;
									break;
								}
							}
						} while (v != t);
						continue loop;
					} else if (prevE[v] < 0) {
						prevE[v] = e;
						prevV[v] = u;
						que[qt++] = v;
					} else if (typeV == 'O' || f[prevE[v]] + f[e] >= 2) {
						int L1 = computeWalk(u, list1);
						int L2 = computeWalk(v, list2);
						used.clear();
						int h = -1;
						for (int j = 0; j < L1; j++) used.add(list1[j]);
						for (int j = 0; j < L2; j++) if (used.get(list2[j])) {
							h = list2[j];
							break;
						}
						if (h < 0) {
							f[e] = 2 - f[e];
							for (int j = 0; j < L1; j++) f[list1[j]] = 2 - f[list1[j]];
							for (int j = 0; j < L2; j++) f[list2[j]] = 2 - f[list2[j]];
						} else {
							f[e] = 1;
							for (int j = 0; list1[j] != h; j++) f[list1[j]] = 1;
							int j = 0;
							for (; list2[j] != h; j++) f[list2[j]] = 1;
							for (; j < L2; j++) f[list2[j]] = 2 - f[list2[j]];
						}
						continue loop;
					}
				}
			}
			double[] x = new double[adj.length + 1];
			for (int i = 0; i < adj[s].length; i++) if (f[es[s][i]] == 2) {
				x[x.length - 1] += 0.5;
				int v = adj[s][i];
				if (prevE[v] < 0) {
					x[v] += 0.5;
				} else {
					int prev = es[s][i];
					while (f[prevE[v]] == 2) {
						for (int j = 0; j < adj[v].length; j++) {
							int e = es[v][j];
							if (e != prev && f[e] == 2) {
								v = adj[v][j];
								prev = e;
								break;
							}
						}
					}
					x[v] = 1;
				}
			}
			return x;
		}
	}
	
	int[][] initE(int[][] adj) {
		int n = adj.length;
		int[][] es = new int[n][];
		int p = 0;
		int[] ps = new int[n];
		for (int i = 0; i < n; i++) es[i] = new int[adj[i].length];
		for (int i = 0; i < n; i++) {
			while (ps[i] < adj[i].length) {
				int j = adj[i][ps[i]];
				es[i][ps[i]++] = p;
				es[j][ps[j]++] = p;
				p++;
			}
		}
		return es;
	}
	
	int computeWalk(int v, int[] list) {
		int i = 0;
		while (prevE[v] >= 0) {
			list[i++] = prevE[v];
			v = prevV[v];
		}
		return i;
	}
	
	char type(int v) {
		if (type[v] != '-') return type[v];
		if (v == s) return type[v] = 'S';
		int n1 = 0, n2 = 0;
		for (int e : es[v]) {
			if (f[e] == 1) n1++;
			else if (f[e] == 2) n2++;
		}
		if (n1 == 0 && n2 == 0) return type[v] = 'O';
		if (n1 == 0 && n2 == 2) return type[v] = 'I';
		if (n1 == 2 && n2 == 0) return type[v] = 'H';
		if (n1 == 2 && n2 == 1) return type[v] = 'T';
		throw null;
	}
	
}
