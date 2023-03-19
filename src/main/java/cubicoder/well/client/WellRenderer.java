package cubicoder.well.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import cubicoder.well.block.WellBlock;
import cubicoder.well.block.entity.WellBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.FluidStack;

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

			fluid.getFluid().getAttributes().getStillTexture(fluid);
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(fluid.getFluid().getAttributes().getStillTexture(fluid));

			int color = fluid.getFluid().getAttributes().getColor(well.getLevel(), well.getBlockPos());

			float corner = 3F / 16F;
			float height = WellBlock.getFluidRenderHeight(amount, capacity, upsideDown);

			float minU = sprite.getU(3);
			float maxU = sprite.getU(13);
			float minV = sprite.getV(3);
			float maxV = sprite.getV(13);
			
			poseStack.pushPose();
			if (upsideDown) {
				poseStack.translate(0.5D, 0, 0.5D);
				poseStack.mulPose(Direction.DOWN.getRotation());
				poseStack.translate(-0.5D, 0, -0.5D);
			}
			
			VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
			Matrix4f matrix = poseStack.last().pose();

			float yNormal = upsideDown ? -1 : 1;
			builder.vertex(matrix, corner, height, corner).color(color).uv(minU, minV).uv2(packedLight).normal(0, yNormal, 0).endVertex();;
			builder.vertex(matrix, corner, height, 1 - corner).color(color).uv(minU, maxV).uv2(packedLight).normal(0, yNormal, 0).endVertex();;
			builder.vertex(matrix, 1 - corner, height, 1 - corner).color(color).uv(maxU, maxV).uv2(packedLight).normal(0, yNormal, 0).endVertex();;
			builder.vertex(matrix, 1 - corner, height, corner).color(color).uv(maxU, minV).uv2(packedLight).normal(0, yNormal, 0).endVertex();;
			
			poseStack.popPose();
		}
	}

}
