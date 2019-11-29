
public class Predictor {

	private static final boolean AOF_ON = true;
	private static final int aof_rewrite_perc = 100;
	private static final long aof_rewrite_min_size = 64 * 1024 * 1024;

	private static final String output_type = "bytes";
	//private static final String output_type = "KB";
	//private static final String output_type = "MB";

        private static final int DATASIZE = 1*1024*1024;
        private static final int COUNT = 256;
        private static final int timeInterval = 8000;

	private static final int interfereIO = 10;  // MB/s
	private static final int diskIObandwidth = 200;  // MB/s

	private long aof_current_size;
	private long aof_rewrite_base_size;

	public Predictor() {
		aof_current_size = 0;
		aof_rewrite_base_size = 0;
	}

	public static void main(String args[]) {
		Predictor p = new Predictor();
		/* Maximum string size is 512MB, both key and value are string based. */
		//long[] key_size = new long[] {20, 7, 20, 14, 12};
		//long[] input = new long[] {100 * 1024 * 1024, 200 * 1024 * 1024, -100 * 1024 * 1024, 75 * 1024 * 1024, 400 * 1024 * 1024};
		long[] key_size = new long[COUNT] ;
		long[] input = new long[COUNT] ;
		for(int i = 0;i < COUNT; i++) {
			key_size[i] = 7;
			input[i] = DATASIZE;
		}
		p.predict(input);
		p.predict(key_size, input);
	}

	public void predict(long[] input ) {
		System.out.println("Start Redis I/O prediction:\n");
		long write_count = 0;
		long remove_count = 0; long ops_cost = 0;
		long sum = 0;
		aof_rewrite_base_size = 0;

		for(long a : input) {
			sum += a;
			if(a > 0) {
				aof_current_size += a + ops_cost;
				write_count += a + ops_cost;
			} else if(a < 0) { 
				aof_current_size += ops_cost;
			}
			if(sum < 0) sum = 0;
			if( AOF_ON && 
			    aof_rewrite_perc > 0 && 
			    aof_current_size > aof_rewrite_min_size) {
				long base = aof_rewrite_base_size > 0 ? aof_rewrite_base_size : 1;
				long growth = (aof_current_size * 100 / base) - 100;
				if(growth >= aof_rewrite_perc) {
					System.out.println("Start aof rewrite, rewrite size = " + sum);
					write_count += sum;
					remove_count += aof_current_size;
					aof_current_size = sum;
					aof_rewrite_base_size = aof_current_size;
					a = a + sum;
				}
				System.out.print("\tgrowth = " + growth +  " ,\t");
			}
			System.out.println("write_count = " + a + " , Total_I/O_write = " + write_count);
		}
		/*
		System.out.println("Total input data size: " + sum + " bytes\n");
		System.out.println("I/O analysis:");
		System.out.println("\tI/O write data size: " + write_count + " bytes");
		System.out.println("\tI/O remove data size: " + remove_count + " bytes");
		*/

		System.out.println();

		switch(output_type) {
			case "bytes":
				break;
			case "KB":
				sum /= 1024;
				write_count /= 1024;
				remove_count /= 1024;
				break;
			case "MB":
				sum = sum / 1024 / 1024;
				write_count = write_count / 1024 / 1024;
				remove_count = remove_count / 1024 / 1024;
				break;
		}
		System.out.println("Total input data size: " + sum + " " + output_type + "\n");
		System.out.println("I/O analysis:");
		System.out.println("\tI/O write data size: " + write_count + " " + output_type);
		System.out.println("\tI/O remove data size: " + remove_count + " " + output_type);
	}

	// Log10 function with result ceiling
	public static int log_ceiling(long a) {
		int res = 0;
		while(a != 0) {
			a /= 10;
			res++;
		}
		return res;
	}

	public void predict(long[] key_size, long[] input ) {
		System.out.println("Start Redis I/O prediction:\n");
		long write_count = 0;
		long remove_count = 0;
		long ops_cost = 0;
		long sum = 0;
		int i = 0;
		aof_rewrite_base_size = 0;

		for(long a : input) {
			if(i == 0) {
				aof_current_size = 23;
				sum = 23;
			}

			if(a > 0) {
				ops_cost = 26 + key_size[i] + log_ceiling(a);
				sum += a + ops_cost;
				aof_current_size += a + ops_cost;
				write_count += a + ops_cost;
			} else if(a < 0) {
				sum = sum + a - key_size[i] - log_ceiling(-a) - 26;
				ops_cost = 21 + key_size[i];
				aof_current_size += ops_cost;
				write_count  += ops_cost;
			}
			System.out.println("\tops_cost = " + ops_cost + " , aof_current_size = " + aof_current_size);
			//System.out.println("ops_cost = " + ops_cost + " , aof_current_size = " + aof_current_size + " , sum = " + sum);
			if(sum < 0) sum = 0;
			if( AOF_ON && 
			    aof_rewrite_perc > 0 && 
			    aof_current_size > aof_rewrite_min_size) {
				long base = aof_rewrite_base_size > 0 ? aof_rewrite_base_size : 1;
				long growth = (aof_current_size * 100 / base) - 100;
				if(growth >= aof_rewrite_perc) {
					System.out.println("Start aof rewrite. rewrite size = " + sum);
					write_count += sum;
					remove_count += aof_current_size;
					aof_current_size = sum;
					aof_rewrite_base_size = aof_current_size;
					a = a + sum;
				}
				System.out.print("\tgrowth = " + growth + " ,\t");
				//System.out.print("\ti = " + i + "\tbase = " + base + "\tgrowth = " + growth + "\t, a = " + a + " ,\t");

			}
			System.out.println("write_count = " + a + " , Total_I/O_write = " + write_count);
			i++;
		}

		System.out.println();

		switch(output_type) {
			case "bytes":
				break;
			case "KB":
				sum /= 1024;
				write_count /= 1024;
				remove_count /= 1024;
				break;
			case "MB":
				sum = sum / 1024 / 1024;
				write_count = write_count / 1024 / 1024;
				remove_count = remove_count / 1024 / 1024;
				break;
		}
		System.out.println("Total input data size: " + sum + " " + output_type + "\n");
		System.out.println("I/O analysis:");
		System.out.println("\tI/O write data size: " + write_count + " " + output_type);
		System.out.println("\tI/O remove data size: " + remove_count + " " + output_type);
	}
}
