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

public class AmnesiaScrollItem extends Item {
    public AmnesiaScrollItem() {
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, DeferredRegister.currentId())));
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack scroll = player.getItemInHand(hand);
        IPlayerSkills skills = PlayerSkillsProvider.get(player);

        if (!player.getAbilities().instabuild) {
            scroll.shrink(1);
        }

        if (!level.isClientSide) {
            level.playSound(null, player, SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), 0.9F, 0.7F + player.getRandom().nextFloat() * 0.3F);
            level.playSound(null, player, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, player.getSoundSource(), 0.4F, 0.2F + player.getRandom().nextFloat() * 0.2F);

            skills.resetTree((ServerPlayer) player);
            skills.setSkillPoints((int) (skills.getSkillPoints() * (1 - ServerConfig.amnesia_scroll_penalty)));
            ((ServerPlayer) player).sendSystemMessage(Component.translatable("skilltree.message.reset_command").withStyle(ChatFormatting.YELLOW));
            ServerNetworking.sendSyncPlayerSkills((ServerPlayer) player);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, @NotNull TooltipFlag tooltipFlag) {
        consumer.accept(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GOLD));
        double penalty = ServerConfig.amnesia_scroll_penalty;
        if (penalty > 0) {
            int textPenalty = (int) (penalty * 100);
            consumer.accept(Component.translatable(getDescriptionId() + ".warning", textPenalty).withStyle(ChatFormatting.RED));
        }
    }
}