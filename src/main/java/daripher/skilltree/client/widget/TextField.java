package daripher.skilltree.client.widget;

import daripher.skilltree.mixin.EditBoxAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
=======
=======
>>>>>>> Stashed changes
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TextField extends EditBox implements TickingWidget {
    // Fix 1.21.8 : couleur ARGB au lieu de RGB depuis 1.21.6 - ces constantes sans octet alpha
    // rendaient en transparent (invisible), notamment le placeholder "Search...", et le champ DEFAULT_TEXT_COLOR
    // hérité d'EditBox (0xE0E0E0, sans alpha) n'avait pas été corrigé, rendant le texte tapé lui-même invisible
    public static final int INVALID_TEXT_COLOR = 0xFFD80000;
    private static final int HINT_COLOR = 0xFF575757;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private Predicate<String> softFilter = Objects::nonNull;
    private Function<String, @Nullable String> suggestionProvider = s -> null;
    private String hint = null;

    public TextField(int x, int y, int width, int height, String defaultText) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        setMaxLength(80);
        setValue(defaultText);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (canConsumeInput() && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            setFocused(false);
            return true;
        }
        EditBoxAccessor accessor = (EditBoxAccessor) this;
        if (keyCode == GLFW.GLFW_KEY_TAB && accessor.getSuggestion() != null) {
            setValue(getValue() + accessor.getSuggestion());
            setSuggestion(null);
            return true;
        }
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        setSuggestion(suggestionProvider.apply(getValue()));
        return result;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean result = super.charTyped(codePoint, modifiers);
        setSuggestion(suggestionProvider.apply(getValue()));
        return result;
    }

    @Override
    public void setResponder(@NotNull Consumer<String> responder) {
        super.setResponder(s -> {
            if (!isValueValid()) {
                return;
            }
            responder.accept(s);
        });
    }

    public TextField setSuggestionProvider(Function<String, @Nullable String> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
        return this;
    }

    public TextField setSoftFilter(Predicate<String> filter) {
        this.softFilter = filter;
        return this;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EditBoxAccessor accessor = (EditBoxAccessor) this;
        if (!isVisible()) {
            return;
        }
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        ResourceLocation texture = new ResourceLocation("skilltree:textures/screen/widgets.png");
        int v = isHoveredOrFocused() ? 42 : 56;
        graphics.blit(texture, getX(), getY(), 0, v, width / 2, height);
        graphics.blit(texture, getX() + width / 2, getY(), -width / 2, v, width / 2, height);
=======
=======
>>>>>>> Stashed changes
        Identifier texture = Identifier.parse("skilltree:textures/screen/widgets.png");
        int v = isHoveredOrFocused() ? 42 : 56;

        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, v, currentWidth / 2, currentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + currentWidth / 2, getY(), (256 - currentWidth / 2F), v, currentWidth / 2, currentHeight, 256, 256);

        int textColor = getTextColor();
        int cursorVisiblePosition = getCursorPosition() - accessor.getDisplayPos();
        int highlightWidth = accessor.getHighlightPos() - accessor.getDisplayPos();
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        String visibleText = font.plainSubstrByWidth(getValue().substring(accessor.getDisplayPos()), getInnerWidth());
        boolean isTextSplitByCursor = cursorVisiblePosition >= 0 && cursorVisiblePosition <= visibleText.length();
        boolean isCursorVisible = isFocused() && accessor.getFrame() / 6 % 2 == 0 && isTextSplitByCursor;
        if (visibleText.isEmpty() && hint != null && !isFocused()) {
            visibleText = hint;
        }
        int textX = getX() + 5;
        int textStartX = textX;
        int textY = getY() + 3;
        if (highlightWidth > visibleText.length()) {
            highlightWidth = visibleText.length();
        }
        if (!visibleText.isEmpty()) {
            int cursorIndex = Math.max(0, Math.min(cursorVisiblePosition, visibleText.length()));
            String s1 = isTextSplitByCursor ? visibleText.substring(0, cursorIndex) : visibleText;
            graphics.drawString(font, FormattedCharSequence.forward(s1, Style.EMPTY), textX, textY, textColor, true);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }
        boolean isCursorSurrounded = getCursorPosition() < getValue().length() || getValue().length() >= accessor.getMaxLength();
        int cursorX = textX;
        if (!isTextSplitByCursor) {
            cursorX = cursorVisiblePosition > 0 ? getX() + this.width : getX();
        } else if (isCursorSurrounded) {
            cursorX = textX - 1;
            --textX;
        }
        if (!visibleText.isEmpty() && isTextSplitByCursor && cursorVisiblePosition < visibleText.length()) {
            int cursorIndex = Math.max(0, Math.min(cursorVisiblePosition, visibleText.length()));
            graphics.drawString(font, FormattedCharSequence.forward(visibleText.substring(cursorIndex), Style.EMPTY), textX, textY, textColor, true);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }
        if (!isCursorSurrounded && accessor.getSuggestion() != null) {
            graphics.drawString(font, accessor.getSuggestion(), cursorX - 1, textY, -8355712, true);
        }
        if (isCursorVisible) {
            if (isCursorSurrounded) {
                graphics.fill(cursorX, textY - 1, cursorX + 1, textY + 9, -3092272);
            } else {
                graphics.drawString(font, "_", cursorX, textY, textColor, true);
            }
        }
        if (highlightWidth != cursorVisiblePosition) {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
            int highlightEndX = textStartX + font.width(visibleText.substring(0, highlightWidth));
            accessor.invokeRenderHighlight(graphics, cursorX, textY - 1, highlightEndX - 1, textY + 9);
=======
=======
>>>>>>> Stashed changes
            int hWidth = Math.max(0, Math.min(highlightWidth, visibleText.length()));
            int highlightEndX = textStartX + font.width(visibleText.substring(0, hWidth));
            // Fix 1.21.11 : EditBox n'a plus de méthode privée de surlignage - elle appelle directement
            // GuiGraphics.textHighlight(...) (méthode publique, confirmée par décompilation). true = invertHighlightedTextColor,
            // valeur par défaut d'EditBox jamais modifiée par TextField
            graphics.textHighlight(cursorX, textY - 1, highlightEndX - 1, textY + 9, true);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
        }
    }

    public boolean isValueValid() {
        return softFilter.test(getValue());
    }

    public TextField setHint(@Nullable String hint) {
        this.hint = hint;
        return this;
    }

    private int getTextColor() {
        return getValue().isEmpty() ? HINT_COLOR : isValueValid() ? TEXT_COLOR : INVALID_TEXT_COLOR;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        setFocused(clicked(mouseX, mouseY));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onWidgetTick() {
        this.tick();
    }

    public TextField setFocused() {
        setFocused(true);
        return this;
    }
}
