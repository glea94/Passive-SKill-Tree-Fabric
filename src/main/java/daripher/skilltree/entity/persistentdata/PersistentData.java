package daripher.skilltree.entity.persistentdata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class PersistentData implements IPersistentData {
    private CompoundTag tag = new CompoundTag();

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    // Alignment 1.21.4: Retains mandatory registryLookup context for dynamic data pack mappings
    @Override
    public void readFromNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        this.tag = nbt.copy();
    }

    @Override
    public void writeToNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.merge(this.tag);
    }
}
