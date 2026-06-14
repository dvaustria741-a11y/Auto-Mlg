package com.zen.automlg.mixin;

import com.zen.automlg.AutoMLGConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique private static final float FALL_DAMAGE_THRESHOLD = 3.0f;
    @Unique private static final int GROUND_RANGE = 4;

    @Unique private int automlg$waterPickupTimer = -1;
    @Unique private BlockPos automlg$waterPickupPos = null;

    @Unique private int automlg$snowPickupTimer = -1;
    @Unique private BlockPos automlg$snowPickupPos = null;

    @Unique private int automlg$boatTimer = -1;
    @Unique private BoatEntity automlg$boat = null;

    @Inject(method = "tick", at = @At("head"))
    private void automlg$onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();

        if (world.isClient) return;
        if (player.getAbilities().creativeMode) return;
        if (player.isSpectator()) return;

        automlg$waterPickup(player, world);
        automlg$snowPickup(player, world);
        automlg$boatCleanup(player, world);

        ItemStack main = player.getStackInHand(Hand.MAIN_HAND);

        if (AutoMLGConfig.boatEnabled && main.getItem() instanceof BoatItem) {
            automlg$boatMLG(player, world);
        }
        if (AutoMLGConfig.waterEnabled && main.isOf(Items.WATER_BUCKET)) {
            automlg$placeWater(player, world);
        }
        if (AutoMLGConfig.snowEnabled && main.isOf(Items.POWDER_SNOW_BUCKET)) {
            automlg$placeSnow(player, world);
        }
    }

    @Unique
    private boolean automlg$nearGround(PlayerEntity player, World world, int range) {
        BlockPos base = player.getBlockPos();
        for (int i = 1; i <= range; i++) {
            BlockState state = world.getBlockState(base.down(i));
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private boolean automlg$fallenEnough(PlayerEntity player) {
        float fallDistance = ((EntityAccessor) (Object) player).automlg$getFallDistance();
        return fallDistance > FALL_DAMAGE_THRESHOLD;
    }

    @Unique
    private void automlg$placeWater(PlayerEntity player, World world) {
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos pos = player.getBlockPos();
        BlockState here = world.getBlockState(pos);
        if (!here.isAir()) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        world.setBlockState(pos, Blocks.WATER.getDefaultState());
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$waterPickupPos = pos;
        automlg$waterPickupTimer = 2;
    }

    @Unique
    private void automlg$waterPickup(PlayerEntity player, World world) {
        if (automlg$waterPickupTimer < 0) return;
        automlg$waterPickupTimer--;
        if (automlg$waterPickupTimer > 0) return;

        if (AutoMLGConfig.waterPickupEnabled) {
            BlockPos pos = automlg$waterPickupPos;
            BlockState state = world.getBlockState(pos);
            boolean isWater = state.getFluidState().getFluid() == Fluids.WATER;
            boolean holdingBucket = player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BUCKET);

            if (isWater && holdingBucket) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

        automlg$waterPickupTimer = -1;
        automlg$waterPickupPos = null;
    }

    @Unique
    private void automlg$placeSnow(PlayerEntity player, World world) {
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos pos = player.getBlockPos();
        BlockState here = world.getBlockState(pos);
        if (!here.isAir()) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        world.setBlockState(pos, Blocks.POWDER_SNOW.getDefaultState());
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW, SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$snowPickupPos = pos;
        automlg$snowPickupTimer = 2;
    }

    @Unique
    private void automlg$snowPickup(PlayerEntity player, World world) {
        if (automlg$snowPickupTimer < 0) return;
        automlg$snowPickupTimer--;
        if (automlg$snowPickupTimer > 0) return;

        if (AutoMLGConfig.snowPickupEnabled) {
            BlockPos pos = automlg$snowPickupPos;
            BlockState state = world.getBlockState(pos);
            boolean isSnow = state.isOf(Blocks.POWDER_SNOW);
            boolean holdingBucket = player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BUCKET);

            if (isSnow && holdingBucket) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.POWDER_SNOW_BUCKET));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

        automlg$snowPickupTimer = -1;
        automlg$snowPickupPos = null;
    }

    @Unique
    private void automlg$boatMLG(PlayerEntity player, World world) {
        if (automlg$boat != null) return;
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos below = player.getBlockPos().down();
        BlockState belowState = world.getBlockState(below);
        if (!belowState.isAir() && belowState.getFluidState().isEmpty()) return;

        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        BoatEntity boat = new BoatEntity(world, player.getX(), player.getY(), player.getZ());
        boat.setYaw(player.getYaw());
        boat.setNoGravity(true);
        boat.setInvulnerable(true);
        boat.setSilent(true);
        world.spawnEntity(boat);
        player.startRiding(boat, true);

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$boat = boat;
        automlg$boatTimer = 1;
    }

    @Unique
    private void automlg$boatCleanup(PlayerEntity player, World world) {
        if (automlg$boatTimer < 0) return;
        automlg$boatTimer--;
        if (automlg$boatTimer >= 0) return;

        if (automlg$boat != null) {
            player.stopRiding();
            automlg$boat.discard();
            automlg$boat = null;
        }
        automlg$boatTimer = -1;
    }
}
