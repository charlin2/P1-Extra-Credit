# Variable Dimension Slide Puzzle Solver

An m\*n puzzle solver in Java.  This project can solve an m\*n puzzle using either A\* search or local beam search.

### How to use
The entry point of this program is through the *SlidePuzzle.java* file.  Please specify **one** .txt file as an argument.

The .txt file should have one command and its respective arguments per line. A list of commands is specified below:

- setSize \<m> \<n> - Initializes a solved puzzle of size *m\*n*.
- printState - Prints the state of the board.
- move \<direction> - *direction* is either "up", "down", "left", or "right". Moves the blank tile in the specified direction.
- randomize \<n> - Performs *n* random moves from the solve state.
- solve A-star <heuristic> - *heuristic* is either "h1" or "h2". Solves the puzzle using A* and prints the solution.
- solve beam <k> - *k* is the number of states for beam search to store at each iteration.
- maxNodes <n> - *n* is the max number of nodes to be considered during the duration of a search.


