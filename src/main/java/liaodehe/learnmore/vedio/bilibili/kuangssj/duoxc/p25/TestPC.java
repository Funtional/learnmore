package liaodehe.learnmore.vedio.bilibili.kuangssj.duoxc.p25;

/**
 * 测试：生产者消费者模型 --> 利用缓冲区解决：管程法
 * 生产者、消费者、产品、缓冲区
 */

public class TestPC {
    public static void main(String[] args) {
        SynContainer synContainer = new SynContainer();
        Productor productor = new Productor("生产者y", synContainer);
        Consumer consumer1 = new Consumer("消费者1", synContainer);
        productor.start();
        consumer1.start();

    }
}

/**
 * 生产者
 */
class Productor extends Thread{
    private SynContainer synContainer;

    public Productor(String name, SynContainer synContainer) {
        super(name);
        this.synContainer = synContainer;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            synContainer.push(new Chicken(i));
        }
    }
}

/**
 * 消费者
 */
class Consumer extends Thread{
    private SynContainer synContainer;

    public Consumer(String name, SynContainer synContainer) {
        super(name);
        this.synContainer = synContainer;
    }

    @Override
    public void run() {
        Chicken chicken = null;
        for (int i = 0; i < 100; i++) {
            chicken = synContainer.pop();
        }
    }
}

/**
 * 产品
 */
class Chicken{
    private int id;

    public Chicken(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}

/**
 * 缓冲区
 */
class SynContainer{
    //需要一个容器大小
    private Chicken[] chickens = new Chicken[10];

    private int count = 0;

    public synchronized void push(Chicken chicken) {
        if (count == 10){
            try {
                System.out.println(Thread.currentThread().getName() + "说盘子放不下了，我休息先！");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        chickens[count++] = chicken;
        System.out.println(Thread.currentThread().getName() + "生产了编号为" + chicken.getId() + "的鸡");
        this.notifyAll();
    }

    public synchronized Chicken pop() {
        if (count == 0){
            try {
                System.out.println(Thread.currentThread().getName() + "说盘子里没有了，我等！");
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Chicken chicken = chickens[--count];
        System.out.println(Thread.currentThread().getName() + "消费了编号为" + chicken.getId() + "的鸡");
        this.notify();
        return chicken;
    }

}
