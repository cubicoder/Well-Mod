package cubicoder.well.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import cubicoder.well.block.WellBlock;
import cubicoder.well.block.entity.WellBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.fluids.FluidStack;

public class WellRenderer extends TileEntityRenderer<WellBlockEntity> {

	
	public WellRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
		
	@Override
	public void render(WellBlockEntity well, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		FluidStack fluid = well.getTank().getFluid();
		if (!fluid.isEmpty()) {
			int amount = fluid.getAmount();
			int capacity = well.getTank().getCapacity();
			boolean upsideDown = well.isUpsideDown();

			fluid.getFluid().getAttributes().getStillTexture(fluid);
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS)
					.apply(fluid.getFluid().getAttributes().getStillTexture(fluid));

			int color = fluid.getFluid().getAttributes().getColor(well.getLevel(), well.getBlockPos());
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color) & 0xFF) / 255f;
			float a = ((color >> 24) & 0xFF) / 255f;

			float corner = 3F / 16F;
			float height = WellBlock.getFluidRenderHeight(amount, capacity, upsideDown);

			float minU = sprite.getU(3);
			float maxU = sprite.getU(13);
			float minV = sprite.getV(3);
			float maxV = sprite.getV(13);
			
			IVertexBuilder builder = buffer.getBuffer(RenderType.translucent());
			Matrix4f matrix = matrixStack.last().pose();
			
			if (upsideDown) {
				builder.vertex(matrix, 1 - corner, height, corner).color(r, g, b, a).uv(maxU, minV).uv2(combinedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, 1 - corner).color(r, g, b, a).uv(maxU, maxV).uv2(combinedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, corner, height, 1 - corner).color(r, g, b, a).uv(minU, maxV).uv2(combinedLight).normal(0, -1, 0).endVertex();
				builder.vertex(matrix, corner, height, corner).color(r, g, b, a).uv(minU, minV).uv2(combinedLight).normal(0, -1, 0).endVertex();
			} else {
				builder.vertex(matrix, corner, height, corner).color(r, g, b, a).uv(minU, minV).uv2(combinedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, corner, height, 1 - corner).color(r, g, b, a).uv(minU, maxV).uv2(combinedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, 1 - corner).color(r, g, b, a).uv(maxU, maxV).uv2(combinedLight).normal(0, 1, 0).endVertex();
				builder.vertex(matrix, 1 - corner, height, corner).color(r, g, b, a).uv(maxU, minV).uv2(combinedLight).normal(0, 1, 0).endVertex();
			}
		}
	}

}
