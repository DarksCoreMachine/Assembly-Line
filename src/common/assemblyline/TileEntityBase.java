package assemblyline;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import universalelectricity.prefab.TileEntityAdvanced;
import universalelectricity.prefab.network.IPacketReceiver;

public abstract class TileEntityBase extends TileEntityAdvanced implements IPacketReceiver, IInventory
{
	/**
	 * The items this container contains.
	 */
	protected ItemStack[] containingItems = new ItemStack[this.getSizeInventory()];
	
	/**
	 * The amount of players using this tile entity.
	 */
	protected int playerUsing = 0;

	/**
	 * Is this tile entity locked?
	 */
	protected boolean locked = false;
	
	/**
	 * The owner of this tile entity.
	 */
	protected String owner = "";

	/*
	 *  { if (count++ >=
	 * 10) { count = 0; if (!worldObj.isRemote &&
	 * this.sendDataA() != null) { Packet packet =
	 * PacketManager.getPacket("asmLine", this,
	 * this.buildData(1));
	 * PacketManager.sendPacketToClients(packet,
	 * worldObj, Vector3.get(this), 40); } if
	 * (!worldObj.isRemote && this.sendDataG() !=
	 * null && this.isOpen) { Packet packet =
	 * PacketManager.getPacket("asmLine", this,
	 * this.buildData(0));
	 * PacketManager.sendPacketToClients(packet,
	 * worldObj, Vector3.get(this), 10); } } if
	 * (ticks++ % tickRate() >= 0 &&
	 * !isDisabled()) { this.tickedUpdate(); } }
	 */
	
	/**
	 * Inventory functions.
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.containingItems[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var3;

			if (this.containingItems[par1].stackSize <= par2)
			{
				var3 = this.containingItems[par1];
				this.containingItems[par1] = null;
				return var3;
			}
			else
			{
				var3 = this.containingItems[par1].splitStack(par2);

				if (this.containingItems[par1].stackSize == 0)
				{
					this.containingItems[par1] = null;
				}

				return var3;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.containingItems[par1] != null)
		{
			ItemStack var2 = this.containingItems[par1];
			this.containingItems[par1] = null;
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.containingItems[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest()
	{
		this.playerUsing++;
	}

	@Override
	public void closeChest()
	{
		this.playerUsing--;
	}

	/**
	 * NBT Data
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.locked = nbt.getBoolean("locked");
		this.owner = nbt.getString("Owner");

		NBTTagList var2 = nbt.getTagList("Items");
		this.containingItems = new ItemStack[this.getSizeInventory()];

		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound) var2.tagAt(var3);
			byte var5 = var4.getByte("Slot");

			if (var5 >= 0 && var5 < this.containingItems.length)
			{
				this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("locked", this.locked);
		nbt.setString("Owner", this.owner);

		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.containingItems.length; ++var3)
		{
			if (this.containingItems[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.containingItems[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		nbt.setTag("Items", var2);
	}
/*
	@Override
	public void handlePacketData(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		if (worldObj.isRemote)
		{
			try
			{
				int ID = dataStream.readInt();
				if (ID == 0)
				{
					this.guiPacket(network, packetType, packet, player, dataStream);
				}
				else if (ID == 1)
				{
					this.animationPacket(network, packetType, packet, player, dataStream);
				}
				else
				{

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used to read GUI only data sent to the TE
	 
	public abstract void guiPacket(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream);

	/**
	 * Used to read animation data for things that
	 * can be seen on a TE
	 
	public abstract void animationPacket(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream);

	/**
	 * Used to read data for none animation or gui
	 * server sent packets
	 *
	public abstract void otherPacket(NetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream);

	public Object[] buildData(int packetID)
	{
		Object[] data = new Object[1];

		if (packetID == 0)
		{
			data = new Object[this.sendDataG().length + 1];
			data[0] = packetID;
			for (int i = 0; i < this.sendDataG().length; i++)
			{
				data[i + 1] = this.sendDataG()[i];
			}
		}
		else if (packetID == 1)
		{
			data = new Object[this.sendDataA().length + 1];
			data[0] = packetID;
			for (int i = 0; i < this.sendDataA().length; i++)
			{
				data[i + 1] = this.sendDataA()[i];
			}
		}
		return data;
	}

	/**
	 * Array of data too be sent for animation
	 * updates
	 * 
	 * @return
	 *
	public abstract Object[] sendDataA();

	/**
	 * Array of data too be sent if the TE's GUI
	 * is open at the time
	 * 
	 * @return
	 *
	public abstract Object[] sendDataG();
	
	*/
}
