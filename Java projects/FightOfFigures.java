import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FightOfFigures {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PlayingSystem sys = new PlayingSystem(in);
        int P = Integer.parseInt(in.nextLine());
        for (int i  = 0; i < P; i++) {
            String[] command = in.nextLine().split(" ");
            sys.processCommand(command);
        }
        sys.printWinner();
    }
}

//Exceptions
class InvalidActionException extends Exception {
    public String getMessage() {
        return "INVALID ACTION";
    }
}

//System
/*PlayingSystem is a Facade that unites functionality
 of other program classes providing more convenient interaction.*/
class PlayingSystem {
    private ArrayList<Coin> coins;
    private OriginalFigure green;
    private OriginalFigure red;
    private ClonedFigure greenClone;
    private ClonedFigure redClone;
    private int greenScore;
    private int redScore;

    /*I pass instance of Scanner to PlayingSystem constructor,
    * and scan all the input (except commands) right here.*/
    public PlayingSystem(Scanner in) {
        int n = Integer.parseInt(in.nextLine());
        Board.initialize(n);
        this.coins = new ArrayList<>();
        this.greenScore = 0;
        this.redScore = 0;

        int x = in.nextInt();
        int y = in.nextInt();
        in.nextLine();
        this.green = new OriginalFigure(x, y, Color.GREEN);
        Board.getInstance().placeItem(x, y, this.green);

        x = in.nextInt();
        y = in.nextInt();
        in.nextLine();
        this.red = new OriginalFigure(x, y, Color.RED);
        Board.getInstance().placeItem(x, y, this.red);

        int M = Integer.parseInt(in.nextLine());
        int v;
        for (int i = 0; i < M; i++) {
            x = in.nextInt();
            y = in.nextInt();
            v = in.nextInt();
            in.nextLine();
            Coin newCoin = new Coin(x, y, v);
            this.coins.add(newCoin);
            Board.getInstance().placeItem(x, y, newCoin);
        }

        this.greenClone = null;
        this.redClone = null;
    }

    public void processCommand(String[] command) {
        try {
            switch (command[1]) {
                case "UP":
                    this.move(command[0], Direction.UP);
                    break;
                case "DOWN":
                    this.move(command[0], Direction.DOWN);
                    break;
                case "LEFT":
                    this.move(command[0], Direction.LEFT);
                    break;
                case "RIGHT":
                    this.move(command[0], Direction.RIGHT);
                    break;
                case "COPY":
                    this.copy(command[0]);
                    break;
                case "STYLE":
                    this.changeStyle(command[0]);
                    break;
            }
        } catch(InvalidActionException e) {
            System.out.println(e.getMessage());
        }
    }

    public void move(String fig, Direction direction) throws InvalidActionException {
        Figure curFig = this.findFigure(fig);
        String extraMessage = curFig.move(direction);
        increaseScore(curFig.getColor(), curFig.getPointsCollectedForMove());
        curFig.setPointsAmount(0);
        System.out.println(fig + " MOVED TO " + curFig.getX() + " " + curFig.getY() + extraMessage);
    }

    public void copy(String fig) throws InvalidActionException {
        Figure curFig = this.findFigure(fig);
        ClonedFigure clone = curFig.copy();
        if (clone.getColor() == Color.GREEN) {
            this.greenClone = clone;
        } else {
            this.redClone = clone;
        }
        Board.getInstance().placeItem(clone.getX(), clone.getY(), clone);
        System.out.println(curFig.getName() + " CLONED TO " + clone.getX() + " " + clone.getY());
    }

    public void changeStyle(String fig) throws InvalidActionException {
        Figure curFig = this.findFigure(fig);
        if (curFig.changeStyle()) {
            System.out.println(curFig.getName() + " CHANGED STYLE TO ATTACKING");
        } else {
            System.out.println(curFig.getName() + " CHANGED STYLE TO NORMAL");
        }
    }

    public void printWinner() {
        if (this.greenScore > this.redScore) {
            System.out.println("GREEN TEAM WINS. SCORE " + greenScore + " " + redScore);
        } else if (this.greenScore < this.redScore) {
            System.out.println("RED TEAM WINS. SCORE " + greenScore + " " + redScore);
        } else {
            System.out.println("TIE. SCORE " + greenScore + " " + redScore);
        }
    }

