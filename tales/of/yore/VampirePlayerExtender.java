package tales.of.yore;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class VampirePlayerExtender implements IExtendedEntityProperties {

	public final static String EXT_PROP_VAMPIRE = "VampirePlayerExtender";
	public boolean isVampire = false, feedingMode = false, ageMode = false;
	private final EntityPlayer player;
	public int currentBlood, maxBlood, age;
	private String maker = "", bloodline = "";
	private boolean isOriginal = false;
	private int ritualStage = 0; 
	public int healthTicker = 0;
	private boolean nightVision = false;
	private boolean wantsMessages = true;
	private int power = 100; //In percentage
	
	public VampirePlayerExtender(EntityPlayer player)
	{
		this.player = player;
		this.currentBlood = 0;
		this.maxBlood = 72000;
		this.power = 100;
	}
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(VampirePlayerExtender.EXT_PROP_VAMPIRE, new VampirePlayerExtender(player));
	}
	public static final VampirePlayerExtender get(EntityPlayer player)
	{
		return (VampirePlayerExtender) player.getExtendedProperties(EXT_PROP_VAMPIRE);
	}
	
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setInteger("CurrentBlood", this.currentBlood);
		properties.setInteger("MaxBlood", this.currentBlood);
		properties.setInteger("Age", this.age);
		properties.setBoolean("isVampire", this.isVampire);
		properties.setBoolean("feedingMode", this.feedingMode);
		properties.setBoolean("ageMode", this.ageMode);
		properties.setString("maker", this.maker);
		properties.setString("bloodline", this.bloodline);
		properties.setBoolean("isOriginal", this.isOriginal);
		properties.setBoolean("wantsMessages", this.wantsMessages);
		properties.setBoolean("nightvision", this.nightVision);
		properties.setInteger("power", this.power);
		
		
		compound.setTag(EXT_PROP_VAMPIRE, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_VAMPIRE);
		this.currentBlood = properties.getInteger("CurrentBlood");
		this.maxBlood = properties.getInteger("MaxBlood");
		this.age = properties.getInteger("Age");
		this.isVampire = properties.getBoolean("isVampire");
		this.ageMode = properties.getBoolean("ageMode");
		this.feedingMode = properties.getBoolean("feedingMode");
		this.maker = properties.getString("maker");
		this.bloodline = properties.getString("bloodline");
		this.isOriginal = properties.getBoolean("isOriginal");
		this.wantsMessages = properties.getBoolean("wantsMessages");
		this.nightVision = properties.getBoolean("nightvision");
		this.power = properties.getInteger("power");
		
		
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}
	
	
	//Blood related methods start here---------------------------------------------------------------
	public boolean consumeBlood(int amount)
	{
		boolean sufficient = amount <= this.currentBlood;
		
		this.currentBlood -= (amount < this.currentBlood ? amount : this.currentBlood);
		
		return sufficient;
		
	}
	public void replenishBlood(int amount)
	{
		if (this.currentBlood + amount >= this.maxBlood)
		{
			this.currentBlood = this.maxBlood;
		}
		else
		{
			this.currentBlood += amount;
		}
			
		
	}
	//And age here-----------------------------------------------------------------------------------
	public void ageUp()
	{
		this.age += 1;
	}
	public void changeVampire(Player player)
	{
		if (this.isVampire)
		{
			resetStats();
			isVampire = false;
			((EntityPlayer)player).addChatMessage("You have been cured by divine intervention.");
		}
		else
		{
			this.currentBlood = 72000;
			isVampire = true;
			((EntityPlayer)player).addChatMessage("You have been cursed by the gods.");
		}
		requestPacket(player);
	}
	public void resetStats()
	{
		this.age = 0;
		this.bloodline = "";
		this.currentBlood = this.maxBlood;
		this.feedingMode = false;
		this.isOriginal = false;
		this.maker = "";
		this.nightVision = false;
		this.ritualStage = 0;
		this.wantsMessages = false;
		this.power = 100;		
		this.currentBlood = 0;
	}
	
	public float getPower()
	{
		return this.power;
	}
	public void setPower(int par1)
	{
		this.power = Math.min(par1, 100);
	}
	public boolean isVampire()
	{
		return isVampire;	
	}

	public boolean isFeeding()
	{
		return feedingMode;
	}

	public String feedingMode()
	{
		if(!feedingMode)feedingMode = true;
		else feedingMode = false;
		return "Feeding mode status is: " + feedingMode;
		
	}
	
	public boolean getNightvision()
	{
		return this.nightVision;
	}
	public String setNightvision()
	{
		if(!nightVision)nightVision = true;
		else nightVision = false;
		return "Nightvision mode is: " + nightVision;
	}
	
	
	public void requestPacket(Player player)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(9);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try 
		{
	        outputStream.writeBoolean(isVampire);
	        outputStream.writeInt(age);
	        outputStream.writeInt(power);
	    //    System.out.println(isVampire);
		}
		catch (Exception ex)
		{
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "VampireChannel";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		PacketDispatcher.sendPacketToPlayer(packet, player);
		//System.out.println("Packet data is: " + packet.data[1]);
		//System.out.println(player);

		
	}
	public void sendFoodPacket(Player player, int food)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try 
		{
				outputStream.writeInt(food);
	    //    System.out.println(isVampire);
		}
		catch (Exception ex)
		{
	        ex.printStackTrace();
		}
		
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "VampireChannel";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		PacketDispatcher.sendPacketToServer(packet);
		
	}
	
	public void setMakerAndBloodline(String maker, String bloodline)
	{
		this.maker = maker;
		this.bloodline = bloodline;
	}

	
	public void askToTurn(EntityPlayer maker, EntityPlayer victim, int amount)
	{
		if(((Entity)maker).getDistanceToEntity((Entity)victim) > 5)
		{
			maker.addChatMessage("You are too far away.");
		}
		else
		{
			if(consumeBlood(amount*1000))
			{
				VampirePlayerExtender vampVictim = VampirePlayerExtender.get(victim);
				vampVictim.replenishBlood(30000);
				victim.addChatMessage(maker.username + " just fed you his blood.");
				maker.addChatMessage("You just fed " + victim + " " + amount*1000 + " units of blood.");
				vampVictim.maker = maker.username;
				vampVictim.bloodline = this.bloodline;
			}
			else
			{
				maker.addChatMessage("You do not have enough blood.");
			}
		}
	}

	public int ritualStage()
	{
		return ritualStage;
	}
	public void setRitualStage(int stage)
	{
		ritualStage = stage;
	}
	
	public void originalRitual(EntityPlayer player, World world, int x, int y, int z)
	{
		player.addChatMessage("The first price has been paid...");
		world.setBlock(x+3, y+1, z+3, 11);
		world.setBlock(x+3, y+1, z-3, 11);
		world.setBlock(x-3, y+1, z+3, 11);
		world.setBlock(x-3, y+1, z-3, 11);
		player.setHealth(1);
		this.ritualStage = 1;
		

	}
	public void finishRitual(EntityPlayer player, World world, int x, int y, int z)
	{
		player.addChatMessage("You feel different... You don't need to breathe, the sun hurts you, but you have succeeded...");
		player.addChatMessage("You have transcended death itself, at hopefully a not too high price...");
		this.isOriginal = true;
		this.maker = player.username;
		this.bloodline = player.username;
		this.changeVampire((Player)player);
		
		
		world.setBlock(x-3, y-1, z-3, Block.cobblestone.blockID);
		world.setBlock(x-3, y-1, z+3, Block.cobblestone.blockID);
		world.setBlock(x+3, y-1, z-3, Block.cobblestone.blockID);
		world.setBlock(x+3, y-1, z+3, Block.cobblestone.blockID);
		world.setBlock(x-3, y, z-3, Block.cobblestone.blockID);
		world.setBlock(x-3, y, z+3, Block.cobblestone.blockID);
		world.setBlock(x+3, y, z-3, Block.cobblestone.blockID);
		world.setBlock(x+3, y, z+3, Block.cobblestone.blockID);
		world.setBlock(x-3, y+1, z-3, Block.cobblestone.blockID);
		world.setBlock(x-3, y+1, z+3, Block.cobblestone.blockID);
		world.setBlock(x+3, y+1, z-3, Block.cobblestone.blockID);
		world.setBlock(x+3, y+1, z+3, Block.cobblestone.blockID);
		
		//Floor
		world.setBlock(x, y-2, z-2, Block.cobblestone.blockID);
		world.setBlock(x, y-2, z+2, Block.cobblestone.blockID);
		world.setBlock(x-2, y-2, z, Block.cobblestone.blockID);
		world.setBlock(x+2, y-2, z, Block.cobblestone.blockID);
		world.setBlock(x+2, y-2, z-2, Block.cobblestone.blockID);
		world.setBlock(x+2, y-2, z+2, Block.cobblestone.blockID);
		world.setBlock(x-2, y-2, z-2, Block.cobblestone.blockID);
		world.setBlock(x-2, y-2, z+2, Block.cobblestone.blockID);
		
        player.addStat(TalesOfYore.immortality, 1); 
		
		
	}
	public boolean isOriginal()
	{
		return this.isOriginal;
	}
	public String getMaker()
	{
		return this.maker;
	}
	public String getBloodline()
	{
		return this.bloodline;
	}
		
	
	public String toggleMessages()
	{
		if(this.wantsMessages)
		{
			this.wantsMessages = false;
			return "Messages will not be shown to you.";
		}
		else
		{
			this.wantsMessages = true;
			return"Messages will be shown to you.";
		}
		
	}
	public boolean returnMessages()
	{
		return this.wantsMessages;
	}
}
