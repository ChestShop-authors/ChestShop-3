package com.Acrobot.Breeze.Tests;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link com.Acrobot.Breeze.Utils.MaterialUtil}
 *
 * @author Acrobot
 */
@RunWith(JUnit4.class)
public class MaterialTest {

    @Test
    public void testForBlank() {
        ItemStack air = new ItemStack(Material.AIR);

        assertTrue(MaterialUtil.isEmpty(air));
        assertTrue(MaterialUtil.isEmpty(null));
    }
    
    @Test
    public void testCodes() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }
            String shortenedName = MaterialUtil.getShortenedName(material.toString(), MaterialUtil.MAXIMUM_SIGN_WIDTH);
            assertSame(shortenedName + " did not produce " + material, material, MaterialUtil.getMaterial(shortenedName));
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
            assertSame(shortenedName + " did not produce " + material, material, MaterialUtil.getMaterial(shortenedName));
        }
    }

    @Test
    public void testCodesWithAndWithoutSpace() {
        assertNotNull(MaterialUtil.getMaterial("DiamonPicka"));
        assertNotNull(MaterialUtil.getMaterial("Diamon Picka"));
        assertNotNull(MaterialUtil.getMaterial("ExpBottle"));
        assertNotNull(MaterialUtil.getMaterial("Exp Bottle"));
    }
}
