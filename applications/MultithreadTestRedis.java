import java.io.*;
//import jredis-alphazero-all.jredis.*;
//import redis.clients.jedis.*;
import redis.clients.jedis.Jedis;
import java.util.Random;
import java.lang.System.*;
import java.lang.Thread;

class TestRedis extends Thread
{
    private static final int DATASIZE = 100*1024*1024;
    private static final int COUNT = 10;
    private static final int timeInterval = 6000;

    private static final int port = 7001;
    private static final String server_ip = "localhost";

    public void run() 
    { 
        try
        { 
            System.out.println ("Thread " + Thread.currentThread().getId() + " is running"); 
            Jedis redis = new Jedis(server_ip, port);
            testWrite(DATASIZE, redis);
        } 
        catch (Exception e) 
        { 
            System.out.println ("Exception is caught"); 
        } 
    } 

    public static void testWrite(int size, Jedis targetRedis) throws Exception{
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        long runtime = 0;
        for (int i = 0; i < COUNT; i++) {
                String key = "kkkk" + Thread.currentThread().getId() + i;
                System.out.println ("Start writing. key is " + key); 
                long startTime = System.nanoTime();
                targetRedis.set(key.getBytes(), b);
                long endTime = System.nanoTime();
                runtime += endTime - startTime;
            	Thread.sleep(timeInterval);
        }
        System.out.println("SET "+COUNT+"*"+DATASIZE+" Bytes cost "+runtime/1000000000.0 + " s"); b = null;
    }
}


public class MultithreadTestRedis
{ 
    private static final int THREADS = 1;
    public static void main(String[] args) 
    { 
        int n = THREADS; // Number of threads 
        for (int i=0; i<n; i++) 
        { 
            TestRedis object = new TestRedis(); 
            object.start(); 
        } 
    } 
} 
