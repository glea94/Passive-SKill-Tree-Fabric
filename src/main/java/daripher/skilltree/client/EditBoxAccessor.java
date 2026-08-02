package daripher.skilltree.client;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

public class EditBoxAccessor {
    private static Field displayPosField;
    private static Field highlightPosField;
    private static Field maxLengthField;
    private static Field suggestionField;
    private static Field formatterField;
    private static Method renderHighlightMethod;

    static {
        try {
            // Recherche des champs obfusqués ou nommés de EditBox
            for (Field field : EditBox.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType() == int.class) {
                    if (displayPosField == null) displayPosField = field;
                    else if (highlightPosField == null) highlightPosField = field;
                    else if (maxLengthField == null) maxLengthField = field;
                } else if (field.getType() == String.class) {
                    suggestionField = field;
                } else if (field.getType() == BiFunction.class) {
                    formatterField = field;
                }
            }
            for (Method method : EditBox.class.getDeclaredMethods()) {
                if (method.getParameterCount() == 5 && method.getParameterTypes()[0] == GuiGraphics.class) {
                    method.setAccessible(true);
                    renderHighlightMethod = method;
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private final EditBox instance;

    public EditBoxAccessor(EditBox instance) {
        this.instance = instance;
    }

    public int getDisplayPos() {
        try { return displayPosField != null ? displayPosField.getInt(instance) : 0; } catch (Exception e) { return 0; }
    }

    public int getHighlightPos() {
        try { return highlightPosField != null ? highlightPosField.getInt(instance) : 0; } catch (Exception e) { return 0; }
    }

    public int getMaxLength() {
        try { return maxLengthField != null ? maxLengthField.getInt(instance) : 0; } catch (Exception e) { return 0; }
    }

    public String getSuggestion() {
        try { return suggestionField != null ? (String) suggestionField.get(instance) : ""; } catch (Exception e) { return ""; }
    }

    @SuppressWarnings("unchecked")
    public BiFunction<String, Integer, FormattedCharSequence> getFormatter() {
        try { return formatterField != null ? (BiFunction<String, Integer, FormattedCharSequence>) formatterField.get(instance) : (t, i) -> FormattedCharSequence.EMPTY; } catch (Exception e) { return (t, i) -> FormattedCharSequence.EMPTY; }
    }

    public void invokeRenderHighlight(GuiGraphics graphics, int startX, int startY, int endX, int endY) {
        try { if (renderHighlightMethod != null) renderHighlightMethod.invoke(instance, graphics, startX, startY, endX, endY); } catch (Exception ignored) {}
    }
}