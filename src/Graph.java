import static java.util.Arrays.*;

import java.util.*;

import tc.wata.data.*;
import tc.wata.debug.*;
import tc.wata.util.*;

public class Graph {
	
	public int n;
	public int[][] adj;
	public char[] used; // 0: not decided, 'S': in FVS, 'F': in Forest
	public int k; // |S|
	public int[] id;
	
	public Graph(int[][] adj) {
		n = adj.length;
		this.adj = adj.clone();
		used = new char[n];
		k = 0;
	}
	
	public Graph(Graph g) {
		n = g.n;
		adj = g.adj.clone();
		used = g.used.clone();
		k = g.k;
	}
	
	/**
	 * add an edge uv.
	 * if uv is already a double edge, do nothing
	 */
	void addE(int u, int v) {
		if (_addE(u, v)) _addE(v, u);
	}
	
	boolean _addE(int u, int v) {
		int i = Utils.upperBound(adj[u], v);
		if (i >= 2 && adj[u][i - 2] == v) return false;
		int[] a = new int[adj[u].length + 1];
		System.arraycopy(adj[u], 0, a, 0, i);
		a[i] = v;
		System.arraycopy(adj[u], i, a, i + 1, adj[u].length - i);
		adj[u] = a;
		return true;
	}
	
	public void removeV(int v) {
		for (int u : adj[v]) if (u != v) _removeE(u, v);
		adj[v] = new int[0];
	}
	
	void removeE(int u, int v) {
		_removeE(u, v);
		_removeE(v, u);
	}
	
	void _removeE(int u, int v) {
		int i = binarySearch(adj[u], v);
		int[] a = new int[adj[u].length - 1];
		System.arraycopy(adj[u], 0, a, 0, i);
		System.arraycopy(adj[u], i + 1, a, i, a.length - i);
		adj[u] = a;
	}
	
	/**
	 * add v to S and remove v from the graph
	 */
	public void setS(int v) {
		Debug.check(used[v] == 0);
		removeV(v);
		used[v] = 'S';
		k++;
	}
	
	/**
	 *  add v to F and add N_2(v) to S
	 */
	public void setF(int v) {
		Debug.check(used[v] == 0);
		used[v] = 'F';
		int[] N2 = new int[adj[v].length];
		int p = N2(v, N2);
		for (int i = 0; i < p; i++) setS(N2[i]);
	}
	
	/**
	 * add v to F, add N_2(v) to S, and make N_1(v) a clique
	 */
	public void eliminate(int v) {
		setF(v);
		int[] N = adj[v];
		removeV(v);
		for (int i = 0; i < N.length; i++) {
			for (int j = 0; j < i; j++) addE(N[i], N[j]);
		}
	}
	
	/**
	 * set u to F, contract u to v, and add N_2(v) to S
	 */
	public void contract(int u, int v) {
		Debug.check(used[v] == 'F');
		used[u] = 'F';
		int[] N = adj[u];
		removeV(u);
		for (int w : N) if (w != v) {
			addE(v, w);
		}
		int[] N2 = new int[adj[v].length];
		int p = N2(v, N2);
		for (int i = 0; i < p; i++) setS(N2[i]);
	}
	
	/**
	 * Returns connected components in increasing order of size.
	 * If there is only one component, returns null.
	 */
	public Graph[] decompose(boolean one) {
		int N = n();
		boolean[] visit = new boolean[n];
		int[] id = new int[n];
		int[] que = new int[n];
		ArrayList<Graph> gs = new ArrayList<>();
		for (int i = 0; i < n; i++) if (adj[i].length > 0 && !visit[i]) {
			int qs = 0, qt = 0;
			visit[i] = true;
			que[qt++] = i;
			while (qs < qt) {
				int v = que[qs++];
				for (int u : adj[v]) if (!visit[u]) {
					visit[u] = true;
					que[qt++] = u;
				}
			}
			if (qt == N && !one) return null;
			sort(que, 0, qt);
			for (int j = 0; j < qt; j++) id[que[j]] = j;
			int[][] adj2 = new int[qt][];
			for (int j = 0; j < qt; j++) {
				int v = que[j];
				adj2[j] = new int[adj[v].length];
				for (int k = 0; k < adj[v].length; k++) adj2[j][k] = id[adj[v][k]];
			}
			Graph g = new Graph(adj2);
			g.id = copyOf(que, qt);
			for (int j = 0; j < qt; j++) g.used[j] = used[que[j]];
			gs.add(g);
		}
		Graph[] res = gs.toArray(new Graph[0]);
		sort(res, new Comparator<Graph>() {
			@Override
			public int compare(Graph o1, Graph o2) {
				return o1.n - o2.n;
			}
		});
		return res;
	}
	
	public int hasEdge(int u, int v) {
		int i = Utils.lowerBound(adj[u], v);
		if (i >= adj[u].length || adj[u][i] != v) return 0;
		i++;
		if (i >= adj[u].length || adj[u][i] != v) return 1;
		return 2;
	}
	
	public int N(int v, int[] list) {
		int p = 0;
		for (int i = 0; i < adj[v].length; i++) {
			list[p++] = adj[v][i];
			if (i + 1 < adj[v].length && adj[v][i + 1] == adj[v][i]) i++;
		}
		return p;
	}
	
	public int N1(int v, int[] list) {
		int p = 0;
		for (int i = 0; i < adj[v].length; i++) {
			if (i + 1 < adj[v].length && adj[v][i + 1] == adj[v][i]) i++;
			else list[p++] = adj[v][i];
		}
		return p;
	}
	
	public int N2(int v, int[] list) {
		int p = 0;
		for (int i = 0; i < adj[v].length; i++) {
			if (i + 1 < adj[v].length && adj[v][i + 1] == adj[v][i]) list[p++] = adj[v][i++];
		}
		return p;
	}
	
	public int n() {
		int n = 0;
		for (int i = 0; i < adj.length; i++) if (adj[i].length > 0) n++;
		return n;
	}
	
	public int m() {
		int m = 0;
		for (int i = 0; i < n; i++) m += adj[i].length;
		return m / 2;
	}
	
	public int maxDeg() {
		int max = 0;
		for (int i = 0; i < n; i++) max = Math.max(max, adj[i].length);
		return max;
	}
	
	public int[] getS() {
		int[] S = new int[k];
		int p = 0;
		for (int i = 0; i < n; i++) if (used[i] == 'S') S[p++] = i;
		Debug.check(p == k);
		return S;
	}
	
	public int[][] simplified() {
		int[][] g = new int[n][];
		int[] tmp = new int[n];
		for (int i = 0; i < n; i++) {
			int d = N(i, tmp);
			g[i] = copyOf(tmp, d);
		}
		return g;
	}
	
	public void test() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < adj[i].length - 1; j++) Debug.check(adj[i][j] <= adj[i][j + 1]);
			for (int j : adj[i]) {
				Debug.check(j != i);
				int d = hasEdge(i, j);
				Debug.check(d <= 2);
				Debug.check(d == hasEdge(j, i));
				Debug.check(used[i] != 'F' || d < 2);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int u = 0; u < n; u++) {
			for (int v : adj[u]) {
				if (u <= v) {
					sb.append(u).append(' ').append(v).append('\n');
				}
			}
		}
		return sb.toString();
	}
	
}
