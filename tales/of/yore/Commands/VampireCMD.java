package tales.of.yore.Commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tales.of.yore.VampirePlayerExtender;

public class VampireCMD implements ICommand {

	
	
	
	private List aliases;

	public VampireCMD()
	{
		this.aliases = new ArrayList();
		this.aliases.add("v");


	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "v";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "/v <cmd> <arg> <arg2>";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		System.out.println("[CMD] /v sent by " + icommandsender.getCommandSenderName());
		EntityPlayer player;
		VampirePlayerExtender vampire;
		
		if(icommandsender instanceof EntityPlayer)
        {
			
			if(astring.length == 0)
			{
				player = (EntityPlayer)icommandsender;
	            vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);
	            if (vampire.isVampire())
	            {
	            	player.addChatMessage("---------Vampire Status---------");
	            	player.addChatMessage("Blood level is: "+ vampire.currentBlood+"/"+vampire.maxBlood);
	            	if(vampire.age >= 10000*24000)player.addChatMessage("Age: Ancient.");
	            	else player.addChatMessage("Age: " + vampire.age/24000 + " minecraft days.");
	            	player.addChatMessage("Feeding mode: " + vampire.isFeeding()); 
	            	player.addChatMessage("Maker: " + vampire.getMaker());
	            	player.addChatMessage("Bloodline: " + vampire.getBloodline());
	            	
	            
	        		return;
	            }
	            else
	            {
	            	player.addChatMessage("---------Vampire Status---------");
	            	player.addChatMessage("Blood level is: "+ vampire.currentBlood+"/"+vampire.maxBlood);
	            	return;
	            }
			}
			else if(astring.length == 1)
			{
				player = (EntityPlayer)icommandsender;
            	vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);

            	if(vampire.isVampire())
            	{
					System.out.println("[CMD] argument used: " + astring[0]);
	
					if(astring[0].equals("feed"))
					{
	                	player.addChatMessage(vampire.feedingMode());
	    				System.out.println("[CMD] argument used: " + astring[0]);
	
					}
					if(astring[0].equals("msg"))
					{
						player.addChatMessage(vampire.toggleMessages());
					}
					if(astring[0].equals("vision"))
					{
						player.addChatMessage(vampire.setNightvision());
					}
            	}
            	else
            	{
            		player.addChatMessage("Not a vampire.");
            	}
				
			}
			else if(astring.length == 2)
			{
				player = (EntityPlayer)icommandsender;
            	vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);
            	World world = ((EntityPlayer) icommandsender).worldObj;
            	if(vampire.isVampire())
            	{
	            	if(astring[0].equals("age"))
	            	{
	            		int agee;
	            		try
	            		{
	            			agee = Integer.parseInt(astring[1]);
	            		}
	            		catch(Exception e)
	            		{
	            			System.out.print(e);
	            			return;
	            		}
	            		vampire.age = agee*24000;
	            	}
	            	
	            	if(astring[0].equals("ageUp"))
	            	{      
	            		int agee;
	            		try
	            		{
	            			agee = Integer.parseInt(astring[1]);
	            		}
	            		catch(Exception e)
	            		{
	            			System.out.print(e);
	            			return;
	            		}
	            		vampire.age += agee*24000;            		            		
	            	}
	            	if(astring[0].equals("ageDown"))
	            	{      
	            		int agee;
	            		try
	            		{
	            			agee = Integer.parseInt(astring[1]);
	            		}
	            		catch(Exception e)
	            		{
	            			System.out.print(e);
	            			return;
	            		}
	            		vampire.age -= agee*24000;            		            		
	            	}
	            	if(astring[0].equals("power"))
	            	{
	            		vampire.setPower(Integer.parseInt(astring[1]));
	            	}

            	}
            	else
            	{
            		player.addChatMessage("Not a vampire.");
            	}
            	
			}
			else if(astring.length == 3)
			{
				
				player = (EntityPlayer)icommandsender;
            	vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);
            	World world = ((EntityPlayer) icommandsender).worldObj;
            	if(astring[0].equals("offer"))
            	{
            		
            		EntityPlayer victim = world.getPlayerEntityByName(astring[1]);
            		try
            		{
            		vampire.askToTurn(player, victim, Integer.parseInt(astring[2]));
            		}catch(Exception e)
            		{
            			player.addChatMessage(e.toString());
            		}
            	}
			}
			else
			{
				player = (EntityPlayer)icommandsender;
				player.addChatMessage("Too many arguments.");
			}
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
