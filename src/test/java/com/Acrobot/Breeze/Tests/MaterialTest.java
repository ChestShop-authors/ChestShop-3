package com.Acrobot.Breeze.Tests;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
            String shortenedName = MaterialUtil.getShortenedName(material.toString(), MaterialUtil.MAXIMUM_SIGN_LETTERS);
            assertSame(material, MaterialUtil.getMaterial(shortenedName));
        }
    }
}
