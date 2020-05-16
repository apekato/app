package a.apkt.backingbean;

import a.apkt.json.LoginJson;
import a.apkt.model.Login;

public class LoginBB {

    private Login loginAux = new Login();

    public LoginBB(LoginJson loginJson) {
        loginAux.setId(loginJson.getId());
        loginAux.setGcmRegId(loginJson.getGcmRegId());
        loginAux.setUsername(loginJson.getUsername());
        loginAux.setDocNum(loginJson.getDocNum());
        loginAux.setDocUsername(loginJson.getDocUsername());
        loginAux.setEmail(loginJson.getEmail());
        loginAux.setSimSerialNumber(loginJson.getSimSerialNumber());
        loginAux.setSimOperator(loginJson.getSimOperator());
        loginAux.setDateSignup(loginJson.getDateSignup());
        loginAux.setCurrencyCode(loginJson.getCurrencyCode());
    }

    public LoginBB(
            Long id,
            String username,
            String email,
            String password) {
        loginAux.setId(id);
        loginAux.setUsername(username);
        loginAux.setEmail(email);
        loginAux.setPassWord(password);
    }

    public LoginBB(String mobileNumState, String mobileNum,
                   String passWord) {
        super();
        loginAux.setMobileNumState(mobileNumState);
        loginAux.setMobileNum(mobileNum);
        loginAux.setPassWord(passWord);
    }

    public Login getLoginAux() {
        return loginAux;
    }

}

