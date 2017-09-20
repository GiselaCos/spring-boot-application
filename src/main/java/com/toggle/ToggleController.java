/***************************************************************************************************************
 *
 * Rest API to manage the toggles and to deliver the toggles values to each client application/service request.
 *
 ***************************************************************************************************************/
package com.toggle;

import com.toggle.configuration.TogglePropertiesManager;
import com.toggle.exceptions.*;
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
    private TogglePropertiesManager togglePropManagement;

    @RequestMapping("/addToggleStatusObserver")
    @Secured({"ROLE_APP"})
    public Boolean addToggleStatusObserver(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {
        logger.info("addToggleStatusObserver: " + toggleId+" - "+ toggleVersion);
        //TODO: use rabbitMQ to add observers and inform of any changes on the queue

        //get user logged in
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String app = "";
        if (principal instanceof UserDetails) {
            app = ((UserDetails)principal).getUsername();
        }
        logger.info("app: " + app);

        Boolean result = togglePropManagement.addToggleStatusObservers(app, toggleId, toggleVersion);
        logger.info("result: " + result);
        return result;
    }


    @RequestMapping("/readToggleAppStatus")
    @Secured({"ROLE_APP"})
    public Boolean readToggleAppStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleForbiddenException {
        logger.info("readToggleAppStatus: " + toggleId+" - "+ toggleVersion);

        //get user logged in
        //by now only users with APP profile can read their toggle app status
        //username logged is used to identify the APP
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String app = "";
        if (principal instanceof UserDetails) {
            app = ((UserDetails)principal).getUsername();
        }
        logger.info("app: "+app);

        Boolean toggleStatus = togglePropManagement.readToggleAppStatus(app, toggleId, toggleVersion);
        logger.info("toggleStatus: "+toggleStatus);
        return toggleStatus;
    }

    @RequestMapping("/updateToggleAppStatus")
    @Secured({"ROLE_APP"})
    public Boolean updateToggleAppStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion, @RequestParam(value=ToggleConstants.TOGGLE_STATUS) String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException {
        logger.info("readToggleStatus: " + toggleId+" - "+ toggleVersion+" - "+toggleStatus);

        //get user logged in
        //by now only users with APP profile can update their toggle app status
        //username logged is used to identify the APP
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String app = "";
        if (principal instanceof UserDetails) {
            app = ((UserDetails)principal).getUsername();
        }

        logger.info("app: " + app);
        boolean result = togglePropManagement.updateToggleAppStatus(app, toggleId, toggleVersion, toggleStatus);

        logger.info("result: " + result);

        return result;
    }

    @RequestMapping("/readToggleStatus")
    @Secured("ROLE_ADMIN")
    public Boolean readToggleStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException {
        logger.info("readToggleStatus: " + toggleId+" - "+ toggleVersion);

        Boolean toggleStatus = togglePropManagement.readToggleStatus(toggleId, toggleVersion);
        logger.info("toggleStatus: " + toggleStatus);
        return toggleStatus;
    }

    @RequestMapping("/readToggleConfig")
    @Secured("ROLE_ADMIN")
    public String readToggleConfig(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion) throws ToggleNotFoundException, ToggleStatusNotValidException {
        logger.info("readToggleConfig: " + toggleId+" - "+ toggleVersion);

        String toggleConfig = togglePropManagement.readToggleConfig(toggleId, toggleVersion);
        logger.info("toggleConfig: " + toggleConfig);
        return toggleConfig;
    }

    @RequestMapping("/updateToggleStatus")
    @Secured("ROLE_ADMIN")
    public Boolean updateToggleStatus(@RequestParam(value=ToggleConstants.TOGGLE_ID) String toggleId, @RequestParam(value=ToggleConstants.TOGGLE_VERSION) String toggleVersion, @RequestParam(value=ToggleConstants.TOGGLE_STATUS) String toggleStatus) throws ToggleNotFoundException, ToggleStatusNotValidException, ToggleStatusAlreadySettedException {
        logger.info("updateToggleStatus: " + toggleId+" - "+ toggleVersion+" - "+toggleStatus);

        boolean result = togglePropManagement.updateToggleStatus(toggleId, toggleVersion, toggleStatus);
        logger.info("result: " + result);
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
        logger.info("addToggleStatus: " + toggleId+" - "+ toggleVersion);

        boolean result = togglePropManagement.addToggleStatus(toggleId, toggleVersion, toggleStatus, appsAlwaysTrue, appsAlwaysFalse, appsWithToggleAccess);
        logger.info("result: " + result);
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
        logger.info("removeToggleConfig: " + toggleId+" - "+ toggleVersion);

        Boolean result = togglePropManagement.removeToggleConfig(toggleId, toggleVersion);
        logger.info("result: " + result);
        return result;
    }


}