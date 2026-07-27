package daripher.skilltree.entity.persistentdata;

import net.minecraft.nbt.CompoundTag;

public class PersistentData implements IPersistentData {
    private CompoundTag tag = new CompoundTag();

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        tag = nbt.copy();
    }

    @Override
    public void writeToNbt(CompoundTag nbt) {
        nbt.merge(tag);
    }
}
