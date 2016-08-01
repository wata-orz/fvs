import static java.util.Arrays.*;

public class DFS {
	
	/** DFSの親 (根の親は-1で非連結の場合は根が複数) */
	public int[] parent;
	/** 行きがけ順 */
	public int[] preOrder;
	
	public DFS(int[][] adj) {
		int n = adj.length;
		parent = new int[n];
		preOrder = new int[n];
		int[] iter = new int[n];
		int[] stack = new int[n];
		fill(parent, -2);
		int st = 0, p = 0;
		for (int r = 0; r < n; r++) if (parent[r] < 0) {
			parent[r] = -1;
			stack[st++] = r;
			preOrder[p++] = r;
			while (st > 0) {
				int v = stack[--st], u = -1;
				while (iter[v] < adj[v].length) {
					u = adj[v][iter[v]++];
					if (iter[v] < adj[v].length && adj[v][iter[v]] == u) { // skip double edges
						iter[v]++;
						continue;
					}
					if (parent[u] == -2) {
						parent[u] = v;
						stack[st++] = v;
						stack[st++] = u;
						preOrder[p++] = u;
						break;
					}
				}
			}
		}
	}
	
}
