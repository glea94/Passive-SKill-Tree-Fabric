package daripher.skilltree.util.event;
import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
public class PSTEventBus<T extends PSTEvent> {
    private final Map<EventPriority, List<Consumer<T>>> listenersByPriority = new EnumMap<>(EventPriority.class);
    public void register(EventPriority priority, Consumer<T> listener) {
        listenersByPriority.computeIfAbsent(priority, p -> new ArrayList<>()).add(listener);
    }
    public void register(Consumer<T> listener) {
        register(EventPriority.NORMAL, listener);
    }
    public T post(T event) {
        for (EventPriority priority : EventPriority.values()) {
            for (Consumer<T> listener : listenersByPriority.getOrDefault(priority, List.of())) {
                listener.accept(event);
            }
        }
        return event;
    }
}
