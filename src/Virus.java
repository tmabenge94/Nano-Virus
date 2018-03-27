import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Virus implements ISubject {

    Timer timer = new Timer();
    private ArrayList<IObserver> Observers = new ArrayList<>();

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


    public void run() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Notify(0);
            }
        };

        timer.schedule(task, 1, 2000);
    }

    public void stop() {
        timer.cancel();
    }
}
