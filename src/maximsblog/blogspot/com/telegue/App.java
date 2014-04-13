package maximsblog.blogspot.com.telegue;

import android.app.Application;

public class App extends Application {
	public static Virt2Real virt2real;
	
	@Override
	public void onCreate() {
		virt2real = new Virt2Real(this);
	}
}