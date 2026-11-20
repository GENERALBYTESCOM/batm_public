package com.generalbytes.batm.server.extensions.aml.verification;

import com.generalbytes.batm.server.extensions.TransactionPaymentType;
import com.generalbytes.batm.server.extensions.compliance.ComplianceContext;
import com.generalbytes.batm.server.extensions.compliance.TransferType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class IVerificationSessionRequestTest {

    @Test
    void shouldHaveNoComplianceContextByDefault() {
        IVerificationSessionRequest request = new IVerificationSessionRequest() {
            @Override
            public String getIdentityPublicId() {
                return "IDENTITY_PUBLIC_ID";
            }

            @Override
            public String getVerificationSessionId() {
                return "SESSION_ID";
            }

            @Override
            public String getVerificationWebUrl() {
                return "https://verification.example.com/session";
            }
        };

        assertNull(request.getComplianceContext());
    }

    @Test
    void shouldReturnComplianceContextWhenOverridden() {
        ComplianceContext complianceContext = new ComplianceContext(3L, TransferType.BUY, TransactionPaymentType.CASH, "USD");
        IVerificationSessionRequest request = new IVerificationSessionRequest() {
            @Override
            public String getIdentityPublicId() {
                return "IDENTITY_PUBLIC_ID";
            }

            @Override
            public String getVerificationSessionId() {
                return "SESSION_ID";
            }

            @Override
            public String getVerificationWebUrl() {
                return "https://verification.example.com/session";
            }

            @Override
            public ComplianceContext getComplianceContext() {
                return complianceContext;
            }
        };

        assertSame(complianceContext, request.getComplianceContext());
    }
}
