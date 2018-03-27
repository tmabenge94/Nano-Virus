import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CellEngine implements IObserver {

    public static int initialCells = 100;


    public java.util.LinkedList<Cell> cells;
    public boolean loop = true;
    File newFile = new File("EngineState.txt");
    private ExecutorService threadExecutor;
    private int cycle = 0;
    private Random sRandom = new Random();
    private int lastCellIndex = 0;

    /**
     * Constructor
     */
    public CellEngine() {
        cells = new LinkedList<>(); //make it large to avoid capacity issues.
        threadExecutor = Executors.newCachedThreadPool();

        Cell cell;


        for (int i = 0; i < initialCells; i++) {

            if (i < (initialCells * 0.7))
                cell = new Cell(new RedBloodCell());
            else {
                if (i > (initialCells * 0.7) && i <= (initialCells * 0.95))
                    cell = new Cell(new WhiteBloodCell());
                else cell = new Cell(new TumorousCell());
            }

            cells.add(cell);
            threadExecutor.execute(cell);
        }


    } // End BouncingBalls constructor

    @Override
    public <E> void Update(E g2d) {
        if (Graphics2D.class.isInstance(g2d)) {
            for (Movable ball : cells) {
                ball.draw((Graphics2D) g2d);
            }

            for (int i = 0; i < cells.size(); i++) {
                Cell c = ((Cell) cells.toArray()[i]);
                for (int j = i + 1; j < cells.size(); j++) {
                    Cell d = (Cell) cells.toArray()[j];
                    if (c.checkCollisions(d)) {
                        c.doCollision(d);
                    }
                }
            }
        } else {
            if (loop) {
                int s = sRandom.nextInt(cells.size() - 1);
                Cycle(s);
                lastCellIndex = s;
            }
        }
    }

    private void Cycle(int s) {

        List<Cell> cells1 = cells.stream()
                .filter(c -> c.distanceBtwn(c, cells.get(lastCellIndex)) < 5000)
                .collect(Collectors.toList());

        if (cells1.size() > 0) {
            if (cells1.contains(cells.get(s)))
                if (cells.get(s).colorOfCell.getClass().getName().equals(TumorousCell.class.getName())) {
                    cells.remove(s);
                }
        }

        List<Cell> tumorousCells1 = cells.stream()
                .filter(cell -> cell.colorOfCell.getClass().getName().equals(TumorousCell.class.getName()))
                .collect(Collectors.toList());


        if (cycle == 5) {

            for (Cell cell : tumorousCells1) {
                List<Cell> redCells1 = cells.stream()
                        .filter(c -> c.colorOfCell.getClass().getName().equals(RedBloodCell.class.getName()) && c.distanceBtwn(c, cell) < 5000)
                        .collect(Collectors.toList());

                if (redCells1.size() > 0) {
                    for (Cell rCell : redCells1) {
                        rCell.setColorOfCell(new TumorousCell());
                        cells.set(cells.indexOf(rCell), rCell);
                    }
                } else {

                    List<Cell> redCells2 = cells.stream()
                            .filter(c -> c.colorOfCell.getClass().getName().equals(RedBloodCell.class.getName()))
                            .collect(Collectors.toList());

                    if (!(redCells2.size() > 0)) {

                        List<Cell> whiteCells1 = cells.stream()
                                .filter(c -> c.colorOfCell.getClass().getName().equals(WhiteBloodCell.class.getName()) && c.distanceBtwn(c, cell) < 5000)
                                .collect(Collectors.toList());
                        if (whiteCells1.size() > 0) {
                            for (Cell wCell : whiteCells1) {
                                wCell.setColorOfCell(new TumorousCell());
                                cells.set(cells.indexOf(wCell), wCell);
                            }
                        }
                    }
                }

            }
            cycle = 0;
        }

        cycle++;

        if (tumorousCells1.size() == cells.size() || tumorousCells1.size() == 0) {
            loop = false;
        }

        try {
            writeEngineState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEngineState() throws IOException {

        PrintStream ps = new PrintStream(newFile);
        ps.println("Cell:" + cells.size());

    }


}
