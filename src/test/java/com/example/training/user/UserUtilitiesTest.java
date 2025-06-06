package com.example.training.user;

import com.example.training.common.AgingProcess;
import com.example.training.common.CommonUtil;
import com.example.training.payment.PaymentAuthorizationDTOEx;
import com.example.training.payment.PaymentHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUtilitiesTest {

    @Mock
    private JbillingAPI jbillingAPI;

    @Mock
    private AssetUtilities assetUtilities;

    @Mock
    private PaymentHelper paymentHelperMock;

    private MockedStatic<CommonUtil> commonUtilMock;
    private CommonUtil commonUtilInstance;
    private ObjectMapper objectMapperInstance;

    private PaymentHelper originalPaymentHelper;

    private UserUtilities userUtilities;

    private UserWS userWS;
    private Map<String, Object> payment;
    private List<Object> assetIds;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userWS = new UserWS();
        userWS.setUserId(123);
        userWS.setId(123);
        userWS.setOwingBalance(BigDecimal.ZERO);

        payment = new HashMap<>();
        payment.put("paymentDate", System.currentTimeMillis());

        assetIds = new ArrayList<>();

        // Set up static mocks
        commonUtilInstance = mock(CommonUtil.class);
        objectMapperInstance = mock(ObjectMapper.class);

        commonUtilMock = mockStatic(CommonUtil.class);

        // Mock CommonUtil.getInstance() to return our mock
        commonUtilMock.when(CommonUtil::getInstance).thenReturn(commonUtilInstance);
        lenient().when(commonUtilInstance.getObjMapper()).thenReturn(objectMapperInstance);

        // Replace static PaymentHelper field with our mock
        try {
            Field paymentHelperField = UserUtilities.class.getDeclaredField("paymentHelper");
            paymentHelperField.setAccessible(true);
            originalPaymentHelper = (PaymentHelper) paymentHelperField.get(null);
            paymentHelperField.set(null, paymentHelperMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to replace static PaymentHelper field", e);
        }

        // Mock PaymentHelper methods
        lenient().when(paymentHelperMock.getLatestSuccessOrEnteredPaymentFromCache(anyString(), anyString())).thenReturn(payment);

        // Create UserUtilities instance
        userUtilities = spy(new UserUtilities());

        // Common mocks for all tests
        lenient().when(commonUtilInstance.getJbillingAPIByBrandId(anyString())).thenReturn(jbillingAPI);
        lenient().when(commonUtilInstance.getValueFromApplicationResource(anyString())).thenReturn("1702");
        lenient().when(commonUtilInstance.getValueFromApplicationResourceByMode(anyString()))
                .thenReturn("1701")
                .thenReturn("1703")
                .thenReturn("1704")
                .thenReturn("1705");
        lenient().when(jbillingAPI.getUserSubscriptions(anyInt())).thenReturn(new OrderWS[0]);
    }

    @AfterEach
    void tearDown() {
        // Close static mocks
        if (commonUtilMock != null) {
            commonUtilMock.close();
        }

        // Restore original PaymentHelper field
        if (originalPaymentHelper != null) {
            try {
                Field paymentHelperField = UserUtilities.class.getDeclaredField("paymentHelper");
                paymentHelperField.setAccessible(true);
                paymentHelperField.set(null, originalPaymentHelper);
            } catch (Exception e) {
                throw new RuntimeException("Failed to restore static PaymentHelper field", e);
            }
        }
    }

    @Test
    void moveToOtherUserStatus_cancelCase() throws Throwable {
        //given
        CustomerNoteWS[] customerNotes = new CustomerNoteWS[0];
        userWS.setCustomerNotes(customerNotes);

        doNothing().when(userUtilities).createCustomerNotesWithStatus(any(UserWS.class), anyString(), anyString(), anyString());
        doNothing().when(userUtilities).changeUserStatusIfCancellationTriggeredForCollectionsCustomer(anyString(), any(UserWS.class));

        //when
        AgingProcess result = userUtilities.moveToOtherUserStatus(
                "accountPin", "brandId", userWS, "fromStatus", "Cancelled on Request", "1702", "fromStatusId");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccountPin()).isEqualTo("accountPin");
        assertThat(result.getUserId()).isEqualTo("123");
        assertThat(result.getBrandId()).isEqualTo("brandId");
        assertThat(result.getStage()).isEqualTo("Cancelled on Request");
        assertThat(result.getStatus()).isEqualTo("Pending");
        assertThat(result.getFromStatus()).isEqualTo("fromStatus");

        verify(userUtilities).createCustomerNotesWithStatus(eq(userWS), eq("brandId"), eq("Moving to Cancelled on Request"), eq("Cancelled on Request"));
        verify(userUtilities).changeUserStatusIfCancellationTriggeredForCollectionsCustomer(eq("brandId"), eq(userWS));
    }

    @Test
    void moveToOtherUserStatus_nonPaymentCase_negativeOwingBalance() throws Throwable {
        //given
        userWS.setOwingBalance(new BigDecimal(-100));

        // Override the mock for nonPaymentId to match toStatusId
        lenient().when(commonUtilInstance.getValueFromApplicationResourceByMode("user.brandId.nonpayment")).thenReturn("1703");

        lenient().doReturn("taskId").when(userUtilities).updateDSTaskWithStatusChangeInfo(anyString(), anyString(), anyString());
        doReturn(true).when(userUtilities).refundNegativeBalance(any(UserWS.class), any(JbillingAPI.class), anyString(), anyList(), anyString(), anyBoolean());
        doNothing().when(userUtilities).deletePaymentInstrument(any(UserWS.class), isNull(), anyString());
        doNothing().when(userUtilities).createCustomerNotesWithStatus(any(UserWS.class), anyString(), anyString(), anyString());

        //when
        AgingProcess result = userUtilities.moveToOtherUserStatus(
                "accountPin", "brandId", userWS, "fromStatus", "Cancelled for Non-Payment", "1703", "fromStatusId");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccountPin()).isEqualTo("accountPin");
        assertThat(result.getUserId()).isEqualTo("123");
        assertThat(result.getBrandId()).isEqualTo("brandId");
        assertThat(result.getStage()).isEqualTo("Cancelled for Non-Payment");
        assertThat(result.getStatus()).isEqualTo("Pending");
        assertThat(result.getFromStatus()).isEqualTo("fromStatus");
        assertThat(result.getTaskId()).isEqualTo("taskId");

        // Don't verify the exact parameters, just verify that the method was called
        verify(userUtilities).refundNegativeBalance(any(UserWS.class), any(JbillingAPI.class), anyString(), anyList(), anyString(), anyBoolean());
        verify(userUtilities).deletePaymentInstrument(eq(userWS), isNull(), eq("brandId"));
        verify(userUtilities).createCustomerNotesWithStatus(eq(userWS), eq("brandId"), eq("Moving to Cancelled for Non-Payment"), eq("Cancelled for Non-Payment"));
    }

    @Test
    void moveToOtherUserStatus_nonPaymentCase_positiveOwingBalance() throws Throwable {
        //given
        userWS.setOwingBalance(new BigDecimal(100));

        when(userUtilities.updateDSTaskWithStatusChangeInfo("accountPin", "123", "Cancelled for Non-Payment")).thenReturn("taskId");
        doReturn(true).when(userUtilities).checkAutoPay(anyString(), anyString());

        PaymentAuthorizationDTOEx paymentAuth = new PaymentAuthorizationDTOEx();
        paymentAuth.setResult(true);
        doReturn(paymentAuth).when(userUtilities).processThePayment(any(UserWS.class), anyString(), any(BigDecimal.class), anyList(), anyString());

        doNothing().when(userUtilities).deletePaymentInstrument(any(UserWS.class), isNull(), anyString());
        doNothing().when(userUtilities).createCustomerNotesWithStatus(any(UserWS.class), anyString(), anyString(), anyString());

        //when
        AgingProcess result = userUtilities.moveToOtherUserStatus(
                "accountPin", "brandId", userWS, "fromStatus", "Cancelled for Non-Payment", "1703", "fromStatusId");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccountPin()).isEqualTo("accountPin");
        assertThat(result.getUserId()).isEqualTo("123");
        assertThat(result.getBrandId()).isEqualTo("brandId");
        assertThat(result.getStage()).isEqualTo("Cancelled for Non-Payment");
        assertThat(result.getStatus()).isEqualTo("Pending");
        assertThat(result.getFromStatus()).isEqualTo("fromStatus");
        assertThat(result.getTaskId()).isEqualTo("taskId");

        verify(userUtilities).checkAutoPay(eq("123"), eq("brandId"));
        verify(userUtilities).processThePayment(eq(userWS), eq("brandId"), eq(new BigDecimal(100)), anyList(), eq("Final payment before cancellation"));
        verify(userUtilities).deletePaymentInstrument(eq(userWS), isNull(), eq("brandId"));
        verify(userUtilities).createCustomerNotesWithStatus(eq(userWS), eq("brandId"), eq("Moving to Cancelled for Non-Payment"), eq("Cancelled for Non-Payment"));
    }

    @Test
    void moveToOtherUserStatus_otherStatusCase() throws Throwable {
        //given
        doNothing().when(userUtilities).createCustomerNotesWithStatus(any(UserWS.class), anyString(), anyString(), anyString());

        //when
        AgingProcess result = userUtilities.moveToOtherUserStatus(
                "accountPin", "brandId", userWS, "fromStatus", "Active", "1701", "fromStatusId");

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAccountPin()).isEqualTo("accountPin");
        assertThat(result.getUserId()).isEqualTo("123");
        assertThat(result.getBrandId()).isEqualTo("brandId");
        assertThat(result.getStage()).isEqualTo("Active");
        assertThat(result.getStatus()).isEqualTo("Pending");
        assertThat(result.getFromStatus()).isEqualTo("fromStatus");

        verify(userUtilities).createCustomerNotesWithStatus(eq(userWS), eq("brandId"), eq("Moving to Active"), eq("Active"));
    }
}
