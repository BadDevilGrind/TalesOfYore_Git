package tales.of.yore;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import tales.of.yore.Blocks.RuneCraftingTable;
import tales.of.yore.Blocks.RunicBrick;
import tales.of.yore.Commands.CheckInfo;
import tales.of.yore.Commands.ToggleVampire;
import tales.of.yore.Commands.VampireCMD;
import tales.of.yore.Items.Bloodtap;
import tales.of.yore.Items.BloodtapEmpty;
import tales.of.yore.Items.BottledBlood;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid="TalesOfYore", name="Tales of Yore", version="0.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=true,channels={"VampireChannel"}, packetHandler = VampirePacketHandler.class)

public class TalesOfYore {
	
	
	
	
	
	//Items
	//Available item ids: 25512-25999
	public static Item BloodtapEmpty = new BloodtapEmpty(25512).setMaxStackSize(1).setTextureName("talesofyore:BloodtapEmpty").setUnlocalizedName("bloodtapEmpty").setCreativeTab(CreativeTabs.tabTools);
	public static Item Bloodtap = new Bloodtap(25513);
	public static Item BottledBlood = new BottledBlood(25514).setMaxStackSize(1).setTextureName("talesofyore:BottledBlood").setUnlocalizedName("bottledBlood");
	//Blocks
	public static Block RuneCraftingTable = new RuneCraftingTable(500, Material.anvil).setHardness(1F).setStepSound(Block.soundClothFootstep).setUnlocalizedName("runeCraftingTable");
	public static Block RunicBrick = new RunicBrick(501, Material.rock).setHardness(5F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName("runicBrick");
	
	
	public static final Achievement vampirism = new Achievement(3720, "Immortality", 3,0, Item.rottenFlesh, null).registerAchievement();
	public static final Achievement immortality = new Achievement(3721, "True Immortality", 5, 0, Item.bone, null).setSpecial().registerAchievement();

	public static AchievementPage page1 = new AchievementPage("Tales of Yore", vampirism, immortality);
	
	
	
	// The instance of your mod that Forge uses.
    @Instance(value = "TalesOfYoreID")
    public static TalesOfYore instance;
    
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="tales.of.yore.ClientProxy", serverSide="tales.of.yore.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler // used in 1.6.2
    //@PreInit    // used in 1.5.2
    public void preInit(FMLPreInitializationEvent event) {
            // Stub Method
    		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    		config.load();
    		
    		Property comment;
    		jumpLimit = config.get("Vampire Configuration", "jumpLimit", 3.0).getDouble(3.0);
    		comment = config.get("Vampire Configuration", "jumpLimit", 3.0);
    		comment.comment = "A float on how high a vampire can reach. 1 = 100days = 15blocks. Default = 3";
    		
    		singleplayerRitual = config.get("Vampire Configuration", "singleplayerRitual", true).getBoolean(true);
    		comment = config.get("Vampire Configuration", "singleplayerRitual", true);
    		comment.comment = "Can you perform the original ritual alone? Default = true";
    		
    		allowOriginals = config.get("Vampire Configuration", "allowOriginals", true).getBoolean(true);
    		comment = config.get("Vampire Configuration", "allowOriginals", true);
    		comment.comment = "Are originals even allowed? Makes singleplayerRitual worthless if false. Default = true";
    		
    		useFoodBar = config.get("Vampire Configuration", "useFoodBar", true).getBoolean(true);
    		comment = config.get("Vampire Configuration", "useFoodBar", true);
    		comment.comment = "Should the food bar represent blood left. Default = true";
    		//boolean = config.get("Vampire Configuration", "singleplayerRitual", true).getBoolean(true);    		
    		
    		suffocateOnly = config.get("Vampire Configuration", "suffocateOnly", true).getBoolean(true);
    		comment = config.get("Vampire Configuration", "suffocateOnly", true);
    		comment.comment ="In order to become a vampire after being fed blood does the player turn into a vampire with any death, or does the player need to suffocate in a wall to become a vampire? Default = true";
    		
    		config.save();
    		
    		
    	
    	
    	
    }
    
    @EventHandler // used in 1.6.2
    //@Init       // used in 1.5.2
    public void load(FMLInitializationEvent event) {
      //      proxy.registerRenderers();
    	MinecraftForge.EVENT_BUS.register(new VampireEventHandler());
    	
    	LanguageRegistry.addName(BloodtapEmpty, "Empty Bloodtap");
    	LanguageRegistry.addName(Bloodtap, "Filled Bloodtap");
    	LanguageRegistry.addName(BottledBlood, "Bottle of Blood");
    	
    	
    	
    	//Recipes
    	GameRegistry.addRecipe(new ItemStack(BloodtapEmpty),
		    			"xxx",
		    			"zyx",
		    			'x', new ItemStack(Item.stick), 'y', new ItemStack(Item.glassBottle), 'z', new ItemStack(Item.ingotIron));
    	
    	
    	GameRegistry.addRecipe(new ItemStack(RunicBrick), "rdr", "dod", "rdr", 'r', new ItemStack(Item.redstone), 'd', new ItemStack(Item.diamond), 'o', new ItemStack(Block.obsidian));
    	
    	
    	LanguageRegistry.addName(RuneCraftingTable, "Runecrafting Table");
        MinecraftForge.setBlockHarvestLevel(RuneCraftingTable, "pickaxe", 2);
        GameRegistry.registerBlock(RuneCraftingTable, "RuneCraftingTable");
        
    	LanguageRegistry.addName(RunicBrick, "Runic Brick");
        MinecraftForge.setBlockHarvestLevel(RunicBrick, "pickaxe", 5);
        GameRegistry.registerBlock(RunicBrick, "RunicBrick");
    	
    	LanguageRegistry.instance().addStringLocalization("achievement.vampirism", "en_US", "Became a vampire!");
    	LanguageRegistry.instance().addStringLocalization("achievement.vampirism.desc", "en_US", "You (sort of) died!");
    	
    	LanguageRegistry.instance().addStringLocalization("achievement.immortality", "en_US", "Became an original");
    	LanguageRegistry.instance().addStringLocalization("achievement.immortality.desc", "en_US", "Hope you made the right call.");
    	
    	AchievementPage.registerAchievementPage(page1);
    	
    	
    	
    }
    
    @EventHandler // used in 1.6.2
    //@PostInit   // used in 1.5.2
    public void postInit(FMLPostInitializationEvent event) {
            // Stub Method
    }
    
  //Configuration
  	public static boolean singleplayerRitual, allowOriginals, useFoodBar, suffocateOnly;
  	public static double jumpLimit;
  	
  	
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {

    	event.registerServerCommand(new ToggleVampire());
    	event.registerServerCommand(new CheckInfo());
    	event.registerServerCommand(new VampireCMD());
    }
    
}
