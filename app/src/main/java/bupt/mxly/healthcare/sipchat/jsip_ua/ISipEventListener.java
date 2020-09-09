package bupt.mxly.healthcare.sipchat.jsip_ua;

import java.util.EventListener;

import bupt.mxly.healthcare.sipchat.jsip_ua.impl.SipEvent;

//import jsip_ua.impl.SipEvent;

public interface ISipEventListener extends EventListener {

	public void onSipMessage(SipEvent sipEvent);
}
