package command.structure;

public class Action {

    public static final int NONE = 0;
    public static final int EXIT = 1;
    public static final int VISUALIZE = 2;
    public static final int SEND = 4;

    private final int actionCode;

    public static Action of(int actionCode) {
        return new Action(actionCode);
    }

    public boolean isExiting() {
        return (this.actionCode & EXIT) == EXIT;
    }

    public boolean isVisualizing() {
        return (this.actionCode & VISUALIZE) == VISUALIZE;
    }

    public boolean isSending() {
        return (this.actionCode & SEND) == SEND;
    }

    private Action(int actionCode) {
        this.actionCode = actionCode;
    }

}
