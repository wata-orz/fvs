import static java.util.Arrays.*;

import java.util.*;

import tc.wata.data.*;
import tc.wata.debug.*;
import tc.wata.util.*;

public class ReductionRoot {
	
	public static int LEVEL = 3;
	public static int D = 8;
	public static boolean DEBUG = true;
	
	/**
	 * F must be empty
	 */
	public static void reduce(Graph g) {
		for (;;) {
			selfLoop(g);
			if (LEVEL >= 1) {
				deg2(g);
			}
			if (LEVEL >= 3) {
				if (sameNeighbor(g)) continue;
				if (deg2Ex(g)) continue;
				if (dominate(g)) continue;
				if (bridge(g)) continue;
			}
			if (LEVEL >= 2) {
				if (ksub(g)) continue;
			}
			break;
		}
		if (DEBUG) System.err.println("reduction finished");
	}
	
	/**
	 * vv in E => v in S
	 */
	static boolean selfLoop(Graph g) {
		int oldN = g.n();
		for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			if (binarySearch(g.adj[v], v) >= 0) {
				g.setS(v);
			}
		}
		int newN = g.n();
		if (DEBUG && oldN != newN) System.err.printf("selfLoop: %d -> %d%n", oldN, newN);
		return oldN != newN;
	}
	
	/**
	 * d(v) <= 2 => v in F
	 */
	static boolean deg2(Graph g) {
		int oldN = g.n();
		for (;;) {
			boolean ok = true;
			for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
				if (g.adj[v].length <= 2) {
					g.eliminate(v);
					ok = false;					
				}
			}
			if (ok) break;
		}
		int newN = g.n();
		if (DEBUG && oldN != newN) System.err.printf("deg2: %d -> %d%n", oldN, newN);
		return oldN != newN;
	}
	
	/**
	 * N(X) = N(v) for all v in X and |X| >= |N(X)| => X in F
	 */
	static boolean sameNeighbor(Graph g) {
		int oldN = g.n();
		TreeMap<int[], IntArray> map = new TreeMap<>(new Utils.CompArray());
		for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			IntArray a = map.get(g.adj[v]);
			if (a == null) map.put(g.adj[v], a = new IntArray());
			a.add(v);
		}
		boolean[] ng = new boolean[g.n];
		int[] tmp = new int[g.n];
		for (IntArray a : map.values()) {
			if (!ng[a.at[0]] && g.N(a.at[0], tmp) <= a.length) {
				for (int u : g.adj[a.at[0]]) ng[u] = true;
				for (int i = 0; i < a.length; i++) {
					g.eliminate(a.at[i]);
				}
			}
		}
		int newN = g.n();
		if (DEBUG && oldN != newN) System.err.printf("sameN: %d -> %d%n", oldN, newN);
		return oldN != newN;
	}
	
	static boolean deg2Ex(Graph g) {
		int oldN = g.n();
		int[] N = new int[g.n];
		int[][] h = new int[D][D];
		boolean[] inN2 = new boolean[D];
		boolean[] isForest = new boolean[1 << D];
		loop : for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			int d = g.N(v, N);
			if (d > D) continue;
			for (int i = 0, p = 0; p < d; i++, p++) {
				if (i + 1 < g.adj[v].length && g.adj[v][i] == g.adj[v][i + 1]) {
					inN2[p] = true;
					i++;
				} else {
					inN2[p] = false;
				}
			}
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < i; j++) h[i][j] = h[j][i] = g.hasEdge(N[i], N[j]);
			}
			for (int F = 0; F < 1 << d; F++) {
				int size = Integer.bitCount(F);
				if (size <= 1) isForest[F] = true;
				else {
					isForest[F] = false;
					for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && isForest[F & ~(1 << i)]) {
						int count = 0;
						for (int j = 0; j < d; j++) if ((F >> j & 1) != 0) count += h[i][j];
						if (count <= 1) {
							isForest[F] = true;
							break;
						}
					}
				}
				if (isForest[F]) {
					if (size == 3) {
						boolean isIndependent = true;
						for (int i = 0; i < d; i++) if ((F >> i & 1) != 0) {
							for (int j = 0; j < i; j++) if ((F >> j & 1) != 0) {
								if (h[i][j] > 0) isIndependent = false;
							}
						}
						if (isIndependent) continue loop;
					} else if (size >= 4) continue loop;
					int w = -1;
					for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && inN2[i]) {
						if (w >= 0) continue loop;
						w = i;
					}
					if (w >= 0 && size >= 3) {
						for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && i != w) {
							if (h[w][i] == 0) continue loop;
						}
					}
				}
			}
			g.eliminate(v);
		}
		int newN = g.n();
		if (DEBUG && oldN != newN) System.err.printf("dex2Ex: %d -> %d%n", oldN, newN);
		return oldN != newN;
	}
	
	static boolean ksub(Graph g) {
		int oldN = g.n();
		int[] que = new int[g.n];
		FastSet used = new FastSet(g.n);
		boolean reduced = false;
		for (int s = 0; s < g.n; s++) if (g.adj[s].length > 0) {
			double[] x = new HalfIntegralRelax().solve(g, s);
			for (int v = 0; v < g.n; v++) if (x[v] == 1) {
				for (int c = g.hasEdge(s, v); c < 2; c++) {
					g.addE(s, v);
					reduced = true;
				}
			}
			used.clear();
			for (int t : g.adj[s]) if (used.add(t) && x[t] < 1) {
				boolean bridge = true;
				int qs = 0, qt = 0;
				que[qt++] = t;
				while (qs < qt) {
					int u = que[qs++];
					for (int v : g.adj[u]) if (x[v] < 1) {
						if (v == s) {
							if (u != t) bridge = false;
						} else if (used.add(v)) {
							que[qt++] = v;
						}
					}
				}
				if (bridge) {
					g.removeE(s, t);
					reduced = true;
				}
			}
		}
		int newN = g.n();
		if (DEBUG && reduced) System.err.printf("ksub: %d -> %d%n", oldN, newN);
		return reduced;
	}
	
	static boolean dominate(Graph g) {
		int oldN = g.n();
		for (int v = 0; v < g.n; v++) {
			loop : for (int i = 0; i + 1 < g.adj[v].length; i++) if (g.adj[v][i] == g.adj[v][i + 1]) {
				int u = g.adj[v][i++];
				if (g.adj[v].length >= g.adj[u].length) {
					for (int j = 0, p = 0; j < g.adj[u].length; j++) if (g.adj[u][j] != v) {
						while (p < g.adj[v].length && (g.adj[u][j] > g.adj[v][p] || g.adj[v][p] == u)) p++;
						if (p >= g.adj[v].length || g.adj[u][j] < g.adj[v][p]) continue loop;
						p++;
					}
					g.setS(v);
					break;
				}
			}
		}
		int newN = g.n();
		if (DEBUG && oldN != newN) System.err.printf("dominate: %d -> %d%n", oldN, newN);
		return oldN != newN;
	}
	
	static boolean bridge(Graph g) {
		int oldN = g.n();
		boolean reduced = false;
		Biconnected bi = new Biconnected(g.adj);
		IntArray tmp = new IntArray();
		for (int v = 0; v < g.n; v++) if (g.adj[v].length > 0) {
			tmp.length = 0;
			for (int i = 0; i < g.adj[v].length; i++) {
				int u = g.adj[v][i];
				if (i + 1 < g.adj[v].length && g.adj[v][i + 1] == u) {
					i++;
					continue;
				}
				if (bi.bi[v] != bi.bi[u]) {
					reduced = true;
					tmp.add(u);
				}
			}
			for (int i = 0; i < tmp.length; i++) g.removeE(v, tmp.at[i]);
		}
		int[] N1 = new int[g.n];
		for (int v = 0; v < g.n; v++) if (g.adj[v].length > 0) {
			int d = g.N1(v, N1);
			tmp.length = 0;
			loop : for (int i = 0; i < d; i++) {
				for (int j = 0; j < d; j++) if (i != j) {
					if (g.hasEdge(N1[i], N1[j]) < 2) continue loop;
				}
				tmp.add(N1[i]);
				reduced = true;
			}
			for (int i = 0; i < tmp.length; i++) g.removeE(v, tmp.at[i]);
		}
		int newN = g.n();
		if (DEBUG && reduced) System.err.printf("bridge: %d -> %d%n", oldN, newN);
		return reduced;
	}
	
}