    private Figure findFigure(String fig) throws InvalidActionException {
        switch(fig) {
            case "GREEN":
                return this.green;
            case "RED":
                return this.red;
            case "GREENCLONE":
                if (this.greenClone != null) {
                    return this.greenClone;
                } else {
                    throw new InvalidActionException();
                }
            case "REDCLONE":
                if (this.redClone != null) {
                    return this.redClone;
                } else {
                    throw new InvalidActionException();
                }
            default:
                throw new InvalidActionException();
        }
    }

    private void increaseScore(Color color, int value) {
        if (color == Color.GREEN) {
            this.greenScore += value;
        } else {
            this.redScore += value;
        }
    }
}

//Board
/*Class board represents NxN board used in game. I implemented singleton
* in this class to ensure uniqueness of Board object. Board has methods
* initialize(int n) to create single instance of class and
* getInstance() to access this instance.*/
class Board {
    private FieldItem[][] board;
    static private Board instance;

    private Board(int n) {
        board = new FieldItem[n][n];
    }

    static public void initialize(int n) {
        if (instance == null) {
            instance = new Board(n);
        }
    }

    static public Board getInstance() {
        return instance;
    }

    public void placeItem(int x, int y, FieldItem item) {
        this.board[x-1][y-1] = item;
    }

    public FieldItem getItemFromField(int x, int y) {
        return this.board[x-1][y-1];
    }

    public void removeItem(int x, int y) {
        this.board[x-1][y-1] = null;
    }

    public int getDimension() {
        return this.board.length;
    }
}

//Items
/*Both figures and coins implement FieldItem interface. It provides
* an opportunity to store them in the same array due to polymorphism.*/
abstract class FieldItem {
    protected int x;
    protected int y;

    protected FieldItem(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected int getX() {
       return this.x;
    }

    protected int getY() {
        return this.y;
    }

    protected void setX(int newX) {
        this.x = newX;
    }

    protected void setY(int newY) {
        this.y = newY;
    }
}

class Coin extends FieldItem{
    private int value;

    public Coin(int x, int y, int val) {
        super(x, y);
        this.value = val;
    }

    public int getValue() {
        return value;
    }
}

/*Abstract class Figure represents features common for all figures.
* Here I implemented pattern State (playing style).*/
abstract class Figure extends FieldItem {
    private Color color;
    private PlayingStyle style;
    private int pointsCollectedForMove;
    private boolean isDead;
    protected String name;


    public Figure(int x, int y, Color color) {
        super(x, y);
        this.color = color;
        this.isDead = false;
        this.style = new NormalStyle(this);
        this.pointsCollectedForMove = 0;
    }

    public String move(Direction direction) throws InvalidActionException {
        if (isDead) {
            throw new InvalidActionException();
        } else {
            return this.style.move(direction);
        }
    }

    public boolean changeStyle() throws InvalidActionException{
        if (this.isDead) {
            throw new InvalidActionException();
        }
        if (this.style instanceof NormalStyle) {
            this.style = new AttackingStyle(this);
            return true;
        } else {
            this.style = new NormalStyle(this);
            return false;
        }
    }

    public abstract ClonedFigure copy() throws InvalidActionException;

    public void kill(Figure victim) {
        victim.die();
    }

    public void die() {
        this.isDead = true;
    }

    public Color getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDead() {
        return this.isDead;
    }

    /*Before increasing team score, I temporarily store
     the value of coin collected by figure as a field inside of it.
     Here are getter and setter for this field.*/
    public int getPointsCollectedForMove() {
        return pointsCollectedForMove;
    }

    public void setPointsAmount(int points) {
        this.pointsCollectedForMove = points;
    }
}

class OriginalFigure extends Figure {
    private boolean wasCloned;

    public OriginalFigure(int x, int y, Color color) {
        super(x, y, color);
        this.wasCloned = false;
        if (color == Color.GREEN) {
            this.name = "GREEN";
        } else {
            this.name = "RED";
        }
    }

    public ClonedFigure copy() throws InvalidActionException {
        if (this.isDead() || this.wasCloned || this.getX() == this.getY()) {
            throw new InvalidActionException();
        } else {
            Board board = Board.getInstance();
            FieldItem fieldCloneTo = board.getItemFromField(this.getY(), this.getX());
            if (fieldCloneTo != null) {
                throw new InvalidActionException();
            } else {
                ClonedFigure clone = new ClonedFigure(this.getY(), this.getX(), this.getColor());
                this.wasCloned = true;
                return clone;
            }
        }
    }
}

class ClonedFigure extends Figure {

