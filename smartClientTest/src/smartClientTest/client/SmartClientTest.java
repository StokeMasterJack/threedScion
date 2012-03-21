package smartClientTest.client;

import com.google.gwt.core.client.EntryPoint;
import com.tms.threed.smartClients.gwt.client.Adapter;
import com.tms.threed.smartClients.gwt.client.ThreedSession;
import com.tms.threed.smartClients.gwt.client.ThreedSessionImpl;

public class SmartClientTest implements EntryPoint {


    ThreedSession threedSession;

    public void onModuleLoad() {
        threedSession = new ThreedSessionImpl();
        Adapter.registerHooks(threedSession);

        System.out.println("SmartClientTest.onModuleLoad");

    }


}
