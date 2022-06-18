package Gumtree.function.similaritycalc;

import java.util.List;
import java.util.Map;

public class vfileSimCalc {
    public static boolean simcalc(Map<String, List<Integer>> res){
        if(res == null)
            return false;
        int tokensize = res.size();
        int totalmat = 0;
        for(String token: res.keySet()){
            List<Integer> tmp = res.get(token);
            int total = tmp.size();
            if(total == 0){
                tokensize--;
                continue;
            }
            int matcnt = 0;
            for(int a: tmp){
                if(a <= 10)
                    matcnt++;
            }
            if((float)matcnt/(float) total<=0.5)
                totalmat++;
        }
        return ((float)totalmat/(float)tokensize<=0.5);
    }
}