    public ClonedFigure(int x, int y, Color color) {
        super(x, y, color);
        if (color == Color.GREEN) {
            this.name = "GREENCLONE";
        } else {
            this.name = "REDCLONE";
        }
    }

    /*Call of copy method for a clone instantly throws an exception,
    * as clones cannot be cloned.*/
    public ClonedFigure copy() throws InvalidActionException {
        throw new InvalidActionException();
    }
}

//Colors
enum Color {
    RED,
    GREEN;
}

//Direction
enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;
}

//Playing styles
/*Both NormalStyle and AttackingStyle implement interface PlayingStyle
* to make State implementation possible.*/
interface PlayingStyle {
    String move(Direction direction) throws InvalidActionException;

    /*Logic for collecting coins and killing enemy figures is same for both playing styles.*/
    default String collectOrKill(Figure fig, int newX, int newY) throws InvalidActionException {
        FieldItem itemInField = Board.getInstance().getItemFromField(newX, newY);
        if (itemInField instanceof Figure && fig.getColor() == ((Figure)itemInField).getColor()) {
            throw new InvalidActionException();
        }
        if (itemInField == null) {
            return "";
        } else if (itemInField instanceof Coin) {
            Coin coinInField = (Coin)itemInField;
            int coinValue = coinInField.getValue();
            fig.setPointsAmount(coinValue);
            Board.getInstance().removeItem(coinInField.getX(), coinInField.getY());
            return (" AND COLLECTED " + coinValue);
        } else {
            Figure figureInField = (Figure)itemInField;
            fig.kill(figureInField);
            Board.getInstance().removeItem(figureInField.getX(), figureInField.getY());
            return (" AND KILLED " + figureInField.getName());
        }
    }
}

class NormalStyle implements PlayingStyle {
    private Figure fig;

    public NormalStyle(Figure fig) {
        this.fig = fig;
    }

    public String move(Direction direction) throws InvalidActionException {
        int newX, newY;
        Board board = Board.getInstance();
        switch(direction) {
            case UP:
                newX = fig.getX() - 1;
                newY = fig.getY();
                break;
            case DOWN:
                newX = fig.getX() + 1;
                newY = fig.getY();
                break;
            case LEFT:
                newX = fig.getX();
                newY = fig.getY() - 1;
                break;
            case RIGHT:
                newX = fig.getX();
                newY = fig.getY() + 1;
                break;
            default:
                throw new InvalidActionException();
        }

        if (newX < 1 || newX > board.getDimension() ||
            newY < 1 || newY > board.getDimension()) {
            throw new InvalidActionException();
        }

        String extraMessage = collectOrKill(fig, newX, newY);
        board.removeItem(fig.getX(), fig.getY());
        fig.setX(newX);
        fig.setY(newY);
        board.placeItem(fig.getX(), fig.getY(), fig);
        return extraMessage;
    }
}

class AttackingStyle implements PlayingStyle {
    private Figure fig;

    public AttackingStyle(Figure fig) {
        this.fig = fig;
    }

    @Override
    public String move(Direction direction) throws InvalidActionException {
        Board board = Board.getInstance();
        int newX, newY;
        switch(direction) {
            case UP:
                newX = fig.getX() - 2;
                newY = fig.getY();
                break;
            case DOWN:
                newX = fig.getX() + 2;
                newY = fig.getY();
                break;
            case LEFT:
                newX = fig.getX();
                newY = fig.getY() - 2;
                break;
            case RIGHT:
                newX = fig.getX();
                newY = fig.getY() + 2;
                break;
            default:
                throw new InvalidActionException();
        }

        if (newX < 1 || newX > board.getDimension() ||
                newY < 1 || newY > board.getDimension()) {
            throw new InvalidActionException();
        }

        String extraMessage = collectOrKill(fig, newX, newY);
        board.removeItem(fig.getX(), fig.getY());
        fig.setX(newX);
        fig.setY(newY);
        board.placeItem(fig.getX(), fig.getY(), fig);
        return extraMessage;
    }
}