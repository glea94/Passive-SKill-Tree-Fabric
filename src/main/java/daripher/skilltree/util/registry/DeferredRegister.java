package daripher.skilltree.util.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;


public class DeferredRegister<T> {
    private final Registry<T> backingRegistry; 
    private final String modId;
    private final Map<Identifier, T> byId = new LinkedHashMap<>();
    private final Map<T, Identifier> byValue = new IdentityHashMap<>();

    private DeferredRegister(Registry<T> backingRegistry, String modId) {
        this.backingRegistry = backingRegistry;
        this.modId = modId;
    }

    
    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>(registry, modId);
    }

    
    public static <T> DeferredRegister<T> create(Identifier registryId, String modId) {
        return new DeferredRegister<>(null, modId);
    }

    private static final ThreadLocal<Identifier> CURRENT_ID = new ThreadLocal<>();

    
    public static Identifier currentId() {
        return CURRENT_ID.get();
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<I> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(modId, name);
        Identifier previousId = CURRENT_ID.get();
        CURRENT_ID.set(id);
        I value;
        try {
            value = supplier.get();
        } finally {
            if (previousId != null) {
                CURRENT_ID.set(previousId);
            } else {
                CURRENT_ID.remove();
            }
        }
        if (backingRegistry != null) {
            value = Registry.register(backingRegistry, id, value);
        }
        byId.put(id, value);
        byValue.put(value, id);
        return RegistryObject.of(id, value);
    }
<<<<<<< Updated upstream

    
    public void register(Object eventBus) {
        
=======
    public void register(Object eventBus) {
>>>>>>> Stashed changes
    }

    public Collection<RegistryObject<? extends T>> getEntries() {
        return byId.entrySet().stream()
                .<RegistryObject<? extends T>>map(e -> RegistryObject.of(e.getKey(), e.getValue()))
                .toList();
    }

    public T getValue(Identifier id) {
        return byId.get(id);
    }

    public Collection<T> getValues() {
        return byId.values();
    }

    public Identifier getKey(T value) {
        return byValue.get(value);
    }
}