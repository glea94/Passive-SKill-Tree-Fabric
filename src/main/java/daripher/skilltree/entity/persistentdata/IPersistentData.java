package daripher.skilltree.entity.persistentdata;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

/** Portage Fabric de Entity.getPersistentData() (Forge). Voir PersistentDataHelper pour l'accès. */
public interface IPersistentData extends Component {
    CompoundTag getTag();
}
