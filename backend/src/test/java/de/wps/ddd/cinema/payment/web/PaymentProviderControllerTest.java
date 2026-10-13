package de.wps.ddd.cinema.payment.web;

import de.wps.ddd.cinema.common.error.ResourceNotFound;
import de.wps.ddd.cinema.common.web.GlobalExceptionHandler;
import de.wps.ddd.cinema.payment.application.PaymentProcessing;
import de.wps.ddd.cinema.payment.domain.Amount;
import de.wps.ddd.cinema.payment.domain.Payment;
import de.wps.ddd.cinema.payment.domain.Payments;
import de.wps.ddd.cinema.payment.domain.PaymentReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentProviderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, PaymentProviderDtoMapper.class})
class PaymentProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Payments payments;
    @MockitoBean
    private PaymentProcessing paymentProcessing;

    private static final UUID REFERENCE = UUID.randomUUID();

    @Test
    void get_knownPayment_returnsPaymentDto() throws Exception {
        // arrange
        var reference = new PaymentReference(REFERENCE);
        when(payments.get(reference)).thenReturn(Payment.create(reference, new Amount(5000)));

        // act / assert
        mockMvc.perform(get("/api/payments/{reference}", REFERENCE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value(REFERENCE.toString()))
                .andExpect(jsonPath("$.amountInCents").value(5000))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void get_unknownReference_returns404() throws Exception {
        // arrange
        var reference = new PaymentReference(REFERENCE);
        when(payments.get(reference)).thenAnswer(invocation -> {
            ResourceNotFound.throwIf(true, "Payment " + REFERENCE + " does not exist");
            return null;
        });

        // act / assert
        mockMvc.perform(get("/api/payments/{reference}", REFERENCE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void pay_knownPayment_triggersProcessingAndReturns202() throws Exception {
        // arrange
        var reference = new PaymentReference(REFERENCE);
        when(payments.get(reference)).thenReturn(Payment.create(reference, new Amount(5000)));

        // act
        mockMvc.perform(post("/api/payments/{reference}/pay", REFERENCE))
                .andExpect(status().isAccepted());

        // assert
        verify(paymentProcessing).pay(reference);
    }
}
