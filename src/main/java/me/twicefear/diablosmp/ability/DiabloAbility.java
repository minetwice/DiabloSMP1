package me.twicefear.diablosmp.ability;

import me.twicefear.diablosmp.stone.StoneType;
import org.bukkit.entity.Player;

public interface DiabloAbility {
    StoneType getStoneType();
    boolean isSecondary(); // false = Right Click, true = Shift + Right Click
    void execute(Player player);
}
