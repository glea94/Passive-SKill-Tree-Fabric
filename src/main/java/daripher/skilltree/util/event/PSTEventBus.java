package daripher.skilltree.util.event;

import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Remplace le mécanisme @SubscribeEvent(priority=...) + IEventBus de Forge pour tous les events
 * "maison" du mod (ceux sans équivalent direct dans Fabric API, ex. LivingHurtEvent, CriticalHitEvent,
 * ShieldBlockEvent...). Contrairement à Forge, chaque type d'event a son propre bus (voir PSTEvents),
 * pas un bus global filtré par type de paramètre - fonctionnellement identique, juste organisé
 * différemment.
 * <p>
 * L'ordre d'exécution (HIGHEST -> LOWEST) et l'annulation (event.setCanceled(true) qui arrête la
 * suite du traitement vanilla) sont reproduits à l'identique.
 */
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
