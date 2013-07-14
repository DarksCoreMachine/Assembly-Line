package assemblyline.common.machine;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import assemblyline.common.AssemblyLine;
import assemblyline.common.TabAssemblyLine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.core.api.INetworkPart;
import dark.library.machine.BlockMachine;

public class BlockAssembly extends BlockMachine
{
	public Icon machine_icon;

	public BlockAssembly(int id, Material material, String name)
	{
		super(AssemblyLine.CONFIGURATION.getBlock(name, id).getInt(), material);
		this.setUnlocalizedName(name);
		this.setCreativeTab(TabAssemblyLine.INSTANCE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister iconReg)
	{
		this.machine_icon = iconReg.registerIcon(AssemblyLine.PREFIX + "machine");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5)
	{
		return this.machine_icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2)
	{
		return this.machine_icon;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).refresh();
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof INetworkPart)
		{
			((INetworkPart) tileEntity).refresh();
		}
	}

}