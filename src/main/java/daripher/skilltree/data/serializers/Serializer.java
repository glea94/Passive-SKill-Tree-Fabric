package daripher.skilltree.data.serializers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
public interface Serializer<T> {
    T deserialize(JsonObject json) throws JsonParseException;
    void serialize(JsonObject json, T object);
    T deserialize(CompoundTag tag);
    CompoundTag serialize(T object);
<<<<<<< Updated upstream


    T deserialize(RegistryFriendlyByteBuf buf);


=======
    T deserialize(RegistryFriendlyByteBuf buf);
>>>>>>> Stashed changes
    void serialize(RegistryFriendlyByteBuf buf, T object);
}
