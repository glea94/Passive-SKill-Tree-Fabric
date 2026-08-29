package daripher.skilltree.entity.persistentdata;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;
public interface IPersistentData extends ComponentV3 {
    CompoundTag getTag();
}
