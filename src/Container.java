import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Container extends JPanel implements ISubject {

    public static Dimension DIM = new Dimension(1100, 600);
    ArrayList<IObserver> Observers = new ArrayList<>();

    @Override
    public Dimension getPreferredSize() {
        return DIM;
    } // End Overloaded JPanel Dimension

    @Override
    public void Attach(IObserver Target) {
        Observers.add(Target);
    }

    @Override
    public void Detach(IObserver Target) {
        Observers.remove(Target);
    }

    @Override
    public <E> void Notify(E e) {
        for (IObserver observer : Observers) {
            observer.Update(e);
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2d = (Graphics2D) g;
        Notify(g2d);
    }
}
