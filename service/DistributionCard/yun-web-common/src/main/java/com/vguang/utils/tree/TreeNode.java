package com.vguang.utils.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode implements ITreeNode{
	// 本节点ID
	public Long id;
	// 父节点ID,为0表示第一层结点
	public Long parentId;
	// 父节点,为null表示第一层结点
	public TreeNode parent;
	// 结点名
	public String name;
	// 结点所在层数
	private int layers = 0;
	// 数据
	private String data;
	private List<TreeNode> childrens = new ArrayList<TreeNode>();
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public void setChildrens(List<TreeNode> childrens) {
		this.childrens = childrens;
	}
	public void setLayers(int layers) {
		this.layers = layers;
	}
	public Long getId() {
		return id;
	}
	public Long getParentId() {
		return parentId;
	}
	public TreeNode getParent() {
		return parent;
	}
	public String getName() {
		return name;
	}
	public int getLayers() {
		return layers;
	}
	public String getData() {
		return data;
	}
	public List<TreeNode> getChildrens() {
		return childrens;
	}
	
	public void addChildren(TreeNode node){
		this.childrens.add(node);
	}
	public TreeNode toTreeNode() {
		this.childrens = new ArrayList<TreeNode>();
		return this;
	}
}
