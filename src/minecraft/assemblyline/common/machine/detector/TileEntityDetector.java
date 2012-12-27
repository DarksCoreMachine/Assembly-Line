package assemblyline.common.machine.detector;

import java.util.ArrayList;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import assemblyline.common.AssemblyLine;

public class TileEntityDetector extends TileEntity implements IInventory, IRedstoneProvider, IPacketReceiver
{
	private boolean powering = false;
	private boolean isInverted = false;
	private ItemStack[] containingItems = new ItemStack[27];

	@Override
	public void updateEntity()
	{
		int metadata = this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 3;
		AxisAlignedBB testArea = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord - 1, this.yCoord, this.xCoord + 1, this.yCoord, this.zCoord + 1);

		ArrayList<Entity> entities = (ArrayList<Entity>) this.worldObj.getEntitiesWithinAABB(EntityItem.class, testArea);
		boolean powerCheck = false;
		if (entities.size() > 0)
		{
			if (hasItems())
			{
				for (int i = 0; i < entities.size(); i++)
				{
					EntityItem e = (EntityItem) entities.get(i);
					ItemStack item = e.func_92014_d();
					boolean tFound = false;

					for (int ii = 0; ii < this.containingItems.length; ii++)
					{
						ItemStack compare = this.containingItems[ii];
						if (compare != null)
						{
							if (this.isInverted)
							{
								if (item.itemID == compare.itemID)
								{
									if (item.getItemDamage() == compare.getItemDamage())
									{
										if (item.hasTagCompound())
										{
											if (item.getTagCompound().equals(compare.getTagCompound()))
											{
												tFound = true;
												break;
											}
										}
										else
										{
											tFound = true;
											break;
										}
									}
								}
							}
							else
							{
								if (item.itemID == compare.itemID)
								{
									if (item.getItemDamage() == compare.getItemDamage())
									{
										if (item.hasTagCompound())
										{
											if (item.getTagCompound().equals(compare.getTagCompound()))
											{
												powerCheck = true;
												break;
											}
										}
										else
										{
											powerCheck = true;
											break;
										}
									}
								}
							}
						}
					}
					if (this.isInverted)
					{
						if (!tFound)
						{
							powerCheck = true;
							break;
						}
					}
				}
			}
			else
			{
				powerCheck = true;
			}
		}
		else
		{
			powerCheck = false;
		}
		if (powerCheck != this.powering)
		{
			this.powering = powerCheck;
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, AssemblyLine.blockDetector.blockID);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord + 1, this.zCoord, AssemblyLine.blockDetector.blockID);
			for (int x = this.xCoord - 1; x <= this.xCoord + 1; x++)
			{
				for (int z = this.zCoord - 1; z <= this.zCoord + 1; z++)
				{
					this.worldObj.notifyBlocksOfNeighborChange(x, this.yCoord + 1, z, AssemblyLine.blockDetector.blockID);
				}
			}
		}
	}

	public boolean hasItems()
	{
		for (int i = 0; i < this.containingItems.length; i++)
		{
			if (this.containingItems[i] != null) { return true; }
		}

		return false;
	}

	public boolean isInverted()
	{
		return isInverted;
	}

	public void setInversion(boolean inverted)
	{
		this.isInverted = inverted;

		if (this.worldObj.isRemote)
		{
			PacketManager.sendPacketToClients(this.getDescriptionPacket());
		}
	}

	public void toggleInversion()
	{
		this.setInversion(!this.isInverted);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketManager.getPacket(AssemblyLine.CHANNEL, this, this.isInverted);
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (this.worldObj.isRemote)
		{
			try
			{
				this.isInverted = dataStream.readBoolean();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList itemList = tag.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < itemList.tagCount(); ++i)
		{
			NBTTagCompound itemAt = (NBTTagCompound) itemList.tagAt(i);
			int itemSlot = itemAt.getByte("Slot") & 255;

			if (itemSlot >= 0 && itemSlot < this.containingItems.length)
			{
				this.containingItems[itemSlot] = ItemStack.loadItemStackFromNBT(itemAt);
			}
		}

		this.isInverted = tag.getBoolean("isInverted");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		NBTTagList itemList = new NBTTagList();

		for (int i = 0; i < this.containingItems.length; ++i)
		{
			if (this.containingItems[i] != null)
			{
				NBTTagCompound itemAt = new NBTTagCompound();
				itemAt.setByte("Slot", (byte) i);
				this.containingItems[i].writeToNBT(itemAt);
				itemList.appendTag(itemAt);
			}
		}

		tag.setTag("Items", itemList);
		tag.setBoolean("isInverted", this.isInverted);
	}

	@Override
	public int getSizeInventory()
	{
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return containingItems[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (this.containingItems[slot] != null)
		{
			ItemStack var3;

			if (this.containingItems[slot].stackSize <= amount)
			{
				var3 = this.containingItems[slot];
				this.containingItems[slot] = null;
				this.onInventoryChanged();
				return var3;
			}
			else
			{
				var3 = this.containingItems[slot].splitStack(amount);

				if (this.containingItems[slot].stackSize == 0)
				{
					this.containingItems[slot] = null;
				}

				this.onInventoryChanged();
				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.containingItems[slot] != null)
		{
			ItemStack var2 = this.containingItems[slot];
			this.containingItems[slot] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.containingItems[slot] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	@Override
	public String getInvName()
	{
		return TranslationHelper.getLocal("tile.detector.name");
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
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

	@Override
	public boolean isPoweringTo(ForgeDirection side)
	{
		return this.powering;
	}

	@Override
	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.isPoweringTo(side);
	}

}
