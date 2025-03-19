import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;
// کلاس مربوط به صفحه بازی
        class Board {
            private char[][] grid;
            private boolean[][] hits;
            private char[][] trackingGrid;
            private int size;
            private int shipCount;

            public Board(int size) {
                this.size = size;
                this.grid = new char[size][size];
                this.hits = new boolean[size][size];
        this.trackingGrid = new char[size][size];
        this.shipCount = 14;
        for (char[] row : grid) Arrays.fill(row, '~');
        for (char[] row : trackingGrid) Arrays.fill(row, '~');
    }

    public boolean placeShip(Ship ship, int row, int col, boolean horizontal) {
        if (!isValidPlacement(ship, row, col, horizontal)) {
            System.out.println("Invalid ship placement. Try again.");
            return false;
        }

        for (int i = 0; i < ship.getSize(); i++) {
            if (horizontal) grid[row][col + i] = 'S';
            else grid[row + i][col] = 'S';
        }

        return true;
    }

    private boolean isValidPlacement(Ship ship, int row, int col, boolean horizontal) {
        if (horizontal && col + ship.getSize() > size) return false;
        if (!horizontal && row + ship.getSize() > size) return false;

        for (int i = 0; i < ship.getSize(); i++) {
            if (horizontal && grid[row][col + i] != '~') return false;
            if (!horizontal && grid[row + i][col] != '~') return false;
        }
        return true;
    }

    public boolean attack(int row, int col) {
        if (hits[row][col]) {
            System.out.println("Already attacked this position. Try again.");
            return false;
        }
        hits[row][col] = true;
        if (grid[row][col] == 'S') {
            grid[row][col] = 'X';
            trackingGrid[row][col] = 'X';
            System.out.println("Hit!");
            shipCount--;
            return true;
        } else {
            grid[row][col] = 'O';
            trackingGrid[row][col] = 'O';
            System.out.println("Miss!");
            return false;
        }
    }

    public boolean allShipsSunk() {
        return shipCount == 0;
    }

    public void display() {
        System.out.println("  A B C D E F G H I J");
        for (int i = 0; i < size; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void displayTrackingGrid() {
        System.out.println("Tracking Grid:");
        System.out.println("  A B C D E F G H I J");
        for (int i = 0; i < size; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < size; j++) {
                System.out.print(trackingGrid[i][j] + " ");
            }
            System.out.println();
        }
    }

}

// کلاس کشتی
class Ship {
    private int size;

    public Ship(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}

// کلاس پلیر
class Player {
    private Board board;
    private String name;

    public Player(String name, int boardSize) {
        this.name = name;
        this.board = new Board(boardSize);
    }

    public String getName() {
        return name;
    }

    public void placeShips() {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        int[] shipSizes = {5, 4, 3, 2};

        for (int size : shipSizes) {
            Ship ship = new Ship(size);
            boolean placed = false;
            while (!placed) {
                System.out.println(name + "! Place your ship of size " + size);
                System.out.println("Do you want to place it manually? (yes/no)");
                String choice = scanner.next();

                if (choice.equalsIgnoreCase("no")) {
                    int row, col;
                    boolean horizontal;
                    do {
                        row = random.nextInt(10);
                        col = random.nextInt(10);
                        horizontal = random.nextBoolean();
                    } while (!board.placeShip(ship, row, col, horizontal));
                    placed = true;
                } else {
                    System.out.println("Enter row (0-9):");
                    int row = -1;
                    while (true) {
                        if (scanner.hasNextInt()) {
                            row = scanner.nextInt();
                            if (row >= 0 && row <= 9) {
                                break;
                            } else {
                                System.out.println("Invalid input. Enter a number between 0 and 9:");
                            }
                        } else {
                            System.out.println("Invalid input. Enter a number between 0 and 9:");
                            scanner.next();
                        }
                    }


                    System.out.println("Enterd column (A-J):");
                    String colInput = scanner.next().toUpperCase();
                    while (colInput.length() != 1 || colInput.charAt(0) < 'A' || colInput.charAt(0) > 'J') {
                        System.out.println("Invalid column input. Enter a letter between A and J:");
                        colInput = scanner.next().toUpperCase();
                    }
                    int col = colInput.charAt(0) - 'A';
                    System.out.println("Horizontal? (yes/no)");
                    boolean horizontal = scanner.next().equalsIgnoreCase("yes");

                    if (board.placeShip(ship, row, col, horizontal)) {
                        placed = true;
                    } else {
                        System.out.println("Invalid placement! Try again.");
                    }
                }
            }
            board.display();
        }
    }

    public boolean attack(Player opponent) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(name + "! Enter attack coordinates (A5):");
        String input = scanner.next().toUpperCase();

        while (input.length() != 2 ||  input.charAt(0) < 'A' || input.charAt(0) > 'J' || input.charAt(1) < '0' || input.charAt(1) > '9') {
            System.out.println("Invalid coordinates. Try again.");
            input = scanner.next().toUpperCase();
        }
            int col = input.charAt(0) - 'A';
            int row = input.charAt(1) - '0';

        opponent.board.attack(row,col);
        opponent.board.displayTrackingGrid();
        return opponent.board.allShipsSunk();
    }
}

// کلاس اصلی بازی
public class BattleShipGame {
    public static void main(String[] args) {
        System.out.println("Welcome to Battleship!");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Player 1 name:");
        Player player1 = new Player(scanner.next(), 10);
        player1.placeShips();

        System.out.println("Enter Player 2 name:");
        Player player2 = new Player(scanner.next(), 10);
        player2.placeShips();

        System.out.println("Both players have placed their ships! Game starts now.");

        boolean gameOver = false;
        while (!gameOver) {
            gameOver = player1.attack(player2);
            if (gameOver) {
                System.out.println(player1.getName() + " wins!");
                break;
            }
            gameOver = player2.attack(player1);
            if (gameOver) {
                System.out.println(player2.getName() + " wins!");
                break;
            }
        }
    }
}