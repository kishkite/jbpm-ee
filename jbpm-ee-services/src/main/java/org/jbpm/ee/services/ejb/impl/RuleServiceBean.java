package org.jbpm.ee.services.ejb.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.drools.core.common.DefaultFactHandle;
import org.jboss.ejb3.annotation.Clustered;
import org.jbpm.ee.services.RuleService;
import org.jbpm.ee.services.ejb.impl.interceptors.JBPMContextEJBBinding;
import org.jbpm.ee.services.ejb.impl.interceptors.JBPMContextEJBInterceptor;
import org.jbpm.ee.services.ejb.local.RuleServiceLocal;
import org.jbpm.ee.services.ejb.remote.RuleServiceRemote;
import org.jbpm.ee.services.ejb.startup.KnowledgeManagerBean;
import org.jbpm.ee.services.model.RuleFactory;
import org.jbpm.ee.services.model.rules.FactHandle;
import org.kie.api.runtime.KieSession;


/***
 * Provides a wrapper implementation for the CDI services in order to support consistent execution by Local, Remote, REST, and SOAP execution.
 * 
 * {@see RuleService}
 * 
 * @author bradsdavis
 *
 */
@JBPMContextEJBBinding
@Interceptors({JBPMContextEJBInterceptor.class})
@Clustered
@Stateless
public class RuleServiceBean implements RuleService, RuleServiceLocal, RuleServiceRemote {

	@EJB
	private KnowledgeManagerBean knowledgeManager;

	private KieSession getSessionByProcess(Long processInstanceId) {
		return knowledgeManager.getRuntimeEngineByProcessId(processInstanceId).getKieSession();
	}
	
	@Override
	public int fireAllRules(Long processInstanceId) {
		return getSessionByProcess(processInstanceId).fireAllRules();
	}

	@Override
	public int fireAllRules(Long processInstanceId, int max) {
		return getSessionByProcess(processInstanceId).fireAllRules(max);
	}

	@Override
	public FactHandle insert(Long processInstanceId, Object object) {
		return RuleFactory.convert(getSessionByProcess(processInstanceId).insert(object));
	}

	@Override
	public void delete(Long processInstanceId, FactHandle factHandle) {
		DefaultFactHandle defaultFactHandle = new DefaultFactHandle(factHandle.getExternalForm()); 
		getSessionByProcess(processInstanceId).delete(defaultFactHandle);		
	}
	
	

	
	
	
}
