package com.Acrobot.Breeze.Tests;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link com.Acrobot.Breeze.Utils.MaterialUtil}
 *
 * @author Acrobot
 */
public class MaterialTest {
    
    @Test
    public void testCodes() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }
            String shortenedName = MaterialUtil.getShortenedName(material.toString(), MaterialUtil.MAXIMUM_SIGN_WIDTH);
            assertSame(material, MaterialUtil.getMaterial(shortenedName), shortenedName + " did not produce " + material);
        }
    }

    @Test
    public void testCodesWithMeta() {
        int maxWidth = MaterialUtil.MAXIMUM_SIGN_WIDTH - StringUtil.getMinecraftStringWidth("#AAA");
        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }
            String shortenedName = MaterialUtil.getShortenedName(material.toString(), maxWidth);
            assertSame(material, MaterialUtil.getMaterial(shortenedName), shortenedName + " did not produce " + material);
        }
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {
            "DiamonPicka",
            "Diamon Picka",
            "ExpBottle",
            "Exp Bottle"
    })
    public void testCodesWithAndWithoutSpace(String materialName) {
        assertNotNull(MaterialUtil.getMaterial(materialName));
    }
}
