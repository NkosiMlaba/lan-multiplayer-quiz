package za.co.theemlaba.ThreadCreation;

public class Thread3 {
    // as an anonymous class
    public static void main(String[] args) {
        
        Runnable threadRunnable = new Runnable() {
            
            @Override
            public void run () {
                System.out.println("Anonymous runnable is running");
            }
        };
    
        Thread thread1 = new Thread(threadRunnable);
        thread1.start();
    }
    
}
