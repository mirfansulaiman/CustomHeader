package CH;
import burp.*;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


public class CustomHeader implements IBurpExtender, ITab, IProxyListener, IHttpListener, ISessionHandlingAction {
    public static IBurpExtenderCallbacks callbacks;
    private JScrollPane MainPanelTab;
    private CustomHeaderGUI panel;
    private PrintWriter mStdOut;
    private IExtensionHelpers helpers;
    
    /**
     * 
     * Implement IBurpExtender
     */
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        CustomHeader.callbacks = callbacks;
        callbacks.registerSessionHandlingAction(this);
        callbacks.setExtensionName("Custom Header");
        mStdOut = new PrintWriter(callbacks.getStdout(), true);
        callbacks.registerHttpListener(this);
        callbacks.registerProxyListener(this);
        helpers = callbacks.getHelpers();
        SwingUtilities.invokeLater(() -> {
            panel = new CustomHeaderGUI(this);
            MainPanelTab = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            callbacks.addSuiteTab(this);
            callbacks.printOutput("- Custom Header v1.0");
            callbacks.printOutput("- Created by Muhammad Irfan Sulaiman <irfan.sulaiman@vantagepoint.co.id>");

        });

    }
   
   
    @Override
    public String getActionName() {
        return "Custom Header";
    }
    
    public void ConfigHeader(List header){header.toString();}
    @Override
    public void processHttpMessage(int toolFlag,
                                   boolean messageIsRequest,
                                   IHttpRequestResponse messageInfo)
    {
        if (messageIsRequest) {
            IRequestInfo infoReq = helpers.analyzeRequest(messageInfo.getRequest());
            byte[] bodyReq  = Arrays.copyOfRange(messageInfo.getRequest(), infoReq.getBodyOffset(), messageInfo.getRequest().length);
            String POSTbodyReq = new String(bodyReq);
            List<String> headers = infoReq.getHeaders();
            if(panel.getMethod().contains(infoReq.getMethod())){
                if (panel.getDebug() == true) mStdOut.println("[+] Request Method: " + infoReq.getMethod());
                try{
                    panel.getHeader().forEach((n) -> headers.add(n));
                }catch(NullPointerException e){
                    mStdOut.println("[!] ERROR : Header Not Save ! Please Click Save Button !");
                }
            }
            if (panel.getDebug() == true) mStdOut.println("[+] Request Header: " + headers.toString());
            messageInfo.setRequest(helpers.buildHttpMessage(headers, POSTbodyReq.getBytes()));
        }

    }
    @Override
    public void performAction(IHttpRequestResponse currentRequest, IHttpRequestResponse[] macroItems) {

    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {

    }

    @Override
    public String getTabCaption() {
        return "Custom Header";
    }

    @Override
    public Component getUiComponent() {
        return MainPanelTab;
    }
}
