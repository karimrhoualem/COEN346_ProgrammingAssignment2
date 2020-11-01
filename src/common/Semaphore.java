package common;

/**
 * Class Semaphore
 * Implements artificial semaphore built on top of Java's sync primitives.
 *
 * Karim Rhoualem
 * Student 26603157
 */
public class Semaphore
{
    /**
     * Current semaphore's value
     */
    private int iValue;

    /**
     * Counter used to control the output message indicating that phase 1 is complete.
     */
    private int counter = 0;

    /*
     * ------------
     * Constructors
     * ------------
     */

    /**
     * With value parameter.
     *
     * NOTE: There is no check made whether the value is positive or negative.
     * This implementation allows initialization of a semaphore to a negative
     * value because it's the only way it can become so. Wait() does not do
     * that for us. The semantic of that could be that how many threads are
     * _anticipated_ to be waiting on that semaphore.
     *
     * @param piValue Initial value of the semaphore to set.
     */
    public Semaphore(int piValue)
    {
        this.iValue = piValue;
    }

    /**
     * Default. Equivalent to Semaphore(0)
     */
    public Semaphore()
    {
        this(0);
    }

    /**
     * Returns true if locking condition is true.
     * Usually used in PA3-4.
     */
    public synchronized boolean isLocked()
    {
        return (this.iValue <= 0);
    }

    /*
     * -----------------------------
     * Standard semaphore operations
     * -----------------------------
     */

    /**
     * Puts thread asleep if semamphore's values is less than or equal to zero.
     *
     * NOTE: This implementation as-is does not allow semaphore's value
     * to become negative.
     * @param callingClassName
     * @param iTID
     */
    public synchronized void Wait(String callingClassName, int iTID) //TODO: For debugging purposes only. Remove parameters
    {
        try
        {
            while(this.iValue <= 0)
            {
                wait();
            }

            this.iValue--;

            //TODO: For debugging purposes. Comment out when done
            //System.out.println("[Semaphore - " + callingClassName + " (TID = " + iTID + ") - Wait] iValue = " + this.iValue);
        }
        catch(InterruptedException e)
        {
            System.out.println
                (
                        "[Semaphore - " + callingClassName + " (TID = " + iTID + ") - Wait] Caught InterruptedException: " +
                                e.getMessage()
                );

            e.printStackTrace();
        }
    }

    /**
     * Increments semaphore's value and notifies another (single) thread of the change.
     *
     * NOTES: By waking up any arbitrary thread using notify() and not notify
     * all are simply being a little more efficient: we don't really care which
     * thread is this, any would do just fine.
     */
    public synchronized void Signal(String callingClassName, int iTID) //TODO: For debugging purposes only. Remove parameters
    {
        ++this.iValue;

        //TODO: For debugging purposes. Comment out when done
        //System.out.println("[Semaphore - " + callingClassName + " (TID = " + iTID + ") - Signal] iValue = " + this.iValue);

        notify();
    }

//    /**
//     * Proberen. An alias for Wait().
//     */
//    public synchronized void P()
//    {
//        this.Wait(this.getClass().getSimpleName());
//    }
//
//    /**
//     * Verhogen. An alias for Signal()
//     */
//    public synchronized void V()
//    {
//        this.Signal();
//    }

    /**
     * Gets the current semaphore's value to display in the console.
     * @return The integer value of the semaphore.
     */
    public int getiValue() {
        return iValue;
    }

    /**
     * Increments and gets the counter used to keep track of the number of semaphores that have been used
     * so that a log message can be displayed in the console.
     * @return The integer value of the counter.
     */
    public synchronized int IncrementCounter() {
        return ++counter;
    }

    /**
     * Gets the counter value
     * @return Integer value of the counter.
     */
    public int getCounter() {
        return counter;
    }
}

// EOF
