public class QueueUtils {

  static <T> Queue<T> copy(Queue<T> input) {
    Queue<T> result = new Queue<>();

    T first = input.front();

    do {
      T front = input.front();
      result.enqueue(front);
      input.enqueue(front);
    } while (input.front() != first);

    return result;
  }

  static <T> boolean includes(Queue<T> q, T v) {
    Queue<T> copy = QueueUtils.copy(q);

    while (!copy.isEmpty()) {
      T front = copy.front();
      copy.dequeue();

      if (front == v) {
        return true;
      }
    }

    return false;
  }

  static <T> int getSize(Queue<T> q) {
    Queue<T> copy = QueueUtils.copy(q);

    int i = 0;

    while (!copy.isEmpty()) {
      i++;
      copy.dequeue();
    }

    return i;
  }

}
