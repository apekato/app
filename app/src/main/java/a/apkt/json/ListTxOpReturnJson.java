package a.apkt.json;

import java.util.List;

import a.apkt.backingbean.AuthAux;
import a.apkt.model.TxOpReturn;

public class ListTxOpReturnJson {
    private Long userId;
    private String type;
    private List<TxOpReturn> txOpReturnList;
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

    public List<TxOpReturn> getTxOpReturnList() {
        return txOpReturnList;
    }

    public void setTxOpReturnList(List<TxOpReturn> txOpReturnList) {
        this.txOpReturnList = txOpReturnList;
    }

    public AuthAux getAuthAux() {
        return authAux;
    }

    public void setAuthAux(AuthAux authAux) {
        this.authAux = authAux;
    }
}