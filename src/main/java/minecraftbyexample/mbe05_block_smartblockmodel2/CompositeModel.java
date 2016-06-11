package minecraftbyexample.mbe05_block_smartblockmodel2;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.Attributes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TheGreyGhost on 22/04/2015.
 * CompositeModel is the IBakedModel used to create a custom IBakedModel based on the web block's IBlockState
 * The Model is constructed from the various models corresponding to the subcomponents:
 * 1) "core" for the solid core in the middle of the web
 * 2) up, down, north, south, east, west for each "stem" that extends from the core in the centre to the edge of the block
 *   (eg the up model connects the core to the top face of the block)
 * An IBakedModel is just a list of quads; the composite model is created by concatenating the quads from the relevant
 *   subcomponents.
 *
 *   BEWARE! Rendering is multithreaded so your IBakedModel must be thread-safe, preferably immutable.

 */
public class CompositeModel implements IBakedModel {

  public CompositeModel(IBakedModel i_modelCore, IBakedModel i_modelUp, IBakedModel i_modelDown,
                        IBakedModel i_modelWest, IBakedModel i_modelEast,
                        IBakedModel i_modelNorth, IBakedModel i_modelSouth)
  {
    modelCore = i_modelCore;
    modelUp = i_modelUp;
    modelDown = i_modelDown;
    modelWest = i_modelWest;
    modelEast = i_modelEast;
    modelNorth = i_modelNorth;
    modelSouth = i_modelSouth;
  }

  private IBakedModel modelCore;
  private IBakedModel modelUp;
  private IBakedModel modelDown;
  private IBakedModel modelWest;
  private IBakedModel modelEast;
  private IBakedModel modelNorth;
  private IBakedModel modelSouth;

  @Override
  public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
  {
    List<BakedQuad> allGeneralQuads = new LinkedList<BakedQuad>();
    allGeneralQuads.addAll(modelCore.getQuads(state, side, rand));
    if(side!=null)
    {
      if (side == EnumFacing.UP)
      {
        allGeneralQuads.addAll(modelUp.getQuads(state, side, rand));
      }
      if (side == EnumFacing.DOWN)
      {
        allGeneralQuads.addAll(modelDown.getQuads(state, side, rand));
      }
      if (side == EnumFacing.WEST)
      {
        allGeneralQuads.addAll(modelWest.getQuads(state, side, rand));
      }
      if (side == EnumFacing.EAST)
      {
        allGeneralQuads.addAll(modelEast.getQuads(state, side, rand));
      }
      if (side == EnumFacing.NORTH)
      {
        allGeneralQuads.addAll(modelNorth.getQuads(state, side, rand));
      }
      if (side == EnumFacing.SOUTH)
      {
        allGeneralQuads.addAll(modelSouth.getQuads(state, side, rand));
      }
    }
    return allGeneralQuads;
  }

  @Override
  public boolean isAmbientOcclusion() {
    return modelCore.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return modelCore.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return false;
  }

  // used for block breaking shards
  @Override
  public TextureAtlasSprite getParticleTexture() {
    TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks()
                                                     .getAtlasSprite("minecraftbyexample:blocks/mbe05_block_3d_web");

    return textureAtlasSprite;
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return modelCore.getItemCameraTransforms();
  }

  @Override
  public ItemOverrideList getOverrides()
  {
    return null;
  }

//  @Override
//  public List<BakedQuad> getFaceQuads(EnumFacing side) {
//    //This should never be called!  The handleBlockState returns an AssembledBakedModel instead of CompositeModel
//    throw new UnsupportedOperationException();
//  }
//
//  @Override
//  public List<BakedQuad> getGeneralQuads() {
//    //This should never be called!  The handleBlockState returns an AssembledBakedModel instead of CompositeModel
//    throw new UnsupportedOperationException();
//  }
//
//  // returns the vertex format for this model (each vertex in the list of quads can have a variety of information
//  //   associated with it - for example, not just position and texture information, but also colour, world brightness,
//  //   etc.)  Just use DEFAULT_BAKED_FORMAT unless you really know what you're doing...
//  @Override
//  public VertexFormat getFormat() {
//    return Attributes.DEFAULT_BAKED_FORMAT;
//  }

