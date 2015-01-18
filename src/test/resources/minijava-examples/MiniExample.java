// sum over an array
// expected output for sum 1+...+10:
// 55
// ---------------------------------------------------------------------------

class ArrSum {
	public static void main(String[] a) {
		System.out.println((new Arr()).do_it(10));
	}
}

class Arr {

	int[] x;
	
	public int do_it(int m) {
		int a;
		x = new int[m];
		x[9] = 12;
		return x[9];
	}
}
