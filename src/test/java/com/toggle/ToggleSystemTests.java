/**
 * This class is responsible to test the scenarios described on the requirements list
 */
package com.toggle;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.toggle.exceptions.ToggleForbiddenException;
import com.toggle.utils.ToggleConstants;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.Assert;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Base64Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ToggleSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSystemRequirements() throws Exception {

        /***************************************************
         * ADMIN
         ***************************************************/

        //add isButtonBlue toggle
        String serviceAddIsButtonBlueConfig = "/addToggleStatus?toggleId=isButtonBlue&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result = this.mockMvc.perform(get(serviceAddIsButtonBlueConfig).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content);

        //read toggle status
        String serviceReadToggleStatus = "/readToggleStatus?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultRead = this.mockMvc.perform(get(serviceReadToggleStatus).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentRead = resultRead.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentRead);

        //add isButtonGreen toggle
        String serviceAddIsButtonGreenConfig = "/addToggleStatus?toggleId=isButtonGreen&toggleVersion=V1.0&toggleStatus=true&toggleAccess=Abc";
        MvcResult result2 = this.mockMvc.perform(get(serviceAddIsButtonGreenConfig).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2 = result2.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content2);

        //read toggle status
        String serviceReadToggleStatus2 = "/readToggleStatus?toggleId=isButtonGreen&toggleVersion=V1.0";
        MvcResult resultRead2 = this.mockMvc.perform(get(serviceReadToggleStatus2).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentRead2 = resultRead2.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentRead2);

        //add isButtonRed toggle
        String serviceAddIsButtonRedConfig = "/addToggleStatus?toggleId=isButtonRed&toggleVersion=V1.0&toggleStatus=true&statusAlwaysFalse=Abc&toggleAccess=Abc,Def";
        MvcResult result3 = this.mockMvc.perform(get(serviceAddIsButtonRedConfig).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3 = result3.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content3);

        //read toggle status
        String serviceReadToggleStatus3 = "/readToggleStatus?toggleId=isButtonRed&toggleVersion=V1.0";
        MvcResult resultRead3 = this.mockMvc.perform(get(serviceReadToggleStatus3).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentRead3 = resultRead3.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentRead3);

        //add isButtonYellow toggle
        String serviceAddIsButtonYellowConfig = "/addToggleStatus?toggleId=isButtonYellow&toggleVersion=V1.0&toggleStatus=false&statusAlwaysTrue=Abc&toggleAccess=Def";
        MvcResult result4 = this.mockMvc.perform(get(serviceAddIsButtonYellowConfig).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content4 = result4.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content4);

        //read toggle status
        String serviceReadToggleStatus4 = "/readToggleStatus?toggleId=isButtonYellow&toggleVersion=V1.0";
        MvcResult resultRead4 = this.mockMvc.perform(get(serviceReadToggleStatus4).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentRead4 = resultRead4.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.FALSE, contentRead4);

        /***************************************************
         * APP - Abc
         ***************************************************/

        //checks toggle isButtonBlue status for app:Abc
        String serviceReadAbc = "/readToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultAbc = this.mockMvc.perform(get(serviceReadAbc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentAbc = resultAbc.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentAbc);

        //update status of app to false
        String serviceUpdateAbc = "/updateToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0&toggleStatus=false";
        MvcResult resultUpdateAbc = this.mockMvc.perform(get(serviceUpdateAbc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentUpdateAbc = resultUpdateAbc.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentUpdateAbc);

        //checks toggle isButtonBlue status for app:Def - false
        String serviceAfterUpdateReadAbc = "/readToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultAfterUpdateAbc = this.mockMvc.perform(get(serviceAfterUpdateReadAbc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentAfterUpdateAbc = resultAfterUpdateAbc.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.FALSE, contentAfterUpdateAbc);

        //checks toggle isButtonGreen status for app:Def - forbidden
        String serviceRead2Abc = "/readToggleAppStatus?toggleId=isButtonGreen&toggleVersion=V1.0";
        MvcResult result2Abc = this.mockMvc.perform(get(serviceRead2Abc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2Abc = result2Abc.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content2Abc);

        //checks toggle isButtonRed status for app:Abc - true
        String serviceRead3Abc = "/readToggleAppStatus?toggleId=isButtonRed&toggleVersion=V1.0";
        MvcResult result3Abc = this.mockMvc.perform(get(serviceRead3Abc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3Abc = result3Abc.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.FALSE, content3Abc);

        //checks toggle isButtonYellow status for app:Abc - Access forbidden
        String serviceRead4Abc = "/readToggleAppStatus?toggleId=isButtonYellow&toggleVersion=V1.0";
        this.mockMvc.perform(get(serviceRead4Abc).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Abc:app1".getBytes())))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleForbiddenException.MESSAGE));

        /***************************************************
         * APP - Abc
         ***************************************************/

        //checks toggle isButtonBlue status for app:Def - true
        String serviceReadDef = "/readToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultDef = this.mockMvc.perform(get(serviceReadDef).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentDef = resultDef.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentDef);

        //update status of app to false
        String serviceUpdateDef = "/updateToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0&toggleStatus=false";
        MvcResult resultUpdateDef = this.mockMvc.perform(get(serviceUpdateDef).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentUpdateDef = resultUpdateDef.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, contentUpdateDef);

        //checks toggle isButtonBlue status for app:Def - false
        String serviceAfterUpdateReadDef = "/readToggleAppStatus?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultAfterUpdateDef = this.mockMvc.perform(get(serviceAfterUpdateReadDef).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentAfterUpdateDef = resultAfterUpdateDef.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.FALSE, contentAfterUpdateDef);

        //checks toggle isButtonGreen status for app:Def - Access Forbidden
        String serviceRead2Def = "/readToggleAppStatus?toggleId=isButtonGreen&toggleVersion=V1.0";
        this.mockMvc.perform(get(serviceRead2Def).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleForbiddenException.MESSAGE));

        //checks toggle isButtonBlue status for app:Abc - true
        String serviceRead3Def = "/readToggleAppStatus?toggleId=isButtonRed&toggleVersion=V1.0";
        MvcResult result3Def = this.mockMvc.perform(get(serviceRead3Def).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3Def = result3Def.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.TRUE, content3Def);

        //checks toggle isButtonYellow status for app:Def - Although for this app status should be true
        //toggle status configured by admin has priority
        String serviceRead4Def = "/readToggleAppStatus?toggleId=isButtonYellow&toggleVersion=V1.0";
        MvcResult result4Def = this.mockMvc.perform(get(serviceRead4Def).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("Def:app2".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content4Def = result4Def.getResponse().getContentAsString();
        Assert.assertEquals(ToggleConstants.FALSE, content4Def);

        /***************************************************
         * ADMIN
         ***************************************************/

        //remove toggle isButtonBlue configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=isButtonBlue&toggleVersion=V1.0";
        MvcResult resultRemove = this.mockMvc.perform(get(serviceRemoveToggleConfig).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String contentRemove = resultRemove.getResponse().getContentAsString();
        Assert.assertEquals(contentRemove, ToggleConstants.TRUE);

        //remove toggle isButtonGreen configuration
        String serviceRemoveToggleConfig2 = "/removeToggleConfig?toggleId=isButtonGreen&toggleVersion=V1.0";
        MvcResult result2Remove = this.mockMvc.perform(get(serviceRemoveToggleConfig2).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2Remove = result2Remove.getResponse().getContentAsString();
        Assert.assertEquals(content2Remove, ToggleConstants.TRUE);

        //remove toggle isButtonRed configuration
        String serviceRemoveToggleConfig3 = "/removeToggleConfig?toggleId=isButtonRed&toggleVersion=V1.0";
        MvcResult result3Remove = this.mockMvc.perform(get(serviceRemoveToggleConfig3).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3Remove = result3Remove.getResponse().getContentAsString();
        Assert.assertEquals(content3Remove, ToggleConstants.TRUE);

        //remove toggle isButtonYellow configuration
        String serviceRemoveToggleConfig4 = "/removeToggleConfig?toggleId=isButtonYellow&toggleVersion=V1.0";
        MvcResult result4Remove = this.mockMvc.perform(get(serviceRemoveToggleConfig4).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString("user:pass".getBytes())))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content4Remove = result4Remove.getResponse().getContentAsString();
        Assert.assertEquals(content4Remove, ToggleConstants.TRUE);

    }

    @Test
    @WithMockUser(username = "Abc", password = "App1", roles = "APP")
    public void test3_AbcForbiddenException() throws Exception {
        //should give an error only admins can perform those actions
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=isButtonBlue&toggleVersion=V1.0";
        this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());

        //should give an error only admins can perform those actions
        String serviceAddIsButtonBlueConfig = "/addToggleStatus?toggleId=isButtonBlueForApp&toggleVersion=V1.0&toggleStatus=true";
        this.mockMvc.perform(get(serviceAddIsButtonBlueConfig))
                .andDo(print()).andExpect(status().isForbidden());

        //should give an error only admins can perform those actions
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=isButtonBlueForApp&toggleVersion=V1.0&toggleStatus=false";
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "Def", password = "App2", roles = "APP")
    public void test5_DefForbiddenException() throws Exception {
        //should give an error only admins can perform those actions
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=isButtonBlue&toggleVersion=V1.0";
        this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());

        //should give an error only admins can perform those actions
        String serviceAddIsButtonBlueConfig = "/addToggleStatus?toggleId=isButtonBlueForApp&toggleVersion=V1.0&toggleStatus=true";
        this.mockMvc.perform(get(serviceAddIsButtonBlueConfig))
                .andDo(print()).andExpect(status().isForbidden());

        //should give an error only admins can perform those actions
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=isButtonBlueForApp&toggleVersion=V1.0&toggleStatus=false";
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());
    }

}
