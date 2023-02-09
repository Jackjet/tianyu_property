package com.vguang.utils.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {
	/**
	 * 计算树对象层数，并组成全名
	 * @param treelist 树结构列表
	 * @return 带树对象层数的列表
	 */
	private static List<TreeNode> compLayer(List<TreeNode> treelist){
		if(treelist != null){
			List<TreeNode> rtreelist = new ArrayList<TreeNode>();
			//把树对象放入HashMap映射表中
			Map<Long, TreeNode> map = new HashMap<Long, TreeNode>(treelist.size());
			for(int i = 0; i<treelist.size(); i++){
				TreeNode treenode = treelist.get(i);
				map.put((treenode).getId(), treenode);
			}
			
			//计算层数
			for(int i = 0; i<treelist.size(); i++){
				TreeNode treenode = treelist.get(i);
//				treenode.setNameAll(treenode.getName());
				TreeNode node = map.get(treenode.getId());
				int layer = 1; //第一层
				while(node != null && node.getParentId() != null && node.getParentId().longValue() >= 0){
					node = map.get(node.getParentId());
					if(node != null){
//						treenode.setNameAll(node.getName() + treenode.getNameAll());
						layer++;
						//在父结点中加入子结点
						if(layer == 2){
							node.addChildren(treenode);
						}
					}else{
						break;
					}
				}
				//设置层数
				treenode.setLayers(layer);
				
				//放置对象回List
				rtreelist.add(treenode);
			}
			return rtreelist;
		}
		
		return treelist;
	}
	
	/**
	 * 得到除了结点ID及其子结点的其它所有结点
	 * @param treelist 树结构列表
	 * @param id 
	 * @return 带树对象层数的列表
	 */
	private static List<TreeNode> getParents(List<TreeNode> treelist, TreeNode iTreeNode){
		if(treelist != null){
			List<TreeNode> rtreelist = new ArrayList<TreeNode>();
			//把树对象放入HashMap映射表中
			Map<Long, TreeNode> map = new HashMap<Long, TreeNode>(treelist.size());
			for(int i = 0; i<treelist.size(); i++){
				TreeNode treenode = (TreeNode)treelist.get(i);
				map.put((treenode).getId(), treenode);
			}
			
			//计算父结点及其上结点，如果等于iTreeNode，则不加入结果列表中
			for(int i = 0; i<treelist.size(); i++){
				TreeNode treenode = treelist.get(i);
				
				//是iTreeNode结点，跳过
				if(treenode.getId() != null && iTreeNode.getId() != null && treenode.getId().longValue() == iTreeNode.getId()){
					continue;
				}
				
				TreeNode node = treenode;
				boolean isChild = false;
				while(node != null && node.getId() != null && node.getId().longValue() > 0 && !isChild){
					if(node.getId() != null && iTreeNode.getId() != null && node.getId().longValue() == iTreeNode.getId().longValue()){
						isChild = true;
					}else{
						node = map.get(node.getParentId());
					}
				}
				
				if(!isChild){
					//放置对象回List
					rtreelist.add(treenode);
				}
				
			}
			return rtreelist;
		}
		
		return treelist;
	}
	
	/**
	 * 把给定的数组按树形结构的排序，
	 *
	 * @param list 需排序的数组,元素为TreeNode
	 * @return List 排序后的数组
	 */
	private static List<TreeNode> sortListToTree(List<TreeNode> list) {
		if (list == null) {
			return null;
		}
		try {

			TreeNode tn1 = null, tn2 = null;

			int count = list.size();
			int i = 0, j = 0, k = 0;

			boolean bl = false;
			if (count > 0) {
				for (i = 0; i < count; i++) {
					bl = false;
					for (j = i + 1; j < count; j++) {
						tn1 = list.get(i);
						tn2 = list.get(j);
						if (tn1.getParentId() != null 
								&& tn2.getId() != null 
								&& tn1.getParentId().longValue() == tn2.getId().longValue()) {
							tn1.setParent(tn2);
							list.set(i, tn2);
							list.set(j, tn1);
							bl = true;
						}
					}
					if (bl) {
						i--;
					}
				} // for i count

				for (i = 0; i < count; i++) {
					k = i;
					for (j = i + 1; j < count; j++) {
						tn1 = list.get(i);
						tn2 = list.get(j);
						if (tn2.getParentId() != null
								&& tn1.getId() != null
								&& tn2.getParentId().longValue() == tn1.getId().longValue()) {
							tn2.setParent(tn1);
							list.remove(j);
							list.add(k + 1, tn2);
							k++;
						} // if
					} // for j count
				} // for i count
			} // if count > 0
		} catch (Exception e) {
			// e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 排序得到树结构列表
	 * @param list
	 * @return list
	 */
	public static List<TreeNode> getTreeList(List<ITreeNode> list) {
		List<TreeNode> slist = toTreeList(list);
		
		return compLayer(sortListToTree(slist));
	}
	public static List<TreeNode> getTreeList2(List<ITreeNode> list) {
		List<TreeNode> slist = toTreeList(list);
		
		return sortListToTree(slist);
	}

	/**
	 * 转换ITreeNode结点数组为StrutsTreeNode结点数组
	 * @param list
	 * @return
	 */
	private static List<TreeNode> toTreeList(List<ITreeNode> list) {
		List<TreeNode> slist = new  ArrayList<TreeNode>();
		for(int i=0; i<list.size(); i++){
			ITreeNode iTreeNode = (ITreeNode)list.get(i);
			TreeNode treeNode = iTreeNode.toTreeNode();
			slist.add(treeNode);
		}
		return slist;
	}

	/**
	 * 排序得到树根结点列表
	 * @param list
	 * @return list
	 */
	public static TreeNode getTreeRoot(List<ITreeNode> list) {
		List<TreeNode> slist = toTreeList(list);
		TreeNode root = new TreeNode();
		root.setId(Long.valueOf(0));
		root.setParentId(Long.valueOf(-1));
		root.setName("root");
		slist.add(0, root);
		List<TreeNode> treelist = compLayer(sortListToTree(slist));
		
		return treelist.get(0);
	}
	
	private static boolean dealRoot(TreeNode treeNode, TreeNode treeNode1){
		if(treeNode != null){
			List<TreeNode> childrens = treeNode.getChildrens();
			if(childrens != null && childrens.size() > 0){
				for(int i=0; i<childrens.size(); i++){
					TreeNode child = childrens.get(i);
					if(child != null 
							&& child.getId() != null 
							&& treeNode1.getId() != null
							&& child.getId().longValue() == treeNode1.getId().longValue()){
						//去除child子节点
						childrens.remove(i);
						treeNode.setChildrens(childrens);
						
						return true;
					}else{
						if(dealRoot(child, treeNode1))
							return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 排序得到树根结点列表
	 * @param list
	 * @return list
	 */
	public static TreeNode getTreeRoot(List<ITreeNode> list, ITreeNode iTreeNode) {
		TreeNode root = getTreeRoot(list);
		
		//去除iTreeNode及其下子结点
		TreeNode treeNode = iTreeNode.toTreeNode();
		if(root != null){
			//是根结点
			if(root.getId() != null 
					&& treeNode.getId() != null
					&& root.getId().longValue() == treeNode.getId().longValue()){
				return null;
			}
			
			dealRoot(root, treeNode);
		}
		
		return root;
	}
	
	/**
	 * 得到除了结点ID及其子结点的其它所有结点
	 * @param list 结构列表
	 * @param id 
	 * @return 带树对象层数的列表
	 */
	public static List<TreeNode> getTreeList(List<ITreeNode> list, ITreeNode iTreeNode){
		//排序
		List<TreeNode> treelist = getTreeList(list);
		
		return getParents(treelist, iTreeNode.toTreeNode());
	}
}
