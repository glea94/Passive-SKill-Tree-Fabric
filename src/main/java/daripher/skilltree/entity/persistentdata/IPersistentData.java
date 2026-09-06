package daripher.skilltree.entity.persistentdata;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public interface IPersistentData extends Component {
    CompoundTag getTag();
}
