package minecraftbyexample.mbe04_block_smartblockmodel1;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by TheGreyGhost on 19/04/2015.
 * This class is used to customise the rendering of the camouflage block, based on the block it is copying.
 */
public class CamouflageBakedModelFactory implements IBakedModel {

  public CamouflageBakedModelFactory(IBakedModel unCamouflagedModel)
  {
    modelWhenNotCamouflaged = unCamouflagedModel;
  }

  // create a tag (ModelResourceLocation) for our model.
  public static final ModelResourceLocation modelResourceLocation
          = new ModelResourceLocation("minecraftbyexample:mbe04_block_camouflage");

  @SuppressWarnings("deprecation")  // IBakedModel is deprecated to encourage folks to use IFlexibleBakedModel instead
                                    // .. but IFlexibleBakedModel is of no use here...

  // This method is used to create a suitable IBakedModel based on the IBlockState of the block being rendered.
  // If IBlockState is an instance of IExtendedBlockState, you can use it to pass in any information you want.
  // Some folks return a new instance of the same IBakedModel; I think it is more logical to return a different
  //   class which implements IBakedModel instead of IBakedModel, but it's a matter of taste.
  //  BEWARE! Rendering is multithreaded so your IBakedModel must be thread-safe, preferably immutable.
  @Override
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
  {
    IBakedModel retval = modelWhenNotCamouflaged;  // default
    IBlockState UNCAMOUFLAGED_BLOCK = Blocks.AIR.getDefaultState();

    // Extract the block to be copied from the IExtendedBlockState, previously set by Block.getExtendedState()
    // If the block is null, the block is not camouflaged so use the uncamouflaged model.
    if (state instanceof IExtendedBlockState) {
      IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) state;
      IBlockState copiedBlockIBlockState = iExtendedBlockState.getValue(BlockCamouflage.COPIEDBLOCK);

      if (copiedBlockIBlockState != UNCAMOUFLAGED_BLOCK) {
        // Retrieve the IBakedModel of the copied block and return it.
        Minecraft mc = Minecraft.getMinecraft();
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
        retval = blockModelShapes.getModelForState(copiedBlockIBlockState);
        // TODO:  Confirm this is the desired behavior in 1.9.4
        return retval.getQuads(copiedBlockIBlockState, side, rand);
      }
    }
    // TODO:  Confirm this is the desired behavior in 1.9.4
    return retval.getQuads(state, side, rand);
  }

  private IBakedModel modelWhenNotCamouflaged;

  // getTexture is used directly when player is inside the block.  The game will crash if you don't use something
  //   meaningful here.
  @Override
  public TextureAtlasSprite getParticleTexture() {
    return modelWhenNotCamouflaged.getParticleTexture();
  }

  // The methods below are all unused for CamouflageBakedModelFactory because we always return a vanilla model
  //  from handleBlockState.

  @Override
  public boolean isAmbientOcclusion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isGui3d() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isBuiltInRenderer() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ItemOverrideList getOverrides()
  {
    return null;
  }

}
