package com.toggle;

import com.toggle.configuration.TogglePropertiesManagement;
import com.toggle.exceptions.ToggleAlreadyExistsException;
import com.toggle.exceptions.ToggleForbiddenException;
import com.toggle.exceptions.ToggleNotFoundException;
import com.toggle.exceptions.ToggleStatusNotValidException;
import com.toggle.utils.ToggleConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class ToggleController {

    private static final Logger logger = LoggerFactory.getLogger(ToggleController.class);

    @Autowired
    private TogglePropertiesManagement togglePropManagement;

    @RequestMapping("/readToggleAppStatus")
    @Secured({"ROLE_APP"})
    public Boolean readToggleAppStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {

        logger.debug("Property to read ", toggleId);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String app = "";
        if (principal instanceof UserDetails) {
            app = ((UserDetails)principal).getUsername();
        }

        Boolean toggleStatus = togglePropManagement.readToggleAppStatus(app, toggleId, toggleVersion);

        return toggleStatus;
    }

    @RequestMapping("/updateToggleAppStatus")
    @Secured({"ROLE_APP"})
    public Boolean updateToggleAppStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion, @RequestParam(value=ToggleConstants.TOGGLE_STATUS) String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String app = "";
        if (principal instanceof UserDetails) {
            app = ((UserDetails)principal).getUsername();
        }

        boolean result = togglePropManagement.updateToggleAppStatus(app, toggleId, toggleVersion, toggleStatus);

        logger.debug("Property updated ", toggleId+" - "+toggleVersion+" - "+toggleStatus);

        return result;
    }

    @RequestMapping("/readToggleStatus")
    @Secured("ROLE_ADMIN")
    public Boolean readToggleStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException {

        logger.debug("Property to read ", toggleId);

        Boolean toggleStatus = togglePropManagement.readToggleStatus(toggleId, toggleVersion);

        return toggleStatus;
    }

    @RequestMapping("/readToggleConfig")
    @Secured("ROLE_ADMIN")
    public String readToggleConfig(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException {


        logger.debug("Property to read ", toggleId+" - "+toggleVersion);

        //TODO: check exceptions
        //model.put("message", app + toggleId);

        String toggleConfig = togglePropManagement.readToggleConfig(toggleId, toggleVersion);

        return toggleConfig;
    }

    @RequestMapping("/updateToggleStatus")
    @Secured("ROLE_ADMIN")
    public Boolean updateToggleStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion, @RequestParam(value=ToggleConstants.TOGGLE_STATUS) String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {

        boolean result = togglePropManagement.updateToggleStatus(toggleId, toggleVersion, toggleStatus);

        logger.debug("Property updated ", toggleId+" - "+toggleVersion+" - "+toggleStatus);

        return result;
    }

    /**
     * Method responsible to add new feature status configuration
     * only available for admins
     *
     * @param toggleId
     * @param toggleVersion
     * @param toggleStatus
     * @param appsAlwaysTrue
     * @param appsAlwaysFalse
     * @return
     */
    @RequestMapping("/addToggleStatus")
    @Secured("ROLE_ADMIN")
    public Boolean addToggleStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion, @RequestParam(value=ToggleConstants.TOGGLE_STATUS) String toggleStatus, @RequestParam(value=ToggleConstants.STATUS_ALWAYS_TRUE, defaultValue= "") String appsAlwaysTrue, @RequestParam(value=ToggleConstants.STATUS_ALWAYS_FALSE, defaultValue="") String appsAlwaysFalse, @RequestParam(value=ToggleConstants.TOGGLE_ACCESS, defaultValue="") String appsWithToggleAccess) throws ToggleAlreadyExistsException, ToggleStatusNotValidException {
        //TODO: implementar auditoria
        boolean result = togglePropManagement.addToggleStatus(toggleId, toggleVersion, toggleStatus, appsAlwaysTrue, appsAlwaysFalse, appsWithToggleAccess);
        logger.debug("Property added ", toggleId+" - "+toggleVersion+" - "+toggleStatus);

        return result;
    }

    /**
     * Method responsible to remove feature status configuration
     * only available for admins
     *
     * @param toggleId
     * @param toggleVersion
     * @return
     * @throws ToggleNotFoundException
     * @throws ToggleStatusNotValidException
     */
    @RequestMapping("/removeToggleConfig")
    @Secured("ROLE_ADMIN")
    public Boolean removeToggleConfig(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException {
        //TODO: Error if not admin
        //TODO: Implement authentication
        //TODO: implementar auditoria
        logger.debug("Property to remove ", toggleId);

        Boolean result = result = togglePropManagement.removeToggleConfig(toggleId, toggleVersion);
        logger.debug("Property removed ", toggleId);

        return result;
    }


}