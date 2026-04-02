package godot.game;

import godot.annotation.Export;
import godot.annotation.RegisterClass;
import godot.annotation.RegisterFunction;
import godot.annotation.RegisterProperty;
import godot.api.Node2D;
import godot.global.GD;

@RegisterClass
public class Cardmanager extends Node2D {
    @Export
    @RegisterProperty
    public int test = 0;

    @RegisterFunction
    @Override
    public void _process(double delta) {
        GD.INSTANCE.print("Nolan Moy");

    }
}
