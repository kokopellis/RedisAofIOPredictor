import java.util.*;
import java.io.*;
public class CollectTime {
       public static void main(String[] args) throws IOException{
              List<Integer> list = new ArrayList<>();
	      Integer res = 0;
	      String filename = args[0];
	      BufferedReader reader = new BufferedReader(new FileReader(filename));
              String line;
	      while((line = reader.readLine())!= null){
	           String[] arr = line.split(" ");
		   String time = arr[arr.length -1];
		   time = time.substring(0, time.length() - 2);
		   list.add(Integer.parseInt(time));
	      }
	      for(int i : list) {
	          res += i;
	      }
	      System.out.println(res);
       }

}
