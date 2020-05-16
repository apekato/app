package a.apkt.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TxOutput {
    private Long id;

    private BigDecimal value;

    private String script;

    @SerializedName("script_type")
    private String scriptType;

    @SerializedName("spent_by")
    private String spentBy;

    @SerializedName("data_hex")
    private String dataHex;

    @SerializedName("data_string")
    private String dataString;

    @SerializedName("addresses_json")
    private String addressesJson;

    @SerializedName("addresses")
    private List<String> addresses = new ArrayList<String>();

    private Long txId;

    private Long orderId;

    public TxOutput() {
    }


    public boolean addAddress(String address) {
        return addresses.add(address);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getSpentBy() {
        return spentBy;
    }

    public void setSpentBy(String spentBy) {
        this.spentBy = spentBy;
    }

    public String getDataHex() {
        return dataHex;
    }

    public void setDataHex(String dataHex) {
        this.dataHex = dataHex;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public String getAddressesJson() {
        return addressesJson;
    }

    public void setAddressesJson(String addressesJson) {
        this.addressesJson = addressesJson;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}

