import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * This is the Cell class. It implements Runnable and also contains methods used for collision detection.
 */
public class Cell implements Runnable, Movable {
    public static Random sRandom = new Random();
    public ICellType colorOfCell; // Store randomized color of cell
    /**
     * Instance variables
     */
    private Ellipse2D.Double cell; // Cell objects shape attributes
    private Point2D.Double cellCoords; // Store location coords of a Cell
    private Point2D.Double cellVelocity; // Store x and y velocity of Cell
    private double mass;
    private double angle;
    private double radiux;

    /**
     * Constructor
     *
     * @param cellColor
     */
    public Cell(ICellType cellColor) {
        cellCoords = new Point2D.Double(sRandom.nextInt(800), sRandom.nextInt(400));
        radiux = sRandom.nextDouble() * 20 + 15;
        angle = sRandom.nextInt(361) * (180 / Math.PI);

        switch (sRandom.nextInt(4)) {
            case 0:
                cellVelocity = new Point2D.Double(sRandom.nextInt(5) + 1, sRandom.nextInt(5) + 1);
                break;

            case 1:
                cellVelocity = new Point2D.Double(-sRandom.nextInt(5) + 1, sRandom.nextInt(5) + 1);
                break;

            case 2:
                cellVelocity = new Point2D.Double(sRandom.nextInt(5) + 1, -sRandom.nextInt(5) + 1);
                break;

            case 3:
                cellVelocity = new Point2D.Double(-sRandom.nextInt(5) + 1, -sRandom.nextInt(5) + 1);
                break;
        }

        mass = radiux * radiux * radiux;
        // 4/3*pi is a constant. So the only thing that affects volume is the radius.
        // And since everything has the same density, volume and mass are directly correlated.
        // Hence, for simplicity, I set mass = r^3.
        colorOfCell = cellColor;
        setCellPosition(cellCoords, radiux);
    } // End Cell constructor

    // Update cell position
    public void run() {
        while (true) {
            move();

            try {
                Thread.sleep(20);
            } catch (Exception e) {
            }
        }
    } // End run() method

    // private method to set initial cell object
    private void setCellPosition(Point2D loc, double radiux) {
        cell = new Ellipse2D.Double(loc.getX(), loc.getY(), radiux, radiux);
    } // End setCellPosition() mutator

    // private overloaded method to set new cell values
    private void setCellPosition(Point2D loc) {
        cell.setFrame(loc, new Dimension2D() {
            @Override
            public double getWidth() {
                return radiux * 2;
            }

            @Override
            public double getHeight() {
                return radiux * 2;
            }

            @Override
            public void setSize(double width, double height) {

            }
        });
    }

    // public method to return coordinates of cell object
    public Point2D getCellCoords() {
        return cellCoords;
    } // End getCellCoords() accessor

    public void setColorOfCell(ICellType color) {
        this.colorOfCell = color;
    }

    // public method to return color of cell object
    public Color getCellColor() {
        return this.colorOfCell.Type();
    } // End getCellColor() accessor

    // Getters and setters for radius
    public double getRadiux() {
        return radiux;
    }

    public void setRadiux(double radiux) {
        this.radiux = radiux;
    }

    public double getCellVelocityX() {
        return cellVelocity.getX();
    }

    public double getCellVelocityY() {
        return cellVelocity.getY();
    }


    public void setCellVelocity(double x, double y) {
        cellVelocity = new Point2D.Double(x, y);
    }

    /**
     * Moves the cell. This function also contains the boundary setting, sot hat the cell bounces off the "walls".
     */
    public void move() {
        double newX = cellCoords.getX() + cellVelocity.getX();
        double newY = cellCoords.getY() + cellVelocity.getY();

        // If the cell is too far to the right and moving to the right
        if (newX >= Container.DIM.getWidth() - 2 * radiux && cellVelocity.getX() > 0) {
            setCellVelocity(-cellVelocity.getX(), cellVelocity.getY());
        }
        // If the cell is too far to the left and moving to the left
        else if (newX <= 0 && cellVelocity.getX() < 0) {
            setCellVelocity(-cellVelocity.getX(), cellVelocity.getY());
        }
        // If the cell is too low and moving downward
        else if (newY >= Container.DIM.getHeight() - 2 * radiux && cellVelocity.getY() > 0) {
            setCellVelocity(cellVelocity.getX(), -cellVelocity.getY());
        }
        // If the cell is too high and moving upward
        else if (newY <= 0 && cellVelocity.getY() < 0) {
            setCellVelocity(cellVelocity.getX(), -cellVelocity.getY());
        }
        cellCoords.setLocation(cellCoords.getX() + cellVelocity.getX(), cellCoords.getY() + cellVelocity.getY());
        // Set new coordinates of cell
        setCellPosition(cellCoords);
    }

