package Gumtree.function.similaritycalc.GraphBasedSim.zhsh;

import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.tree.Tree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws IOException {
		// Sample trees (in preorder).
		String tree1Str1 = "f(d(a c(b)) e)";
		String tree1Str2 = "f(c(d(a b)) e)";
		// Distance: 2 (main example used in the Zhang-Shasha paper)

//		String tree1Str3 = "a(b(c d) e(f g(i)))";
//		String tree1Str4 = "a(b(c d) e(f g(h)))";
//		// Distance: 1
//
//		String tree1Str5 = "d";
//		String tree1Str6 = "g(h)";
//		// Distance: 2

		Map<String, Integer> tokenWeight = new HashMap<>();
		tokenWeight.put("request1/SimpleName", 10);
		tokenWeight.put("A/SimpleName", 2);
		tokenWeight.put("1/NumberLiteral", 2);
//		tokenWeight.put("a", 2);
//		tokenWeight.put("d", 1);
		Run.initGenerators();
		Tree s = TreeGenerators.getInstance().getTree("./Gumtree.test.java").getRoot();
		Tree s2 = TreeGenerators.getInstance().getTree("./test2.java").getRoot();
		TreeMap treeMap1 = new TreeMap(s, tokenWeight);
		TreeMap treeMap2 = new TreeMap(s2, tokenWeight);

//		Tree tree3 = new Tree(tree1Str3);
//		Tree tree4 = new Tree(tree1Str4);
//
//		TreeMap treeMap5 = new TreeMap(tree1Str5,tokenWeight);
//		TreeMap treeMap6 = new TreeMap(tree1Str6,tokenWeight);

		int distance1 = TreeMap.ZhangShasha(treeMap1, treeMap2);
		System.out.println("Expected 2; got " + distance1);

//		int distance2 = Tree.ZhangShasha(tree3, tree4);
//		System.out.println("Expected 1; got " + distance2);
//
//		int distance3 = Tree.ZhangShasha(tree5, tree6);
//		System.out.println("Expected 2; got " + distance3);
	}
}
