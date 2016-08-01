package tc.wata.data;

import static java.lang.Math.*;
import static java.util.Arrays.*;

import java.util.*;

import tc.wata.debug.*;
import tc.wata.util.*;

/**
 * ArrayList&lt;Integer&gt;の高速版
 */
public class IntArray {
	
	public int[] at;
	public int length;
	
	public IntArray(int n) {
		at = new int[max(4, (int)min(Integer.MAX_VALUE - 8, (long)(n * Utils.GROW_FACTOR)))];
		length = n;
	}
	
	public IntArray() {
		this(0);
	}
	
	public void add(int v) {
		if (length == at.length) at = Utils.grow(at);
		at[length++] = v;
	}
	
	public int popLast() {
		return at[--length];
	}
	
	public int[] toArray() {
		return copyOf(at, length);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < length; i++) {
			if (i > 0) sb.append(',');
			sb.append(at[i]);
		}
		sb.append(']');
		return sb.toString();
	}
	
}
