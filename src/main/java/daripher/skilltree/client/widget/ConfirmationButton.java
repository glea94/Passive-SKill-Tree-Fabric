package daripher.skilltree.client.widget;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
public class ConfirmationButton extends Button {
    protected boolean confirming;
    private Component confirmationMessage;
    public ConfirmationButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
    @Override
    public @NotNull Component getMessage() {
        if (confirming && confirmationMessage != null) {
            return confirmationMessage;
        }
        return super.getMessage();
    }
    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        if (!confirming) {
            confirming = true;
            return;
        }
        pressFunc.onPress(this);
    }
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        boolean clicked = super.mouseClicked(mouseButtonEvent, doubleClick);
        if (!clicked) {
            confirming = false;
        }
        return clicked;
    }
    public void setConfirmationMessage(Component message) {
        this.confirmationMessage = message;
    }
}