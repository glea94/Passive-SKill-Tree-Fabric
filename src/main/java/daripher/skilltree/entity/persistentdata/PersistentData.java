package daripher.skilltree.entity.persistentdata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class PersistentData implements IPersistentData {
    private CompoundTag tag = new CompoundTag();

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    // CORRECTION 1.21.1 : Cardinal Components API (ComponentV3 -> Component) attend maintenant
    // readFromNbt(CompoundTag, HolderLookup.Provider) / writeToNbt(CompoundTag, HolderLookup.Provider)
    // (le HolderLookup.Provider a été ajouté partout où du NBT est (dé)sérialisé, pour gérer les
    // registres dynamiques). Voir PlayerSkills.java qui utilise déjà cette signature.
    @Override
    public void readFromNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        tag = nbt.copy();
    }

    @Override
    public void writeToNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.merge(tag);
    }
}