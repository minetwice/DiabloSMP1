package com.twicefear.diablosmp.manager;

import com.twicefear.diablosmp.stone.StoneType;

public final class DiabloCooldowns {
    private DiabloCooldowns() {}
    public static int primary(StoneType type) {
        return switch (type) {
            case EARTHQUAKE -> 45; case INFERNO -> 30; case TEMPEST -> 35; case FROSTBITE -> 40;
            case SHADOW -> 25; case HOLY -> 50; case VOID -> 45; case NATURE -> 30;
            case LIGHTNING -> 20; case BLOOD -> 35; case GRAVITY -> 40; case SOUL -> 45;
            case ARCANE -> 30; case PLAGUE -> 35; case CHRONOS -> 60;
        };
    }
    public static int secondary(StoneType type) {
        return switch (type) {
            case EARTHQUAKE -> 120; case INFERNO -> 90; case TEMPEST -> 100; case FROSTBITE -> 110;
            case SHADOW -> 80; case HOLY -> 140; case VOID -> 130; case NATURE -> 95;
            case LIGHTNING -> 70; case BLOOD -> 100; case GRAVITY -> 115; case SOUL -> 125;
            case ARCANE -> 90; case PLAGUE -> 105; case CHRONOS -> 150;
        };
    }
}
