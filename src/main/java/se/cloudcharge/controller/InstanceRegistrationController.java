package se.cloudcharge.controller;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.web.bind.annotation.*;
import se.cloudcharge.service.InstanceRegistrationService;

@RestController
@RequestMapping("registration")
public class InstanceRegistrationController {

    private final InstanceRegistrationService instanceRegistrationService;

    public InstanceRegistrationController(InstanceRegistrationService instanceRegistrationService) {
        this.instanceRegistrationService = instanceRegistrationService;
    }

    @PostMapping("register")
    public void registerInstance() {
        instanceRegistrationService.registerInstance("UP");
    }

    @PostMapping("register/{status}")
    public InstanceInfo registerInstanceWithCustomStatus(@PathVariable("status") InstanceInfo.InstanceStatus status) {
        return instanceRegistrationService.registerInstance(status.toString());
    }

    @PostMapping("set-status/{status}")
    public InstanceInfo setInstanceStatus(@PathVariable("status") InstanceInfo.InstanceStatus status) {
        return instanceRegistrationService.setStatus(status.toString());
    }

    @PostMapping("deregister")
    public InstanceInfo deregisterInstance() throws InterruptedException {
        return instanceRegistrationService.deregisterInstance();
    }
}
