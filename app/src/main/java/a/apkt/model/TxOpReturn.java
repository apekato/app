package a.apkt.model;

import java.math.BigDecimal;
import java.util.Date;

public class TxOpReturn {

    private Long id;
    private String text;
    private String address;
    private String status;
    private String type;
    private String email;
    private Date dateOpReturn;
    private String txId;
    private BigDecimal fee;
    private Long loginId;

    public TxOpReturn() {
    }

    public TxOpReturn(Long id) {
        this.id = id;
    }

    public TxOpReturn(String text, String address, String status, Date dateOpReturn) {
        this.text = text;
        this.address = address;
        this.status = status;
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

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
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
}

