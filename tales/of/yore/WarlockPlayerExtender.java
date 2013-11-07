package tales.of.yore;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class WarlockPlayerExtender implements IExtendedEntityProperties {

	
	public final static String EXT_PROP_WARLOCK = "WarlockPlayerExtender";
	private final EntityPlayer player;
	private int currentPower = 100, maxPower = 100;
	private int corruption = 0;
	private String learnedRituals = "";
	private boolean isWarlock = false;
	
	
	public WarlockPlayerExtender(EntityPlayer player)
	{		
		this.player = player;
	}
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(WarlockPlayerExtender.EXT_PROP_WARLOCK, new WarlockPlayerExtender(player));
	}
	
	public static final WarlockPlayerExtender get(EntityPlayer player)
	{
		return (WarlockPlayerExtender) player.getExtendedProperties(EXT_PROP_WARLOCK);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		
		
		properties.setString("learnedRituals", learnedRituals);
		
		
		compound.setTag(EXT_PROP_WARLOCK, properties);

	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_WARLOCK);
		
		this.learnedRituals = properties.getString("learnedRituals");
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub
		
	}

}
