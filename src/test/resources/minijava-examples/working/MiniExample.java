
class Main {
    public static void main(String[] a){
    	//System.out.println((new Test()).run());
    	//System.out.println(new int[12]);
    	//System.out.println(1);
    	if ((new Test()).run() == 1) {
    		System.out.println(1);
    	}
    	else {
    		System.out.println(2);
    	}
    }
}

class Test2 {
	public int run() {
		System.out.println(2);
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
		a = x.run();
		return 1;
	}
}