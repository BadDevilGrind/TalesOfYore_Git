package tales.of.yore.Items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class Bloodtap extends Item {

	public Bloodtap(int par1) {
		super(par1);
		setUnlocalizedName("Bloodtap");
		setTextureName("talesofyore:Bloodtap");
		setMaxStackSize(1);
		
	}
	
	public void addInformation(ItemStack itemStack, EntityPlayer player,
            List list, boolean par4) {
    if (itemStack.stackTagCompound != null) {
            String type = itemStack.stackTagCompound.getString("type");
            String purity = itemStack.stackTagCompound.getString("purity");

            list.add(EnumChatFormatting.ITALIC + purity);
            list.add(type);
            
            
    }
}

}
