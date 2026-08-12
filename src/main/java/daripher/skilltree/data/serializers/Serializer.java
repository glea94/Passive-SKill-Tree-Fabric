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

    // Factual Fix 1.21.4: Refactored binary stream signature from FriendlyByteBuf to RegistryFriendlyByteBuf
    T deserialize(RegistryFriendlyByteBuf buf);

    // Factual Fix 1.21.4: Refactored binary stream signature from FriendlyByteBuf to RegistryFriendlyByteBuf
    void serialize(RegistryFriendlyByteBuf buf, T object);
}
