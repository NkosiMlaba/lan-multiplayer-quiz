package za.co.theemlaba.ThreadCreation;

public class Thread4 {

    // lambda implementing runnable
    public static void main(String[] args) {
        
        Runnable threadRunnableLambda = () -> {
            System.out.println("Lambda thread is running"); // lambda body
        };

        Thread thread4 = new Thread( threadRunnableLambda );
        thread4.start();
    }
        


}
