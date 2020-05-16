package a.apkt.json;

import java.util.List;

import a.apkt.backingbean.AuthAux;
import a.apkt.model.OpReturn;

public class ListOpReturnJson {
    private Long userId;
    private String type;
    private List<OpReturn> opReturnList;
    private AuthAux authAux;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OpReturn> getOpReturnList() {
        return opReturnList;
    }

    public void setOpReturnList(List<OpReturn> opReturnList) {
        this.opReturnList = opReturnList;
    }

    public AuthAux getAuthAux() {
        return authAux;
    }

    public void setAuthAux(AuthAux authAux) {
        this.authAux = authAux;
    }
}
