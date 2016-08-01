import java.io.*;

import tc.wata.debug.*;
import tc.wata.util.*;
import tc.wata.util.SetOpt.*;

public class Main {
	
	public static Main main;
	
	@Option(abbr = 'a')
	public String algo = "FPTBranchingSolver";
	
	@Option(abbr = 's')
	public long seed = 467328;
	
	@Option(abbr = 'l', usage = "0: without reduction/lowerbound, 1: basic reduction, 2: ksub, 3: heuristic")
	public int LEVEL = 3;
	
	@Option(abbr = 'o')
	public boolean output = true;
	
	void run() throws Exception {
		GraphIO io = new GraphIO();
		io.read(System.in);
		Solver solver = (Solver)Class.forName(algo).newInstance();
		solver.outputUB = true;
		long time = -System.currentTimeMillis();
		solver.solve(new Graph(io.adj));
		time += System.currentTimeMillis();
		System.err.printf("time = %.3f%n", time * 1e-3);
		if (solver.ub < Integer.MAX_VALUE) {
			Test.test(io.adj, solver.res);
			System.err.printf("opt = %d%n", solver.ub);
			if (output) {
				for (int i : solver.res) System.out.println(io.name[i]);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Stat.setShutdownHook();
		main = new Main();
		SetOpt.setOpt(main, args);
		ReductionRoot.LEVEL = main.LEVEL;
		Reduction.LEVEL = main.LEVEL;
		LowerBound.LEVEL = main.LEVEL;
		try {
			main.run();
		} catch (Throwable e) {
			System.err.println("Error = " + e.toString());
			e.printStackTrace();
		}
	}
	
}
