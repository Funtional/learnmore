package liaodehe.learnmore.shizhanjvm.c4;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class SoftRefQ {
    static ReferenceQueue<User> softQueue = null;

    public static class UserSoftReference extends SoftReference<User>{
        int uid;
        public UserSoftReference(User referent, ReferenceQueue<? super User> q) {
            super(referent, q);
            this.uid = referent.getId();
        }
    }

    public static class CheckRefQueue extends Thread{
        @Override
        public void run() {
            while (true){
                if (softQueue != null){
                    UserSoftReference obj = null;
                    try{
                        // 当垃圾回收器准备回收一个对象时，如果发现它还有软引用，会在回收对象后，将这个软引用加入引用队列，
                        // 以通知应用程序对象的回收情况。所以此处才可以获取。
                        obj = (UserSoftReference)softQueue.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (obj != null){
                        System.out.println("User id " + obj.uid + "is delete.");
                    }
                }
            }
        }
    }

    // 编译后，进入target的classes目录，执行命令 java -Xms10m -Xmx15m liaodehe.learnmore.shizhanjvm.c4.SoftRefQ
    public static void main(String[] args){
        Thread t = new CheckRefQueue();
        t.setDaemon(true);
        t.start();

        softQueue = new ReferenceQueue<User>();
        List<UserSoftReference> list = new ArrayList();



        System.out.println("Try to create byte array and gc");
        byte[]  b = null;
        for (int i =0; i< 15; i++){
            User u = new User(i, "geym");
            UserSoftReference userSoftRef = new UserSoftReference(u, softQueue);
            list.add(userSoftRef);
            if (i == 2){
                u = null;
                System.out.println(userSoftRef.get());
                System.gc();
                System.out.println("After gc.");
                System.out.println(userSoftRef.get());
            }
        }


        System.gc();
        for (UserSoftReference ref: list ){
            System.out.println(ref.get());
        }


        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class User{
        public int id;
        public String name;
        private byte[] b = new byte[1*1024*1024];

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}
