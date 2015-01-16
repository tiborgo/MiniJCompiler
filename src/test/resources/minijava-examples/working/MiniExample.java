
class Main {
    public static void main(String[] a){
    	//System.out.println((new Test()).run());
    	//System.out.println(new int[12]);
    	//System.out.println(100);
    	System.out.println((new Test3()).test());
    	/*if ((new Test()).run() < 1) {
    		System.out.println(1);
    	}
    	else {
    		System.out.println(2);
    	}*/
    	//System.out.print((char)100);
    	
    	/*if (1 < 0) {
    		System.out.print((char)new Test().test());
    	}
    	else {
    		System.out.print((char)new Test().test());
    	}*/
    }
}

class Test {
	public int test(int a) {
		/*int b;
		int c;
		int d;
		int e;
		int f;
		int g;
		int h;
		int i;
		int j;
		int k;
		int l;
		int m;
		int n;
		int o;
		int p;
		b = 0;
		c = 0;
		d = 0;
		e = 0;
		f = 0;
		g = 0;
		h = 0;
		i = 0;
		j = 0;
		k = 0;
		l = 0;
		m = 0;
		n = 0;
		o = 0;
		p = a + b + c + d + e + f + g + h + i + j + k + l + m + n + o;*/
		return 10;
	}
}

class Test3 {
	public int test() {
		int[] a;
		a = new int[10];
		/*if (5 < a[0]) {
			a[9] = 5;
		}
		else {
			a[9] = 3;
		}*/
		a[2] = 50;
		return (new Test()).test(89);
	}
}

/*class Test2 {
	int a;
	
	public int run() {
		System.out.println(a);
		return 1;
	}
	
	public int setA(int a_) {
		a = a_;
		return 1;
	}
}

class Test {
	public int run() {
		Test2 x;
		int a;
		int[] b;
		b = new int[12];
		x = new Test2();
		a = x.setA(10);
		a = x.run();
		return 1;
	}
}*/