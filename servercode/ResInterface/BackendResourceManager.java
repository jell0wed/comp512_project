package ResInterface;

import ResImpl.ResourceManagerImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-27.
 */
public interface BackendResourceManager extends ResourceManager, Remote {
    public boolean reserveItem(int id, String key)
            throws RemoteException;
}
