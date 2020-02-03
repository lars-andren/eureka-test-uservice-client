package se.company.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class InstanceRegistrationService implements ApplicationContextAware {

    private EurekaRegistration registration;
    private final ServiceRegistry<EurekaRegistration> serviceRegistry;
    private final EurekaInstanceConfigBean eurekaInstanceConfigBean;
    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaClient eurekaClient;
    private final ObjectProvider<HealthCheckHandler> eurekaHealthCheckHandler;

    @Autowired
    public InstanceRegistrationService(ServiceRegistry<EurekaRegistration> serviceRegistry,
                                       @Qualifier("eurekaRegistration") @Autowired(required = false)
                                               EurekaRegistration registration,
                                       EurekaInstanceConfigBean eurekaInstanceConfigBean,
                                       @Qualifier("eurekaApplicationInfoManager")
                                                   ApplicationInfoManager applicationInfoManager,
                                       @Qualifier("eurekaClient") EurekaClient eurekaClient,
                                       ObjectProvider<HealthCheckHandler> eurekaHealthCheckHandler) {

        this.serviceRegistry = serviceRegistry;
        this.registration = registration;
        this.eurekaInstanceConfigBean = eurekaInstanceConfigBean;
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaClient = eurekaClient;
        this.eurekaHealthCheckHandler = eurekaHealthCheckHandler;
    }

    public InstanceInfo registerInstance(String status) {
        EurekaRegistration newRegistration = EurekaRegistration.builder(eurekaInstanceConfigBean)
                .with(applicationInfoManager)
                .with(eurekaClient)
                .with(eurekaHealthCheckHandler)
                .build();

        serviceRegistry.register(newRegistration);
        serviceRegistry.setStatus(newRegistration, status);
        this.registration = newRegistration;
        return newRegistration.getApplicationInfoManager().getInfo();
    }

    public InstanceInfo deregisterInstance() throws InterruptedException {
        serviceRegistry.setStatus(registration, "OUT_OF_SERVICE");
        // Give time for the OUT_OF_SERVICE status to propagate to all instances before shutdown
        Thread.sleep(10000L);
        serviceRegistry.deregister(registration);
        return registration.getApplicationInfoManager().getInfo();
    }

    public InstanceInfo setStatus(String status) {
        if (registration != null) {
            serviceRegistry.setStatus(registration, status);
            return registration.getApplicationInfoManager().getInfo();
        } else {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
