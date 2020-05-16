package a.apkt.json;

import android.telephony.TelephonyManager;

import java.util.Date;

import a.apkt.backingbean.AuthAux;

public class RegUserPostJson {

    private Long id;
    private byte[] emailB;
    private byte[] passwordB;
    private String gcmRegId;
    private String username;
    private String docNum;
    private String mobileNumState;
    private String mobileNum;
    private Date dateLogin;
    private String deviceId;
    private String simSerialNumber;
    private String simOperator;
    private String line1Number;
    private String simOperatorName;
    private String networkOperator;
    private String subscriberId; // IMSI (International mobile subscriber
    // identity)
    // numero atrelado ao ship (SerialNumber)
    private String networkOperatorName;
    private String networkCountryIso;
    private int callState;
    private int dataState;
    private int simState;
    private String deviceSoftwareVersion;
    private int networkType;
    private int phoneType;
    private String voiceMailNumber;
    private int dataActivity;
    private String currencyCode;
    private String lang;
    private AuthAux authAux;

    public RegUserPostJson(TelephonyManager mTelephonyMgr) {

        this.simOperator = mTelephonyMgr.getSimOperator();
        this.simSerialNumber = mTelephonyMgr.getSimSerialNumber();
        this.deviceId = mTelephonyMgr.getDeviceId();
        this.networkCountryIso = mTelephonyMgr.getNetworkCountryIso();
        this.simOperatorName = mTelephonyMgr.getSimOperatorName();
        this.subscriberId = mTelephonyMgr.getSubscriberId();
        this.dataState = mTelephonyMgr.getDataState();
        this.networkOperator = mTelephonyMgr.getNetworkOperator();
        this.networkOperatorName = mTelephonyMgr.getNetworkOperatorName();
        this.line1Number = mTelephonyMgr.getLine1Number();
        this.callState = mTelephonyMgr.getCallState();
        this.simState = mTelephonyMgr.getSimState();
        this.dataActivity = mTelephonyMgr.getDataActivity();
        this.deviceSoftwareVersion = mTelephonyMgr.getDeviceSoftwareVersion();
        this.networkType = mTelephonyMgr.getNetworkType();
        this.phoneType = mTelephonyMgr.getPhoneType();
        this.voiceMailNumber = mTelephonyMgr.getVoiceMailNumber();
    }

//    @Override
//    public String toString() {
//        return mobileNum + "," + mobileNumState + "," + username + ","
//                + passWord + "," + email + "," + deviceId + ","
//                + simSerialNumber + "," + simOperator + "," + line1Number + ","
//                + simOperatorName + "," + networkOperator + "," + subscriberId
//                + "," + networkOperatorName + "," + networkCountryIso + ","
//                + callState + "," + dataState + "," + simState + ","
//                + deviceSoftwareVersion + "," + networkType + "," + phoneType
//                + "," + voiceMailNumber + "," + dataActivity;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getEmailB() {
        return emailB;
    }

    public void setEmailB(byte[] emailB) {
        this.emailB = emailB;
    }

    public byte[] getPasswordB() {
        return passwordB;
    }

    public void setPasswordB(byte[] passwordB) {
        this.passwordB = passwordB;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getMobileNumState() {
        return mobileNumState;
    }

    public void setMobileNumState(String mobileNumState) {
        this.mobileNumState = mobileNumState;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public Date getDateLogin() {
        return dateLogin;
    }

    public void setDateLogin(Date dateLogin) {
        this.dateLogin = dateLogin;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    public String getLine1Number() {
        return line1Number;
    }

    public void setLine1Number(String line1Number) {
        this.line1Number = line1Number;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getNetworkOperator() {
        return networkOperator;
    }

    public void setNetworkOperator(String networkOperator) {
        this.networkOperator = networkOperator;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        this.networkOperatorName = networkOperatorName;
    }

    public String getNetworkCountryIso() {
        return networkCountryIso;
    }

    public void setNetworkCountryIso(String networkCountryIso) {
        this.networkCountryIso = networkCountryIso;
    }

    public int getCallState() {
        return callState;
    }

    public void setCallState(int callState) {
        this.callState = callState;
    }

    public int getDataState() {
        return dataState;
    }

    public void setDataState(int dataState) {
        this.dataState = dataState;
    }

    public int getSimState() {
        return simState;
    }

    public void setSimState(int simState) {
        this.simState = simState;
    }

    public String getDeviceSoftwareVersion() {
        return deviceSoftwareVersion;
    }

    public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
        this.deviceSoftwareVersion = deviceSoftwareVersion;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType;
    }

    public String getVoiceMailNumber() {
        return voiceMailNumber;
    }

    public void setVoiceMailNumber(String voiceMailNumber) {
        this.voiceMailNumber = voiceMailNumber;
    }

    public int getDataActivity() {
        return dataActivity;
    }

    public void setDataActivity(int dataActivity) {
        this.dataActivity = dataActivity;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public AuthAux getAuthAux() {
        return authAux;
    }

    public void setAuthAux(AuthAux authAux) {
        this.authAux = authAux;
    }
}