  // This method is used to create a suitable IBakedModel based on the IBlockState of the block being rendered.
  // If IBlockState is an instance of IExtendedBlockState, you can use it to pass in any information you want.
  // You must return a new IBakedModel instance each time because the rendering code is multithreaded.
  //   Alternatively, you can return a cached immutable instance.
  // But you can't just set some member variables and return "this", because the multiple rendering threads may
  //   cause the model in one thread to be overwritten by the other thread.  Thanks to Herbix for pointing this out.

  // TODO 1.9.4
  //@Override
  public IBakedModel handleBlockState(IBlockState iBlockState) {
    if (iBlockState instanceof IExtendedBlockState) {
      IExtendedBlockState iExtendedBlockState = (IExtendedBlockState) iBlockState;
      return new AssembledBakedModel(iExtendedBlockState);
    }
    return new AssembledBakedModel();
  }

  // AssembledBakedModel represents the model for this particular blockstate
  // I have implemented it as an inner class but it doesn't have to be.
  public class AssembledBakedModel implements IBakedModel
  {
    public AssembledBakedModel()
    {
      // default is all false
    }

    @Override
    public ItemOverrideList getOverrides()
    {
      // TODO
      return null;
    }

    public AssembledBakedModel(IExtendedBlockState iExtendedBlockState)
    {
      Boolean linkUp = iExtendedBlockState.getValue(Block3DWeb.LINK_UP);
      if (linkUp != null) {
        up = linkUp;
      }
      Boolean linkDown = iExtendedBlockState.getValue(Block3DWeb.LINK_DOWN);
      if (linkDown != null) {
        down = linkDown;
      }
      Boolean linkWest = iExtendedBlockState.getValue(Block3DWeb.LINK_WEST);
      if (linkWest != null) {
        west = linkWest;
      }
      Boolean linkEast = iExtendedBlockState.getValue(Block3DWeb.LINK_EAST);
      if (linkEast != null) {
        east = linkEast;
      }
      Boolean linkNorth = iExtendedBlockState.getValue(Block3DWeb.LINK_NORTH);
      if (linkNorth != null) {
        north = linkNorth;
      }
      Boolean linkSouth = iExtendedBlockState.getValue(Block3DWeb.LINK_SOUTH);
      if (linkSouth != null) {
        south = linkSouth;
      }
    }

    private boolean up = false;
    private boolean down = false;
    private boolean west = false;
    private boolean east = false;
    private boolean north = false;
    private boolean south = false;

    // quads that correspond to faces of the cube (up, down, north, south, east, west).  A face may be hidden if
    //   the model's 'cullface' flag is true and it touches the adjacent block
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
      List<BakedQuad> allFaceQuads = new LinkedList<BakedQuad>();
      allFaceQuads.addAll(modelCore.getQuads(state, side, rand));
      if (up) {
        allFaceQuads.addAll(modelUp.getQuads(state, side, rand));
      }
      if (down) {
        allFaceQuads.addAll(modelDown.getQuads(state, side, rand));
      }
      if (west) {
        allFaceQuads.addAll(modelWest.getQuads(state, side, rand));
      }
      if (east) {
        allFaceQuads.addAll(modelEast.getQuads(state, side, rand));
      }
      if (north) {
        allFaceQuads.addAll(modelNorth.getQuads(state, side, rand));
      }
      if (south) {
        allFaceQuads.addAll(modelSouth.getQuads(state, side, rand));
      }
      return allFaceQuads;
    }

    @Override
    public boolean isAmbientOcclusion() {
      return modelCore.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
      return modelCore.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
      return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
      return modelCore.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
      return modelCore.getItemCameraTransforms();
    }

  }

}
