package za.co.theemlaba.ThreadCreation;


public class Thread1 {
    

    public static class MyThread extends Thread {
        
        @Override
        public void run() {
            System.out.println("Thread 1 is running");
        }
    }

    public static void main(String[] args) {
        MyThread thread1Instance = new MyThread();
        thread1Instance.start();

        Thread thread2Instance = new MyThread();
        thread2Instance.start();
    }
}