    /**
     * Draws the screen
     *
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        g2d.setPaint(this.getCellColor());
        g2d.fillOval((int) this.getCellCoords().getX(), (int) this.getCellCoords().getY(), (int) this.getRadiux() * 2, (int) this.getRadiux() * 2);
    }

    /**
     * Collision checker
     *
     * @param cell
     * @return
     */
    public boolean checkCollisions(Cell cell) {
        if (cell != this) // It cannot collide with itself
        {
            // Checks if the rectangular bounds of each cell are overlapping, which indicates they are within range.
            if (cellCoords.getX() + radiux + cell.radiux > cell.cellCoords.getX()
                    && cellCoords.getX() < cell.cellCoords.getX() + radiux + cell.radiux
                    && cellCoords.getY() + radiux + cell.radiux > cell.cellCoords.getY()
                    && cellCoords.getY() < cell.cellCoords.getY() + radiux + cell.radiux) {
                if (distanceBtwn(this, cell) < (radiux + cell.radiux) * (radiux + cell.radiux)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks the distance between the center of two spheres with different radii
     *
     * @param cellA
     * @param cellB
     * @return
     */
    public double distanceBtwn(Cell cellA, Cell cellB) {
        double aX = cellA.cellCoords.getX();
        double aY = cellA.cellCoords.getY();
        double bX = cellB.cellCoords.getX();
        double bY = cellB.cellCoords.getY();

        double xDist = aX - bX;
        double yDist = aY - bY;

        double distanceSq = (xDist * xDist) + (yDist * yDist); // To avoid using square root
        return distanceSq;
    }

    /**
     * Performs the collision.
     *
     * @param cell
     */
    public void doCollision(Cell cell) {
//        System.out.println("Collided!");
        getNewVelocities(this, cell); //Calculate new velocities
    }

    /**
     * New velocites after two cells bounce off each other
     *
     * @param cellA
     * @param cellB
     */
    private void getNewVelocities(Cell cellA, Cell cellB) {
        double aMass = cellA.mass;
        double bMass = cellB.mass;
        double aVx = cellA.cellVelocity.getX();
        double bVx = cellB.cellVelocity.getX();
        double aVy = cellA.cellVelocity.getY();
        double bVy = cellB.cellVelocity.getY();
        double aX = cellA.cellCoords.getX();
        double aY = cellA.cellCoords.getY();
        double bX = cellB.cellCoords.getX();
        double bY = cellB.cellCoords.getY();

        double xDist = aX - bX;
        double yDist = aY - bY;

        double xVel = bVx - aVx;
        double yVel = bVy - aVy;
        double dotProd = (xDist * xVel) + (yDist * yVel);

        // Check if objects are moving towards one another
        // Inspiration/source from: http://gamedev.stackexchange.com/questions/20516/ball-collisions-sticking-together
        if (dotProd > 0) {
            double collScale = dotProd / distanceBtwn(cellA, cellB);
            double xColl = xDist * collScale;
            double yColl = yDist * collScale;
            double totalMass = cellA.mass + cellB.mass;
            double collWeightA = (2 * cellB.mass) / totalMass;
            double collWeightB = (2 * cellA.mass) / totalMass;

            double newaVx = cellA.getCellVelocityX() + (collWeightA * xColl);
            double newaVy = cellA.getCellVelocityY() + (collWeightA * yColl);
            double newbVx = cellB.getCellVelocityX() - (collWeightB * xColl);
            double newbVy = cellB.getCellVelocityY() - (collWeightB * yColl);

            cellA.setCellVelocity(newaVx, newaVy);
            cellB.setCellVelocity(newbVx, newbVy);

            cellA.cellCoords.setLocation((cellA.cellCoords.getX() + newaVx), (cellA.cellCoords.getY() + newaVy));
            cellB.cellCoords.setLocation((cellB.cellCoords.getX() + newbVx), (cellB.cellCoords.getY() + newbVy));

        }
        // Calculate new velocities based on elastic collision formula: https://en.wikipedia.org/wiki/Elastic_collision
        // See the two-dimensional section


    }

} //
