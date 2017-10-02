package ResInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by jpoisson on 2017-09-27.
 */
public interface BackendResourceManager extends ResourceManager, Remote {
    public boolean reserveItem(int id, String key)
            throws RemoteException;
}
