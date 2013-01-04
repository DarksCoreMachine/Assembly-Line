package assemblyline.common.machine.encoder;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityEncoder extends TileEntityAdvanced implements IPacketReceiver, ISidedInventory
{
	private ItemStack disk;
	private IInventoryWatcher watcher;

	public TileEntityEncoder()
	{
		super();
	}

	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot == 0)
			return disk;
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (slot == 0)
		{
			if (amount >= 1)
			{
				ItemStack ret = disk.copy();
				disk = null;
				return ret;
			}
		}
		watcher.inventoryChanged();
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (slot == 0)
		{
			if (stack != null)
			{
				if (stack.stackSize > 1)
				{
					stack.stackSize = 1;
				}
			}
			disk = stack;
		}
		if (watcher != null)
			watcher.inventoryChanged();
	}

	@Override
	public String getInvName()
	{
		return "Encoder";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	public void setWatcher(IInventoryWatcher watcher)
	{
		this.watcher = watcher;
	}

	public IInventoryWatcher getWatcher()
	{
		return this.watcher;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return (side == ForgeDirection.UP) ? 1 : 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		if (this.disk != null)
		{
			NBTTagCompound diskNBT = new NBTTagCompound();
			this.disk.writeToNBT(diskNBT);
			nbt.setCompoundTag("disk", diskNBT);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		NBTTagCompound diskNBT = nbt.getCompoundTag("disk");

		if (diskNBT != null)
		{
			this.disk = ItemStack.loadItemStackFromNBT(diskNBT);
		}
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
		{
			// TODO: Get this to work and add commands to the stack
			int newAddCommandID = dataStream.readInt();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}