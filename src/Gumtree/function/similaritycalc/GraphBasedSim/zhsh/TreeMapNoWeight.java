package Gumtree.function.similaritycalc.GraphBasedSim.zhsh;

import java.util.ArrayList;

public class TreeMapNoWeight{

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
			forestdist[i1][0] = forestdist[i1 - 1][0] + Delete;
		}
		for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
			forestdist[0][j1] = forestdist[0][j1 - 1] + Insert;
		}
		for (int i1 = l1.get(i - 1); i1 <= i; i1++) {
			for (int j1 = l2.get(j - 1); j1 <= j; j1++) {
				int i_temp = (l1.get(i - 1) > i1 - 1) ? 0 : i1 - 1;
				int j_temp = (l2.get(j - 1) > j1 - 1) ? 0 : j1 - 1;
				if ((l1.get(i1 - 1) == l1.get(i - 1)) && (l2.get(j1 - 1) == l2.get(j - 1))) {

					int Cost = (treeMap1.labels.get(i1 - 1).equals(treeMap2.labels.get(j1 - 1))) ? 0 : Relabel;
					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete, forestdist[i1][j_temp] + Insert),
							forestdist[i_temp][j_temp] + Cost);
					TD[i1][j1] = forestdist[i1][j1];
				} else {
					int i1_temp = l1.get(i1 - 1) - 1;
					int j1_temp = l2.get(j1 - 1) - 1;

					int i_temp2 = (l1.get(i - 1) > i1_temp) ? 0 : i1_temp;
					int j_temp2 = (l2.get(j - 1) > j1_temp) ? 0 : j1_temp;

					forestdist[i1][j1] = Math.min(
							Math.min(forestdist[i_temp][j1] + Delete, forestdist[i1][j_temp] + Insert),
							forestdist[i_temp2][j_temp2] + TD[i1][j1]);
				}
			}
		}
		return forestdist[i][j];
	}

}
