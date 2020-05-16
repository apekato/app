package a.apkt.json;

import java.util.Date;

import a.apkt.backingbean.AuthAux;

public class LoginJson {

    private Long id;
    private String gcmRegId;
    private String username;
    private String docNum;
    private String docUsername;
    private String email;
    private String simSerialNumber;
    private String simOperator;
    private String networkCountryIso;
    private String subscriberId;
    private Date dateSignup;
    private boolean userNonexistent;
    private String simSerialNumExists;
    private String emailExists;
    private String usernameExists;
    private String appVersion;
    private boolean verifyVersion;
    private String currencyCode;
    private AuthAux authAux;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDocUsername() {
        return docUsername;
    }

    public void setDocUsername(String docUsername) {
        this.docUsername = docUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    public String getNetworkCountryIso() {
        return networkCountryIso;
    }

    public void setNetworkCountryIso(String networkCountryIso) {
        this.networkCountryIso = networkCountryIso;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Date getDateSignup() {
        return dateSignup;
    }

    public void setDateSignup(Date dateSignup) {
        this.dateSignup = dateSignup;
    }

    public boolean getUserNonexistent() {
        return userNonexistent;
    }

    public void setUserNonexistent(boolean userNonexistent) {
        this.userNonexistent = userNonexistent;
    }

    public String getSimSerialNumExists() {
        return simSerialNumExists;
    }

    public void setSimSerialNumExists(String simSerialNumExists) {
        this.simSerialNumExists = simSerialNumExists;
    }

    public String getEmailExists() {
        return emailExists;
    }

    public void setEmailExists(String emailExists) {
        this.emailExists = emailExists;
    }

    public String getUsernameExists() {
        return usernameExists;
    }

    public void setUsernameExists(String usernameExists) {
        this.usernameExists = usernameExists;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isVerifyVersion() {
        return verifyVersion;
    }

    public void setVerifyVersion(boolean verifyVersion) {
        this.verifyVersion = verifyVersion;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public AuthAux getAuthAux() {
        return authAux;
    }

    public void setAuthAux(AuthAux authAux) {
        this.authAux = authAux;
    }
}
