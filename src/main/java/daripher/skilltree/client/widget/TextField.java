package daripher.skilltree.client.widget;

import daripher.skilltree.client.EditBoxAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TextField extends EditBox implements TickingWidget {



    public static final int INVALID_TEXT_COLOR = 0xFFD80000;
    private static final int HINT_COLOR = 0xFF575757;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private Predicate<String> softFilter = Objects::nonNull;
    private Function<String, @Nullable String> suggestionProvider = s -> null;
    private String hint = null;

    public TextField(int x, int y, int width, int height, String defaultText) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        this.setMaxLength(80);
        this.setValue(defaultText);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (canConsumeInput() && keyEvent.isEscape()) {
            setFocused(false);
            return true;
        }
        EditBoxAccessor accessor = new EditBoxAccessor(this);
        if (keyEvent.key() == GLFW.GLFW_KEY_TAB && accessor.getSuggestion() != null) {
            setValue(getValue() + accessor.getSuggestion());
            setSuggestion(null);
            return true;
        }
        boolean result = super.keyPressed(keyEvent);
        setSuggestion(suggestionProvider.apply(getValue()));
        return result;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        boolean result = super.charTyped(characterEvent);
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
    public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        EditBoxAccessor accessor = new EditBoxAccessor(this);
        if (!this.visible) {
            return;
        }
        Identifier texture = Identifier.parse("skilltree:textures/screen/widgets.png");
        int v = isHoveredOrFocused() ? 42 : 56;

        int currentWidth = this.getWidth();
        int currentHeight = this.getHeight();

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0F, v, currentWidth / 2, currentHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX() + currentWidth / 2, getY(), (256 - currentWidth / 2F), v, currentWidth / 2, currentHeight, 256, 256);

        int textColor = getTextColor();
        int valueLength = getValue().length();

        int displayPos = Math.max(0, Math.min(accessor.getDisplayPos(), valueLength));
        int highlightPos = Math.max(0, Math.min(accessor.getHighlightPos(), valueLength));

        int cursorVisiblePosition = getCursorPosition() - displayPos;
        int highlightWidth = highlightPos - displayPos;

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        String visibleText = font.plainSubstrByWidth(getValue().substring(displayPos), getInnerWidth());

        boolean isTextSplitByCursor = cursorVisiblePosition >= 0 && cursorVisiblePosition <= visibleText.length();
        boolean isCursorVisible = isFocused() && (Util.getMillis() / 300L) % 2L == 0L && isTextSplitByCursor;
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
            graphics.text(font, FormattedCharSequence.forward(s1, Style.EMPTY), textX, textY, textColor, true);
        }

        boolean isCursorSurrounded = getCursorPosition() < getValue().length() || getValue().length() >= accessor.getMaxLength();
        int cursorX = textX;
        if (!isTextSplitByCursor) {
            cursorX = cursorVisiblePosition > 0 ? getX() + currentWidth : getX();
        } else if (isCursorSurrounded) {
            cursorX = textX - 1;
            --textX;
        }

        if (!visibleText.isEmpty() && isTextSplitByCursor && cursorVisiblePosition < visibleText.length()) {
            int cursorIndex = Math.max(0, Math.min(cursorVisiblePosition, visibleText.length()));
            graphics.text(font, FormattedCharSequence.forward(visibleText.substring(cursorIndex), Style.EMPTY), textX, textY, textColor, true);
        }
        if (!isCursorSurrounded && accessor.getSuggestion() != null) {
            graphics.text(font, accessor.getSuggestion(), cursorX - 1, textY, -8355712, true);
        }
        if (isCursorVisible) {
            if (isCursorSurrounded) {
                graphics.fill(cursorX, textY - 1, cursorX + 1, textY + 9, -3092272);
            } else {
                graphics.text(font, "_", cursorX, textY, textColor, true);
            }
        }

        if (highlightWidth != cursorVisiblePosition) {
            int hWidth = Math.max(0, Math.min(highlightWidth, visibleText.length()));
            int highlightEndX = textStartX + font.width(visibleText.substring(0, hWidth));



            graphics.textHighlight(cursorX, textY - 1, highlightEndX - 1, textY + 9, true);
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
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        this.setFocused(this.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y()));
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }

    @Override
    public void onWidgetTick() {
    }

    public TextField setFocused() {
        this.setFocused(true);
        return this;
    }
}