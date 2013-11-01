package com.Acrobot.ChestShop.Tests;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
}
