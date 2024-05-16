package za.co.theemlaba.ThreadCreation;

public class Thread5 {
    
    // showing methods executable on threads
    
    public static class MyThread extends Thread {
        
        @Override
        public void run() {
            System.out.println("Thread 1 is running");
        }

        public MyThread () {
            // nothing
        }

        public MyThread (String name) {
            this.setName(name);
        }
    }

    public static void main(String[] args) {
        Runnable threadRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("we are runnning");
            }
        };


        // since main is thread itself can we get information aboout it?;
        System.out.println(Thread.currentThread()); // get the current thread being executed by the current block
        System.out.println(Thread.currentThread().getName()); // gets the name of the thread

        MyThread thread1Instance = new MyThread();
        thread1Instance.start();

        // creating and specifying the name of the thread
        Thread thread2Instance = new Thread("Thread With Name");
        thread2Instance.start();
        System.out.println(thread2Instance.currentThread().getName());

        Thread threadThatIsARunnable = new Thread( threadRunnable, "runnable thread" );
        threadThatIsARunnable.start();
        System.out.println(threadThatIsARunnable.currentThread().getName());
    }
}
