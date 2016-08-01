import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.*;

public class Biconnected {
	
	/** 二重連結成分木のID->親ID (根の親は-1で非連結の場合は根が複数) */
	public int[] parent;
	/** 頂点の属する二重連結成分のID */
	public int[] bi;
	
	public Biconnected(int[][] adj) {
		int n = adj.length;
		DFS dfs = new DFS(adj);
		int[] depth = new int[n];
		for (int i = 0; i < n; i++) {
			int v = dfs.preOrder[i];
			if (dfs.parent[v] >= 0) depth[v] = depth[dfs.parent[v]] + 1;
		}
		int[] low = depth.clone();
		for (int i = n - 1; i >= 0; i--) {
			int v = dfs.preOrder[i];
			for (int j = 0; j < adj[v].length; j++) {
				int u = adj[v][j];
				if (j + 1 < adj[v].length && adj[v][j + 1] == u) { // skip double edges
					j++;
					continue;
				}
				if (u != dfs.parent[v]) {
					low[v] = min(low[v], low[u]);
				}
			}
		}
		parent = new int[n];
		fill(parent, -2);
		bi = new int[n];
		for (int i = 0; i < n; i++) {
			int v = dfs.preOrder[i];
			if (depth[v] == low[v]) {
				bi[v] = v;
				if (dfs.parent[v] < 0) parent[v] = -1;
				else parent[v] = bi[dfs.parent[v]];
			} else {
				bi[v] = bi[dfs.parent[v]];
			}
		}
	}
	
}
