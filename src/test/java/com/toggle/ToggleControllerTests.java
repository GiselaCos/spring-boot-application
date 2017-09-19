/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.toggle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.toggle.exceptions.ToggleAlreadyExistsException;
import com.toggle.exceptions.ToggleNotFoundException;
import com.toggle.exceptions.ToggleStatusNotValidException;
import com.toggle.utils.ToggleConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.Assert;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ToggleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    //@Test(expected = ToggleNotFoundException.class)
    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testRemoveToggleNotFoundException() throws Exception {

        //remove feature configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";
        this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleNotFoundException.MESSAGE));
    }

    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testUpdateToggleNotFoundException() throws Exception {

        //update feature configuration
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=false";
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleNotFoundException.MESSAGE));

    }

    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testAddToggleWithStatusNotValidException() throws Exception {

        //adding feature configuration with invalid status
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=batatas";
        this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleStatusNotValidException.MESSAGE));
    }


    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testUpdateToggleWithStatusNotValidException() throws Exception {

        //adding toggle configuration
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result = this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, ToggleConstants.TRUE);

        //updating toggle configuration with invalid status
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=batatas";
        this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleStatusNotValidException.MESSAGE));

        //remove toggle configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";
        MvcResult result2 = this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2 = result2.getResponse().getContentAsString();
        Assert.assertEquals(content2, ToggleConstants.TRUE);
    }


    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testAddToggleSuccess() throws Exception {

        //adding toggle configuration
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result = this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, ToggleConstants.TRUE);

        //remove toggle configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";
        MvcResult result2 = this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2 = result2.getResponse().getContentAsString();
        Assert.assertEquals(content2, ToggleConstants.TRUE);
    }

    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testAddDuplicatedToggleConfig() throws Exception {

        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature2&toggleVersion=V1.0&toggleStatus=true";
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature2&toggleVersion=V1.0";

        //TODO: Only admins can perform this action

        //add feature configuration
        MvcResult result = this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, ToggleConstants.TRUE);

        //configure duplicated features should give an error
        this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(status().reason(ToggleAlreadyExistsException.MESSAGE));

        //remove so that test goes green next time
        MvcResult result2 = this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        String content2 = result2.getResponse().getContentAsString();

        Assert.assertEquals(content2, ToggleConstants.TRUE);
    }

    @Test
    @WithMockUser(username = "gisela", password = "pass", roles = "ADMIN")
    public void testAddToggleAndDisableIt() throws Exception {

        //adding toggle configurations
        String serviceAddToggleConfig = "/addToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=true";
        MvcResult result = this.mockMvc.perform(get(serviceAddToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assert.assertEquals(content, ToggleConstants.TRUE);

        //read toggle status
        String serviceReadToggleStatus = "/readToggleStatus?toggleId=feature1&toggleVersion=V1.0";
        MvcResult result2 = this.mockMvc.perform(get(serviceReadToggleStatus))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content2 = result2.getResponse().getContentAsString();
        Assert.assertEquals(content2, ToggleConstants.TRUE);

        //updating toggle status to false
        String serviceUpdateToggleConfig = "/updateToggleStatus?toggleId=feature1&toggleVersion=V1.0&toggleStatus=false";
        MvcResult result3 = this.mockMvc.perform(get(serviceUpdateToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content3 = result3.getResponse().getContentAsString();
        Assert.assertEquals(content3, ToggleConstants.TRUE);

        //read toggle status
        MvcResult result4 = this.mockMvc.perform(get(serviceReadToggleStatus))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content4 = result4.getResponse().getContentAsString();
        Assert.assertEquals(content4, ToggleConstants.FALSE);

        //remove the feature configuration
        String serviceRemoveToggleConfig = "/removeToggleConfig?toggleId=feature1&toggleVersion=V1.0";
        MvcResult result5 = this.mockMvc.perform(get(serviceRemoveToggleConfig))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();
        String content5 = result5.getResponse().getContentAsString();
        Assert.assertEquals(content5, ToggleConstants.TRUE);

    }

}
