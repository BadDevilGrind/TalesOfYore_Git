package tales.of.yore.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import tales.of.yore.VampirePlayerExtender;

public class BottledBlood extends Item {

	public BottledBlood(int par1) {
		super(par1);
		setCreativeTab(CreativeTabs.tabFood);
		// TODO Auto-generated constructor stub
	}	
	 public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	    {
	        if (!par3EntityPlayer.capabilities.isCreativeMode)
	        {
	            --par1ItemStack.stackSize;
	        }

	        if (!par2World.isRemote)
	        {
	        	VampirePlayerExtender vampire = VampirePlayerExtender.get(par3EntityPlayer);
	        	if(vampire.isVampire())
	        	{
	        		if(par1ItemStack.getTagCompound().getString("purity") == "Vampire")
	        		{

		        	}
	        		else
	        		{
		        		vampire.replenishBlood(Integer.parseInt(par1ItemStack.getTagCompound().getString("amount")));
	        		}
	        	}
	        	if(!vampire.isVampire())
	        	{
	        		if(par1ItemStack.getTagCompound().getString("purity") == "Vampire")
	        		{
	        			vampire.replenishBlood(Integer.parseInt(par1ItemStack.getTagCompound().getString("amount")));
	        		}
	        		else
	        		{
	        			par3EntityPlayer.heal(4);
	        			par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.confusion.id, 200));
	        			par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));

	        		}
	        	}
	        }

	        return par1ItemStack.stackSize <= 0 ? new ItemStack(Item.glassBottle) : par1ItemStack;
	    }

	    /**
	     * How long it takes to use or consume an item
	     */
	    public int getMaxItemUseDuration(ItemStack par1ItemStack)
	    {
	        return 32;
	    }

	    /**
	     * returns the action that specifies what animation to play when the items is being used
	     */
	    public EnumAction getItemUseAction(ItemStack par1ItemStack)
	    {
	        return EnumAction.drink;
	    }

	    /**
	     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	     */
	    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	    {
	        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
	        return par1ItemStack;
	    }
	    
	    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
	        itemStack.stackTagCompound = new NBTTagCompound();
	    }
	    
		public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		    if (itemStack.stackTagCompound != null)
		    {
		    		String amount = itemStack.stackTagCompound.getString("amount");
		            String purity = itemStack.stackTagCompound.getString("purity");
	
		            list.add(EnumChatFormatting.ITALIC + purity);
		            list.add("Amount: " + amount);
		    }
		    
		}
	    
}
