/**
 * Class BlockStack
 * Implements character block stack and operations upon it.
 *
 * Karim Rhoualem
 * Student 26603157
 */
class BlockStack {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * # of letters in the English alphabet + 2
     */
    private static final int MAX_SIZE = 28;

    /**
     * Default stack size
     */
    private static final int DEFAULT_SIZE = 6;

    /**
     * Current size of the stack
     */
    private int iSize = DEFAULT_SIZE;

    /**
     * Current top position of the stack (a value (index) of 3 means that the stack has 'a', 'b', 'c', and 'd'
     * currently in the stack (well, technically it goes up to an index of 5 because it has the two stars as well -
     * which represents empty spaces in the array.))
     */
    private int iTop = 3;

    /**
     * stack[0:5] with four defined values
     */
    private char accessCounterStack[] = new char[]{'a', 'b', 'c', 'd', '*', '*'};

    /**
     * Keeps track of the amount of times that the stack is accessed.
     */
    private int stackAccessCounter = 0;

    /**
     * Default constructor
     */
    public BlockStack() {
    }

    /**
     * Supplied size for process i
     */
    public BlockStack(final int piSize) {
        if (piSize != DEFAULT_SIZE) {
            this.accessCounterStack = new char[piSize];

            // Fill in with letters of the alphabet and keep 2 free blocks
            for (int i = 0; i < piSize - 2; i++)
                this.accessCounterStack[i] = (char) ('a' + i);

            // Insert the * sign into the last two indices of the stack.
            this.accessCounterStack[piSize - 2] = this.accessCounterStack[piSize - 1] = '*';

            // Top position in the stack will be below the *'s, so we must exclude the two * signs.
            this.iTop = piSize - 3;

            this.iSize = piSize;
        }
    }

    /**
     * Picks a value from the top of the stack without modifying the stack
     *
     * @return top element of the stack, char
     */
    public char pick() {
        stackAccessCounter++;
        return this.accessCounterStack[this.iTop];
    }

    /**
     * Returns arbitrary value from the stack array
     *
     * @return the element, char
     */
    public char getAt(final int piPosition) {
        stackAccessCounter++;
        return this.accessCounterStack[piPosition];
    }

    /**
     * Standard push operation
     */
    public void push(final char character) {
        this.accessCounterStack[++this.iTop] = character;
        System.out.println(ANSI_CYAN + "[BlockStack] " + character + " has been pushed to the stack." + ANSI_RESET);
        stackAccessCounter++;
    }

    /**
     * Standard pop operation
     *
     * @return ex-top element of the stack, char
     */
    public char pop() {
            char character = this.accessCounterStack[this.iTop];
            this.accessCounterStack[this.iTop--] = '*'; // Leave prev. value undefined
            stackAccessCounter++;
            System.out.println(ANSI_CYAN + "[BlockStack] " + character + " has been popped from the stack." + ANSI_RESET);
            return character;
    }

    /**
     * Gets the current top position of the stack.
     * @return Integer for the top position of the stack.
     */
    public int getITop() {
        return iTop;
    }

    /**
     * Gets the size of the stack.
     * @return Integer representing the size of the stack.
     */
    public int getISize() {
        return iSize;
    }

    /**
     * Gets the number of times that the stack was accessed.
     * @return An integer value representing the number of stack accesses.
     */
    public int getStackAccessCounter() {
        return stackAccessCounter;
    }

    /**
     * Checks if the stack is currently empty.
     * @return True if the stack is empty. False if it is not.
     */
    public boolean isEmpty() {
        return this.iTop == -1;
    }

    public boolean isFull() {
        return (this.iTop + 1) == this.iSize;
    }

    /**
     * Gets the current access counter stack.
     * @return The character array access counter stack.
     */
    public char[] getAccessCounterStack() {
        return accessCounterStack;
    }
}

// EOF
