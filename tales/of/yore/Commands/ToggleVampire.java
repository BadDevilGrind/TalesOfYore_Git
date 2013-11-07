package tales.of.yore.Commands;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.Player;
import tales.of.yore.TalesOfYore;
import tales.of.yore.VampirePlayerExtender;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;

public class ToggleVampire implements ICommand {

	private List aliases;
	public ToggleVampire()
	{
		this.aliases = new ArrayList();
		this.aliases.add("vampire");
		this.aliases.add("vamp");
		this.aliases.add("vampireToggle");
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "vampireToggle";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "vampireToggle";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		System.out.println("Vampire command started");
		if(astring.length == 0)
		{
			EntityPlayer player;
			VampirePlayerExtender vampire;
	        
	        if(icommandsender instanceof EntityPlayer)
	        {
	        		
	                player = (EntityPlayer)icommandsender;
	                vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);
	                vampire.changeVampire((Player)icommandsender);
	                
	                System.out.println("Vampire command used.");
	                System.out.println(icommandsender.getCommandSenderName());
	                player.addStat(TalesOfYore.vampirism, 1); 

	        }
			return;
		}
		 
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		// TODO Auto-generated method stub
		return false;
	}

}
