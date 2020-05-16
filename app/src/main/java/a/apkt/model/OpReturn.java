package a.apkt.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OpReturn implements Serializable {

    private Long id;
    private String text;
    private String address;
    private String status;
    private String type;
    private String email;
    private Date dateOpReturn;
    private BigDecimal fee;
    private Long loginId;
    private String lang;
    private String txId;

    public OpReturn() {
    }

    public OpReturn(String text, String type, String email, Long loginId, String lang) {
        this.text = text;
        this.type = type;
        this.email = email;
        this.loginId = loginId;
        this.lang = lang;
    }

    public OpReturn(String text, String address, String status, String type, Date dateOpReturn) {
        this.text = text;
        this.address = address;
        this.status = status;
        this.type = type;
        this.dateOpReturn = dateOpReturn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOpReturn() {
        return dateOpReturn;
    }

    public void setDateOpReturn(Date dateOpReturn) {
        this.dateOpReturn = dateOpReturn;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Long getLoginId() {
        return loginId;
    }

    public void setLoginId(Long loginId) {
        this.loginId = loginId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public static class OpReturnStatus {
        public static final String OP_RETURN_STATUS_INVALID_DATA = "INVALID_DATA";
        public static final String OP_RETURN_STATUS_WAITING_TX = "WAITING_TX";
        public static final String OP_RETURN_STATUS_REGISTERED = "REGISTERED";
    }

    public static class OpReturnType {
        public static final String OP_RETURN_TYPE_TEXT = "TEXT";
        public static final String OP_RETURN_TYPE_NOTARIZATION = "NOTARIZATION";
    }

}
