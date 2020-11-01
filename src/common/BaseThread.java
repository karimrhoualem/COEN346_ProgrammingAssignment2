package common;

/**
 * Class BaseThread
 * Simply one customized base class for many of our own threads.
 *
 * An attempt to maintain an automatic unique TID (thread ID)
 * among all the derivatives and allow setting your own if needed.
 * Plus some methods for the sync exercises.
 *
 * Karim Rhoualem
 * Student 26603157
 */
public class BaseThread extends Thread
{
    /*
     * Declaration of ANSI color codes that are used in the console logs
     * to improve readability by assigning a color code to each class within the program.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";

    /*
     * ------------
     * Data members
     * ------------
     */

    /**
     * Preserves value across all instances.
     */
    public static int siNextTID = 1;

    /**
     * Our Thread ID.
     */
    protected int iTID;

    /**
     * TID of a thread to proceed to the phase II.
     */
    private static int siTurn = 1;

    /**
     * Boolean value used for each instance of this class in order to minimize print statements
     * indicating that the thread tried to run for Phase II before being allowed to.
     * This is used in the testTurnAndSet method.
     */
    private boolean printedWaitMessage = false;

    /*
     * ------------
     * Constructors
     * ------------
     */

    /**
     * Default
     */
    public BaseThread()
    {
        setTID();
    }

    /**
     * Assigns name to the thread and places it to the specified group.
     *
     * @param poGroup ThreadGroup to add this thread to
     * @param pstrName A string indicating human-readable thread's name
     */
    public BaseThread(ThreadGroup poGroup, String pstrName)
    {
        super(poGroup, pstrName);
        setTID();
    }

    /**
     * Sets user-specified TID.
     */
    public BaseThread(final int piTID)
    {
        this.iTID = piTID;
    }

    /**
     * Retrieves our TID.
     * @return TID, integer
     */
    public final int getTID()
    {
        return this.iTID;
    }

    /**
     * Sets internal TID and updates next TID on contruction time, so it's private.
     */
    private final void setTID()
    {
        this.iTID = siNextTID++;
    }

    /**
     * Allows setting initial turn value to something else
     * other than the default "1" (one).
     * @param piInitTurn new initial value of the turn.
     */
    public static synchronized final void setInitialTurn(int piInitTurn)
    {
        siTurn = piInitTurn;
    }

    /**
     * Just a make up for the PHASE I to make it somewhat tangeable.
     * Must be atomic as it touches siTurn and siNextTID.
     */
    protected synchronized void phase1()
    {
        System.out.println(ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - PHASE 1] " + "Thread [TID=" + this.iTID + "] starts PHASE I." + ANSI_RESET);

        System.out.println
                (
                        ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - PHASE 1] " + "Some stats info in the PHASE I:\n" +
                                "\t\t    iTID = " + this.iTID +
                                ", siNextTID = " + siNextTID +
                                ", siTurn = " + siTurn +
                                ".\n\t\t    Their \"checksum\": " + (siNextTID * 100 + this.iTID * 10 + siTurn) + ANSI_RESET
                );

        System.out.println(ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - PHASE 1] " + "Thread [TID=" + this.iTID + "] finishes PHASE I." + ANSI_RESET);
    }

    /**
     * Just a make up for the PHASE II to make it somewhat tangeable.
     * Must be atomic as it touches siTurn and siNextTID.
     */
    protected synchronized void phase2()
    {
        System.out.println(ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - PHASE 2] " + "Thread [TID=" + this.iTID + "] starts PHASE II." + ANSI_RESET);

        System.out.println
                (
                        ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - Phase 2] + " + "Some stats info in the PHASE II:\n" +
                                "\t\t    iTID = " + this.iTID +
                                ", siNextTID = " + siNextTID +
                                ", siTurn = " + siTurn +
                                ".\n\t\t    Their \"checksum\": " + (siNextTID * 100 + this.iTID * 10 + siTurn) + ANSI_RESET
                );

        System.out.println(ANSI_BLUE + "[BaseThread - " + this.getClass().getSimpleName() + " - PHASE 2] " + "Thread [TID=" + this.iTID + "] finishes PHASE II." + ANSI_RESET);
    }

    // Semaphore used to ensure that field siTurn is incremented by each thread independently.
    private static Semaphore test = new Semaphore(1);

    /**
     * Test-and-Set for the iTurn variable.
     *
     * Use to proceed to the phase II in the correct order.
     * Must be atomic.
     *
     * @param pcIncreasingOrder true if TIDs are in increasing order; false otherwise
     *
     * @return Returns true if the turn has changed, 'false' otherwise
     */
    public synchronized boolean turnTestAndSet(boolean pcIncreasingOrder)
    {
        //----------------------------------------- CRITICAL SECTION ------------------------------------------------------

        test.Wait(this.getClass().getSimpleName(), this.iTID);
        // test
        if(siTurn == this.iTID)
        {
            // set siTurn = siTurn +/- 1;
            if(pcIncreasingOrder) {
                siTurn++;
                System.out.println(ANSI_RED + "[BaseThread - " + this.getClass().getSimpleName() + " - turnTestAndSet] " +
                        "Thread " + this.iTID + "'s turn to run Phase II." + ANSI_RESET);
                this.phase2();
            }
            else {
                siTurn--;
            }

        test.Signal(this.getClass().getSimpleName(), this.iTID);
            return true;
        }

        if (!printedWaitMessage) {
            System.out.println(ANSI_RED + "[BaseThread - " + this.getClass().getSimpleName() + " - turnTestAndSet] " +
                    "Thread " + this.iTID + " has attempted to run Phase II but must wait its turn." + ANSI_RESET);

            printedWaitMessage = true;
        }

        test.Signal(this.getClass().getSimpleName(), this.iTID);

        //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ CRITICAL SECTION ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

        return false;
    }

    /**
     * Always assumes the increasing order.
     */
    public synchronized boolean turnTestAndSet()
    {
        return turnTestAndSet(true);
    }
}

// EOF
