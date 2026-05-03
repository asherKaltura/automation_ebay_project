package tests;

import assertion.KAssertion;
import il.co.topq.difido.ReportDispatcher;
import il.co.topq.difido.ReportManager;
import org.testng.annotations.Listeners;

@Listeners(il.co.topq.difido.ReportManagerHook.class)
public abstract class AbstractTestCase {
    protected KAssertion assertion = new KAssertion();
    protected ReportDispatcher report = ReportManager.getInstance();

}