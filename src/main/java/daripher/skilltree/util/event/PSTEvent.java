package daripher.skilltree.util.event;

/** Reproduit net.minecraftforge.eventbus.api.Event : simple event annulable, base de tous nos events "maison". */
public abstract class PSTEvent {
    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
