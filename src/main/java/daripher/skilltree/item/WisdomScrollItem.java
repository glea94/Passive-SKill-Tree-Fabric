package daripher.skilltree.item;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.config.ServerConfig;
import daripher.skilltree.network.ServerNetworking;
import daripher.skilltree.util.registry.DeferredRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WisdomScrollItem extends Item {
    public WisdomScrollItem() {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, DeferredRegister.currentId())));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        IPlayerSkills skillsCapability = PlayerSkillsProvider.get(player);
        int totalSkillPoints = skillsCapability.getPlayerSkills().size() + skillsCapability.getSkillPoints();

        if (totalSkillPoints >= ServerConfig.max_skill_points) {
            return InteractionResult.FAIL;
        }

        if (!player.getAbilities().instabuild) {
            itemInHand.shrink(1);
        }


        if (!level.isClientSide()) {
            level.playSound(null, player, SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), 0.9F, 0.7F + player.getRandom().nextFloat() * 0.3F);
            level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), 0.4F, 0.2F + player.getRandom().nextFloat() * 0.3F);

            skillsCapability.grantSkillPoints(1);
            ServerNetworking.sendSyncPlayerSkills((ServerPlayer) player);

            if (ServerConfig.show_chat_messages) {
                ((ServerPlayer) player).sendSystemMessage(Component.translatable("skilltree.message.point_command").withStyle(ChatFormatting.YELLOW));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, @NotNull TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
    }
}