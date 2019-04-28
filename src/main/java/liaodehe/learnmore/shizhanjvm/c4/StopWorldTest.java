package liaodehe.learnmore.shizhanjvm.c4;

import java.util.HashMap;

/**
 *  Stop the World(STW) 垃圾回收时，应用程序会停顿
 */
public class StopWorldTest {
    public static int BYTE_ARRAY_SIZE = 1024;

    public static class MyThread extends Thread {
        private HashMap map = new HashMap();

        @Override
        public void run() {
            try {
                while (true) {
                    if (map.size() * BYTE_ARRAY_SIZE / 1024 / 1024 >=550) { // 第一个参数运行时，改为 >900
                        map.clear();
                        System.out.println("Clear Map!");
                    }

                    byte[] b1;
                    for (int i = 0; i < 100; i++) {
                        b1 = new byte[BYTE_ARRAY_SIZE];
                        map.put(System.nanoTime(), b1);
                    }
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static class PrintThread extends Thread {
        public static final long startTime = System.currentTimeMillis();

        @Override
        public void run() {
            try{
                while (true){
                    long t = System.currentTimeMillis() - startTime;
                    System.out.println(t/1000 + "." + t%1000);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // java -Xmx1g -Xms1g -Xmn512k -XX:+UseSerialGC -Xloggc:gc.log -XX:+PrintGCDetails liaodehe.learnmore.shizhanjvm.c4.StopWorldTest
    // java -Xmx1g -Xms1g -Xmn900m -XX:SurvivorRatio=1 -XX:+UseSerialGC -Xloggc:gc.log -XX:+PrintGCDetails liaodehe.learnmore.shizhanjvm.c4.StopWorldTest
    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        PrintThread printThread = new PrintThread();
        myThread.start();
        printThread.start();
    }
}
