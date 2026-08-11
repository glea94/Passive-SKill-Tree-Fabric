package daripher.skilltree.util.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Remplace net.minecraftforge.registries.DeferredRegister, en couvrant les DEUX usages qu'en
 * fait le mod d'origine :
 * <p>
 * 1) Registries vanilla Minecraft (Items, Blocks, MobEffects, Potions, RecipeSerializers,
 *    RecipeTypes, MenuTypes, CreativeModeTab...) via {@link #create(Registry, String)} :
 *    l'enregistrement se fait immédiatement dans la Registry Minecraft fournie via
 *    Registry.register(), au lieu d'être différé sur un IEventBus Forge.
 * <p>
 * 2) Registries "maison" du mod (skill bonuses, predicates, float functions, requirements...)
 *    via {@link #create(Identifier, String)} : ce ne sont pas de vraies registries
 *    Minecraft (pas de synchronisation réseau, pas de tags, pas d'override par datapack côté
 *    Forge non plus) mais de simples annuaires Identifier -> Serializer utilisés pour
 *    désérialiser les fichiers JSON de l'arbre de compétences. Une table associative reproduit
 *    ce comportement à l'identique.
 * <p>
 * Dans les deux cas, l'ordre d'initialisation entre classes PSTxxx qui se référencent entre
 * elles (ex. PSTPotions -> PSTMobEffects) reste correct : Java garantit qu'une classe est
 * initialisée avant le premier accès à un de ses champs statiques.
 */
public class DeferredRegister<T> {
    private final Registry<T> backingRegistry; // null pour les registries "maison"
    private final String modId;
    private final Map<Identifier, T> byId = new LinkedHashMap<>();
    private final Map<T, Identifier> byValue = new IdentityHashMap<>();

    private DeferredRegister(Registry<T> backingRegistry, String modId) {
        this.backingRegistry = backingRegistry;
        this.modId = modId;
    }

    /** Registry vanilla Minecraft réelle (Items, Blocks, MobEffects, Potions...). */
    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>(registry, modId);
    }

    /** Registry "maison" du mod, sans registry Minecraft derrière (skill bonuses, predicates...). */
    public static <T> DeferredRegister<T> create(Identifier registryId, String modId) {
        return new DeferredRegister<>(null, modId);
    }

<<<<<<< Updated upstream
    public <I extends T> RegistryObject<I> register(String name, Supplier<I> supplier) {
        ResourceLocation id = new ResourceLocation(modId, name);
        I value = supplier.get();
=======
    private static final ThreadLocal<Identifier> CURRENT_ID = new ThreadLocal<>();

    /**
     * Id en cours d'enregistrement, disponible le temps de l'appel à supplier.get() dans
     * register(). Nécessaire depuis 1.21.5 : Item.Properties exige désormais que l'id soit
     * connu avant la construction de l'Item (Objects.requireNonNull(this.id, "Item id not set")).
     */
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
>>>>>>> Stashed changes
        if (backingRegistry != null) {
            value = Registry.register(backingRegistry, id, value);
        }
        byId.put(id, value);
        byValue.put(value, id);
        return RegistryObject.of(id, value);
    }

    /** No-op : conservé pour compatibilité de signature avec les appels Forge "REGISTRY.register(eventBus)". */
    public void register(Object eventBus) {
        // rien à faire : l'enregistrement se fait immédiatement dans register(name, supplier)
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
