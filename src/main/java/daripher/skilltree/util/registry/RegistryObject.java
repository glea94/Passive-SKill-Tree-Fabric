package daripher.skilltree.util.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * Remplace net.minecraftforge.registries.RegistryObject.
 * Contrairement à la version Forge, la valeur est déjà connue au moment de la création
 * (l'enregistrement Fabric est immédiat, pas différé sur un event bus), mais l'API .get()
 * est conservée à l'identique pour que tous les appels existants dans le mod continuent
 * de compiler sans modification.
 */
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
