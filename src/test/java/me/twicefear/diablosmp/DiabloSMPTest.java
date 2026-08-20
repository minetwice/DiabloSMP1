package me.twicefear.diablosmp;

import me.twicefear.diablosmp.stone.StoneType;
import me.twicefear.diablosmp.user.UserData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DiabloSMPTest {

    @Test
    public void testStoneTypeEnum() {
        assertEquals(15, StoneType.values().length);
        StoneType earth = StoneType.fromId("earth_smasher");
        assertNotNull(earth);
        assertEquals(1001, earth.getCustomModelData());
        assertEquals("earth_smasher", earth.getId());
    }

    @Test
    public void testUserData() {
        UUID uuid = UUID.randomUUID();
        UserData userData = new UserData(uuid);

        assertNull(userData.getAbsorbedStone());
        assertFalse(userData.hasAbsorbedStone());

        userData.setAbsorbedStone(StoneType.FLAME_LORD);
        assertTrue(userData.hasAbsorbedStone());
        assertEquals(StoneType.FLAME_LORD, userData.getAbsorbedStone());

        // Test Cooldown
        String abilityKey = "flame_lord_primary";
        assertFalse(userData.isCooldowned(abilityKey));

        userData.setCooldown(abilityKey, 10);
        assertTrue(userData.isCooldowned(abilityKey));
        assertTrue(userData.getRemainingCooldownSeconds(abilityKey) > 0);
    }
}
