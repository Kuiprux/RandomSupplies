package com.jimdo.jjh4296.core;

import com.jimdo.jjh4296.proxies.CommonProxy;
import com.jimdo.jjh4296.utils.WorldNBTHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

@Mod(modid = RandomSupplies.MODID, name = RandomSupplies.MODNAME, version = RandomSupplies.VERSION)
public class RandomSupplies {

	public static final String MODID = "randomsupplies";
	public static final String MODNAME = "RandomSupplies";
	public static final String VERSION = "0.0.1";

	@Instance
	public static RandomSupplies instance = new RandomSupplies();
	
	@SidedProxy(
		      clientSide="com.jimdo.jjh4296.proxies.ClientProxy", 
		      serverSide="com.jimdo.jjh4296.proxies.ServerProxy"
		    )
	public static CommonProxy proxy;
	
	public static ConfigHandler config = new ConfigHandler();
	
	public static WorldNBTHandler nbtHandler = new WorldNBTHandler("RSChunks");
	
	RSEventHandler tickHandler;
	

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		config.init();
		tickHandler = new RSEventHandler(RandomSupplies.config.getDropDimensions());
		proxy.preInit(e);
		MinecraftForge.EVENT_BUS.register(tickHandler);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
	
	@EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
		tickHandler.isWorldLoaded = true;
		System.out.println("HI!");
	}
}
