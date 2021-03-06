package com.builtbroken.assemblyline.client;

import com.builtbroken.assemblyline.AssemblyLine;
import com.builtbroken.mc.framework.mod.ModCreativeTab;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/28/2016.
 */
public class ALCreativeTab extends ModCreativeTab
{
    public ALCreativeTab()
    {
        super("AL");
    }

    @Override
    public void displayAllReleventItems(List list)
    {
        //Put most used items at top
        add(list, AssemblyLine.blockInserter);
        add(list, AssemblyLine.blockRail);
        add(list, AssemblyLine.blockPowerRail);
        add(list, AssemblyLine.simpleBelt);
        add(list, InventoryUtility.getBlock("assemblyline:beltPipe"));
        add(list, AssemblyLine.itemCart);
        add(list, AssemblyLine.itemParts);
    }
}
