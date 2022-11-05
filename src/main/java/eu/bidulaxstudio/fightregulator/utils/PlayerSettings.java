package eu.bidulaxstudio.fightregulator.utils;

public class PlayerSettings {
    public String mode;
    public long lastChange;

    public PlayerSettings(String mode, long lastChange) {
        this.mode = mode;
        this.lastChange = lastChange;
    }

    public PlayerSettings(String mode) {
        this.mode = mode;
        this.lastChange = Time.getTime();
    }

}
