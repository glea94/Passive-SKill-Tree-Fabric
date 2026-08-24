package daripher.skilltree.util.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;


public class RegistryObject<T> implements Supplier<T> {
    private final Identifier id;
    private final T value;

    private RegistryObject(Identifier id, T value) {
        this.id = id;
        this.value = value;
    }

    public static <T> RegistryObject<T> of(Identifier id, T value) {
        return new RegistryObject<>(id, value);
    }

    @Override
    public T get() {
        return value;
    }

    public Identifier getId() {
        return id;
    }

    public boolean isPresent() {
        return value != null;
    }
}
