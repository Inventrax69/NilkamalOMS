package com.example.inventrax.falconOMS.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditLimitCommitmentDTO {

    @SerializedName("CreditLimitCommitments")
    private List<ApprovalListDTO> CreditLimitCommitments;

    public List<ApprovalListDTO> getCreditLimitCommitments() {
        return CreditLimitCommitments;
    }

    public void setCreditLimitCommitments(List<ApprovalListDTO> creditLimitCommitments) {
        CreditLimitCommitments = creditLimitCommitments;
    }

}
