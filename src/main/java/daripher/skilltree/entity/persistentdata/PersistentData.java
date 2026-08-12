package daripher.skilltree.entity.persistentdata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PersistentData implements IPersistentData {
    private CompoundTag tag = new CompoundTag();

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    @Override
<<<<<<< Updated upstream
<<<<<<< Updated upstream
    public void readFromNbt(CompoundTag nbt) {
        tag = nbt.copy();
    }

    @Override
    public void writeToNbt(CompoundTag nbt) {
        nbt.merge(tag);
=======
    public void readData(ValueInput readView) {
        this.tag = readView.read("Data", CompoundTag.CODEC).orElseGet(CompoundTag::new);
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.store("Data", CompoundTag.CODEC, this.tag);
>>>>>>> Stashed changes
    }
=======
    public void readData(ValueInput readView) {
        this.tag = readView.read("Data", CompoundTag.CODEC).orElseGet(CompoundTag::new);
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.store("Data", CompoundTag.CODEC, this.tag);
    }
>>>>>>> Stashed changes
}