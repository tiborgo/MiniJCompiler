
class Main {
    public static void main(String[] a){
    	//System.out.println((new Test()).run());
    	//System.out.println(new int[12]);
    	//System.out.println(1);
    	/*if ((new Test()).run() < 1) {
    		System.out.println(1);
    	}
    	else {
    		System.out.println(2);
    	}*/
    	System.out.println((new Test3()).test());
    }
}

class Test3 {
	public int test() {
		int[] a;
		a = new int[10];
		a[9] = 5;
		return a.length;
	}
}

class Test2 {
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
}