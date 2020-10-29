// Import (aka include) some stuff.
import common.*;

/**
 * Class BlockManager
 * Implements character block "manager" and does twists with threads.
 *
 * Karim Rhoualem
 * Student 26603157
 */
public class BlockManager
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    /**
     * The stack itself
     */
    private static BlockStack soStack = new BlockStack();

    /**
     * Number of threads dumping stack
     */
    private static final int NUM_PROBERS = 4;

    /**
     * Number of steps they take
     */
    private static int siThreadSteps = 5;

    /**
     * For atomicity
     */
    private static Semaphore mutex = new Semaphore(1);

    /*
     * For synchronization
     */

    /**
     * s1 is to make sure phase I for all is done before any phase II begins
     */
    private static Semaphore s1 = new Semaphore(-3);

    /**
     * s2 is for use in conjunction with Thread.turnTestAndSet() for phase II proceed
     * in the thread creation order
     */
    private static Semaphore s2 = new Semaphore(0);


    // The main()
    public static void main(String[] argv)
    {
        try
        {
            // Some initial stats...
            System.out.println("[Main] Main thread starts executing.");
            System.out.println("[Main] Initial value of top = " + soStack.getITop() + ".");
            System.out.println("[Main] Initial value of stack top = " + soStack.pick() + ".");
            System.out.println("[Main] Main thread will now fork several threads.");

            /*
             * The birth of threads
             */
            AcquireBlock ab1 = new AcquireBlock();
            AcquireBlock ab2 = new AcquireBlock();
            AcquireBlock ab3 = new AcquireBlock();

            System.out.println("[Main] Three AcquireBlock threads have been created.");

            ReleaseBlock rb1 = new ReleaseBlock();
            ReleaseBlock rb2 = new ReleaseBlock();
            ReleaseBlock rb3 = new ReleaseBlock();

            System.out.println("[Main] Three ReleaseBlock threads have been created.");

            // Create an array object first
            CharStackProber	aStackProbers[] = new CharStackProber[NUM_PROBERS];

            // Then the CharStackProber objects
            for(int i = 0; i < NUM_PROBERS; i++)
                aStackProbers[i] = new CharStackProber();

            System.out.println("[Main] " + NUM_PROBERS + " CharStackProber threads have been created.");

            /*
             * Twist 'em all
             */
            ab1.start();
            aStackProbers[0].start();
            rb1.start();
            aStackProbers[1].start();
            ab2.start();
            aStackProbers[2].start();
            rb2.start();
            ab3.start();
            aStackProbers[3].start();
            rb3.start();

            System.out.println("[Main] All the threads are ready.");

            /*
             * Wait by here for all forked threads to die
             */
            ab1.join();
            ab2.join();
            ab3.join();

            rb1.join();
            rb2.join();
            rb3.join();

            for(int i = 0; i < NUM_PROBERS; i++) {
                aStackProbers[i].join();
            }

            // Some final stats after all the child threads terminated...
            System.out.println("[Main] System terminates normally.");
            System.out.println("[Main] Final value of top = " + soStack.getITop() + ".");
            System.out.println("[Main] Final value of stack top = " + soStack.pick() + ".");
            System.out.println("[Main] Final value of stack top-1 = " + soStack.getAt(soStack.getITop() - 1) + ".");
            System.out.println("[Main] Stack access count = " + soStack.getStackAccessCounter());
            System.out.print("[Main] Final stack: ");
            // [s] - means ordinay slot of a stack
            // (s) - current top of the stack
            for(int s = 0; s < soStack.getISize(); s++)
                System.out.print
                        (
                                ANSI_YELLOW + (s == BlockManager.soStack.getITop() ? "(" : "[") +
                                        BlockManager.soStack.getAt(s) +
                                        (s == BlockManager.soStack.getITop() ? ")" : "]") + ANSI_RESET
                        );

            System.out.println(ANSI_YELLOW + "." + ANSI_RESET);

            System.exit(0);
        }
        catch(InterruptedException e)
        {
            System.err.println("[Main] Caught InterruptedException (internal error): " + e.getMessage());
            e.printStackTrace(System.err);
        }
        catch(Exception e)
        {
            reportException(e);
        }
        finally
        {
            System.exit(1);
        }
    } // main()


    /**
     * Inner AcquireBlock thread class.
     */
    static class AcquireBlock extends BaseThread
    {
        /**
         * A copy of a block returned by pop().
         * @see BlockStack#pop()
         */
        private char cCopy;

        public void run()
        {
            System.out.println(ANSI_GREEN + "[AcquireBlock - Starting] AcquireBlock thread [TID=" + this.iTID + "] starts executing." + ANSI_RESET);

            phase1();
            s1.Wait("(S1) " + this.getClass().getSimpleName(), this.iTID);
            s1.IncrementCounter();

            if (s1.getCounter() == 3) {
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "[AcquireBlock] ALL THREADS HAVE COMPLETED PHASE I." + ANSI_RESET);
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
            }

            try
            {
                mutex.Wait("(Mutex) " + this.getClass().getSimpleName(), this.iTID);

                System.out.println(ANSI_GREEN + "[AcquireBlock - CS] AcquireBlock thread [TID=" + this.iTID + "] requests Ms block." + ANSI_RESET);

                /**
                 * Check to see if the stack is empty. If so, do not pop anything. Just print out information about stack.
                 */
                if (soStack.isEmpty()) {
                    System.out.println(ANSI_GREEN + "[AcquireBlock - CS] Stack is empty. Did not perform pop.");
                    System.out.println
                            (
                                    ANSI_GREEN + "[AcquireBlock - CS] AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
                                            " from position " + (soStack.getITop()) + "." + ANSI_RESET
                            );
                }
                else {
                    this.cCopy = soStack.pop();
                    System.out.println
                            (
                                    ANSI_GREEN + "[AcquireBlock - CS] AcquireBlock thread [TID=" + this.iTID + "] has obtained Ms block " + this.cCopy +
                                            " from position " + (soStack.getITop() + 1) + "." + ANSI_RESET
                            );
                }

                System.out.println
                        (
                                ANSI_GREEN + "[AcquireBlock - CS] Acq[TID=" + this.iTID + "]: Current value of top = " +
                                        soStack.getITop() + "." + ANSI_RESET
                        );

                System.out.println
                        (
                                ANSI_GREEN + "[AcquireBlock - CS] Acq[TID=" + this.iTID + "]: Current value of stack top = " +
                                        soStack.pick() + "." + ANSI_RESET
                        );

                mutex.Signal("(Mutex) " + this.getClass().getSimpleName(), this.iTID);
            }
            catch(Exception e)
            {
                reportException(e);
                System.exit(1);
            }

            s2.Signal(this.getClass().getSimpleName(), this.iTID);
            while (!turnTestAndSet(true));

            System.out.println(ANSI_GREEN + "[AcquireBlock - Terminating] AcquireBlock thread [TID=" + this.iTID + "] terminates." + ANSI_RESET);

            if (this.iTID == 10) {
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "[AcquireBlock] ALL THREADS HAVE COMPLETED PHASE II." + ANSI_RESET);
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
            }
        }
    } // class AcquireBlock


    /**
     * Inner class ReleaseBlock.
     */
    static class ReleaseBlock extends BaseThread
    {
        /**
         * Block to be returned. Default is 'a' if the stack is empty.
         */
        private char cBlock = 'a';

        public void run()
        {
            System.out.println(ANSI_PURPLE + "[ReleaseBlock - Starting] ReleaseBlock thread [TID=" + this.iTID + "] starts executing." + ANSI_RESET);

            phase1();
            s1.Signal("(S1) " + this.getClass().getSimpleName(), this.iTID);

            try
            {
                mutex.Wait("(Mutex) " + this.getClass().getSimpleName(), this.iTID);

                /**
                 * If the stack is not empty, get the next char and push it to next open position.
                 * If the stack is full, don't push anything. Log the message and just display the stack the way it was before.
                 */
                if(!soStack.isEmpty()) {
                    if (soStack.isFull()) {
                        this.cBlock = soStack.pick();
                        System.out.println(ANSI_PURPLE + "[ReleaseBlock - CS] Stack is full. Not pushing new value to stack." + ANSI_RESET);
                    }
                    else {
                        this.cBlock = (char)(soStack.pick() + 1);
                        soStack.push(this.cBlock);
                    }
                }

                System.out.println
                        (
                                ANSI_PURPLE + "[ReleaseBlock - CS] ReleaseBlock thread [TID=" + this.iTID + "] returns Ms block " + this.cBlock +
                                        " to position " + (soStack.getITop()) + "." + ANSI_RESET
                        );

                System.out.println
                        (
                                ANSI_PURPLE + "[ReleaseBlock - CS] Rel[TID=" + this.iTID + "]: Current value of top = " +
                                        soStack.getITop() + "." + ANSI_RESET
                        );

                System.out.println
                        (
                                ANSI_PURPLE + "[ReleaseBlock - CS] Rel[TID=" + this.iTID + "]: Current value of stack top = " +
                                        soStack.pick() + "." + ANSI_RESET
                        );

                mutex.Signal("(Mutex) " + this.getClass().getSimpleName(), this.iTID);
            }
            catch(Exception e)
            {
                reportException(e);
                System.exit(1);
            }


            s2.Wait("(S2) " + this.getClass().getSimpleName(), this.iTID);
            s2.Signal("(S2) " + this.getClass().getSimpleName(), this.iTID);
            while (!turnTestAndSet(true));

            System.out.println(ANSI_PURPLE + "[ReleaseBlock - Terminating] ReleaseBlock thread [TID=" + this.iTID + "] terminates." + ANSI_RESET);

            if (this.iTID == 10) {
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "[AcquireBlock] ALL THREADS HAVE COMPLETED PHASE II." + ANSI_RESET);
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
            }
        }
    } // class ReleaseBlock


    /**
     * Inner class CharStackProber to dump stack contents.
     */
    static class CharStackProber extends BaseThread
    {
        public void run()
        {
            System.out.println(ANSI_YELLOW + "[CharStackProber - Starting] CharStackProber thread [TID=" + this.iTID + "] starts executing." + ANSI_RESET);

            phase1();
            s1.Signal("(S1) " + this.getClass().getSimpleName(), this.iTID);

            try
            {
                mutex.Wait("(Mutex) " + this.getClass().getSimpleName(), this.iTID);

                for(int i = 0; i < siThreadSteps; i++)
                {
                    System.out.print(ANSI_YELLOW + "[CharStackProber - CS] Stack Prober [TID=" + this.iTID + "]: Stack state: " + ANSI_RESET);

                    // [s] - means ordinay slot of a stack
                    // (s) - current top of the stack
                    for(int s = 0; s < soStack.getISize(); s++)
                        System.out.print
                                (
                                        ANSI_YELLOW + (s == BlockManager.soStack.getITop() ? "(" : "[") +
                                                BlockManager.soStack.getAt(s) +
                                                (s == BlockManager.soStack.getITop() ? ")" : "]") + ANSI_RESET
                                );

                    System.out.println(ANSI_YELLOW + "." + ANSI_RESET);

                }

                mutex.Signal("(Mutex) " + this.getClass().getSimpleName(), this.iTID);
            }
            catch(Exception e)
            {
                reportException(e);
                System.exit(1);
            }


            s2.Wait("(S2) " + this.getClass().getSimpleName(), this.iTID);
            s2.Signal("(S2) " + this.getClass().getSimpleName(), this.iTID);
            while (!turnTestAndSet(true));

            System.out.println(ANSI_YELLOW + "[CharStackProber - Terminating] CharStackProber thread [TID=" + this.iTID + "] terminates." + ANSI_RESET);

            if (this.iTID == 10) {
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "[AcquireBlock] ALL THREADS HAVE COMPLETED PHASE II." + ANSI_RESET);
                System.out.println(ANSI_GREEN + "---------------------------------------------------------------------------" + ANSI_RESET);
            }
        }
    } // class CharStackProber


    /**
     * Outputs exception information to STDERR
     * @param poException Exception object to dump to STDERR
     */
    private static void reportException(Exception poException)
    {
        System.err.println("Caught exception : " + poException.getClass().getName());
        System.err.println("Message          : " + poException.getMessage());
        System.err.println("Stack Trace      : ");
        poException.printStackTrace(System.err);
    }
} // class BlockManager

// EOF
