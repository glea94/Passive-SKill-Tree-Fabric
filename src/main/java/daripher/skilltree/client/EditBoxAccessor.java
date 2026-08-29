package daripher.skilltree.client;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
public class EditBoxAccessor {
    private static Field displayPosField;
    private static Field highlightPosField;
    private static Field formatterField;
    private static Field maxLengthField;
    private static Field suggestionField;
    private static Method renderHighlightMethod;
    static {
        try {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
            try {
                displayPosField = EditBox.class.getDeclaredField("displayPos");
            } catch (NoSuchFieldException e) {
                try { displayPosField = EditBox.class.getDeclaredField("displayPosition"); } catch (Exception ignored) {}
            }
            try {
                highlightPosField = EditBox.class.getDeclaredField("highlightPos");
            } catch (NoSuchFieldException e) {
                try { highlightPosField = EditBox.class.getDeclaredField("selectionStart"); } catch (Exception ignored) {}
            }
<<<<<<< Updated upstream


            try {
                maxLengthField = EditBox.class.getDeclaredField("maxLength");
            } catch (NoSuchFieldException ignored) {}


=======
            try {
                maxLengthField = EditBox.class.getDeclaredField("maxLength");
            } catch (NoSuchFieldException ignored) {}
>>>>>>> Stashed changes
            try {
                suggestionField = EditBox.class.getDeclaredField("suggestion");
            } catch (NoSuchFieldException ignored) {}
            if (displayPosField != null) displayPosField.setAccessible(true);
            if (highlightPosField != null) highlightPosField.setAccessible(true);
            if (maxLengthField != null) maxLengthField.setAccessible(true);
            if (suggestionField != null) suggestionField.setAccessible(true);
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
            for (Field field : EditBox.class.getDeclaredFields()) {
                if (field.getType() == BiFunction.class) {
                    field.setAccessible(true);
                    formatterField = field;
                    break;
                }
            }
<<<<<<< Updated upstream


=======
>>>>>>> Stashed changes
            for (Method method : EditBox.class.getDeclaredMethods()) {
                if (method.getParameterCount() == 5) {
                    Class<?>[] types = method.getParameterTypes();
                    if (types[0] == GuiGraphicsExtractor.class &&
                            types[1] == int.class &&
                            types[2] == int.class &&
                            types[3] == int.class &&
                            types[4] == int.class) {
                        method.setAccessible(true);
                        renderHighlightMethod = method;
                        break;
                    }
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
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        try { return maxLengthField != null ? maxLengthField.getInt(instance) : 80; } catch (Exception e) { return 80; }
    }
    public String getSuggestion() {
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        try { return (suggestionField != null && suggestionField.get(instance) != null) ? (String) suggestionField.get(instance) : ""; } catch (Exception e) { return ""; }
    }
    @SuppressWarnings("unchecked")
    public BiFunction<String, Integer, FormattedCharSequence> getFormatter() {
        try { return formatterField != null ? (BiFunction<String, Integer, FormattedCharSequence>) formatterField.get(instance) : (t, i) -> FormattedCharSequence.EMPTY; } catch (Exception e) { return (t, i) -> FormattedCharSequence.EMPTY; }
    }
    public void invokeRenderHighlight(GuiGraphicsExtractor graphics, int startX, int startY, int endX, int endY) {
        try { if (renderHighlightMethod != null) renderHighlightMethod.invoke(instance, graphics, startX, startY, endX, endY); } catch (Exception ignored) {}
    }
}