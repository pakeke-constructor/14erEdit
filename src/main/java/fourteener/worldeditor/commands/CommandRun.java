package fourteener.worldeditor.commands;

import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fourteener.worldeditor.main.GlobalVars;
import fourteener.worldeditor.operations.Operator;

// These are dedicated versions of the undo and redo commands
public class CommandRun implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			String opStr = "";
			for (String s : args) {
				opStr = opStr.concat(s).concat(" ");
			}
			Operator op = Operator.newOperator(opStr);
			BlockState bs = GlobalVars.world.getBlockAt(((Player) sender).getLocation()).getState();
			op.operateOnBlock(bs, (Player) sender);
			return true;
		}
		return false;
	}
}
