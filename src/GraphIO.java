import static java.util.Arrays.*;

import java.io.*;
import java.util.*;

import tc.wata.data.*;
import tc.wata.io.*;

public class GraphIO {
	
	public int n;
	public int[][] adj;
	public String[] name;
	
	public void read(InputStream in) {
		Map<String, Integer> ids = new TreeMap<>();
		ArrayList<String> names = new ArrayList<>();
		ArrayList<int[]> es = new ArrayList<int[]>();
		FastScanner sc = new FastScanner(in);
		while (sc.hasNext()) {
			String u = sc.next();
			if (u.startsWith("#")) {
				sc.nextLine();
				continue;
			}
			String v = sc.next();
			es.add(new int[]{getID(ids, names, u), getID(ids, names, v)});
		}
		n = names.size();
		name = names.toArray(new String[0]);
		IntArray[] g = new IntArray[n];
		for (int i = 0; i < n; i++) g[i] = new IntArray();
		for (int[] e : es) {
			int u = e[0], v = e[1];
			g[u].add(v);
			g[v].add(u);
		}
		adj = new int[n][];
		for (int i = 0; i < n; i++) {
			adj[i] = g[i].toArray();
			sort(adj[i]);
		}
	}
	
	int getID(Map<String, Integer> ids, ArrayList<String> names, String s) {
		Integer id = ids.get(s);
		if (id == null) {
			id = names.size();
			ids.put(s, id);
			names.add(s);
		}
		return id;
	}
	
	public void write(PrintStream out) {
		FastPrintWriter pw = new FastPrintWriter(out);
		for (int i = 0; i < n; i++) {
			for (int e = 0; e < adj[i].length; e++) {
				int j = adj[i][e];
				if (i <= j) {
					if (e > 0 && adj[i][e - 1] == j) {
						pw.print(name[j]);
						pw.print(' ');
						pw.println(name[i]);
					} else {
						pw.print(name[i]);
						pw.print(' ');
						pw.println(name[j]);
					}
				}
			}
		}
		pw.flush();
	}
	
}
