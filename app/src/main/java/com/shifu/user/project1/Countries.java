package com.shifu.user.project1;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Countries {

    @SerializedName("IsSuccess")
    @Expose
    private boolean isSuccess;

    @SerializedName("UserMessage")
    @Expose
    private Object userMessage;
    @SerializedName("TechnicalMessage")
    @Expose
    private Object technicalMessage;
    @SerializedName("TotalCount")
    @Expose
    private int totalCount;
    @SerializedName("Response")
    @Expose
    private List<CountriesResponse> response = null;

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Object getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(Object userMessage) {
        this.userMessage = userMessage;
    }

    public Object getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(Object technicalMessage) {
        this.technicalMessage = technicalMessage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<CountriesResponse> getResponse() {
        return response;
    }

    public void setResponse(List<CountriesResponse> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("isSuccess", isSuccess).append("userMessage", userMessage).append("technicalMessage", technicalMessage).append("totalCount", totalCount).append("response", response).toString();
    }

}