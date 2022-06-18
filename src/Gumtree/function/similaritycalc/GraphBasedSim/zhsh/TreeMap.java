package Gumtree.function.similaritycalc.GraphBasedSim.zhsh;

import Gumtree.DataObj.DefUse;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.utils.Pair;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeMap {
	public Node root;
	// Gumtree.function l() which gives the leftmost child
	ArrayList<Integer> l = new ArrayList<Integer>();
	// list of keyroots, i.e., nodes with a left child and the tree root
	ArrayList<Integer> keyroots = new ArrayList<Integer>();
	// list of the labels of the nodes used for node comparison
	ArrayList<String> labels = new ArrayList<String>();
	static Map<String, Integer> tokenWeight;

//	// the following constructor handles preorder notation. E.g., f(a b(c))
//	public TreeMap(String s, Map tokenWeight) throws IOException {
//		this.tokenWeight = tokenWeight;
//		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(s));
//		tokenizer.nextToken();
//		root = parseString(root, tokenizer);
//		if (tokenizer.ttype != StreamTokenizer.TT_EOF) {
//			throw new RuntimeException("Leftover token: " + tokenizer.ttype);
//		}
//	}

	// the following constructor handles preorder notation. E.g., f(a b(c))
	public TreeMap(Tree s, Map<String, Integer> tokenWeight) throws IOException {
		this.tokenWeight = tokenWeight;
		root = setTree(s);
	}

	public TreeMap(List<Tree> trees, Map<String, Integer> tokenWeight){
		this.tokenWeight = tokenWeight;
		root = setTree(trees);
	}

	public TreeMap(Map<Integer, Tree> CtxTrees, CompilationUnit cu, Map<String, Integer> tokenWeight, Map<Integer,List<DefUse>> connectionPoistion){
		this.tokenWeight = tokenWeight;

		root = setTree(CtxTrees, cu, connectionPoistion);
	}

	public TreeMap(Map<Integer, Tree> CtxTrees, CompilationUnit cu, Map<String, Integer> tokenWeight){
		this.tokenWeight = tokenWeight;
		root = setTree(CtxTrees, cu);
	}

	public Node setTree(Map<Integer, Tree> CtxTrees, CompilationUnit cu, Map<Integer,List<DefUse>> connectionPoistion){
		if(CtxTrees.size() == 0)
			return null;
		if(connectionPoistion.isEmpty())
			return setTree(CtxTrees, cu);
		List<Integer> defLinesToReplace = new ArrayList<>();
		for(int useline: connectionPoistion.keySet()){
			for(DefUse defUse: connectionPoistion.get(useline)){
				if(!defLinesToReplace.contains(defUse.defLine))
					defLinesToReplace.add(defUse.defLine);
			}
		}
		Node rt = new Node("Root");
		for(int line: CtxTrees.keySet()){
			if(defLinesToReplace.contains(line))
				continue;
			else if(connectionPoistion.containsKey(line)){
				rt.children.add(setTree(CtxTrees.get(line), cu, line, connectionPoistion.get(line)));
				continue;
			}
			rt.children.add(setTree(CtxTrees.get(line), line, cu));
		}
		return rt;
	}

	public Node setTree(Tree Node, CompilationUnit cu, int useline, List<DefUse> posList){
		Node rt = new Node(Node.getLabel()+"/"+Node.getType().name);
		for(DefUse defUse: posList){
			if(Node.equals(defUse.usePoint))
				rt.children.add(setTree(defUse.defRoot, defUse.defLine, cu));
		}
		if(Node.getChildren() == null)
			return rt;
		for(Tree tmp:Node.getChildren()){
			if(cu.getLineNumber(tmp.getPos()) == useline)
				rt.children.add(setTree(tmp, cu, useline, posList));
//			setTree(tmp);
		}
		return rt;
	}
//	public Node setTree(Map<Integer, Tree> CtxTrees, CompilationUnit cu,
//						List<Pair<Integer, Integer>> connectionAdd, List<Pair<Tree, Tree>> connectionPoistion){
//		if(connectionAdd.size() == 0)
//			return setTree(CtxTrees, cu);
//		if(CtxTrees.size() == 0)
//			return null;
//		Node rt = new Node("Root");
//		List<Integer> defLines = new ArrayList<>();
//		List<Integer> useLines = new ArrayList<>();
//		for(Pair<Integer, Integer> p:connectionAdd)
//			if(p.first!=null) {
//				defLines.add(p.first);
//				useLines.add(p.second);
//			}
//		for(int line: CtxTrees.keySet()){
//			if(defLines.contains(line))
//				continue;
//			else if(!useLines.contains(line))
//				rt.children.add(setTree(CtxTrees.get(line), line, cu));
//			else
//				rt.children.add(setTree(CtxTrees.get(line)),);
//		}
//		return rt;
//	}

	public Node setTree(Map<Integer, Tree> CtxTrees, CompilationUnit cu){
		if(CtxTrees.size() == 0)
			return null;
		Node rt = new Node("Root");
		for(int line: CtxTrees.keySet()){
			rt.children.add(setTree(CtxTrees.get(line), line, cu));
		}
		return rt;
	}

	public Node setTree(Tree Node, int line, CompilationUnit cu){
		Node rt = new Node(Node.getLabel()+"/"+Node.getType().name);
		if(Node.getChildren() == null)
			return rt;
		for(Tree tmp:Node.getChildren()){
			if(cu.getLineNumber(tmp.getPos()) == line)
				rt.children.add(setTree(tmp, line, cu));
//			setTree(tmp);
		}
		return rt;
	}

	public Node setTree(List<Tree> Nodes){
		if(Nodes.size() == 0)
			return null;
		Node rt = new Node("Root");
		for(Tree Node: Nodes){
			rt.children.add(setTree(Node));
		}
		return rt;
	}

	public Node setTree(Tree Node){
		Node rt = new Node(Node.getLabel()+"/"+Node.getType().name);
		if(Node.getChildren() == null)
			return rt;
		for(Tree tmp:Node.getChildren()){
			rt.children.add(setTree(tmp));
//			setTree(tmp);
		}
		return rt;
	}

//	private static Node parseString(Node node, StreamTokenizer tokenizer) throws IOException {
//		node.label = tokenizer.sval;
//		tokenizer.nextToken();
//		if (tokenizer.ttype == '(') {
//			tokenizer.nextToken();
//			do {
//				node.children.add(parseString(new Node(), tokenizer));
//			} while (tokenizer.ttype != ')');
//			tokenizer.nextToken();
//		}
//		return node;
//	}

	public void traverse() {
		// put together an ordered list of node labels of the tree
		traverse(root, labels);
	}

	private static ArrayList<String> traverse(Node node, ArrayList<String> labels) {
		for (int i = 0; i < node.children.size(); i++) {
			labels = traverse(node.children.get(i), labels);
		}
		labels.add(node.label);
		return labels;
	}

	public void index() {
		// index each node in the tree according to traversal method
		index(root, 0);
	}

	private static int index(Node node, int index) {
		for (int i = 0; i < node.children.size(); i++) {
			index = index(node.children.get(i), index);
		}
		index++;
		node.index = index;
		return index;
	}

	public void l() {
		// put together a Gumtree.function which gives l()
		leftmost();
		l = l(root, new ArrayList<Integer>());
	}

	private ArrayList<Integer> l(Node node, ArrayList<Integer> l) {
		for (int i = 0; i < node.children.size(); i++) {
			l = l(node.children.get(i), l);
		}
		l.add(node.leftmost.index);
		return l;
	}

	private void leftmost() {
		leftmost(root);
	}

	private static void leftmost(Node node) {
		if (node == null)
			return;
		for (int i = 0; i < node.children.size(); i++) {
			leftmost(node.children.get(i));
		}
		if (node.children.size() == 0) {
			node.leftmost = node;
		} else {
			node.leftmost = node.children.get(0).leftmost;
		}
	}

	public void keyroots() {
		// calculate the keyroots
		for (int i = 0; i < l.size(); i++) {
			int flag = 0;
			for (int j = i + 1; j < l.size(); j++) {
				if (l.get(j) == l.get(i)) {
					flag = 1;
				}
			}
			if (flag == 0) {
				this.keyroots.add(i + 1);
			}
		}
	}

	static int[][] TD;

	public static int ZhangShasha(TreeMap treeMap1, TreeMap treeMap2) {
		treeMap1.index();
		treeMap1.l();
		treeMap1.keyroots();
		treeMap1.traverse();
		treeMap2.index();
		treeMap2.l();
		treeMap2.keyroots();
		treeMap2.traverse();

		ArrayList<Integer> l1 = treeMap1.l;
		ArrayList<Integer> keyroots1 = treeMap1.keyroots;
		ArrayList<Integer> l2 = treeMap2.l;
		ArrayList<Integer> keyroots2 = treeMap2.keyroots;

		// space complexity of the algorithm
		TD = new int[l1.size() + 1][l2.size() + 1];

		// solve subproblems
		for (int i1 = 1; i1 < keyroots1.size() + 1; i1++) {
			for (int j1 = 1; j1 < keyroots2.size() + 1; j1++) {
				int i = keyroots1.get(i1 - 1);
				int j = keyroots2.get(j1 - 1);
				TD[i][j] = treedist(l1, l2, i, j, treeMap1, treeMap2);
			}
		}

		return TD[l1.size()][l2.size()];
	}

	private static int treedist(ArrayList<Integer> l1, ArrayList<Integer> l2, int i, int j, TreeMap treeMap1, TreeMap treeMap2) {
		int[][] forestdist = new int[i + 1][j + 1];

		// costs of the three atomic operations
		int Delete = 1;
		int Insert = 1;
		int Relabel = 1;

		forestdist[0][0] = 0;
		for (int i1 = l1.get(i - 1); i1 <= i; i1++) {
//			forestdist[i1][0] = forestdist[i1 - 1][0] + Delete;
			forestdist[i1][0] = forestdist[i1 - 1][0] + Delete*Math.abs(tokenWeight.getOrDefault(treeMap1.labels.get(i1-1).split("/")[0],1));
		}
		for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
			forestdist[0][j1] = forestdist[0][j1 - 1] + Insert*Math.abs(tokenWeight.getOrDefault(treeMap2.labels.get(j1-1).split("/")[0],1));
		}
		for (int i1 = l1.get(i - 1); i1 <= i; i1++) {
			for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
				int i_temp = (l1.get(i - 1) > i1 - 1) ? 0 : i1 - 1;
				int j_temp = (l2.get(j - 1) > j1 - 1) ? 0 : j1 - 1;
				if ((l1.get(i1 - 1) == l1.get(i - 1)) && (l2.get(j1 - 1) == l2.get(j - 1))) {

					int Cost = (treeMap1.labels.get(i1 - 1).equals(treeMap2.labels.get(j1 - 1))) ? 0 : Relabel*(Math.abs(tokenWeight.getOrDefault(treeMap1.labels.get(i1-1).split("/")[0],1))+
							Math.abs(tokenWeight.getOrDefault(treeMap2.labels.get(j1-1).split("/")[0],1)));
					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete*Math.abs(tokenWeight.getOrDefault(treeMap1.labels.get(i1-1).split("/")[0],1)), forestdist[i1][j_temp] + Insert*Math.abs(tokenWeight.getOrDefault(treeMap2.labels.get(j1-1).split("/")[0],1))),
							forestdist[i_temp][j_temp] + Cost);
//					forestdist[i1][j1] = getMin(forestdist[i_temp][j1] + Delete*tokenWeight.getOrDefault(tree1.labels.get(i_temp),1), tree1.labels.get(i_temp), forestdist[i1][j_temp] + Insert*tokenWeight.getOrDefault(tree2.labels.get(j_temp),1), tree2.labels.get(j_temp),
//							forestdist[i_temp][j_temp] + Cost, tree1.labels.get(i1-1), tree2.labels.get(j1 - 1));
					TD[i1][j1] = forestdist[i1][j1];
				} else {
					int i1_temp = l1.get(i1 - 1) - 1;
					int j1_temp = l2.get(j1 - 1) - 1;

					int i_temp2 = (l1.get(i - 1) > i1_temp) ? 0 : i1_temp;
					int j_temp2 = (l2.get(j - 1) > j1_temp) ? 0 : j1_temp;

					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete*Math.abs(tokenWeight.getOrDefault(treeMap1.labels.get(i1-1).split("/")[0],1)), forestdist[i1][j_temp] + Insert*Math.abs(tokenWeight.getOrDefault(treeMap2.labels.get(j1-1).split("/")[0],1))),
							forestdist[i_temp2][j_temp2] + TD[i1][j1]);
//					forestdist[i1][j1] = getMin(forestdist[i_temp][j1] + Delete*tokenWeight.getOrDefault(tree1.labels.get(i_temp),1), tree1.labels.get(i_temp), forestdist[i1][j_temp] + Insert*tokenWeight.getOrDefault(tree2.labels.get(j_temp),1), tree2.labels.get(j_temp),
//							forestdist[i_temp2][j_temp2] + TD[i1][j1], tree1.labels.get(i1-1), tree2.labels.get(j1 - 1));
				}
			}
		}
		return forestdist[i][j];
	}

	private static int getMin(int del, String dellabel, int ins, String inslabel, int upd, String uplabel,String uplab2){
		if(del <= ins && del <= upd){
			System.out.println("Delete:" + dellabel);
			return del;
		}
		if(ins <= del && ins<= upd){
			System.out.println("Insert:" + inslabel);
			return ins;
		}
		System.out.println("Update:" + uplabel +" to " + uplab2);
		return upd;
	}
}
