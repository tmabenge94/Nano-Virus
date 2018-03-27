public interface ISubject {
    void Attach(IObserver Target);

    void Detach(IObserver Target);

    <E> void Notify(E e);
}
