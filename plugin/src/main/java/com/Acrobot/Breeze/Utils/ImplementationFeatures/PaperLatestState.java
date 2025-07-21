package com.Acrobot.Breeze.Utils.ImplementationFeatures;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.function.BiFunction;

public class PaperLatestState {

    public static final BiFunction<Block, Boolean, BlockState> PROVIDER = Block::getState;

}
