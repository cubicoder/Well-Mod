package cubicoder.well.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import cubicoder.well.block.WellBlock;
import cubicoder.well.block.entity.WellBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class WellRenderer implements BlockEntityRenderer<WellBlockEntity> {

	public WellRenderer(BlockEntityRendererProvider.Context context) {}
	
	@Override
	public void render(WellBlockEntity well, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		FluidStack fluid = well.getTank().getFluid();
		if (!fluid.isEmpty()) {
			int amount = fluid.getAmount();
			int capacity = well.getTank().getCapacity();
			boolean upsideDown = well.isUpsideDown();

			Level level = well.getLevel();
			BlockPos pos = well.getBlockPos();
			
			FluidType fluidType = fluid.getFluid().getFluidType();
			IClientFluidTypeExtensions fluidEx = IClientFluidTypeExtensions.of(fluidType.getStateForPlacement(level, pos, fluid));
			
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(fluidEx.getStillTexture(fluid));

			int color = fluidEx.getTintColor(fluidType.getStateForPlacement(level, pos, fluid), level, pos);

			float corner = 3F / 16F;
			float height = WellBlock.getFluidRenderHeight(amount, capacity, upsideDown);

			float minU = sprite.getU(3);
			float maxU = sprite.getU(13);
			float minV = sprite.getV(3);
			float maxV = sprite.getV(13);
			
			VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
			Matrix4f matrix = poseStack.last().pose();
			
			if (upsideDown) {
				builder.vertex(matrix, 1 - corner, height, corner).color(color).uv(maxU, minV).uv2(packedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, 1 - corner).color(color).uv(maxU, maxV).uv2(packedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, corner, height, 1 - corner).color(color).uv(minU, maxV).uv2(packedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, corner, height, corner).color(color).uv(minU, minV).uv2(packedLight).normal(0, -1, 0).endVertex();
			} else {
				builder.vertex(matrix, corner, height, corner).color(color).uv(minU, minV).uv2(packedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, corner, height, 1 - corner).color(color).uv(minU, maxV).uv2(packedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, 1 - corner).color(color).uv(maxU, maxV).uv2(packedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, corner).color(color).uv(maxU, minV).uv2(packedLight).normal(0, 1, 0).endVertex();
			}
			
		}
	}

}
