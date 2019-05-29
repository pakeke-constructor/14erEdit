package fourteener.worldeditor.operations;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import fourteener.worldeditor.main.Main;
import fourteener.worldeditor.operations.operators.AndNode;
import fourteener.worldeditor.operations.operators.BlockAdjacentNode;
import fourteener.worldeditor.operations.operators.BlockNode;
import fourteener.worldeditor.operations.operators.EntryNode;
import fourteener.worldeditor.operations.operators.FacesExposedNode;
import fourteener.worldeditor.operations.operators.FalseNode;
import fourteener.worldeditor.operations.operators.IfNode;
import fourteener.worldeditor.operations.operators.Node;
import fourteener.worldeditor.operations.operators.NotNode;
import fourteener.worldeditor.operations.operators.NumberNode;
import fourteener.worldeditor.operations.operators.OddsNode;
import fourteener.worldeditor.operations.operators.OrNode;
import fourteener.worldeditor.operations.operators.SameNode;
import fourteener.worldeditor.operations.operators.SetNode;
import fourteener.worldeditor.operations.operators.TrueNode;

public class Parser {
	// This starts as -1 since the first thing the parser does is increment it
	static int index = -1;
	static String[] parts;
	
	public static EntryNode parseOperation (String op) {
		
		// Here there be parsing magic
		// A massive recursive nightmare
		index = -1;
		parts = op.split(" ");
		Node rootNode = parsePart();
		
		// This is an error if this is true
		if (rootNode.equals(new Node()))
			return null;
		
		// Generate the entry node of the operation
		if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Building entry node from root node"); // -----
		EntryNode entryNode = EntryNode.createEntryNode(rootNode);
		return entryNode;
	}
	
	// This is the massive recursive nightmare
	private static Node parsePart () {
		index++;
		
		if (parts[index].equals("~")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Not node created"); // -----
			return NotNode.newNode(parsePart());
		} else if (parts[index].equals("%")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Odds node created"); // -----
			return OddsNode.newNode(parseNumberNode(), parsePart(), parsePart());
		} else if (parts[index].equals("&")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] And node created"); // -----
			return AndNode.newNode(parsePart(), parsePart());
		} else if (parts[index].equals("|")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Or node created"); // -----
			return OrNode.newNode(parsePart(), parsePart());
		} else if (parts[index].equals("?")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] If node created"); // -----
			return IfNode.newNode(parsePart(), parsePart(), parsePart());
		} else if (parts[index].equals(">")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Set node created"); // -----
			return SetNode.newNode(parsePart());
		} else if (parts[index].equals("false")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] False node created"); // -----
			return FalseNode.newNode();
		} else if (parts[index].equals("true")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] True node created"); // -----
			return TrueNode.newNode();
		} else if (parts[index].equals("*")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Faces exposed node created"); // -----
			return FacesExposedNode.newNode(parseNumberNode(), parsePart(), parsePart());
		} else if (parts[index].equals("@")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Block adjacent node created"); // -----
			return BlockAdjacentNode.newNode(parsePart(), parsePart(), parsePart());
		} else if (parts[index].equals("same")) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Same node created"); // -----
			return SameNode.newNode();
		} else if (Material.matchMaterial(parts[index]) != null) {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Block node created, type " + Material.matchMaterial(parts[index]).name()); // -----
			return BlockNode.newNode(Material.matchMaterial(parts[index]));
		}
		else {
			if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] New node created"); // -----
			return new Node();
		}
	}
	
	private static NumberNode parseNumberNode () {
		index ++;
		if (Main.isDebug) Bukkit.getServer().broadcastMessage("§c[DEBUG] Number node created"); // -----
		return NumberNode.newNode(parts[index]);
	}
}
