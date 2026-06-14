package com.zen.automlg.mixin;

import com.zen.automlg.AutoMLGConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
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

    @Unique private int automlg$hayBalePickupTimer = -1;
    @Unique private BlockPos automlg$hayBalePickupPos = null;

    @Unique private int automlg$cobwebPickupTimer = -1;
    @Unique private BlockPos automlg$cobwebPickupPos = null;

    @Unique private int automlg$sweetBerryPickupTimer = -1;
    @Unique private BlockPos automlg$sweetBerryPickupPos = null;

    @Unique private int automlg$vehicleTimer = -1;
    @Unique private Entity automlg$vehicle = null;

    @Inject(method = "tick", at = @At("head"))
    private void automlg$onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getWorld();

        if (world.isClient) return;
        if (player.getAbilities().creativeMode) return;
        if (player.isSpectator()) return;

        automlg$waterPickup(player, world);
        automlg$snowPickup(player, world);
        automlg$hayBalePickup(player, world);
        automlg$cobwebPickup(player, world);
        automlg$sweetBerryPickup(player, world);
        automlg$vehicleCleanup(player, world);

        ItemStack main = player.getStackInHand(Hand.MAIN_HAND);

        if (AutoMLGConfig.boatEnabled && main.getItem() instanceof BoatItem) {
            automlg$vehicleMLG(player, world, false);
        }
        if (AutoMLGConfig.horseEnabled && main.isOf(Items.SADDLE)) {
            automlg$vehicleMLG(player, world, true);
        }
        if (AutoMLGConfig.waterEnabled && main.isOf(Items.WATER_BUCKET)) {
            automlg$placeWater(player, world);
        }
        if (AutoMLGConfig.snowEnabled && main.isOf(Items.POWDER_SNOW_BUCKET)) {
            automlg$placeSnow(player, world);
        }
        if (AutoMLGConfig.hayBaleEnabled && main.isOf(Items.HAY_BLOCK)) {
            automlg$placeHayBale(player, world);
        }
        if (AutoMLGConfig.cobwebEnabled && main.isOf(Items.COBWEB)) {
            automlg$placeCobweb(player, world);
        }
        if (AutoMLGConfig.sweetBerryEnabled && main.isOf(Items.SWEET_BERRIES)) {
            automlg$placeSweetBerryBush(player, world);
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
    private void automlg$placeHayBale(PlayerEntity player, World world) {
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos pos = player.getBlockPos();
        BlockState here = world.getBlockState(pos);
        if (!here.isAir()) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        BlockState place = Blocks.HAY_BLOCK.getDefaultState();
        world.setBlockState(pos, place);
        automlg$consumeOne(player);
        world.playSound(null, pos, place.getSoundGroup().getPlaceSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$hayBalePickupPos = pos;
        automlg$hayBalePickupTimer = 2;
    }

    @Unique
    private void automlg$hayBalePickup(PlayerEntity player, World world) {
        if (automlg$hayBalePickupTimer < 0) return;
        automlg$hayBalePickupTimer--;
        if (automlg$hayBalePickupTimer > 0) return;

        if (AutoMLGConfig.hayBalePickupEnabled) {
            BlockPos pos = automlg$hayBalePickupPos;
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.HAY_BLOCK)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                automlg$returnOne(player, Items.HAY_BLOCK);
                world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

        automlg$hayBalePickupTimer = -1;
        automlg$hayBalePickupPos = null;
    }

    @Unique
    private void automlg$placeCobweb(PlayerEntity player, World world) {
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos pos = player.getBlockPos();
        BlockState here = world.getBlockState(pos);
        if (!here.isAir()) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        BlockState place = Blocks.COBWEB.getDefaultState();
        world.setBlockState(pos, place);
        automlg$consumeOne(player);
        world.playSound(null, pos, place.getSoundGroup().getPlaceSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$cobwebPickupPos = pos;
        automlg$cobwebPickupTimer = 2;
    }

    @Unique
    private void automlg$cobwebPickup(PlayerEntity player, World world) {
        if (automlg$cobwebPickupTimer < 0) return;
        automlg$cobwebPickupTimer--;
        if (automlg$cobwebPickupTimer > 0) return;

        if (AutoMLGConfig.cobwebPickupEnabled) {
            BlockPos pos = automlg$cobwebPickupPos;
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.COBWEB)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                automlg$returnOne(player, Items.COBWEB);
                world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

        automlg$cobwebPickupTimer = -1;
        automlg$cobwebPickupPos = null;
    }

    @Unique
    private void automlg$placeSweetBerryBush(PlayerEntity player, World world) {
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;

        BlockPos pos = player.getBlockPos();
        BlockState here = world.getBlockState(pos);
        if (!here.isAir()) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        BlockState place = Blocks.SWEET_BERRY_BUSH.getDefaultState();
        world.setBlockState(pos, place);
        automlg$consumeOne(player);
        world.playSound(null, pos, place.getSoundGroup().getPlaceSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$sweetBerryPickupPos = pos;
        automlg$sweetBerryPickupTimer = 2;
    }

    @Unique
    private void automlg$sweetBerryPickup(PlayerEntity player, World world) {
        if (automlg$sweetBerryPickupTimer < 0) return;
        automlg$sweetBerryPickupTimer--;
        if (automlg$sweetBerryPickupTimer > 0) return;

        if (AutoMLGConfig.sweetBerryPickupEnabled) {
            BlockPos pos = automlg$sweetBerryPickupPos;
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.SWEET_BERRY_BUSH)) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                automlg$returnOne(player, Items.SWEET_BERRIES);
                world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }

        automlg$sweetBerryPickupTimer = -1;
        automlg$sweetBerryPickupPos = null;
    }

    @Unique
    private void automlg$consumeOne(PlayerEntity player) {
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        stack.decrement(1);
    }

    @Unique
    private void automlg$returnOne(PlayerEntity player, net.minecraft.item.Item item) {
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        if (stack.isEmpty()) {
            player.setStackInHand(Hand.MAIN_HAND, new ItemStack(item));
        } else if (stack.isOf(item) && stack.getCount() < stack.getMaxCount()) {
            stack.increment(1);
        } else {
            ItemStack extra = new ItemStack(item);
            if (!player.getInventory().insertStack(extra)) {
                player.dropItem(extra, false);
            }
        }
    }

    @Unique
    private void automlg$vehicleMLG(PlayerEntity player, World world, boolean horse) {
        if (automlg$vehicle != null) return;
        if (player.getVelocity().y >= -0.5) return;
        if (!automlg$fallenEnough(player)) return;
        if (!automlg$nearGround(player, world, GROUND_RANGE)) return;

        Entity vehicle;
        if (horse) {
            HorseEntity horseEntity = new HorseEntity(EntityType.HORSE, world);
            horseEntity.setPosition(player.getX(), player.getY(), player.getZ());
            horseEntity.setYaw(player.getYaw());
            horseEntity.setTame(true);
            horseEntity.setNoGravity(true);
            horseEntity.setInvulnerable(true);
            horseEntity.setSilent(true);
            vehicle = horseEntity;
        } else {
            BoatEntity boat = new BoatEntity(world, player.getX(), player.getY(), player.getZ());
            boat.setYaw(player.getYaw());
            boat.setNoGravity(true);
            boat.setInvulnerable(true);
            boat.setSilent(true);
            vehicle = boat;
        }

        world.spawnEntity(vehicle);
        player.startRiding(vehicle, true);

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.PLAYERS, 1.0f, 1.0f);

        automlg$vehicle = vehicle;
        automlg$vehicleTimer = 1;
    }

    @Unique
    private void automlg$vehicleCleanup(PlayerEntity player, World world) {
        if (automlg$vehicleTimer < 0) return;
        automlg$vehicleTimer--;
        if (automlg$vehicleTimer >= 0) return;

        if (automlg$vehicle != null) {
            player.stopRiding();
            automlg$vehicle.discard();
            automlg$vehicle = null;
        }
        automlg$vehicleTimer = -1;
    }
}
