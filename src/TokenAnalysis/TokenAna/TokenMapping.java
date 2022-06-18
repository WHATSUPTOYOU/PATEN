package TokenAnalysis.TokenAna;

import java.util.ArrayList;
import java.util.Map;

public class TokenMapping {
    boolean match = false;


    public TokenMapping(ArrayList<Map<String, Integer>> TokenRevAddList, ArrayList<Map<String,Integer>> TgtMethodTokenList, Map<String, Integer> maxAddtion){
        if(TokenRevAddList == null)
            return;

    }

    private void cmpTokens(ArrayList<Map<String, Integer>> TokenRevAddList, ArrayList<Map<String,Integer>> TgtMethodTokenList, Map<String, Integer> maxAddtion){
        Map<String, Integer> keyMethod = positionMaxToken(TokenRevAddList, maxAddtion);
        if(keyMethod == null)
            return;

    }

    private Map<String, Integer> positionMaxToken(ArrayList<Map<String, Integer>> TokenRevAddList, Map<String, Integer> maxAddtion){
        int max = 0;
        Map<String, Integer> position = null;
        for(Map<String, Integer> singleM: TokenRevAddList){
            int tokenCnt = 0;
            for(String token: maxAddtion.keySet()){
                tokenCnt += singleM.getOrDefault(token,0);
            }
            if(tokenCnt > max){
                max = tokenCnt;
                position = singleM;
            }
        }
        return position;
    }

    private void methodMatch(ArrayList<Map<String,Integer>> TgtMethodTokenList, Map<String, Integer> keyMethod){

    }
}
