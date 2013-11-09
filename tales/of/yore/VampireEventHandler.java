package tales.of.yore;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.network.Player;

public class VampireEventHandler {
	
	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event)
	{
	/*
	Be sure to check if the entity being constructed is the correct type for the extended properties you're about to add! The null check may not be necessary - I only use it to make sure properties are only registered once per entity
	*/
	if (event.entity instanceof EntityPlayer && VampirePlayerExtender.get((EntityPlayer) event.entity) == null)
	// This is how extended properties are registered using our convenient method from earlier
		VampirePlayerExtender.register((EntityPlayer) event.entity);
	// That will call the constructor as well as cause the init() method
	// to be called automatically

	// If you didn't make the two convenient methods from earlier, your code would be
	// much uglier:
	if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties(VampirePlayerExtender.EXT_PROP_VAMPIRE) == null)
	event.entity.registerExtendedProperties(VampirePlayerExtender.EXT_PROP_VAMPIRE, new VampirePlayerExtender((EntityPlayer) event.entity));
	}	
	
	@ForgeSubscribe
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			EntityPlayer foodPlayer = (EntityPlayer)event.entity;

			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			player.requestPacket((Player)event.entity);
			player.maxBlood = 72000;
			
		}
	}	
	
	int ticker = 0;
	int secondTicker = 0;
	
	@ForgeSubscribe
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{			
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			World world = event.entityLiving.worldObj;
			if (!player.isVampire())
			{
				player.consumeBlood(4);
			}
			if (!player.isVampire() && player.currentBlood == 0 && player.getMaker() != "")
			{
				player.setMakerAndBloodline("", "");
			}
			if (player.isVampire())
			{	
				EntityPlayer foodPlayer = (EntityPlayer)event.entity;			
				if(!world.isRemote)
				{
					//"player.currentBlood/3600 - foodPlayer.getFoodStats().getFoodLevel()" is to be used if I want the food meter to represent currentBlood
					if(TalesOfYore.useFoodBar)foodPlayer.getFoodStats().addStats((player.currentBlood/3600 - foodPlayer.getFoodStats().getFoodLevel()), 20);
					else foodPlayer.getFoodStats().addStats(20, 20);
					foodPlayer.getFoodStats().onUpdate(foodPlayer);	
					foodPlayer.setAir(100);					
				
				if(player.getNightvision() && !world.isRemote)event.entityLiving.addPotionEffect(new PotionEffect(Potion.nightVision.id, 400, 0));
				
				
				if(foodPlayer.getHeldItem() != null && foodPlayer.getHeldItem().itemID == Item.ingotIron.itemID)
				{
					event.entity.setFire(1);
				}
				
				player.consumeBlood(1);
				
				if(player.age >= 10000*24000)
				{
					player.age = 10000*24000;
				}
				else
				{
					player.ageUp();	
				}
				player.requestPacket((Player)event.entity);

				//Petrification
				if(player.currentBlood <= 2000)
				{
					event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 2, 4));
					//event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 2, 1));
				}
				else if(player.currentBlood <= 0)
				{
					event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 2, 8));
					event.entityLiving.addPotionEffect(new PotionEffect(Potion.blindness.id, 2));
					//event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 2, 3));
				}
				
				//Vampiric Speed
				if(event.entity.isSprinting())
				{
					if(player.age >= (TalesOfYore.jumpLimit*100)*24000)
					{
						event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2, (3 + ((int)Math.round(TalesOfYore.jumpLimit)*2))*2));
					}
					else
					{
						event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 2, (3 + Math.round(player.age/240000))));					
					}
			    }
				
				if (event.entityLiving.isPotionActive(Potion.moveSlowdown)) 
				{
					if (event.entityLiving.getActivePotionEffect(Potion.moveSlowdown).getDuration()==0) 
					{
						event.entityLiving.removePotionEffect(Potion.moveSlowdown.id);
					}
				}
				
				if(ticker == 2400)
				{
					EntityPlayer pPlayer = (EntityPlayer)event.entityLiving;
					if (player.currentBlood <= 8000 && player.returnMessages())pPlayer.addChatMessage("The hunger is overwhelming...");
					ticker = 0;
				}
				else
				{
					ticker++;
				}
				
				if (secondTicker == 40)
				{
					if(player.currentBlood > 0)
					{
						if(player.isVampire())
						{
							EntityPlayer pPlayer = (EntityPlayer)event.entityLiving;
							pPlayer.heal(2);
							player.consumeBlood(500);
						}
						else
						{
							EntityPlayer pPlayer = (EntityPlayer)event.entityLiving;
							pPlayer.heal(2);
							player.consumeBlood(800);	
						}
					}
					secondTicker = 0;
				}
				else
				{
					secondTicker++;
				}
			  }//World remote check
				
			}
			//isVampire End
		}
	}
	
	@ForgeSubscribe
	public void noPassSilver(LivingUpdateEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{					
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			World world = event.entityLiving.worldObj;
			if (player.isVampire())
			{
					if(event.entityLiving.posX >= 0 && event.entityLiving.posZ >= 0 && world.getBlockId((int)event.entity.posX, (int)event.entity.posY-1, (int)event.entity.posZ) == Block.blockIron.blockID)
					{
						event.entity.setFire(1);
					}
					else if(event.entityLiving.posX >= 0 && event.entityLiving.posZ < 0 && world.getBlockId((int)event.entity.posX, (int)event.entity.posY-1, (int)event.entity.posZ-1) == Block.blockIron.blockID)
					{
						event.entity.setFire(1);
					}
					else if(event.entityLiving.posX < 0 && event.entityLiving.posZ >= 0 && world.getBlockId((int)event.entity.posX-1, (int)event.entity.posY-1, (int)event.entity.posZ) == Block.blockIron.blockID)
					{
						event.entity.setFire(1);
					}
					else if(event.entityLiving.posX < 0 && event.entityLiving.posZ < 0 && world.getBlockId((int)event.entity.posX-1, (int)event.entity.posY-1, (int)event.entity.posZ-1) == Block.blockIron.blockID)
					{
						event.entity.setFire(1);
					}				
			}
			
		}
	}
	

	@ForgeSubscribe
	public void onDaytimeBurn(LivingUpdateEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{					
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			World world = event.entityLiving.worldObj;
			if (player.isVampire())
			{	

				//Daytime burn				
				if (world.isDaytime() && !world.isRaining())
				{
					if (event.entityLiving.posX >= 0 && event.entityLiving.posZ >= 0 && world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY+1, (int)event.entity.posZ))
					{
						event.entity.setFire(1);
		
					}
					else if (event.entityLiving.posX >= 0 && event.entityLiving.posZ < 0 && world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY+1, (int)event.entity.posZ-1))
					{
						event.entity.setFire(1);
		
					}
					else if (event.entityLiving.posX < 0 && event.entityLiving.posZ < 0 && world.canBlockSeeTheSky((int)event.entity.posX-1, (int)event.entity.posY+1, (int)event.entity.posZ-1))
					{
						event.entity.setFire(1);
		
					}
					else if (event.entityLiving.posX < 0 && event.entityLiving.posZ >= 0 && world.canBlockSeeTheSky((int)event.entity.posX-1, (int)event.entity.posY+1, (int)event.entity.posZ))
					{
						event.entity.setFire(1);
					}
				}
			}
		}
	}
	
	
	
	//Handling death
	@ForgeSubscribe
	public void onDeath(LivingHurtEvent event)
	{
		if (event.entity instanceof EntityPlayer && !event.entityLiving.worldObj.isRemote)
		{			
			World world = event.entityLiving.worldObj;
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			System.out.println("DeathEvent " + world.isRemote+ " " + player.isOriginal());
			if(player.isOriginal() && event.entityLiving.getHealth() - event.ammount <= 0)
			{
				event.setCanceled(true);
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 400));	
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.weakness.id, 200));
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.blindness.id, 200));
			}
			if(!player.isVampire() && player.currentBlood > 0 && event.entityLiving.getHealth() - event.ammount <= 0 && event.source == DamageSource.inWall)
			{
				player.isVampire = true;				
				player.replenishBlood(15000);
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200));
				event.entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, 4000));
				event.setCanceled(true);
				
			}
			
			NBTTagCompound playerData = new NBTTagCompound();
			player.saveNBTData(playerData);
			CommonProxy.storeEntityData(((EntityPlayer)event.entity).username, playerData);
			
			
			
			
		}
	}
	
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			NBTTagCompound playerData = CommonProxy.getEntityData(((EntityPlayer) event.entity).username);
			
			if (playerData != null)
			{
				((VampirePlayerExtender)(event.entity.getExtendedProperties(VampirePlayerExtender.EXT_PROP_VAMPIRE))).loadNBTData(playerData);
				VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
				player.requestPacket((Player)event.entity);
				player.age = player.age / 2;
			}
		}
	}
	
	@ForgeSubscribe
	public void onFall(LivingFallEvent event)
	{
		
		if (event.entity instanceof EntityPlayer)
		{
			
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			World world = event.entityLiving.worldObj;
			if (player.isVampire())
			{
				event.setCanceled(true);
			}
		}
	}
	@ForgeSubscribe
	public void onJump(LivingJumpEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{		
				System.out.println(TalesOfYore.jumpLimit + " " + TalesOfYore.allowOriginals);
				World world = event.entity.worldObj;
				VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
				System.out.println(player.age);
				if (player.isVampire())
				{
					if (event.entity.isSneaking() || event.entity.isSprinting())
					{
						System.out.println(player.getPower()/100);
						if (player.age >= (TalesOfYore.jumpLimit*100)*24000){event.entity.motionY += TalesOfYore.jumpLimit*(float)(player.getPower()/100); System.out.println("Max age reached, jump: "+ TalesOfYore.jumpLimit);}
						else {event.entity.motionY += 0.3F + (double)player.age/2400000;	System.out.println("JumpEvent: "+ (0.3 + (double)player.age/2400000) *(player.getPower()/100));}
						event.entity.motionZ += event.entityLiving.motionZ*4;
						event.entity.motionX += event.entityLiving.motionX*4;
						System.out.println("Age: " + player.age + ". Bonus jump height: " + (double)player.age/2400000);
					}
				}
			
			
		}
	}	
	

	
	@ForgeSubscribe
	public void onUse(PlayerInteractEvent event)
	{
		
		VampirePlayerExtender vPlayer = VampirePlayerExtender.get(event.entityPlayer);

		
		List<Integer> itemList = new ArrayList<Integer>();
		itemList.add(Item.appleRed.itemID);
		itemList.add(Item.bread.itemID);
		itemList.add(Item.bakedPotato.itemID);
		itemList.add(Item.beefCooked.itemID);
		itemList.add(Item.beefRaw.itemID);
		itemList.add(Item.porkCooked.itemID);
		itemList.add(Item.porkRaw.itemID);
		itemList.add(Item.bowlSoup.itemID);
		itemList.add(Item.chickenCooked.itemID);
		itemList.add(Item.chickenRaw.itemID);
		itemList.add(Item.cookie.itemID);
		itemList.add(Item.fishCooked.itemID);
		itemList.add(Item.fishRaw.itemID);
		itemList.add(Item.melon.itemID);
		itemList.add(Item.rottenFlesh.itemID);
		itemList.add(Item.spiderEye.itemID);
		itemList.add(Item.carrot.itemID);
		itemList.add(Item.potato.itemID);
		itemList.add(Item.poisonousPotato.itemID);
		itemList.add(Item.pumpkinPie.itemID);
		itemList.add(Item.appleGold.itemID);
		
		
		//Draw blood
		if(event.action == Action.RIGHT_CLICK_AIR && event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().itemID == TalesOfYore.BloodtapEmpty.itemID && event.entityPlayer.isSneaking())
		{
			if(vPlayer.isVampire() && vPlayer.consumeBlood(2000))
			{			
				ItemStack itemStack = new ItemStack(TalesOfYore.Bloodtap);
				itemStack.stackTagCompound = new NBTTagCompound();
				itemStack.stackTagCompound.setBoolean("player", true);
				itemStack.stackTagCompound.setString("purity","Vampire");										
				itemStack.stackTagCompound.setString("type", event.entityPlayer.username);
				event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, itemStack);
				return;
			}
			else if(!vPlayer.isVampire())
			{
				ItemStack itemStack = new ItemStack(TalesOfYore.Bloodtap);
				itemStack.stackTagCompound = new NBTTagCompound();
				itemStack.stackTagCompound.setBoolean("player", true);
				itemStack.stackTagCompound.setString("purity","Pure");										
				itemStack.stackTagCompound.setString("type", event.entityPlayer.username);
				event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, itemStack);
				return;
			}
		}		

		//Can't eat food
		if(event.action == Action.RIGHT_CLICK_AIR && event.entityPlayer.getHeldItem() != null && vPlayer.isVampire() && itemList.contains(event.entityPlayer.getHeldItem().itemID))
		{ 
			event.setCanceled(true); 
			if(vPlayer.returnMessages())event.entityPlayer.addChatMessage("Vampires can't eat regular food!"); 
			return; 
		}
		
		//Can't sleep in beds
		if (event.action == Action.RIGHT_CLICK_BLOCK && vPlayer.isVampire() && event.entityLiving.worldObj.getBlockId(event.x, event.y, event.z) == Block.bed.blockID)
		{
			if(vPlayer.returnMessages())event.entityPlayer.addChatMessage("There can be no rest for the living dead...");
			event.setCanceled(true);
			return;
		}
		//Can't open doors with Iron under them
		if (event.action == Action.RIGHT_CLICK_BLOCK && vPlayer.isVampire() && event.entityLiving.worldObj.getBlockId(event.x, event.y, event.z) == Block.doorWood.blockID)
		{
			if (event.entityLiving.worldObj.getBlockId(event.x, event.y-1, event.z) == Block.doorWood.blockID)
			{
				if(event.entityLiving.worldObj.getBlockId(event.x, event.y-2, event.z) == Block.blockIron.blockID || event.entityLiving.worldObj.getBlockId(event.x, event.y-3, event.z) == Block.blockIron.blockID)
				{
					event.setCanceled(true);
					return;
				}
			}
			else if(event.entityLiving.worldObj.getBlockId(event.x, event.y+1, event.z) == Block.doorWood.blockID)
			{
				if(event.entityLiving.worldObj.getBlockId(event.x, event.y-1, event.z) == Block.blockIron.blockID || event.entityLiving.worldObj.getBlockId(event.x, event.y-2, event.z) == Block.blockIron.blockID)
				{
					event.setCanceled(true);
					return;
				}
			}
		}
		
		if(event.action == Action.RIGHT_CLICK_AIR && event.entityLiving.getHeldItem() != null && event.entityLiving.getHeldItem().itemID == TalesOfYore.Bloodtap.itemID && event.entityLiving.isSneaking())
		{
			ItemStack bottledBlood = new ItemStack(TalesOfYore.BottledBlood);
			bottledBlood.stackTagCompound = new NBTTagCompound();
			
			if(event.entityLiving.getHeldItem().getTagCompound().getString("purity") == "Vampire")
			{
				bottledBlood.stackTagCompound.setString("amount", "2000");
			}
			else if(event.entityLiving.getHeldItem().getTagCompound().getString("purity") == "Pure")
			{
				bottledBlood.stackTagCompound.setString("amount", "1500");
			}
			else if(event.entityLiving.getHeldItem().getTagCompound().getString("purity") == "Unpure")
			{
				bottledBlood.stackTagCompound.setString("amount", "1000");				
			}

			bottledBlood.stackTagCompound.setString("purity", event.entityLiving.getHeldItem().getTagCompound().getString("purity"));
			event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, bottledBlood);
			return;
		}
	}	
	
	
	@ForgeSubscribe
	public void onBloodFill(LivingHurtEvent event)
	{
		if(event.source.getSourceOfDamage() instanceof EntityPlayer)
		{
			if(!event.source.getSourceOfDamage().worldObj.isRemote)
			{
				
				EntityPlayer pPlayer = (EntityPlayer)event.source.getSourceOfDamage();
				if(pPlayer.getHeldItem() != null && pPlayer.getHeldItem().itemID == TalesOfYore.BloodtapEmpty.itemID)
				{
					
					
					List<String> pureBlood = new ArrayList<String>();
					
					pureBlood.add("Villager");
					pureBlood.add("Ender Dragon");
					
					if(event.entityLiving instanceof EntityPlayer)
					{
						VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entityLiving);
						if (player.consumeBlood(2000))
						{
							ItemStack itemStack = new ItemStack(TalesOfYore.Bloodtap);
							itemStack.stackTagCompound = new NBTTagCompound();
							itemStack.stackTagCompound.setBoolean("player", true);
							itemStack.stackTagCompound.setString("purity", "Vampire");										
							itemStack.stackTagCompound.setString("type", event.entityLiving.getEntityName());
							pPlayer.inventory.setInventorySlotContents(pPlayer.inventory.currentItem, itemStack);
							return;
						}
					}					
					
					ItemStack itemStack = new ItemStack(TalesOfYore.Bloodtap);
					itemStack.stackTagCompound = new NBTTagCompound();
					if(event.entityLiving instanceof EntityPlayer)itemStack.stackTagCompound.setBoolean("player", true);
					else itemStack.stackTagCompound.setBoolean("player", false);					
					if(pureBlood.contains(event.entityLiving.getEntityName()) || event.entityLiving instanceof EntityPlayer)itemStack.stackTagCompound.setString("purity", "Pure");
					else itemStack.stackTagCompound.setString("purity", "Unpure");										
					itemStack.stackTagCompound.setString("type", event.entityLiving.getEntityName());
					pPlayer.inventory.setInventorySlotContents(pPlayer.inventory.currentItem, itemStack);
					return;
				}
			}
		}
	}
	
	
	@ForgeSubscribe
	public void onDamage(LivingHurtEvent event)
	{
		if (event.entityLiving instanceof EntityPlayer)
		{
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entityLiving);
			if (player.isVampire())
			{
				if(event.source == DamageSource.inWall || event.source == DamageSource.starve)
				{
					event.setResult(Result.DENY);
					event.setCanceled(true);					
				}
				if(event.source == DamageSource.onFire)
				{
					event.entityLiving.attackEntityFrom(DamageSource.inFire, 3);
				}
				if(event.source.getSourceOfDamage() instanceof EntityPlayer)
				{
					EntityPlayer dmgSource = (EntityPlayer)event.source.getSourceOfDamage();
					if(dmgSource.getHeldItem() != null)
					{
						if(dmgSource.getHeldItem().itemID == Item.swordIron.itemID)
						{
							event.entity.setFire(1);
						}
						else if(dmgSource.getHeldItem().itemID == Item.swordWood.itemID || dmgSource.getHeldItem().itemID == Item.stick.itemID)
						{
							event.entityLiving.attackEntityFrom(DamageSource.generic, event.ammount * 4);
						}
					}
				}
			}

		}
	}
	
	
	//Feeding Time
	final String[] uneatables = {"Skeleton", "Zombie", "Zombie Pigman", "Slime",  "Blaze", "Magma Cube", "Ghast"};
	
	@ForgeSubscribe
	public void onAttack(LivingHurtEvent event)
	{
		if(event.source.getSourceOfDamage() instanceof EntityPlayer)
		{
			try
			{
			World world = event.source.getSourceOfDamage().worldObj;

				if (world.isRemote == false)
				{					
					
					//Declaration of variables
					EntityPlayer ePlayer = (EntityPlayer)event.source.getSourceOfDamage();
					VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.source.getSourceOfDamage());
					//Checking if the player is a vampire who is feeding with his hands
					if (player.isVampire() == true && player.isFeeding() == true && ePlayer.getHeldItem() == null)
					{
						//Better make sure the vampire can feed on the target
						for (int i = 0; i < uneatables.length; i++)
						{
							//And if the target is in this list, do nothing.
							if(event.entityLiving.getEntityName().equals(uneatables[i]))
							{
								return;
							}
						}				
						//Deal half damage
						event.entityLiving.heal(event.ammount/2);
						//Slow the target down so they can't escape
						event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 10));
						//Yumyum, blood
						player.replenishBlood((int)event.ammount*1000);
						System.out.println("[EVENT] Blood: " + event.source.getEntity().getEntityName() + " just fed");
							
						EntityPlayer messagePlayer = (EntityPlayer)event.source.getSourceOfDamage();
						if(player.returnMessages())messagePlayer.addChatMessage("You fed for " + event.ammount*1000 + " blood from " + event.entity.getEntityName());
						return;
					}
					if(player.isVampire() && !player.isFeeding())
					{
						event.entityLiving.attackEntityFrom(DamageSource.generic, event.ammount);
					}				
				}	
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}		
	}
	
	@ForgeSubscribe
	public void onTarget(LivingSetAttackTargetEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
		{
			return;
		}
		else
		{
			if(event.target instanceof EntityPlayer)
			{
				
				List<String> mobList = new ArrayList<String>();
				mobList.add("Zombie");
				mobList.add("Skeleton");
				
				
				VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.target);
				EntityPlayer playerEnt = (EntityPlayer)event.target;
				
				if (player.isVampire() && mobList.contains(event.entityLiving.getEntityName()))
				{
					((EntityLiving)event.entityLiving).setAttackTarget(null);
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void vampireRitual(PlayerInteractEvent event)
	{		
		World world = event.entity.worldObj;
		
		if(world.isRemote == false)
		{

			
		if (event.action == Action.RIGHT_CLICK_BLOCK && world.getBlockId(event.x, event.y, event.z) == Block.skull.blockID  && event.entityPlayer.getHeldItem() == null)
		{
			
			VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
			if(player.isVampire())
			{
				System.out.println("Ehm, a vampire tried making the original ritual");
			}
			else
			{
				
			//Is the altar correctly made?
			Block[] immortalityCatalyst = {Block.blockLapis, Block.blockDiamond };
			if(world.getBlockId(event.x, event.y-1, event.z) == immortalityCatalyst[0].blockID &&
					//Pillars
					world.getBlockId(event.x-3, event.y-1, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y-1, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y-1, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y-1, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y+1, event.z-3) == 121 &&
					world.getBlockId(event.x-3, event.y+1, event.z+3) == 121 &&
					world.getBlockId(event.x+3, event.y+1, event.z-3) == 121 &&
					world.getBlockId(event.x+3, event.y+1, event.z+3) == 121 &&
					
					//Floor
					world.getBlockId(event.x, event.y-2, event.z-2) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x, event.y-2, event.z+2) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z-2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z+2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z-2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z+2) == Block.blockEmerald.blockID
					)
			{

				EntityPlayer playerEnt = (EntityPlayer)event.entity;
				player.originalRitual(playerEnt, world, event.x, event.y, event.z);
				
				
			}
			
			}
		}
		}
	}
	
	
	
	@ForgeSubscribe
	public void vampireRitualContinue(PlayerInteractEvent event)
	{		
		World world = event.entity.worldObj;
		
		if(world.isRemote == false)
		{

			
		if (event.action == Action.RIGHT_CLICK_BLOCK && world.getBlockId(event.x, event.y, event.z) == Block.skull.blockID)
		{
						
			//Is the altar correctly made?
			Block[] immortalityCatalyst = {Block.blockLapis, Block.blockDiamond };
			if(world.getBlockId(event.x, event.y-1, event.z) == immortalityCatalyst[0].blockID &&
					//Pillars
					world.getBlockId(event.x-3, event.y-1, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y-1, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y-1, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y-1, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y, event.z-3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x+3, event.y, event.z+3) == TalesOfYore.RunicBrick.blockID &&
					world.getBlockId(event.x-3, event.y+1, event.z-3) == 11 &&
					world.getBlockId(event.x-3, event.y+1, event.z+3) == 11 &&
					world.getBlockId(event.x+3, event.y+1, event.z-3) == 11 &&
					world.getBlockId(event.x+3, event.y+1, event.z+3) == 11 &&
					
					//Floor
					world.getBlockId(event.x, event.y-2, event.z-2) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x, event.y-2, event.z+2) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z) == Block.blockDiamond.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z-2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x+2, event.y-2, event.z+2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z-2) == Block.blockEmerald.blockID &&
					world.getBlockId(event.x-2, event.y-2, event.z+2) == Block.blockEmerald.blockID
					)
			{
				VampirePlayerExtender player = VampirePlayerExtender.get((EntityPlayer) event.entity);
				EntityPlayer playerEnt = (EntityPlayer)event.entity;
				if(player.ritualStage() == 0)
				{
					
				}				
				else if(player.ritualStage() == 1 && event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().itemID == Item.stick.itemID)
				{
					playerEnt.addChatMessage("Something tells you this is wrong...");
					playerEnt.inventory.decrStackSize(playerEnt.inventory.currentItem, 1);
					player.setRitualStage(2);
				}
				else if(player.ritualStage() == 2 && event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().itemID == Item.appleGold.itemID)
				{
					playerEnt.addChatMessage("This is unnatural...");
					playerEnt.inventory.decrStackSize(playerEnt.inventory.currentItem, 1);
					player.setRitualStage(3);
				}
				else if(player.ritualStage() == 3)
				{
					player.finishRitual(playerEnt, world, event.x, event.y, event.z);
				}

				
				
				
				
			}
			
		}
		}
	}
	
	
	
	
}
