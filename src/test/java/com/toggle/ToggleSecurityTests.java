package com.toggle;

import com.toggle.utils.ToggleConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ToggleSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRemoveToggleUnauthorizedException() throws Exception {

        //remove toggle configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";

        //Check if unauthorized access
        this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "Abc", password = "app1", roles = "APP")
    public void testRemoveToggleForbiddenException() throws Exception {

        //remove toggle configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";

        //Check if forbidden to this user
        this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void testAddToggleUnauthorizedException() throws Exception {

        //add feature configuration
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";

        //Check if unauthorized access
        this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "Abc", password = "app1", roles = "APP")
    public void testAddToggleForbiddenException() throws Exception {

        //add feature configuration
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";

        //Check if forbidden to this user
        this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    public void testUpdateToggleUnauthorizedException() throws Exception {

        //update feature configuration
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";

        //Check if unauthorized access
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "Abc", password = "app1", roles = "APP")
    public void testUpdateToggleForbiddenException() throws Exception {

        //update feature configuration
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";

        //Check if forbidden to this user
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isForbidden());

    }



    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testAddUpdateRemoveSuccess() throws Exception {

        //add toggle configuration
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result = this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, ToggleConstants.TRUE);

        //update toggle configuration
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result2 = this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2 = result2.getResponse().getContentAsString();
        Assert.assertEquals(content2, ToggleConstants.TRUE);

        //remove feature configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";
        MvcResult result3 = this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3 = result3.getResponse().getContentAsString();
        Assert.assertEquals(content3, ToggleConstants.TRUE);

    }

}
