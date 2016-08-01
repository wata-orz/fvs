import static java.lang.Math.*;
import static java.util.Arrays.*;

import tc.wata.data.*;
import tc.wata.debug.*;

public class Reduction {
	
	public static int LEVEL = 3;
	public static int D = 6;
	
	public static void reduce(Graph g, int ub) {
		for (;;) {
			if (LEVEL >= 1) deg2(g);
			if (LEVEL >= 3) {
				if (deg2Ex(g)) continue;
				if (degLB(g, ub)) continue;
				if (dominate(g)) continue;
				if (bridge(g)) continue;
			}
			break;
		}
	}
	
	/**
	 * d(v) <= 2 => v in F
	 */
	static boolean deg2(Graph g) {
		int oldN = g.n();
		int[] N2 = new int[g.n];
		for (;;) {
			boolean ok = true;
			loop : for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
				int p = g.N2(v, N2);
				for (int i = 0; i < p; i++) if (g.used[N2[i]] == 'F') {
					g.setS(v);
					ok = false;
					continue loop;
				}
				if (g.adj[v].length <= 2) {
					g.eliminate(v);
					ok = false;					
				}
			}
			if (ok) break;
		}
		int newN = g.n();
		return oldN != newN;
	}
	
	static boolean deg2Ex(Graph g) {
		int oldN = g.n();
		int[] N = new int[g.n];
		int[][] h = new int[D][D];
		boolean[] inN2 = new boolean[D];
		boolean[] checked = new boolean[1 << D];
		int[] que = new int[1 << D];
		boolean[] adjS = new boolean[g.n];
		for (int i = 0; i < g.n; i++) if (g.used[i] == 'F' && g.adj[i].length > 0) {
			for (int v : g.adj[i]) adjS[v] = true;
		}
		int qs = 0, qt = 0;
		loop : for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			for (int i = qs; i < qt; i++) checked[que[i]] = false;
			qs = qt = 0;
			int d = g.N(v, N);
			if (d > D) continue;
			int s = -1;
			for (int i = 0, p = 0; p < d; i++, p++) {
				if (i + 1 < g.adj[v].length && g.adj[v][i] == g.adj[v][i + 1]) {
					if (g.used[g.adj[v][i]] == 'F') {
						g.setS(v);
						continue loop;
					}
					inN2[p] = true;
					i++;
				} else {
					if (g.used[g.adj[v][i]] == 'F') s = p;
					inN2[p] = false;
				}
			}
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < i; j++) h[i][j] = h[j][i] = g.hasEdge(N[i], N[j]);
			}
			if (s >= 0) {
				que[qt++] = 1 << s;
			} else {
				for (int i = 0; i < d; i++) que[qt++] = 1 << i;
			}
			while (qs < qt) {
				int F = que[qs++];
				int size = Integer.bitCount(F);
				Debug.check(size <= 3);
				checked[F] = false;
				boolean independent = false;
				if (size == 2) {
					loop2 : for (int i = 0; i < d; i++) if ((F >> i & 1) != 0) {
						for (int j = i + 1; j < d; j++) if ((F >> j & 1) != 0) {
							if (h[i][j] == 0) independent = true;
							break loop2;
						}
					}
				}
				loop2 : for (int i = 0; i < d; i++) if ((F >> i & 1) == 0 && !checked[F | (1 << i)]) {
					int count = 0;
					for (int j = 0; j < d && count <= 1; j++) if ((F >> j & 1) != 0 && h[i][j] > 0) {
						if (adjS[N[i]] && adjS[N[j]]) continue loop2;
						count += h[i][j];
					}
					if (count <= 1) {
						if (size + 1 >= 4 || independent && count == 0) continue loop;
						checked[F | 1 << i] = true;
						que[qt++] = F | 1 << i;
					}
				}
				int w = -1;
				for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && inN2[i]) {
					if (w >= 0) continue loop;
					w = i;
				}
				if (size == 3) {
					if (w >= 0) {
						boolean b = false;
						for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && i != w) {
							if (h[w][i] == 0) {
								if (b || s >= 0 || !adjS[N[w]] || !adjS[N[i]]) continue loop;
								b = true;
							}
						}
					} else if (s >= 0) {
						loop2 : for (int i = 0; i < d; i++) if ((F >> i & 1) != 0 && i != s) {
							for (int j = i + 1; j < d; j++) if ((F >> j & 1) != 0 && j != s) {
								if (h[i][j] == 0) continue loop;
								break loop2;
							}
						}
					}
				}
			}
			if (adjS[v]) {
				for (int u : g.adj[v]) if (g.used[u] == 0) adjS[u] = true;
			}
			g.eliminate(v);
		}
		int newN = g.n();
		return oldN != newN;
	}
	
	static boolean degLB(Graph g, int ub) {
		int oldN = g.n();
		int[] deg = new int[g.n];
		int n = 0, p = 0, m = 0;
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0) {
			n++;
			m += g.adj[i].length;
			if (g.used[i] != 'F') deg[p++] = g.adj[i].length;
		}
		m /= 2;
		if (p == 0 || g.k + 1 >= ub || g.k + p < ub) return false;
		sort(deg, 0, p);
		int sum = 0, k = 1;
		for ( ; k + g.k < ub; k++) {
			sum += deg[p - k];
		}
		boolean[] adjF = new boolean[g.n];
		int s = -1;
		for (int i = 0; i < g.n; i++) if (g.adj[i].length > 0 && g.used[i] == 'F') s = i;
		if (s >= 0) {
			for (int i : g.adj[s]) adjF[i] = true;
		}
		IntArray a = new IntArray(), b = new IntArray();
		for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			int sum2 = sum + min(0, deg[p - k] - g.adj[v].length);
			for (int i = 0; i < g.adj[v].length; i++) {
				int u = g.adj[v][i];
				if (i + 1 < g.adj[v].length && u == g.adj[v][i + 1]) {
					i++;
					sum2 += min(0, g.adj[u].length - deg[p - k]);
				} else if (adjF[v] && adjF[u]) {
					sum2 += min(0, g.adj[u].length - deg[p - k]);
				}
			}
			if (sum2 - (ub - g.k - 1) <= m - n) {
				a.add(v);
			}
		}
		for (int i = 0; i < g.n; i++) if (adjF[i] && g.adj[i].length < deg[p - k + 1] && g.used[i] != 'F') {
			if (sum + g.adj[i].length - deg[p - k + 1] - (ub - g.k - 1) <= m - n) {
				b.add(i);
			}
		}
		for (int i = 0; i < a.length; i++) g.setS(a.at[i]);
		for (int i = 0; i < b.length; i++) if (g.used[b.at[i]] == 0) g.contract(b.at[i], s);
		int newN = g.n();
		return oldN != newN;
	}
	
	static boolean dominate(Graph g) {
		int oldN = g.n();
		boolean[] adjS = new boolean[g.n];
		for (int s = 0; s < g.n; s++) if (g.used[s] == 'F' && g.adj[s].length > 0) {
			for (int v : g.adj[s]) adjS[v] = true;
		}
		for (int v = 0; v < g.n; v++) if (g.used[v] == 0) {
			loop : for (int i = 0; i < g.adj[v].length; i++) {
				int u = g.adj[v][i];
				if (i + 1 < g.adj[v].length && g.adj[v][i + 1] == u) i++;
				else if (!adjS[u] || !adjS[v]) continue;
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
		return oldN != newN;
	}
	
	static boolean bridge(Graph g) {
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
		return reduced;
	}
	
}
