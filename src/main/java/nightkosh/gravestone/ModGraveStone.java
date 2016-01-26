package nightkosh.gravestone;

import nightkosh.gravestone.api.GraveStoneAPI;
import nightkosh.gravestone.api.IGraveGeneration;
import nightkosh.gravestone.api.IGraveStone;
import nightkosh.gravestone.config.GSConfig;
import nightkosh.gravestone.core.*;
import nightkosh.gravestone.core.commands.GSCommands;
import nightkosh.gravestone.core.compatibility.GSCompatibility;
import nightkosh.gravestone.core.event.GSEventHandlerNetwork;
import nightkosh.gravestone.core.event.GSEventsHandler;
import nightkosh.gravestone.core.event.GSTickEventHandler;
import nightkosh.gravestone.core.proxy.CommonProxy;
import nightkosh.gravestone.helper.GraveStoneHelper;
import nightkosh.gravestone.helper.api.APIGraveGeneration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ModGraveStone {

    @Instance("GraveStone")
    public static ModGraveStone instance;
    @SidedProxy(clientSide = "nightkosh.gravestone.core.proxy.ClientProxy", serverSide = "nightkosh.gravestone.core.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final IGraveStone gravestoneHelper = new GraveStoneHelper();
    public static final IGraveGeneration apiGraveGeneration = new APIGraveGeneration();

    public ModGraveStone() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GSConfig.getInstance(event.getModConfigurationDirectory().getAbsolutePath() + "/GraveStoneMod/", "GraveStone.cfg");

        // API
        GraveStoneAPI.graveStone = gravestoneHelper;
        GraveStoneAPI.graveGenerationAtDeath = apiGraveGeneration;
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        // register death event
        MinecraftForge.EVENT_BUS.register(new GSEventsHandler());
        FMLCommonHandler.instance().bus().register(new GSEventHandlerNetwork());
        FMLCommonHandler.instance().bus().register(new GSTickEventHandler());
        proxy.registerHandlers();

        // tabs
        GSTabs.registration();

        // blocks registration
        GSBlock.registration();


        // tileEntities registration
        GSTileEntity.registration();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GSGuiHandler());

        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GSCompatibility.getInstance();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        GSCommands.getInstance(event);
    }
}