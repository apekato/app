package a.apkt.json;

public class GcmMsgJson {
    private String gcmRegId;
    private String message;

    // for resetting password
    private boolean resetPassword;
    // token (key signature) used as temporary password when resetting password
    private String navigate;

    public GcmMsgJson() {
        super();
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public String getNavigate() {
        return navigate;
    }

    public void setNavigate(String navigate) {
        this.navigate = navigate;
    }

}

