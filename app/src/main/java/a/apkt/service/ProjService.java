package a.apkt.service;

public class ProjService {


    public static final String URLTYPE = Url.HTTP;
    public static final String NETWORK_TYPE = BlockCypherNetworkType.TEST3;
    public static final String BLOCK_CYPHER_ENDPOINT = "https://api.blockcypher.com/v1"
            + "/" + BlockCypherCurrencyType.BTC
            + "/" + NETWORK_TYPE
            + "/";
    public static final String BLOCK_CYPHER_TOKEN = "15cec3f0a8754248af469151f249b5d3";

    public static class Url{
        public static final String HTTPS = "https://apekato.com/webresources/";
//        public static final String HTTPS = "https://b1tes.com/homolog/webresources/";

        // fone
//        public static final String HTTP = "http://192.168.43.169:8080/webresources/";
//         home
//        public static final String HTTP = "http://192.168.100.4:8080/webresources/";
        // ufpr
//        public static final String HTTP= "http://192.168.43.169:8080/webresources/";
        // Provence2004
//        public static final String HTTP= "http://192.168.25.76:8080/webresources/";
        // Dig
        public static final String HTTP= "http://206.189.253.194:8080/webresources/";
    }

    public static class BlockCypherCurrencyType{
        public static final String BTC = "btc";
        public static final String BLOCK_CYPHER_TESTNET = "bcy";
    }

    public static class BlockCypherNetworkType{
        public static final String MAIN = "main";
        public static final String TEST3 = "test3";
        public static final String BLOCK_CYPHER_TESTNET = "test";
    }


}


