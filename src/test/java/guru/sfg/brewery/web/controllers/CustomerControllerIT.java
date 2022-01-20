/*Code written by:

	Dipl. Eng. Ioan Simiciuc
	Software Developer

	ioan.simiciuc@continental-corporation.com

	Advanced driver-assistance systems (ADAS)
	Autonomous Mobility and Safety (AMS)

	S.C. Continental Automotive Romania S.R.L. Iasi
 */

package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
public class CustomerControllerIT extends BaseIT{

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAdminCustomer")
    void testListCustomersAUTH(String user, String pass) throws Exception{
        mockMvc.perform(get("/customers")
                    .with(httpBasic(user, pass)))
                .andExpect(status().isOk());
    }

    @Test
    void testListCustomersUSER() throws Exception {
        mockMvc.perform(get("/customers")
                    .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListCustomersNotLoggedIn() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Add Customers")
    @Nested
    class AddCustomer{

        @Rollback
        @Test
        void processCreationForm() throws Exception {
            mockMvc.perform(get("/customers/new")
                        .with(csrf())
                        .param("customerName", "Foo Customer")
                        .with(httpBasic("simi", "password")))
                    .andExpect(status().isOk());
        }

        @Rollback
        @ParameterizedTest( name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamNotAdmin")
        void processCreationFormNOAUTH(String user, String password)throws Exception {
            mockMvc.perform(post("/customers/new")
                        .param("customerName", "Foo Customer2")
                        .with(httpBasic(user, password)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void processCreationFormNOAUTH() throws Exception {
            mockMvc.perform(post("/customers/new")
                        .with(csrf())
                        .param("customerName", "Foo Customer"))
                    .andExpect(status().isUnauthorized());
        }

    }

}
