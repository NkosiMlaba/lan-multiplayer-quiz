package za.co.theemlaba.ThreadCreation;

public class Thread2 {
    
    // implementing the runnable interface

    public static class Thread2Runnable implements Runnable {
        
        @Override
        public void run(){
            System.out.println("my runnable thread is running");
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Thread2Runnable());
        thread1.start();
    }
    
}
