package dark.assembly.common.armbot;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dark.api.al.armbot.IArmbot;
import dark.api.al.armbot.ILogicDevice;

public abstract class TaskArmbot extends TaskBase
{
    /** Armbot instance */
    protected IArmbot armbot;

    public TaskArmbot(String name, TaskType tasktype)
    {
        super(name, tasktype);
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, ILogicDevice armbot)
    {
        super.onMethodCalled(world, location, armbot);
        if (armbot instanceof IArmbot)
        {
            this.armbot = (IArmbot) armbot;

            return ProcessReturn.CONTINUE;
        }
        return ProcessReturn.GENERAL_ERROR;
    }

    @Override
    public Object[] onCCMethodCalled(World world, Vector3 location, ILogicDevice armbot, IComputerAccess computer, ILuaContext context) throws Exception
    {
        super.onCCMethodCalled(world, location, armbot, computer, context);
        if (armbot instanceof IArmbot)
        {
            this.armbot = (IArmbot) armbot;
        }

        return null;
    }

    @Override
    public boolean canUseTask(ILogicDevice device)
    {
        return device instanceof IArmbot;
    }

}