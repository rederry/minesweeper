// package minesweeper;

public class Square {
    
    /*
     * Represent a mutable datatype of the state of a square
     */
    enum State {UNTOUCHED, FLAG, DUG};
    
    private boolean isBoom;
    private State state;
    private int adjacentBoomCount;
    private final int x;
    private final int y;
    
    // TODO: Abstraction function, rep invariant, rep exposure, thread safety
    
    // Abstraction function: 
    //      Represent a square of a minesweeper board
    
    // Rep invariant:
    //       Content is final and can not be change
    
    // Rep exposure
    //      All fields are private;
    
    /*
     * Create a new square with content in state untouched
     */
    public Square(boolean isBoom, int x, int y) {
        this.isBoom = isBoom;
        this.state = State.UNTOUCHED;
        this.adjacentBoomCount = 0;
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setAdjacentBoomCount(int count) {
        this.adjacentBoomCount = count;
    }
    
    public int adjacentBoomCount() {
        return this.adjacentBoomCount;
    }
    
    public void setDug() {
        this.state = State.DUG;
    }
    
    public void setFlag() {   
        this.state = State.FLAG;
    }
    
    public boolean isFlagged() {
        return this.state == State.FLAG;
    }
    
    public void setUntouched() {
        this.state = State.UNTOUCHED;
    }

    public boolean isUntouched() {
        return this.state == State.UNTOUCHED;
    }    

    public boolean containBoom() {
        return isBoom;
    }
    
    public void setUnBoom() {
        this.isBoom = false;
    }
}

