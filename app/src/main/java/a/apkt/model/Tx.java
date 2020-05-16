package a.apkt.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Tx {

    private Long id;

    private Long orderId;

    @SerializedName("block_height")
    private Long blockHeight;

    private String hash;

    private Long total;

    private BigDecimal fees;

    @SerializedName("size")
    private Long size;

    private String preference;

    @SerializedName("relayed_by")

    private String relayedBy;

    private String received;

    private Long ver;

    @SerializedName("lock_time")
    private Long lockTime;

    @SerializedName("double_spend")
    private Boolean doubleSpend;

    @SerializedName("double_spend_tx")
    private String doubleSpendTx;

    @SerializedName("vin_sz")
    private Long vinSz;

    @SerializedName("vout_sz")
    private Long voutSz;

    private Long confirmations;

    private BigDecimal confidence;

    private String confirmed;

    @SerializedName("receive_count")
    private Long receiveCount;

    @SerializedName("change_address")
    private String changeAddress;

    @SerializedName("block_hash")
    private String blockHash;

    @SerializedName("double_of")
    private String doubleOf;

    private String hex;

    private String addressesJson;

    private List<String> addresses;

    private Long addressId;

    private Long paymentForwardId;

    private String inputsJson;

    private String outputsJson;

    private String eventType;

//    @SerializedName("inputs")
//    private List<TxInput> inputs = new ArrayList<TxInput>();

    @SerializedName("outputs")
    private List<TxOutput> outputs = new ArrayList<TxOutput>();

    public Tx() {
    }

//    public boolean addInput(TxInput input) {
//        return inputs.add(input);
//    }

    public boolean addOutput(TxOutput output) {
        return outputs.add(output);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getRelayedBy() {
        return relayedBy;
    }

    public void setRelayedBy(String relayedBy) {
        this.relayedBy = relayedBy;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public Long getVer() {
        return ver;
    }

    public void setVer(Long ver) {
        this.ver = ver;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public Boolean getDoubleSpend() {
        return doubleSpend;
    }

    public void setDoubleSpend(Boolean doubleSpend) {
        this.doubleSpend = doubleSpend;
    }

    public String getDoubleSpendTx() {
        return doubleSpendTx;
    }

    public void setDoubleSpendTx(String doubleSpendTx) {
        this.doubleSpendTx = doubleSpendTx;
    }

    public Long getVinSz() {
        return vinSz;
    }

    public void setVinSz(Long vinSz) {
        this.vinSz = vinSz;
    }

    public Long getVoutSz() {
        return voutSz;
    }

    public void setVoutSz(Long voutSz) {
        this.voutSz = voutSz;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public Long getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(Long receiveCount) {
        this.receiveCount = receiveCount;
    }

    public String getChangeAddress() {
        return changeAddress;
    }

    public void setChangeAddress(String changeAddress) {
        this.changeAddress = changeAddress;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getDoubleOf() {
        return doubleOf;
    }

    public void setDoubleOf(String doubleOf) {
        this.doubleOf = doubleOf;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
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

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getPaymentForwardId() {
        return paymentForwardId;
    }

    public void setPaymentForwardId(Long paymentForwardId) {
        this.paymentForwardId = paymentForwardId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getInputsJson() {
        return inputsJson;
    }

    public void setInputsJson(String inputsJson) {
        this.inputsJson = inputsJson;
    }

    public String getOutputsJson() {
        return outputsJson;
    }

    public void setOutputsJson(String outputsJson) {
        this.outputsJson = outputsJson;
    }

//    public List<TxInput> getInputs() {
//        return inputs;
//    }
//
//    public void setInputs(List<TxInput> inputs) {
//        this.inputs = inputs;
//    }

    public List<TxOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<TxOutput> outputs) {
        this.outputs = outputs;
    }

}
