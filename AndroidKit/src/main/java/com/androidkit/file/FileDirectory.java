/**
 * Copyright (c) 2013, BigBeard Team, Inc. All rights reserved. 
 */
package com.androidkit.file;

import java.util.ArrayList;
import java.util.List;

public class FileDirectory {
	/**
	 * The file directory type
	 */
	private String type; 
	
	/**
	 * The file directory value
	 */
	private String value; 
	
	/**
	 * The file's parent directory, it is the top directory if it's parent is null
	 */
	private FileDirectory parent; 
	
	/**
	 * The file's child directory, it has not the child directory if it is the bottom directory
	 */
	private List<FileDirectory> children; 

	/**
	 * Get the file type
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the file type
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the file value
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the file value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the parent directory object
	 * 
	 * @return
	 */
	public FileDirectory getParent() {
		return parent;
	}

	/**
	 * Set the parent directory
	 * 
	 * @param parent
	 */
	public void setParent(FileDirectory parent) {
		this.parent = parent;
	}

	/**
	 * Get the children directory list
	 * 
	 * @return
	 */
	public List<FileDirectory> getChildren() {
		return children;
	}
	
	/**
	 * Add a file directory for itself
	 * 
	 * @param folderDirectory
	 */
	public void addChild(FileDirectory folderDirectory) {
		if(children == null)
			children = new ArrayList<FileDirectory>();
		folderDirectory.setParent(this);
		children.add(folderDirectory);
	}
}
