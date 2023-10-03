import java.util.*;
import java.io.File;

public class SlidePuzzle implements Comparable<SlidePuzzle> {
    private int[][] grid;

    private int m;
    
    private int n;

    private int r;

    private int c;

    private SlidePuzzle parent;

    private String prevMove;

    private int value;

    private static int maxNodes = Integer.MAX_VALUE;

    /**
     * Constructs a new m*n-puzzle in solved state
     * @param m # rows
     * @param n # cols
     */
    public SlidePuzzle(int m, int n) {
        this.m = m;
        this.n = n;
        r = 0;
        c = 0;
        grid = makeGrid(m, n);
        parent = null;
        prevMove = null;
        value = 0;
    }

    /**
     * Make new grid in solved state
     * 
     * @param m rows
     * @param n cols
     * @return solved grid
     */
    private int[][] makeGrid(int m, int n) {
        int[][] solvedGrid = new int[m][n];
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                solvedGrid[i][j] = count++;
            }
        }
        return solvedGrid;
    }


    /**
     * Set the max number of nodes to be considered during a search
     * 
     * @param n Number of nodes to consider
     */
    public static void setMaxNodes(int n) throws IllegalArgumentException {
        if (n <= 0) {
            throw new IllegalArgumentException("Please enter a value greater than 0.");
        }
        maxNodes = n;
    }

    /**
     * Moves blank tile up
     * 
     * @return True if blank tile can move up
     */
    public boolean up() {
        if (r == 0) {
            return false;
        }
        grid[r][c] = grid[--r][c];
        grid[r][c] = 0;
        prevMove = "up";
        return true;
    }

    /**
     * Moves blank tile down
     * 
     * @return True if blank tile can move down
     */
    public boolean down() {
        if (r == m-1) {
            return false;
        }
        grid[r][c] = grid[++r][c];
        grid[r][c] = 0;
        prevMove = "down";
        return true;
    }

    /**
     * Moves blank tile left
     * 
     * @return True if blank tile can move left
     */
    public boolean left() {
        if (c == 0) {
            return false;
        }
        grid[r][c] = grid[r][--c];
        grid[r][c] = 0;
        prevMove = "left";
        return true;
    }

    /**
     * Moves blank tile right
     * 
     * @return True if blank tile can move right
     */
    public boolean right() {
        if (c == n-1) {
            return false;
        }
        grid[r][c] = grid[r][++c];
        grid[r][c] = 0;
        prevMove = "right";
        return true;
    }

    /**
     * Randomly performs n moves from the goal state
     * 
     * @param n Number of random moves to perform
     */
    public void randomize(int n) {
        Random random = new Random();
        // Reset grid
        grid = makeGrid(m, this.n);
        r = 0;
        c = 0;
        for (int i = 0; i < n; i++) {
            List<String> moves = getValidMoves();
            String move = moves.get((int)(random.nextDouble()*moves.size()));
            if (move.equals("up")) {
                up();
            } else if (move.equals("down")) {
                down();
            }  else if (move.equals("left")) {
                left();
            } else {
                right();
            }
        }
    }

    /**
     * Randomly performs n moves from the goal state
     * Overloaded method to allow for seeding
     * 
     * @param n Number of random moves to perform
     * @param seed Seed for random number generator
     */
    public void randomize(int n, long seed) {
        Random random = new Random(seed);
        // Reset grid
        grid = makeGrid(m, this.n);
        r = 0;
        c = 0;
        for (int i = 0; i < n; i++) {
            List<String> moves = getValidMoves();
            String move = moves.get((int)(random.nextDouble()*moves.size()));
            if (move.equals("up")) {
                up();
            } else if (move.equals("down")) {
                down();
            }  else if (move.equals("left")) {
                left();
            } else {
                right();
            }
        }
    }

    /**
     * Returns a list of valid moves for given board state
     * 
     * @return List of valid moves
     */
    private List<String> getValidMoves() {
        List<String> moves = new LinkedList<>();
        // can move up
        if (r != 0)
            moves.add("up");
        // can move down
        if (r != m)
            moves.add("down");
        // can move left
        if (c != 0)
            moves.add("left");
        // can move right
        if (c != n)
            moves.add("right");
        return moves;
    }

    /**
     * Convert 2D array board to string
     * 
     * @param p puzzle to convert
     * @return String of board state
     */
    protected static String gridToString(SlidePuzzle p) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : p.grid) {
            for (int x : row) {
                sb.append(x);
                sb.append(' ');
            }
        }
        return sb.toString().trim();
    }

    /**
     * Solves puzzle using A* search and prints the solution
     * Specify heuristic as "h1" (number of misplaced tiles) or "h2" (manhattan distance)
     * 
     * @param heuristic Either "h1" or "h2"
     * @return Number of moves
     */
    public int solveAStar(String heuristic) throws IllegalArgumentException {
        if (heuristic.equals("h1")) {
            int moves = solveH1();
            System.out.println("Number of moves: " + moves + "\n");
            return moves;
        } else if (heuristic.equals("h2")) {
            int moves = solveH2();
            System.out.println("Number of moves: " + moves + "\n");
            return moves;
        }
        throw new IllegalArgumentException("Invalid heuristic");
    }

    /**
     * A* search using h1
     * 
     * @return Number of moves
     * @throws OutOfMemoryError Max node limit exceeded
     */
    private int solveH1() throws OutOfMemoryError {
        // min cost heap
        PriorityQueue<SlidePuzzle> pq = new PriorityQueue<>();

        // keep track of visited states
        Map<String, SlidePuzzle> visited = new HashMap<>();

        String key = getSolvedKey();
        
        // count of generated nodes
        int nodes = 0;

        // add initial state to pq
        pq.add(this);

        while (!visited.containsKey(key) && !pq.isEmpty() && nodes <= maxNodes) {
            SlidePuzzle currState = pq.poll();
            String gridString = gridToString(currState);
            if (!visited.containsKey(gridString)) {
                visited.put(gridString, currState);

                List<String> validMoves = currState.getValidMoves();
                for (String move : validMoves) {
                    // generate and add child state if not already visited
                    SlidePuzzle child = currState.duplicate();
                    if (move.equals("up")) {
                        child.up();
                    } else if (move.equals("down")) {
                        child.down();
                    } else if (move.equals("left")) {
                        child.left();
                    } else {
                        child.right();
                    }
                    if (!visited.containsKey(gridToString(child))) {
                        int depth = currState.value - h1(currState) + 1;
                        child.parent = currState;
                        child.value = depth + h1(child);
                        pq.add(child);
                        nodes++;
                    }
                }
            }
        }
        
        if (nodes > maxNodes) {
            throw new OutOfMemoryError("Max node limit exceeded.");
        }

        // Extract path
        SlidePuzzle trav = null;
        if (visited.containsKey(key)) {
            trav = visited.get(key);
            int moveCount = 0;
            List<String> path = new LinkedList<>();
            while (trav != null) {
                path.add(trav.prevMove);
                moveCount++;
                trav = trav.parent;
            }
            path.remove(path.size()-1);
            Collections.reverse(path);
            System.out.println(path.toString());
            System.out.println("Nodes considered: " + nodes);
            return moveCount-1;
        }
        System.out.println("No path found.");
        return 0;
    }

    private int h1(SlidePuzzle state) {
        int misplaced = 0;
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != count++) {
                    misplaced++;
                }
            }
        }
        
        return misplaced;
    }

    /**
     * Create deep copy of current puzzle state
     * 
     * @return Deep copy of puzzle
     */
    private SlidePuzzle duplicate() {
        SlidePuzzle copy = new SlidePuzzle(m, n);
        int[][] copyGrid = new int[m][n];
        int i = 0;
        for (int[] row : grid) {
            int j = 0;
            for (int tile : row) {
                copyGrid[i][j++] = tile;
            }
            i++;
        }
        copy.grid = copyGrid;
        copy.c = c;
        copy.r = r;
        return copy;
    }

    /**
     * A* search using h2
     * 
     * @return Number of moves
     * @throws OutOfMemoryError Max node limit exceeded
     */
    private int solveH2() throws OutOfMemoryError {
        // min cost heap
        PriorityQueue<SlidePuzzle> pq = new PriorityQueue<>();

        // keep track of visited states
        Map<String, SlidePuzzle> visited = new HashMap<>();

        String key = getSolvedKey();

        // count of generated nodes
        int nodes = 0;

        // add initial state to pq
        pq.add(this);

        while (!pq.isEmpty() && !visited.containsKey(key) && nodes <= maxNodes) {
            SlidePuzzle currState = pq.poll();
            String gridString = gridToString(currState);
            if (!visited.containsKey(gridString)) {
                visited.put(gridString, currState);
                List<String> validMoves = currState.getValidMoves();
                for (String move : validMoves) {
                    // generate and add child state if not already visited
                    SlidePuzzle child = currState.duplicate();
                    if (move.equals("up")) {
                        child.up();
                    } else if (move.equals("down")) {
                        child.down();
                    } else if (move.equals("left")) {
                        child.left();
                    } else {
                        child.right();
                    }
                    if (!visited.containsKey(gridToString(child))) {
                        int depth = currState.value - h2(currState) + 1;
                        child.parent = currState;
                        child.value = depth + h2(child);
                        pq.add(child);
                        nodes++;
                    }
                }
            }
        }
        
        if (nodes > maxNodes) {
            throw new OutOfMemoryError("Max node limit exceeded.");
        }

        // Extract path
        SlidePuzzle trav = null;
        if (visited.containsKey(key)) {
            trav = visited.get(key);
            int moveCount = 0;
            List<String> path = new LinkedList<>();
            while (trav != null) {
                path.add(trav.prevMove);
                moveCount++;
                trav = trav.parent;
            }
            path.remove(path.size()-1);
            Collections.reverse(path);
            System.out.println(path.toString());
            System.out.println("Nodes considered: " + nodes);
            return moveCount-1;
        }
        System.out.println("No path found.");
        return 0;
    }

    /**
     * Heuristic function based on Manhattan distance of tiles to correct spot
     * 
     * @param state State of the board
     * @return Function value
     */
    private int h2(SlidePuzzle state) {
        int sum = 0;
        int[][] solved = makeGrid(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int[] ind = getIndices(solved[i][j]);
                sum += Math.abs(ind[0] - i) + Math.abs(ind[1] - j);
            }
        }
        return sum;
    }

    /**
     * Helper for h2, returns the indices of a specified value 
     * 
     * @param x value to find index of
     * @return int[2] with indices
     */
    private int[] getIndices(int x) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == x) {
                    int[] ind = new int[] {i, j};
                    return ind;
                }
            }
        }
        return null;
    }

    /**
     * Solves puzzle using beam search and prints the solution
     * This version of beam search uses h2
     * 
     * @param k Number of states to be considered at each iteration
     * @return Number of moves
     * @throws Exception
     */
    public int solveBeam(int k) throws Exception {
        if (k <= 0) {
            throw new IllegalArgumentException("Invalid input for k.");
        }

        // min cost heap storing the best k nodes
        PriorityQueue<SlidePuzzle> best = new PriorityQueue<>();

        // list of open nodes
        List<SlidePuzzle> frontier = new ArrayList<>();

        // keep track of visited states
        Map<String, SlidePuzzle> visited = new HashMap<>();

        String key = getSolvedKey();

        // count of generated nodes
        int nodes = 0;

        // flag for goal state
        boolean solved = false;

        // initial state is solved
        if (gridToString(this).equals(key)) {
            System.out.println("[]");
            System.out.println("Nodes considered: 1");
            System.out.println("Number of moves: 0");
            return 0;
        }

        frontier.add(this);

        while (!frontier.isEmpty() && !solved && nodes <= maxNodes) {
            for (SlidePuzzle currState : frontier) {
                String gridString = gridToString(currState);
                if (!visited.containsKey(gridString)) {
                    visited.put(gridString, currState);
                    List<String> validMoves = currState.getValidMoves();
                    // generate and add child states if not visited
                    for (String move : validMoves) {
                        SlidePuzzle child = currState.duplicate();
                        if (move.equals("up")) {
                            child.up();
                        } else if (move.equals("down")) {
                            child.down();
                        } else if (move.equals("left")) {
                            child.left();
                        } else {
                            child.right();
                        }
                        // using h2 for beam search
                        if (!visited.containsKey(gridToString(child))) {
                            child.parent = currState;
                            child.value = h2(child);
                            best.add(child);
                            nodes++;
                        }
                    }
                }
            }
            // reset list to add k best nodes back
            frontier.clear();

            // add k best children into consideration
            for (int i = 0; !best.isEmpty() && i < k; i++) {
                frontier.add(best.poll());
                if (gridToString(frontier.get(i)).equals(key)) {
                    solved = true;
                    visited.put(key, frontier.get(i));
                }
            }
            // reset priority queue
            best.clear();
        }

        if (nodes > maxNodes) {
            throw new OutOfMemoryError("Max node limit exceeded.");
        }

        // extract path
        SlidePuzzle trav = visited.get(key);
        if (trav != null) {
            List<String> path = new LinkedList<>();
            while (trav != null) {
                path.add(trav.prevMove);
                trav = trav.parent;
            }
            Collections.reverse(path);
            path.remove(0);
            System.out.println(path.toString());
            System.out.println("Nodes considered: " + nodes);
            System.out.println("Number of moves: " + path.size() + "\n");
            return path.size();
        }

        throw new Exception("No path found.");
    }

    /**
     * Returns the solved state as a string
     * 
     * @return Solved state string
     */
    private String getSolvedKey() {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < m*n; i++) {
            key.append(i);
            key.append(" ");
        }
        return key.toString().trim();
    }

    @Override
    public String toString() {
        StringBuilder printOut = new StringBuilder();
        for (int[] row : grid) {
            for (int tile : row) {
                printOut.append(tile + " ");
            }
            printOut.append("\n");
        }
        System.out.println(printOut.toString());
        return printOut.toString();
    }

    @Override
    public int compareTo(SlidePuzzle o) {
        return this.value - o.value;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please specify a file.");
            return;
        }
        File file = new File(args[0]);
        Scanner scan = new Scanner(file);
        SlidePuzzle p = new SlidePuzzle(3, 3);
        while (scan.hasNextLine()) {
            String command = scan.nextLine();
            String[] arguments = command.split(" ");
            if (arguments[0].equals("setSize")) {
                p = new SlidePuzzle(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]));
            } else if (arguments[0].equals("printState")) {
                p.toString();
            } else if (arguments[0].equals("move")) {
                if (arguments[1].equals("up")) {
                    if (!p.up()) {
                        System.out.println("Cannot move up.");
                    }
                } else if (arguments[1].equals("down")) {
                    if (!p.down()) {
                        System.out.println("Cannot move down.");
                    }
                } else if (arguments[1].equals("left")) {
                    if (!p.left()) {
                        System.out.println("Cannot move left.");
                    }
                } else if (arguments[1].equals("right")) {
                    if (!p.right()) {
                        System.out.println("Cannot move right.");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid direction.");
                }
            } else if (arguments[0].equals("randomizeState")) {
                if (arguments.length == 2) {
                    int n = Integer.parseInt(arguments[1]);
                    p.randomize(n);
                } else {
                    int n = Integer.parseInt(arguments[1]);
                    long seed = Long.parseLong(arguments[2]);
                    p.randomize(n, seed);
                }
            } else if (arguments[0].equals("solve")) {
                if (arguments[1].equals("A-star")) {
                    String heuristic = arguments[2];
                    p.solveAStar(heuristic);
                } else if (arguments[1].equals("beam")) {
                    int k = Integer.parseInt(arguments[2]);
                    p.solveBeam(k);
                } else {
                    throw new IllegalArgumentException("Invalid search method.");
                }
            } else if (arguments[0].equals("maxNodes")) {
                int n = Integer.parseInt(arguments[1]);
                setMaxNodes(n);
            } else if (command.isEmpty()) {
                continue;
            } else {
                throw new IllegalArgumentException("Command not recognized.");
            }
        }
        scan.close();
    }
}
