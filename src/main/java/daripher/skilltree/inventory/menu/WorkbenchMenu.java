package daripher.skilltree.inventory.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import daripher.skilltree.client.network.ClientWorkbenchRecipeCache;
import daripher.skilltree.init.PSTBlocks;
import daripher.skilltree.init.PSTMenuTypes;
import daripher.skilltree.init.PSTRecipeTypes;
import daripher.skilltree.inventory.slot.WorkbenchBaseSlot;
import daripher.skilltree.inventory.slot.WorkbenchResultSlot;
import daripher.skilltree.inventory.slot.WorkbenchIngredientSlot;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import daripher.skilltree.recipe.workbench.WorkbenchVanillaCraftingRecipe;
import daripher.skilltree.skill.SkillBonusProvider;
import daripher.skilltree.skill.bonus.player.CraftedItemBonusBonus;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorkbenchMenu extends AbstractContainerMenu {
    private static final List<RecipeHolder<AbstractWorkbenchRecipe>> WORKBENCH_RECIPE_CACHE = new ArrayList<>();
    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = RESULT_SLOT + 1;
    private static final int CRAFT_SLOT_END = CRAFT_SLOT_START + 10;
    private static final int INV_SLOT_START = CRAFT_SLOT_END;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int HOTBAR_SLOT_START = INV_SLOT_END;
    private static final int HOTBAR_SLOT_END = HOTBAR_SLOT_START + 9;
    private final WorkbenchContainer workbenchContainer;
    private final ResultContainer resultSlots;
    private final ContainerLevelAccess levelAccess;
    private final Player player;
    private final DataSlot selectedRecipeIndex;
    private List<RecipeHolder<AbstractWorkbenchRecipe>> selectedRecipes = new ArrayList<>();
    private @NotNull ItemStack prevInput = ItemStack.EMPTY;
    private final Level level;
    private @Nullable Runnable recipeListUpdateListener;

    public WorkbenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public WorkbenchMenu(int containerId, Inventory playerInventory, ContainerLevelAccess levelAccess) {
        super(PSTMenuTypes.ARTISAN_WORKBENCH.get(), containerId);
        this.selectedRecipeIndex = DataSlot.standalone();
        this.workbenchContainer = new WorkbenchContainer(this);
        this.resultSlots = new ResultContainer();
        this.levelAccess = levelAccess;
        this.player = playerInventory.player;
        this.level = player.level();

        addSlot(new WorkbenchResultSlot(playerInventory.player, workbenchContainer, resultSlots, 0, 143, 129));
        addSlot(new WorkbenchBaseSlot(workbenchContainer, 0, 8, 120));

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }
                addSlot(new WorkbenchIngredientSlot(workbenchContainer, j + i * 5, 8 + j * 18, 120 + i * 18, j + i * 5 - 1));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 218));
        }
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 160 + i * 18));
            }
        }
        addDataSlot(selectedRecipeIndex);
        setupRecipeList();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack movedStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return movedStack;
        }
        ItemStack clickedStack = slot.getItem();
        movedStack = clickedStack.copy();
        if (slotIndex == RESULT_SLOT) {
            levelAccess.execute((level, blockPos) -> clickedStack.onCraftedBy(player, clickedStack.getCount()));
            if (!moveItemStackTo(clickedStack, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(clickedStack, movedStack);
        } else if (slotIndex >= INV_SLOT_START && slotIndex < HOTBAR_SLOT_END) {
            if (!moveItemStackTo(clickedStack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                if (slotIndex < INV_SLOT_END) {
                    // Factual Fix 1.21.4: Swapped invalid placeholders with real class fields
                    if (!moveItemStackTo(clickedStack, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(clickedStack, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!moveItemStackTo(clickedStack, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
            return ItemStack.EMPTY;
        }
        if (clickedStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (clickedStack.getCount() == movedStack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, clickedStack);
        if (slotIndex == 0) {
            player.drop(clickedStack, false);
        }
        return movedStack;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        levelAccess.execute((level, blockPos) -> clearContainer(player, workbenchContainer));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, PSTBlocks.WORKBENCH.get());
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == -1) {
            selectedRecipeIndex.set(id);
            setupRecipeList();
        }
        if (isValidRecipeIndex(id)) {
            selectedRecipeIndex.set(id);
            RecipeHolder<AbstractWorkbenchRecipe> selectedRecipeHolder = getSelectedRecipeHolder();
            if (selectedRecipeHolder != null) {
                updateCraftingResult(selectedRecipeHolder);
            }
        }
        return true;
    }

    private boolean isValidRecipeIndex(int recipeIndex) {
        return recipeIndex >= 0 && recipeIndex < selectedRecipes.size();
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        updateSelectedRecipe();
    }

    private void updateSelectedRecipe() {
        ItemStack input = workbenchContainer.getBaseItem();
        RecipeHolder<AbstractWorkbenchRecipe> selectedRecipeHolder = getSelectedRecipeHolder();
        if (selectedRecipeHolder != null) {
            updateCraftingResult(selectedRecipeHolder);
            return;
        }
        if (!ItemStack.isSameItemSameComponents(input, prevInput)) {
            setupRecipeList();
            prevInput = input.copy();
        }
        broadcastChanges();
        if (recipeListUpdateListener != null) {
            recipeListUpdateListener.run();
        }
    }

    public void setRecipeListUpdateListener(@Nullable Runnable recipeListUpdateListener) {
        this.recipeListUpdateListener = recipeListUpdateListener;
    }

    private void updateCraftingResult(RecipeHolder<AbstractWorkbenchRecipe> selectedRecipeHolder) {
        AbstractWorkbenchRecipe recipe = selectedRecipeHolder.value();
        if (!recipe.matches(workbenchContainer, level)) {
            resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            // Fix 1.21.9 : isClientSide champ private, méthode isClientSide() confirmée par décompilation
            if (!level.isClientSide()) {
                ItemStack craftResult = recipe.assemble(workbenchContainer, level.registryAccess());
                addCraftingBonuses(craftResult);
                resultSlots.setRecipeUsed(selectedRecipeHolder);
                resultSlots.setItem(0, craftResult);
            }
        }
    }
    public void addCraftingBonuses(ItemStack craftResult) {
        SkillBonusProvider.getMergedSkillBonuses(player, CraftedItemBonusBonus.class).forEach(bonus -> bonus.itemCrafted(craftResult));
    }

    private void setupRecipeList() {
        this.selectedRecipeIndex.set(-1);
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        this.selectedRecipes = getAllWorkbenchRecipes().stream()
                .filter(holder -> this.shouldDisplayRecipe(holder.value()))
                .sorted(Comparator.comparing(holder -> holder.id().toString()))
                .toList();
    }

    private List<RecipeHolder<AbstractWorkbenchRecipe>> getAllWorkbenchRecipes() {
        if (!WORKBENCH_RECIPE_CACHE.isEmpty()) {
            return WORKBENCH_RECIPE_CACHE;
        }

        if (this.level.getServer() != null) {
            RecipeManager recipeManager = this.level.getServer().getRecipeManager();

            // Factual Fix 1.21.5 : CraftingRecipe#getResultItem(RegistryAccess) a été retiré de l'interface Recipe<T>,
            // remplacé par le système RecipeDisplay (confirmé par décompilation Fernflower de Recipe<T>, RecipeDisplay
            // et SlotDisplay) : Recipe#display() -> List<RecipeDisplay>, RecipeDisplay#result() -> SlotDisplay,
            // SlotDisplay#resolveForFirstStack(ContextMap) -> ItemStack. Le ContextMap est construit via
            // SlotDisplayContext.fromLevel(Level), qui y injecte REGISTRIES (HolderLookup.Provider) et FUEL_VALUES.
            List<RecipeHolder<CraftingRecipe>> vanillaCraftingRecipes = recipeManager.getRecipes().stream()
                    .filter(holder -> holder.value().getType() == RecipeType.CRAFTING)
                    .map(holder -> new RecipeHolder<>(holder.id(), (CraftingRecipe) holder.value()))
                    .filter(recipe -> {
                        List<RecipeDisplay> displays = recipe.value().display();
                        if (displays.isEmpty()) {
                            return false;
                        }
                        ItemStack previewResult = displays.get(0).result().resolveForFirstStack(SlotDisplayContext.fromLevel(this.level));
                        return !previewResult.isEmpty();
                    })
                    .toList();

            recipeManager.getRecipes().stream()
                    .filter(holder -> holder.value().getType() == PSTRecipeTypes.WORKBENCH)
                    .map(holder -> new RecipeHolder<>(holder.id(), (AbstractWorkbenchRecipe) holder.value()))
                    .forEach(WORKBENCH_RECIPE_CACHE::add);

            for (RecipeHolder<CraftingRecipe> vanillaHolder : vanillaCraftingRecipes) {
                WORKBENCH_RECIPE_CACHE.add(new RecipeHolder<>(
                        vanillaHolder.id(),
                        new WorkbenchVanillaCraftingRecipe(vanillaHolder, this.level.registryAccess())
                ));
            }
        } else {
            ClientWorkbenchRecipeCache.getAll().stream()
                    .map(recipe -> new RecipeHolder<>((ResourceKey<Recipe<?>>) ResourceKey.create(Registries.RECIPE, recipe.getId()), recipe))
                    .forEach(WORKBENCH_RECIPE_CACHE::add);
        }
        return WORKBENCH_RECIPE_CACHE;
    }

    private boolean shouldDisplayRecipe(AbstractWorkbenchRecipe recipe) {
        if (recipe.isLockedFor(this.player)) {
            return false;
        }
        return this.workbenchContainer.getBaseItem().isEmpty() || recipe.isValidBaseItem(this.workbenchContainer.getBaseItem());
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<RecipeHolder<AbstractWorkbenchRecipe>> getSelectedRecipes() {
        return this.selectedRecipes;
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public WorkbenchContainer getWorkbenchContainer() {
        return this.workbenchContainer;
    }

    public ItemStack getResultItem() {
        return this.resultSlots.getItem(0);
    }

    public @Nullable RecipeHolder<AbstractWorkbenchRecipe> getSelectedRecipeHolder() {
        if (this.selectedRecipes.isEmpty()) {
            return null;
        }
        int index = this.selectedRecipeIndex.get();
        if (index >= this.selectedRecipes.size() || index < 0) {
            return null;
        }
        return this.selectedRecipes.get(index);
    }

    @Deprecated
    public @Nullable AbstractWorkbenchRecipe getSelectedRecipe() {
        RecipeHolder<AbstractWorkbenchRecipe> holder = getSelectedRecipeHolder();
        return holder != null ? holder.value() : null;
    }
}