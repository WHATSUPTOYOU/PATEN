package Gumtree.function.tools;

import Gumtree.function.AntlrJava.JavaLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class TokenAnalysis {
    public static List<String> specialTokenList = new ArrayList<String>(){
        {
            this.add("String");
        }
    };

    public static Map tokenChange(Map<String, Integer> del, Map<String, Integer> add){
        Map<String, Integer> MapSum = new HashMap<>();
        for(String key:del.keySet()){
            if(add.keySet().contains(key)) {
                if(del.get(key) + add.get(key) == 0){
                    add.remove(key);
                    continue;
                }
                MapSum.put(key, del.get(key) + add.get(key));
                add.remove(key);
            }
            else
                MapSum.put(key, del.get(key));
        }
        MapSum.putAll(add);
        return MapSum;
    }

    public static Map tokenAna(String del, String add){
        CharStream inputs_del = CharStreams.fromString(del);
        CharStream inputs_add = CharStreams.fromString(add);
        JavaLexer lexer_del = new JavaLexer(inputs_del);  //词法分析
        JavaLexer lexer_add = new JavaLexer(inputs_add);  //词法分析
        Map<String, Integer> del_tokens = new HashMap<>();
        Iterator ite_del = lexer_del.getAllTokens().iterator();
        while(ite_del.hasNext()){
            Token s =(Token) ite_del.next();
            if(s.getType() == JavaLexer.IDENTIFIER && !specialTokenList.contains(s.getText())) {
                if(del_tokens.containsKey(s.getText())){
                    del_tokens.put(s.getText(),del_tokens.get(s.getText())-1);
                }
                else
                    del_tokens.put(s.getText(),-1);
//                System.out.println(s.getText());
            }
        }
//        System.out.println(del_tokens.toString());
//        System.out.println("111");
        Map<String, Integer> add_tokens = new HashMap<>();
        Iterator ite_add = lexer_add.getAllTokens().iterator();
        while(ite_add.hasNext()){
            Token s =(Token) ite_add.next();
            if(s.getType() == JavaLexer.IDENTIFIER && !specialTokenList.contains(s.getText())){
                if(add_tokens.containsKey(s.getText())){
                    add_tokens.put(s.getText(),add_tokens.get(s.getText())+1);
                }
                else
                    add_tokens.put(s.getText(),1);
            }
        }
//        System.out.println(add_tokens.toString());
        Map sum = tokenChange(del_tokens, add_tokens);
        return sum;
    }

}
