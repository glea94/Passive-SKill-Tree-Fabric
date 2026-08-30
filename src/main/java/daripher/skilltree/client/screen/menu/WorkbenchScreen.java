package daripher.skilltree.client.screen.menu;
import daripher.skilltree.SkillTreeMod;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.inventory.menu.WorkbenchMenu;
import daripher.skilltree.recipe.workbench.AbstractWorkbenchRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
public class WorkbenchScreen extends AbstractContainerScreen<WorkbenchMenu> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/gui/container/workbench.png");
    private static final Identifier RECIPES_TEXTURE = Identifier.fromNamespaceAndPath(SkillTreeMod.MOD_ID, "textures/gui/container/workbench_recipes.png");
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_FULL_HEIGHT = 90;
    private static final int RECIPES_X = 8;
    private static final int RECIPES_Y = 24;
    private static final int RECIPE_WIDTH = 143;
    private static final int RECIPE_HEIGHT = 18;
    private final List<Pair<RecipeHolder<AbstractWorkbenchRecipe>, Integer>> searchedRecipes = new ArrayList<>();
    private EditBox searchBox;
    private int amountScrolled;
    private float tickCount;
    public WorkbenchScreen(WorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 242);
        menu.setRecipeListUpdateListener(this::refreshSearchResults);
    }
    @Override
    protected void init() {
        String previousSearch = searchBox != null ? searchBox.getValue() : "";
        super.init();
        clearWidgets();
        searchBox = new EditBox(this.font, this.leftPos + 36, this.topPos + 9, 102, 10, Component.empty());
        searchBox.setMaxLength(57);
        searchBox.setBordered(false);
        searchBox.setTextColor(ARGB.opaque(0xffffff));
        this.addRenderableWidget(searchBox);
        if (!previousSearch.isEmpty()) {
            searchBox.setValue(previousSearch);
            refreshSearchResults();
        }
    }
    @Override
    public void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0F, 0F, this.imageWidth, this.imageHeight, 256, 256);
        renderScroll(guiGraphics);
        renderRecipes(guiGraphics, mouseX, mouseY);
        if (searchBox.getValue().isEmpty()) {
            Component searchHint = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC);
            guiGraphics.text(this.font, searchHint, searchBox.getX(), searchBox.getY(), ARGB.opaque(0x555555), false);
        }
        super.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
    }
    private void renderScroll(@NotNull GuiGraphicsExtractor guiGraphics) {
        int scrollerIconIndex = (isScrollBarActive() ? 2 : 1);
        int scrollerX = this.leftPos + 156;
        float scrollOffset = (float) amountScrolled / getMaxScroll();
        int scrollerY = (int) (this.topPos + 24 + (SCROLLER_FULL_HEIGHT - SCROLLER_HEIGHT) * scrollOffset);
        int uOffset = 256 - (SCROLLER_WIDTH * scrollerIconIndex);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, scrollerX, scrollerY, (float) uOffset, 0F, SCROLLER_WIDTH, SCROLLER_HEIGHT, 256, 256);
    }
    private void renderRecipes(GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        int x = this.leftPos + RECIPES_X;
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            int recipeIndex = getRecipeInSlot(i).getValue();
            int y = this.topPos + RECIPES_Y + i * RECIPE_HEIGHT;
            int recipeTexture = getRecipeTexture(mouseX, mouseY, recipeIndex, i);
            int vOffset = recipeTexture * RECIPE_HEIGHT;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, RECIPES_TEXTURE, x, y, 0F, (float) vOffset, RECIPE_WIDTH, RECIPE_HEIGHT, 256, 256);
            AbstractWorkbenchRecipe recipe = getRecipeInSlot(i).getKey().value();
            String tooltip = recipe.getShortDescription().getString();
            tooltip = TooltipHelper.getTrimmedString(this.font, tooltip, RECIPE_WIDTH - 4);
            guiGraphics.text(this.font, tooltip, x + 2, y + 5, ARGB.opaque(0xffffff));
        }
    }
    private int getRecipeTexture(double mouseX, double mouseY, int recipeIndex, int recipeSlot) {
        if (this.menu.getSelectedRecipeIndex() == recipeIndex) {
            return 1;
        }
        if (isMouseOverRecipe(recipeSlot, mouseX, mouseY)) {
            return 2;
        }
        return 0;
    }
    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.extractContents(guiGraphics, mouseX, mouseY, partialTicks);
        super.extractCarriedItem(guiGraphics, mouseX, mouseY);
        renderGhostRecipe(guiGraphics);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
        tickCount += partialTicks;
    }
    private void renderGhostRecipe(GuiGraphicsExtractor guiGraphics) {
        AbstractWorkbenchRecipe selectedRecipe = this.menu.getSelectedRecipe();
        if (selectedRecipe == null) {
            for (int i = 1; i < 10; i++) {
                int itemX = this.leftPos + 8 + i % 5 * 18;
                int itemY = this.topPos + 120 + i / 5 * 18;
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
            }
            return;
        }
        renderGhostBaseIngredient(guiGraphics, selectedRecipe);
        renderGhostAdditionalIngredients(guiGraphics, selectedRecipe);
        renderGhostResult(guiGraphics, selectedRecipe);
    }
    private void renderGhostResult(GuiGraphicsExtractor guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        if (!this.menu.getResultItem().isEmpty()) {
            return;
        }
        renderMissingItemOverlay(guiGraphics, this.leftPos + 134, this.topPos + 120, 34);
        renderMissingItemStack(guiGraphics, this.leftPos + 143, this.topPos + 129, getResultItem(selectedRecipe));
    }
    private void renderGhostAdditionalIngredients(GuiGraphicsExtractor guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        ItemStack baseItemStack = this.menu.getWorkbenchContainer().getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        List<Map.Entry<Ingredient, Integer>> requiredIngredients = additionalIngredients.entrySet().stream().toList();
        for (int ingredientIndex = 0; ingredientIndex < 9; ingredientIndex++) {
            int slot = ingredientIndex + 1;
            int itemX = this.leftPos + 8 + (slot % 5) * 18;
            int itemY = this.topPos + 120 + (slot / 5) * 18;
            if (ingredientIndex >= requiredIngredients.size()) {
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
                continue;
            }
            ItemStack existingIngredient = this.menu.getWorkbenchContainer().getItem(slot);
            Map.Entry<Ingredient, Integer> ingredientAmountEntry = requiredIngredients.get(ingredientIndex);
            if (existingIngredient.isEmpty()) {
                renderMissingIngredient(guiGraphics, slot, itemX, itemY, Pair.of(ingredientAmountEntry.getKey(), ingredientAmountEntry.getValue()));
                continue;
            }
            int requiredAmount = ingredientAmountEntry.getValue();
            if (existingIngredient.getCount() < requiredAmount) {
                renderMissingItemOverlay(guiGraphics, itemX, itemY);
            }
        }
    }
    private void renderGhostBaseIngredient(GuiGraphicsExtractor guiGraphics, AbstractWorkbenchRecipe selectedRecipe) {
        int requiredBaseItemAmount = selectedRecipe.requiredBaseItemAmount();
        Pair<Ingredient, Integer> baseIngredient = selectedRecipe.getBaseIngredient();
        boolean usingValidBaseItem = selectedRecipe.isValidBaseItem(this.menu.getWorkbenchContainer().getBaseItem());
        if (requiredBaseItemAmount == 0 && !this.menu.getWorkbenchContainer().getBaseItem().isEmpty()) {
            renderMissingItemOverlay(guiGraphics, this.leftPos + 8, this.topPos + 120);
        }
        if (baseIngredient != null && requiredBaseItemAmount > 0 && !usingValidBaseItem) {
            renderMissingIngredient(guiGraphics, 0, this.leftPos + 8, this.topPos + 120, baseIngredient);
        }
    }
    private void renderMissingIngredient(GuiGraphicsExtractor guiGraphics, int slot, int x, int y, Pair<Ingredient, Integer> ingredientAmountPair) {
        renderMissingItemOverlay(guiGraphics, x, y);
        ItemStack itemStack = getDisplayedItemStack(ingredientAmountPair, slot).copy();
        itemStack.setCount(ingredientAmountPair.getRight());
        if (!itemStack.isEmpty()) {
            renderMissingItemStack(guiGraphics, x, y, itemStack);
        }
    }
    private ItemStack getDisplayedItemStack(Pair<Ingredient, Integer> ingredientAmountPair, int slot) {
        List<ItemStack> ingredientItemStacks = getIngredientDisplayStacks(ingredientAmountPair.getLeft());
        Random random = new Random(slot);
        int ingredientCount = ingredientItemStacks.size();
        if (ingredientCount == 0) {
            return ItemStack.EMPTY;
        }
        int displayedItemIndex = Mth.floor((tickCount / 20f + random.nextInt(ingredientCount)) % ingredientCount);
        return ingredientItemStacks.get(displayedItemIndex);
    }
    private List<ItemStack> getIngredientDisplayStacks(Ingredient ingredient) {
        Objects.requireNonNull(this.minecraft);
        ContextMap context = SlotDisplayContext.fromLevel(this.minecraft.level);
        return ingredient.display().resolveForStacks(context);
    }
    private void renderMissingItemOverlay(GuiGraphicsExtractor guiGraphics, int x, int y) {
        renderMissingItemOverlay(guiGraphics, x, y, 16);
    }
    private void renderMissingItemOverlay(GuiGraphicsExtractor guiGraphics, int x, int y, int slotSize) {
        guiGraphics.fill(x, y, x + slotSize, y + slotSize, 0x30ff0000);
    }
    private @NotNull ItemStack getResultItem(AbstractWorkbenchRecipe selectedRecipe) {
        ItemStack resultItem = selectedRecipe.getResult(this.menu.getWorkbenchContainer());
        this.menu.addCraftingBonuses(resultItem, selectedRecipe);
        return resultItem;
    }
    @Override
    protected void extractTooltip(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltip(guiGraphics, mouseX, mouseY);
        renderRecipesTooltip(guiGraphics, mouseX, mouseY);
        renderGhostRecipeTooltip(guiGraphics, mouseX, mouseY);
    }
    private void renderGhostRecipeTooltip(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        int selectedRecipeIndex = this.menu.getSelectedRecipeIndex();
        if (selectedRecipeIndex <= -1) {
            return;
        }
        List<RecipeHolder<AbstractWorkbenchRecipe>> selectedRecipes = this.menu.getSelectedRecipes();
        if (selectedRecipes.isEmpty()) {
            return;
        }
        AbstractWorkbenchRecipe selectedRecipe = selectedRecipes.get(selectedRecipeIndex).value();
        int requiredBaseItemAmount = selectedRecipe.requiredBaseItemAmount();
        Pair<Ingredient, Integer> requiredBaseItem = selectedRecipe.getBaseIngredient();
        boolean missingBaseItem = this.menu.getWorkbenchContainer().getBaseItem().isEmpty();
        if (requiredBaseItem != null && requiredBaseItemAmount > 0 && missingBaseItem) {
            int itemX = this.leftPos + 8;
            int itemY = this.topPos + 120;
            if (isMouseOverArea(mouseX, mouseY, itemX, itemY, 16, 16)) {
                ItemStack requiredBaseItemStack = getDisplayedItemStack(requiredBaseItem, 0);
                if (!requiredBaseItemStack.isEmpty() && this.menu.getWorkbenchContainer().getBaseItem().isEmpty()) {
                    renderItemTooltip(guiGraphics, mouseX, mouseY, requiredBaseItemStack);
                }
            }
        }
        AtomicInteger slotIndex = new AtomicInteger(1);
        ItemStack baseItemStack = this.menu.getWorkbenchContainer().getBaseItem();
        Map<Ingredient, Integer> additionalIngredients = selectedRecipe.getAdditionalIngredients(baseItemStack);
        additionalIngredients.entrySet().forEach(ingredientAmountEntry -> {
            int slot = slotIndex.get();
            if (this.menu.getWorkbenchContainer().getItem(slot).isEmpty()) {
                int itemX = this.leftPos + 8 + slot % 5 * 18;
                int itemY = this.topPos + 120 + slot / 5 * 18;
                if (isMouseOverArea(mouseX, mouseY, itemX, itemY, 16, 16)) {
                    Pair<Ingredient, Integer> ingredientAmountPair = Pair.of(ingredientAmountEntry.getKey(), ingredientAmountEntry.getValue());
                    renderItemTooltip(guiGraphics, mouseX, mouseY, getDisplayedItemStack(ingredientAmountPair, slot));
                }
                slotIndex.getAndIncrement();
            }
        });
        if (this.menu.getResultItem().isEmpty() && isMouseOverArea(mouseX, mouseY, this.leftPos + 134, this.topPos + 120, 34, 34)) {
            Objects.requireNonNull(this.minecraft);
            ItemStack resultItem = getResultItem(selectedRecipe);
            renderItemTooltip(guiGraphics, mouseX, mouseY, resultItem);
        }
    }
    private void renderRecipesTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            if (!isMouseOverRecipe(i, mouseX, mouseY)) {
                continue;
            }
            AbstractWorkbenchRecipe recipe = getRecipeInSlot(i).getKey().value();
            guiGraphics.setComponentTooltipForNextFrame(this.font, recipe.getFullDescription(), mouseX, mouseY);
        }
    }
    private void renderItemTooltip(@NotNull GuiGraphicsExtractor guiGraphics, int x, int y, ItemStack itemStack) {
        List<Component> tooltip = getTooltipFromContainerItem(itemStack);
        Optional<TooltipComponent> tooltipImage = itemStack.getTooltipImage();
        guiGraphics.setTooltipForNextFrame(this.font, tooltip, tooltipImage, x, y);
    }
    private void renderMissingItemStack(GuiGraphicsExtractor guiGraphics, int itemX, int itemY, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        guiGraphics.fakeItem(itemStack, itemX, itemY);
        guiGraphics.fill(itemX, itemY, itemX + 16, itemY + 16, 0x30ffffff);
        guiGraphics.itemDecorations(this.font, itemStack, itemX, itemY);
    }
    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
    }
    @Override
    protected void containerTick() {
        super.containerTick();
    }
    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        String search = searchBox.getValue();
        if (searchBox.charTyped(characterEvent)) {
            if (!Objects.equals(search, searchBox.getValue())) {
                refreshSearchResults();
            }
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isEscape() && menu.getSelectedRecipe() != null) {
            selectRecipe(-1);
            return true;
        }
        String search = searchBox.getValue();
        if (searchBox.keyPressed(keyEvent)) {
            if (!Objects.equals(search, searchBox.getValue())) {
                refreshSearchResults();
            }
            return true;
        } else {
            return searchBox.isFocused() && searchBox.visible && !keyEvent.isEscape() || super.keyPressed(keyEvent);
        }
    }
    private boolean isScrollBarActive() {
        return searchedRecipes.size() > 5;
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        if (searchBox.mouseClicked(mouseButtonEvent, doubleClick)) {
            searchBox.setFocused(true);
            return true;
        }
        searchBox.setFocused(false);
        Objects.requireNonNull(this.minecraft);
        LocalPlayer player = this.minecraft.player;
        Objects.requireNonNull(player);
        for (int i = 0; i < Math.min(5, searchedRecipes.size()); i++) {
            int recipeIndex = getRecipeInSlot(i).getValue();
            if (isMouseOverRecipe(i, mouseX, mouseY) && this.menu.clickMenuButton(player, recipeIndex)) {
                selectRecipe(recipeIndex);
                return true;
            }
        }
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isScrollBarActive()) {
            amountScrolled = (int) Mth.clamp(amountScrolled - scrollY, 0, getMaxScroll());
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    private Pair<RecipeHolder<AbstractWorkbenchRecipe>, Integer> getRecipeInSlot(int slot) {
        if (slot + amountScrolled >= searchedRecipes.size()) {
            return searchedRecipes.get(0);
        }
        return searchedRecipes.get(slot + amountScrolled);
    }
    private int getMaxScroll() {
        return searchedRecipes.size() - 5;
    }
    private void refreshSearchResults() {
        List<RecipeHolder<AbstractWorkbenchRecipe>> selectedRecipes = this.menu.getSelectedRecipes();
        searchedRecipes.clear();
        for (int i = 0; i < selectedRecipes.size(); i++) {
            RecipeHolder<AbstractWorkbenchRecipe> recipeHolder = selectedRecipes.get(i);
            String search = searchBox.getValue();
            String recipeTitle = recipeHolder.value().getShortDescription().getString().toLowerCase(Locale.ROOT);
            if (search.isEmpty() || recipeTitle.contains(search.toLowerCase(Locale.ROOT))) {
                searchedRecipes.add(Pair.of(recipeHolder, i));
            }
        }
    }
    private void selectRecipe(int index) {
        Objects.requireNonNull(this.minecraft);
        SoundManager soundManager = this.minecraft.getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
        MultiPlayerGameMode gameMode = this.minecraft.gameMode;
        Objects.requireNonNull(gameMode);
        gameMode.handleInventoryButtonClick(this.menu.containerId, index);
    }
    private boolean isMouseOverRecipe(int recipeIndex, double mouseX, double mouseY) {
        int recipeX = this.leftPos + RECIPES_X;
        int recipeY = this.topPos + RECIPES_Y + recipeIndex * RECIPE_HEIGHT;
        return mouseX >= recipeX && mouseY >= recipeY && mouseX < recipeX + RECIPE_WIDTH && mouseY < recipeY + RECIPE_HEIGHT;
    }
    private boolean isMouseOverArea(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}