// Fichier : src/main/java/daripher/skilltree/item/WisdomScrollItem.java
package daripher.skilltree.item;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.network.ServerNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WisdomScrollItem extends Item {
    // CORRECTION 1.21.4 : Item.Properties doit recevoir son id via setId(...) avant construction
    // (sinon NullPointerException "Item id not set" - voir DeferredRegister).
    public WisdomScrollItem(ResourceLocation id) {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, id)));
    }

    @Override
<<<<<<< Updated upstream
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
=======
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
>>>>>>> Stashed changes
        ItemStack itemInHand = player.getItemInHand(hand);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        int totalSkillPoints = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();
        if (totalSkillPoints >= ServerConfig.max_skill_points) {
            return InteractionResult.FAIL;
        }
        if (!player.getAbilities().instabuild) {
            itemInHand.shrink(1);
        }
        if (!level.isClientSide) {
            level.playSound(null, player, SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), 0.9F, 0.7F + player.getRandom()
                    .nextFloat() * 0.3F);
            level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 0.4F, 0.2F + player.getRandom()
                    .nextFloat() * 0.3F);
            skillsCapability.grantSkillPoints(1);
            ServerNetworking.sendSyncPlayerSkills((ServerPlayer) player);
            if (ServerConfig.show_chat_messages) {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Level level, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
    }
}