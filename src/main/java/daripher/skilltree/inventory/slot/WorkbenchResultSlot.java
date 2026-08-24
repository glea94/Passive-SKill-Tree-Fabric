package daripher.skilltree.inventory.slot;

import daripher.skilltree.init.PSTStats;
import daripher.skilltree.inventory.menu.WorkbenchContainer;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.skill.bonus.predicate.item.EquipmentPredicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class WorkbenchResultSlot extends Slot {
    private final WorkbenchContainer workbenchContainer;
    private final Player player;
    private int removeCount;

    public WorkbenchResultSlot(Player player, WorkbenchContainer workbenchContainer, ResultContainer container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.player = player;
        this.workbenchContainer = workbenchContainer;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        if (hasItem()) {
            removeCount += Math.min(amount, getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack itemStack, int amount) {
        removeCount += amount;
        checkTakeAchievements(itemStack);
        repeatQuickCraft();
    }

    private void repeatQuickCraft() {

        RecipeHolder<AbstractWorkbenchRecipe> selectedRecipeHolder = workbenchContainer.menu.getSelectedRecipeHolder();
        if (selectedRecipeHolder == null) {
            return;
        }
        AbstractWorkbenchRecipe selectedRecipe = selectedRecipeHolder.value();
        int additionalCrafts = -1;
        int requiredBaseItems = selectedRecipe.requiredBaseItemAmount();
        if (requiredBaseItems != 0) {
            additionalCrafts = workbenchContainer.getBaseItem().getCount() / requiredBaseItems;
        }
        ItemStack baseItemStack = workbenchContainer.getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        List<Map.Entry<Ingredient, Integer>> requiredIngredients = additionalIngredients.entrySet().stream().toList();
        for (int i = 0; i < requiredIngredients.size(); i++) {
            int requiredAmount = requiredIngredients.get(i).getValue();
            int availableAmount = workbenchContainer.getItem(i + 1).getCount();
            int availableCrafts = availableAmount / requiredAmount;
            if (additionalCrafts == -1 || additionalCrafts > availableCrafts) {
                additionalCrafts = availableCrafts;
            }
        }
        for (int i = 0; i < additionalCrafts; i++) {
            ItemStack craftedStack = selectedRecipe.assemble(workbenchContainer);
            player.addItem(craftedStack);



            awardCraftingStats(craftedStack, craftedStack.getCount());
            consumeMaterials();
        }
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted) {
        removeCount += numItemsCrafted;
    }

    @Override
    protected void checkTakeAchievements(@NotNull ItemStack itemStack) {
        if (removeCount > 0) {
            awardCraftingStats(itemStack, removeCount);
            consumeMaterials();
        }
        removeCount = 0;
    }

    
    private void awardCraftingStats(@NotNull ItemStack itemStack, int count) {
        if (count <= 0) {
            return;
        }
        player.awardStat(Stats.ITEM_CRAFTED.get(itemStack.getItem()), count);
        if (EquipmentPredicate.isRangedWeapon(itemStack)) {
            player.awardStat(PSTStats.rangedWeaponCrafted(), count);
        }
        itemStack.onCraftedBy(player, count);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
        checkTakeAchievements(itemStack);
    }

    private void consumeMaterials() {

        RecipeHolder<AbstractWorkbenchRecipe> selectedRecipeHolder = workbenchContainer.menu.getSelectedRecipeHolder();
        if (selectedRecipeHolder == null) {
            return;
        }
        AbstractWorkbenchRecipe selectedRecipe = selectedRecipeHolder.value();
        if (!workbenchContainer.getItem(0).isEmpty()) {
            workbenchContainer.removeItem(0, selectedRecipe.requiredBaseItemAmount());
        }
        ItemStack baseItemStack = workbenchContainer.getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        List<Map.Entry<Ingredient, Integer>> requiredIngredients = additionalIngredients.entrySet().stream().toList();
        for (int i = 0; i < requiredIngredients.size(); i++) {
            int requiredAmount = requiredIngredients.get(i).getValue();
            workbenchContainer.removeItem(i + 1, requiredAmount);
        }
    }
}