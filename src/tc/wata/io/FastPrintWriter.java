package tc.wata.io;

import java.io.*;

/**
 * java.io.PrintWriter互換の高速出力クラス<br>
 * bufサイズを1にすれば常にflushされる<br>
 * 大量に出力しそうなintと文字列のみ特別扱いで，他はObjectに変換してから出力される<br>
 * 配列を渡すとスペース区切りで出力する<br>
 * 改行コードは\nで固定(printfで%n使うと混ざる)<br>
 * 1～100万の整数値出力が100msくらい
 */
public class FastPrintWriter implements Closeable {
	
	PrintStream out;
	byte[] buf = new byte[1 << 15];
	int p;
	
	public FastPrintWriter(PrintStream out) {
		this.out = out;
	}
	
	public void print(char c) {
		buf[p++] = (byte)c;
		if (p == buf.length) flush();
	}
	
	public void print(String s) {
		for (int i = 0, n = s.length(); i < n; i++) print(s.charAt(i));
	}
	
	public void println(String s) {
		print(s);
		print('\n');
	}
	
	public void print(int i) {
		print(Integer.toString(i));
	}
	
	public void println(int i) {
		print(i);
		print('\n');
	}
	
	public void printf(String format, Object...args) {
		print(String.format(format, args));
	}
	
	public void print(Object...os) {
		for (int i = 0; i < os.length; i++) {
			if (i > 0) print(' ');
			print(os[i].toString());
		}
	}
	
	public void println(Object...os) {
		print(os);
		print('\n');
	}
	
	public void flush() {
		out.write(buf, 0, p);
		out.flush();
		p = 0;
	}
	
	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}
	
}
