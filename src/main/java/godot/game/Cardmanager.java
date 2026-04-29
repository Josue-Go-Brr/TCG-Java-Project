package godot.game;

import godot.api.Node2D;
import godot.global.GD;

/**
 * Not registered as a Godot script: avoids KSP registrar name clash with {@link godot.CardManager}
 * on case-insensitive filesystems. Attach {@code CardManager.java} on scenes instead.
 */
public class Cardmanager extends Node2D {

    @Override
    public void _process(double delta) {
        GD.INSTANCE.print("Nolan Moy");
    }
}
