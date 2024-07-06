package cubicoder.well.client;

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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class WellRenderer implements BlockEntityRenderer<WellBlockEntity> {

	public WellRenderer(BlockEntityRendererProvider.Context context) {}
	
	@Override
	public void render(WellBlockEntity well, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		FluidStack fluidStack = well.getTank().getFluid();
		if (!fluidStack.isEmpty()) {
			int amount = fluidStack.getAmount();
			int capacity = well.getTank().getCapacity();
			boolean upsideDown = well.isUpsideDown();
			
			Fluid fluid = fluidStack.getFluid();
			IClientFluidTypeExtensions fluidEx = IClientFluidTypeExtensions.of(fluid);
			
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(fluidEx.getStillTexture(fluidStack));

			Level level = well.getLevel();
			BlockPos pos = well.getBlockPos();
			int color = fluidEx.getTintColor(fluid.getFluidType().getStateForPlacement(level, pos, fluidStack), level, pos);

			float corner = 3F / 16F;
			float height = WellBlock.getFluidRenderHeight(amount, capacity, upsideDown);

			float minU = sprite.getU(3F / 16F);
			float maxU = sprite.getU(13F / 16F);
			float minV = sprite.getV(3F / 16F);
			float maxV = sprite.getV(13F / 16F);
			
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
